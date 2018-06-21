package org.ski4spam.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.nio.charset.Charset;

public class TSMSTextExtractor extends TextExtractor{
	private static final Logger logger = LogManager.getLogger(TSMSTextExtractor.class);
		
	static TextExtractor instance=null;
	
	private TSMSTextExtractor(){
		
	}
	
	public static String getExtension(){
		return "tsms";
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new TSMSTextExtractor();
		}
		return instance;
	}
	
	public StringBuffer extractText(File f){
		StringBuffer sbResult=new StringBuffer();
		FileInputStream is=null;
		
		try{
  		  is=new FileInputStream(f);
		
		  byte contents[]=new byte[is.available()];
		  is.read(contents);
		
	      CharsetDetector detector = new CharsetDetector();
	      detector.setText(contents);
          CharsetMatch cm = detector.detect();
		  logger.info("Charset guesed: "+cm.getName()+"[confidence="+cm.getConfidence()+"/100] for "+f.getAbsolutePath());
		  sbResult.append(new String(contents, Charset.forName(cm.getName())));		
		  is.close();
		} catch (IOException e){
			logger.error("IOException cautght"+e.getMessage());
		} finally {
			if (is!=null) try { 
				is.close(); 
			} catch (IOException e){
				logger.fatal("IOException cautght trying to recover from a previous IO error: "+e.getMessage());
			}
		}
		return sbResult;
	}
}