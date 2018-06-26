package org.ski4spam.util.textextractor;

import java.io.File;

public class TWTIDTextExtractor extends TextExtractor{
	static TextExtractor instance=null;
	
	private TWTIDTextExtractor(){
		
	}
	
	public static String getExtension(){
		return "twtid";
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new TWTIDTextExtractor();
		}
		return instance;
	}
	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of twtid text");
	}
}