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
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;

/**
  * A DateExtractor for WarcFiles. In this case the data extracted corresponds to 
  * the download date
  * @author Reyes Pavón
  * @author Rosalía Laza
  */
public class WARCDateExtractor extends DateExtractor {
	/**
	  * For loging purposes
	  */	
	private static final Logger logger = LogManager.getLogger(WARCDateExtractor.class);
	
	/**
	  * An instance to implement a singleton pattern
	  */	
    static DateExtractor instance = null;

	/**
	   * The default constructor (converted to private to implement singleton)
	   */
    private WARCDateExtractor() {

    }

    /**
		* Retrieve a list of file extensions that can be processed
		* @return an array of file extensions that can be handled with this DateExtractor
		*/
    public static String[] getExtensions() {
        return new String[] {"warc"};
    }

    /**
		* Retrieve an instance of the current DateExtractor
		* @return an instance of the current DateExtractor
		*/
    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new WARCDateExtractor();
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
        Date sbResult = null;
        ArchiveRecord ar = null;
   
        try 
        {  
            WARCReader warcReader = WARCReaderFactory.get(f);
            Iterator<ArchiveRecord> it = warcReader.iterator();
            if (it.hasNext()) {
                   ar = it.next();
                   Map<String,Object> header = ar.getHeader().getHeaderFields();
                   String warcType = (String)header.get("WARC-Type");

                    if (warcType.equals("warcinfo")){
                        String dateWarc = (String)ar.getHeader().getDate();
                        SimpleDateFormat sdf = new SimpleDateFormat();
                        sdf.applyPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                        sbResult = sdf.parse(dateWarc, new ParsePosition(0));
                    }
                    else{
                        logger.error("warc error at date extraction / " + " | Current warc: " + f.getAbsolutePath());
                        return null;   
                    }
            }
        } 
        catch (IOException e) {
            logger.error(e.getMessage() + " while processing " + f.getAbsolutePath());
            return null;
        }
        return sbResult;
    }
}