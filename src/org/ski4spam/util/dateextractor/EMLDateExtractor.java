package org.ski4spam.util.dateextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;

public class EMLDateExtractor extends DateExtractor {
	private static final Logger logger = LogManager.getLogger(EMLDateExtractor.class);

    static DateExtractor instance = null;

	
    private EMLDateExtractor() {

    }

    public static String[] getExtensions() {
        return new String []{"eml"};
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new EMLDateExtractor();
        }
        return instance;
    }

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
