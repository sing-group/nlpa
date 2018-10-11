package org.ski4spam.util.dateextractor; 
 
import java.io.File;
import java.util.Date; 
 
/**
  * This dataextractor returns null. This is useful to process files from
  * which the date is imposible to extract
  * @author José Ramón Méndez 
  */
public class NullDateExtractor extends DateExtractor { 
	/**
	  * An instance to implement a singleton pattern
	  */	
    static DateExtractor instance = null; 
 
	/**
	   * The default constructor (converted to private to implement singleton)
	   */
    private NullDateExtractor() { 
 
    } 
 
    /**
		* Retrieve a list of file extensions that can be processed
		* @return an array of file extensions that can be handled with this DateExtractor
		*/
    public static String[] getExtensions() { 
        return new String[] {"ttwt","sms","tsms","tytb"}; 
    } 
 
    /**
		* Retrieve an instance of the current DateExtractor
		* @return an instance of the current DateExtractor
		*/
    public static DateExtractor getInstance() { 
        if (instance == null) { 
            instance = new NullDateExtractor(); 
        } 
        return instance; 
    } 
 
  	/**
  	  * Finds the content date from a file
  	  * @param f The file to use to retrieve the content date
 	  * @return the date of the content
  	  */
		@Override		
    public Date extractDate(File f) { 
        return null; 
    } 
}