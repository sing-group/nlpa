package org.ski4spam.util;
import java.io.File;

public class TSMSTextExtractor extends TextExtractor{
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
		return new StringBuffer("This is an example of TSMS text");
	}
}