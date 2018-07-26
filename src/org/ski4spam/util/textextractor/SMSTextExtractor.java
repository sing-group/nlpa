package org.ski4spam.util.textextractor;
import java.io.File;

public class SMSTextExtractor extends TextExtractor{
	static TextExtractor instance=null;
	
	private SMSTextExtractor(){
		
	}
	
	public static String[] getExtensions(){
		return new String[]{"sms"};
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new SMSTextExtractor();
		}
		return instance;
	}
	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of SMS text");
	}
}