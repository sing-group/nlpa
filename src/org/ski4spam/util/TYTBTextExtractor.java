package org.ski4spam.util;
import java.io.File;

public class TYTBTextExtractor extends TextExtractor{
	static TextExtractor instance=null;
	
	private TYTBTextExtractor(){
		
	}
	
	public static String getExtension(){
		return "tytb";
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new TYTBTextExtractor();
		}
		return instance;
	}
	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of TYTB text");
	}
}