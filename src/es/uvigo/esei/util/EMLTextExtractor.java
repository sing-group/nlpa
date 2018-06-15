package es.uvigo.esei.util;
import java.io.File;

public class EMLTextExtractor extends TextExtractor{
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
	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of eml text");
	}
}