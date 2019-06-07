package org.ski4spam.util.dateextractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.util.Configuration;

/**
  * A DateExtractor for ytbid files (that stands for youtube comments)
  * These files only contains the identifier of the youtube comment
  * @author Reyes Pavón
  * @author Rosalía Laza
  */
public class YTBIDDateExtractor extends DateExtractor {
	/**
	  * For loging purposes
	  */	
	private static final Logger logger = LogManager.getLogger(YTBIDDateExtractor.class);

	/**
	  * An instance to implement a singleton pattern
	  */
    static DateExtractor instance = null;

	/**
	   * The default constructor (converted to private to implement singleton)
	   */
    private YTBIDDateExtractor() {

    }

    /**
		* Retrieve a list of file extensions that can be processed
		* @return an array of file extensions that can be handled with this DateExtractor
		*/
    public static String[] getExtensions() {
        return new String[] {"ytbid"};
    }

    /**
		* Retrieve an instance of the current DateExtractor
		* @return an instance of the current DateExtractor
		*/
    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new YTBIDDateExtractor();
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
		String youtubeId;
        Date dateResult = null;
        try {
            FileReader file = new FileReader(f);
            BufferedReader b = new BufferedReader(file);
            youtubeId = b.readLine();
            b.close();
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage() + "Current youtube: " + f.getAbsolutePath());
            return null; //Return a null will cause a fuerther invalidation of the instance
        }

        String apiKey =  Configuration.getSystemConfig().getConfigOption("youtube", "APIKey");
        
        //Extracting and returning the youtube data or error if not available.
        try {
            URL url = new URL("https://www.googleapis.com/youtube/v3/comments?part=snippet&id=" + youtubeId + "&textFormat=html&key="+apiKey);
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is);
            JsonObject obj = rdr.readObject();
            JsonArray arr = obj.getJsonArray("items");
            if (arr.isEmpty()){
                logger.error("empty array while processing " + f.getAbsolutePath());
                return null;
            }
            else {
                String text = arr.getJsonObject(0).getJsonObject("snippet").getString("publishedAt");   
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                dateResult = sdf.parse(text, new ParsePosition(0));
            }
        } 
        catch (MalformedURLException e) {
            logger.error(e.getMessage() + " while processing " + f.getAbsolutePath());
            return null;
        }
        catch (IOException e) {
            logger.error(e.getMessage() + " while processing " + f.getAbsolutePath());
            return null;
        }
        return dateResult;
    }
}