package org.ski4spam.util.dateextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
  * This is a DateExtracfor for EML files (RFC 2822)
  * <a href="https://tools.ietf.org/html/rfc2822">https://tools.ietf.org/html/rfc2822</a>
  * @author José Ramón Méndez
  */ 
public class EMLDateExtractor extends DateExtractor {
	/**
	  * For loging purposes
	  */
	private static final Logger logger = LogManager.getLogger(EMLDateExtractor.class);

	/**
	  * An instance to implement a singleton pattern
	  */
    static DateExtractor instance = null;

	/**
	   * The default constructor (converted to private to implement singleton)
	   */
    private EMLDateExtractor() {

    }

    /**
		* Retrieve a list of file extensions that can be processed
		* @return an array of file extensions that can be handled with this DateExtractor
		*/
    public static String[] getExtensions() {
        return new String []{"eml"};
    }

    /**
		* Retrieve an instance of the current DateExtractor
		* @return an instance of the current DateExtractor
		*/
    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new EMLDateExtractor();
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
		Date returnValue=null;
		
		try {
					//Create a mime message
					FileInputStream fis=new FileInputStream(f);
					MimeMessage mimeMultipart = new MimeMessage(null,fis);
					returnValue= mimeMultipart.getSentDate();
					fis.close();
					
				
		} catch (MessagingException e) {
					logger.error("Messagging Exception caught / "+e.getMessage()+"Current e-mail: "+f.getAbsolutePath());
					return null;
		} catch (IOException e) {
			        logger.error("IO Exception caught / "+e.getMessage()+"Current e-mail: "+f.getAbsolutePath());
			        return null;
		} catch (Exception e) {
 	                logger.error("Exception caught / "+e.getMessage()+"Current e-mail: "+f.getAbsolutePath());
	                return null;
		}
		
		//System.out.println(sbResult.toString());
		
		return returnValue;
    }
}
