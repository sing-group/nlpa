package org.ski4spam.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

public class EMLTextExtractor extends TextExtractor{
	private static String cfgPartSelectedOnAlternative="text/plain";
	
	public static String getCfgPartSelectedOnAlternative(){ 
		return cfgPartSelectedOnAlternative;
    }
	
	public static void setCfgPartSelectedOnAlternative(String cfg){
        cfgPartSelectedOnAlternative=cfg;
    }
	
	static TextExtractor instance=null;
	
	private EMLTextExtractor(){
		
	}
	
	public static String getExtension(){
		return "eml";
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new EMLTextExtractor();
		}
		return instance;
	}
	
	/**
	 * Reconstruye los adjuntos
	 * @param partlist Salida: lista de partes del correo electr�nica dadas en forma de <Tipo de parte> <parte>
	 * @param mp Objeto multipart
	 * @throws Exception Excepcion que se puede producir al procesar
	 */
	private void buildPartInfoList(ArrayList<Pair<String,InputStream>> partlist, Multipart mp) throws Exception {
		for (int i=0; i<mp.getCount(); i++) {
			//Get part
			Part apart=mp.getBodyPart(i);
			//handle single & multiparts
			if(apart.isMimeType("multipart/mixed")) {
				//log.info("Cuidad�n, adjunto multipart en email "+this.getId());
				//recurse
				buildPartInfoList(partlist,(Multipart)apart.getContent());	
			} else if(apart.isMimeType("multipart/alterntive")) {
				Multipart mp2=(Multipart)apart.getContent();
				for (int j=0;j<mp2.getCount();j++){
					Part bpart=mp2.getBodyPart(j);
					if (bpart.isMimeType(cfgPartSelectedOnAlternative)){
					    partlist.add(new Pair<String,InputStream>(bpart.getContentType(),bpart.getInputStream()));
					}
			    }
			} else {
				//append the part
				partlist.add(new Pair<String,InputStream>(apart.getContentType(),apart.getInputStream()));
			}
		}
	}//buildPartList	
	
	/**
	  *  Estact text for a file (which is written in eml format)
	  *  @param f The file where the contents will be extracted
	  *  @return an StringBuffer with text contents of message
	  */
	public StringBuffer extractText(File f){
		StringBuffer sbResult=new StringBuffer();
		ArrayList<Pair<String,InputStream>> parts=new ArrayList<Pair<String,InputStream>>();
		
		try {
					//Create a multipart object to read it
					MimeMessage mimeMultipart = new MimeMessage(null,new FileInputStream(f));
					buildPartInfoList(parts,(Multipart)mimeMultipart.getContent());
				
					//Transform each part
					for (Pair<String,InputStream> i:parts){
						String contentType=i.getObj1();
						if (contentType.toLowerCase().indexOf("text/")==0){
						  try {
							if (i.getObj2() instanceof InputStream){
								InputStream is=(InputStream)i.getObj2();
								InputStreamReader isr=new InputStreamReader(is);
								BufferedReader br=new BufferedReader(isr);
								String linea;
				
								while ((linea=br.readLine())!=null) sbResult.append(linea+"\n");
								
								br.close();
								isr.close();
								is.close();
							}
						  } catch (IOException e) {
							
						  }
					    }
					}
				
		} catch (MessagingException e) {
					//log.error("Excepci�n del mensaje (la t�pica): "+e.getMessage()+"Email actual: "+this.getId());
		
		} catch (IOException e) {
		
		} catch (Exception e) {
		
		}
		
		System.out.println(sbResult.toString());
		
		return sbResult;
	}
}