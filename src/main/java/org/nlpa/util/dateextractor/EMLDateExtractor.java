/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.util.dateextractor;

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
