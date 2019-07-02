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