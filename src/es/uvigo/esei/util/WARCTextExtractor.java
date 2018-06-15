package es.uvigo.esei.util;
import java.io.File;

public class WARCTextExtractor extends TextExtractor{
	static TextExtractor instance=null;
	
	private WARCTextExtractor(){
		
	}
	
	public static String getExtension(){
		return "warc";
	}
	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new WARCTextExtractor();
		}
		return instance;
	}
	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of WARC text");
	}
}