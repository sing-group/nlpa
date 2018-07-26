package org.ski4spam.util.textextractor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.archive.io.ArchiveRecord;
import org.archive.io.warc.*;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;

public class WARCTextExtractor extends TextExtractor{
	static TextExtractor instance=null;
	private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
	private static final Logger logger = LogManager.getLogger(WARCTextExtractor.class);
	
	private WARCTextExtractor(){
		
	}
	
	public static String[] getExtensions(){
		return new String[]{"warc"};
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new WARCTextExtractor();
		}
		return instance;
	}
	
	private String getCharsetFromContentType(String contentType) {
      if (contentType == null)
        return null;

      Matcher m = charsetPattern.matcher(contentType);
      if (m.find()) {
        return m.group(1).trim().toUpperCase();
      }
      return null;
    }


public StringBuffer extractText(File f){
    
    StringBuffer sbResult=new StringBuffer();
    ArchiveRecord ar = null;
   
    try 
    {  
        WARCReader warcReader = WARCReaderFactory.get(f);

        Iterator<ArchiveRecord> it = warcReader.iterator();
        while (it.hasNext()) {
            
                    ar = it.next();
                    
            
                    Map<String,Object> header = ar.getHeader().getHeaderFields();
                    String warcType = (String)header.get("WARC-Type");

                    if (warcType.equals("response") || warcType.equals("resource")){
                            String value = null; 
                            try{

                                int available=ar.available();
                                byte[] rawData = new byte[available];
                                ar.read(rawData);
                                String content = new String(rawData);  //guarda todo el contenido de los archive record

                                if (warcType.equals("response") ){    
                                    String patternStr = "\\r?\\n\\r?\\n"; //para detectar retorno de carro y salto de linea
                                    Pattern pattern = Pattern.compile(patternStr);
                                    Matcher matcher = pattern.matcher(content);
                                    int CRPosition=-1;
                                    if(matcher.find()) CRPosition=matcher.start();
                                    else { // error no tiene formato correcto 
                                        logger.error("Double carriage return expected while processing " + f.getAbsolutePath());
                                        return null;
                                    }
                                    //guardar hasta la primera linea en blanco (respuesta del servidor)
                                    value = content.substring(0, CRPosition);

                                    //comprueba que en la respuesta del servidor la entrada Content-Type: text/...
                                    if (value.toLowerCase().contains("text/plain") || value.toLowerCase().contains("text/html")){
                                        for (;CRPosition<content.length() && (content.charAt(CRPosition)=='\n' || content.charAt(CRPosition)=='\r');CRPosition++);
                                        if (CRPosition==content.length()){
                                            //Esto es un error formato incorrecto
                                            logger.error("Warc record content expected while processing " + f.getAbsolutePath());
                                            return null;
                                        }
                                        rawData=Arrays.copyOfRange(rawData, CRPosition, available); //guarda solo data
                                    }

                                }
                                else if (warcType.equals("resource")){ 
                                        value = (String)header.get("Content-Type");
                                }
                                //comprueba que en la cabecera del resource y otra vez que en la respuesta del servidor del response content-type = text/...  
                                if (value.toLowerCase().contains("text/plain") || value.toLowerCase().contains("text/html")){
                                    String rawDataAsStr=new String(rawData);
                                    String charsetName=getCharsetFromContentType(rawDataAsStr); //busca charSet en data
                                    if (charsetName==null)
                                       charsetName=getCharsetFromContentType(value); //busca charSet en respuesta del servidor (response) o en la etiqueta ContentType de resource 
                                    if (charsetName!=null){
                                            logger.info("charset found in content-type: "+charsetName);
                                            sbResult.append(new String(rawData, Charset.forName(charsetName)));
                                    }else{ //Detect the charset using CharsetDetector Library
                                            CharsetDetector detector = new CharsetDetector(); 
                                            detector.setText(rawData);
                                            CharsetMatch cm = detector.detect();
                                            logger.warn("Charset guesed: "+cm.getName()+" [confidence="+cm.getConfidence()+"/100]for "+f.getAbsolutePath()+" Content type: "+value);                                    
                                            sbResult.append(new String(rawData, Charset.forName(cm.getName())));
                                    }

                                }
                                ar.close();                    
                            }catch(IOException e) {
                                logger.error(e.getMessage() + " while processing " + f.getAbsolutePath());
                                return null;
                            } finally {
                                  if (ar!=null) ar.close();
                        }
                    } 
            
                
        }

    } 
    catch (IOException e) {
        logger.error(e.getMessage() + " while processing " + f.getAbsolutePath());
        return null;
    }
    catch (RuntimeException e){
        logger.error(e.getMessage() + " while processing " + f.getAbsolutePath());
        return null;
    }

        
    
    return sbResult;
    
}
}