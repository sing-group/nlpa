package org.ski4spam.util.textextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/** 
  * A TextExtractor used to extract text from text/plain files
  * It is currrently used to handle SMS texts and other plain texts
  * Files using this TextExtractor should contain only text 
  */
public class GenericTextExtractor extends TextExtractor {
	/**
	  * For logging purposes
	  */	
    private static final Logger logger = LogManager.getLogger(GenericTextExtractor.class);

 	/**
 	  * A static instance of the TexTextractor to implement a singleton pattern
 	  */	
    static TextExtractor instance = null;

 	/**
 	  * Private default constructor
 	  */	
    private GenericTextExtractor() {

    }

    /**
 	* Retrieve the extensions that can process this TextExtractor
 	* @return An array of Strings containing the extensions of files that this TextExtractor can handle
 	*/		
	public static String[] getExtensions(){
		return new String[]{"tsms","ttwt","tytb"};
	}

   /**
	* Return an instance of this TextExtractor
	* @return an instance of this TextExtractor
	*/		
    public static TextExtractor getInstance() {
        if (instance == null) {
            instance = new GenericTextExtractor();
        }
        return instance;
    }
	 
    /**
 	* Extracts text from a given file
 	* @param f The file where the text is included
	* @return A StringBuffer with the extracted text		 
 	*/
  @Override	
    public StringBuffer extractText(File f) {
        StringBuffer sbResult = new StringBuffer();
        FileInputStream is = null;

        try {
            is = new FileInputStream(f);

            byte contents[] = new byte[is.available()];
            is.read(contents);

            CharsetDetector detector = new CharsetDetector();
            detector.setText(contents);
            CharsetMatch cm = detector.detect();
            logger.info("Charset guessed: " + cm.getName() + "[confidence=" + cm.getConfidence() + "/100] for " + f.getAbsolutePath());
            sbResult.append(new String(contents, Charset.forName(cm.getName())));
            is.close();
        } catch (IOException e) {
            logger.error("IOException caught" + e.getMessage());
			sbResult=null;
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                logger.fatal("IOException caught trying to recover from a previous IO error: " + e.getMessage());
            }
        }
        return sbResult;
    }
}