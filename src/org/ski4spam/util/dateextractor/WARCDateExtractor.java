package org.ski4spam.util.dateextractor;

import java.io.File;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import org.archive.io.ArchiveRecord;
import org.archive.io.warc.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class WARCDateExtractor extends DateExtractor {
	private static final Logger logger = LogManager.getLogger(WARCDateExtractor.class);
    static DateExtractor instance = null;

    private WARCDateExtractor() {

    }

    public static String getExtension() {
        return "warc";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new WARCDateExtractor();
        }
        return instance;
    }

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