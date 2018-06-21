package org.ski4spam.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class EMLTextExtractor extends TextExtractor{
	private static final Logger logger = LogManager.getLogger(EMLTextExtractor.class);
	
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
	
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
		if (mp.getContentType().indexOf("multipart/alternative")>-1){
			for (int j=0;j<mp.getCount();j++){
				Part bpart=mp.getBodyPart(j);
				if (bpart.isMimeType(cfgPartSelectedOnAlternative)){
				    partlist.add(new Pair<String,InputStream>(bpart.getContentType(),bpart.getInputStream()));
				}
		    }	
		}else if (mp.getContentType().indexOf("multipart/signed")>-1){
			for (int j=0;j<mp.getCount();j++){
				Part bpart=mp.getBodyPart(j);
				if (!bpart.isMimeType("application/pgp-signature")){
				    partlist.add(new Pair<String,InputStream>(bpart.getContentType(),bpart.getInputStream()));
				}
		    }	
		}else if (mp.getContentType().indexOf("multipart/mixed")>-1){
		  for (int i=0; i<mp.getCount(); i++) {
			//Get part
			Part apart=mp.getBodyPart(i);
			//handle single & multiparts			
			if(apart.getContentType().indexOf("multipart/")>=0) {
				buildPartInfoList(partlist,(Multipart)apart.getContent());	
			} else {
				partlist.add(new Pair<String,InputStream>(apart.getContentType(),apart.getInputStream()));
			}
		  }
	    }
	}//buildPartList	

    private String getCharsetFromContentType(String contentType) {
      if (contentType == null)
        return null;

      Matcher m = charsetPattern.matcher(contentType);
      if (m.find()) {
        return m.group(1).trim().toUpperCase();
      }
      return null;
    }
	
	/**
	  *  Estact text for a file (which is written in eml format)
	  *  @param f The file where the contents will be extracted
	  *  @return an StringBuffer with text contents of message
	  */
	public StringBuffer extractText(File f){
		StringBuffer sbResult=new StringBuffer();
		ArrayList<Pair<String,InputStream>> parts=new ArrayList<Pair<String,InputStream>>();
		
		try {
					//Create a mime message
					MimeMessage mimeMultipart = new MimeMessage(null,new FileInputStream(f));
					
					//If it is not multipart, anotate the part to handle it later
				    if (mimeMultipart.getContentType().indexOf("multipart/") == -1) 
						parts.add(new Pair<String,InputStream>(mimeMultipart.getContentType(),mimeMultipart.getInputStream()));
					//If multipart, then recursivelly compile parts to handle them later
					else buildPartInfoList(parts,(Multipart)mimeMultipart.getContent());
					
					//Transform each compiled part
					for (Pair<String,InputStream> i:parts){
						String contentType=i.getObj1();
						if (contentType.toLowerCase().indexOf("text/")==0){
							InputStream is=null;

						  try {
							if (i.getObj2() instanceof InputStream){
								is=(InputStream)i.getObj2();

							       
								byte contents[]=new byte[is.available()];
								is.read(contents);
								
								String charsetName=getCharsetFromContentType(contentType);
								if (charsetName!=null){
									 logger.info("charset found in content-type: "+charsetName);
								     sbResult.append(new String(contents, Charset.forName(charsetName)));
							    }else{
								    CharsetDetector detector = new CharsetDetector();
								    detector.setText(contents);
 					                CharsetMatch cm = detector.detect();
									logger.warn("Charset guesed: "+cm.getName()+" [confidence="+cm.getConfidence()+"/100]for "+f.getAbsolutePath()+" Content type: "+contentType);
									sbResult.append(new String(contents, Charset.forName(cm.getName())));
								}

								is.close();
							}
						  } catch (IOException e) {
							  System.out.println("Error while processing "+f.getAbsolutePath());
							  System.out.println(e.getMessage());
						  } finally {
							  if (is!=null) is.close();
						  }
					    }
					}
				
		} catch (MessagingException e) {
					logger.error("Messagging Exception caught / "+e.getMessage()+"Current e-mail: "+f.getAbsolutePath());
		
		} catch (IOException e) {
		
		} catch (Exception e) {
		
		}
		
		//System.out.println(sbResult.toString());
		
		return sbResult;
	}
}
