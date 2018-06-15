package es.uvigo.esei.util;
import java.io.File;

public class TTWTTextExtractor extends TextExtractor{
	static TextExtractor instance=null;
	
	private TTWTTextExtractor(){
		
	}
	
	public static String getExtension(){
		return "ttwt";
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new TTWTTextExtractor();
		}
		return instance;
	}
	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of ttwt text");
	}
}