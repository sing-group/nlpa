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


public class YTBIDDateExtractor extends DateExtractor {
	private static final Logger logger = LogManager.getLogger(YTBIDDateExtractor.class);
    static DateExtractor instance = null;

    private YTBIDDateExtractor() {

    }

    public static String[] getExtensions() {
        return new String[] {"ytbid"};
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new YTBIDDateExtractor();
        }
        return instance;
    }

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

        //Extracting and returning the youtube data or error if not available.
        try {
            URL url = new URL("https://www.googleapis.com/youtube/v3/comments?part=snippet&id=" + youtubeId + "&textFormat=html&key=AIzaSyAgGPiyeKbC7xnY3eTmRXU22lxc2TpyQoE");
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