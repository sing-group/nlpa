package org.ski4spam.util.textextractor;
import java.io.File;

/** 
  * A TextExtractor used to extract text from SMSs 
  * Files using this TextExtractor should contain the original 
  * representation of SMS 
  * TODO: Implement this
  */
public class SMSTextExtractor extends TextExtractor{
	/**
	  * A static instance of the TexTextractor to implement a singleton pattern
	  */	
	static TextExtractor instance=null;

	/**
	  * Private default constructor
	  */	
	private SMSTextExtractor(){
		
	}

   /**
	* Retrieve the extensions that can process this TextExtractor
	* @return An array of Strings containing the extensions of files that this TextExtractor can handle
	*/	
	public static String[] getExtensions(){
		return new String[]{"sms"};
	}

   /**
	* Return an instance of this TextExtractor
	* @return an instance of this TextExtractor
	*/	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new SMSTextExtractor();
		}
		return instance;
	}
	
   /**
	* Extracts text from a given file
	* @param f The file where the text is included
	* @return A StringBuffer with the extracted text		
	*/
 @Override	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of SMS text");
	}
}