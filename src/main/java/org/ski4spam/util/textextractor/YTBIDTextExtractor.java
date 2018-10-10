package org.ski4spam.util.textextractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.ski4spam.util.Configuration;

/** 
  * A TextExtractor used to extract text from youtube comments identifier
  * Files using this TextExtractor should contain only a identifier of a 
  * youtube comment
  * @author Rosalía Laza
  * @author Reyes Pavón
  */
public class YTBIDTextExtractor extends TextExtractor {
	/**
	  * For logging purposes
	  */
    private static final Logger logger = LogManager.getLogger(YTBIDTextExtractor.class);
	
	/**
	  * A static instance of the TexTextractor to implement a singleton pattern
	  */
    static TextExtractor instance = null;

	/**
	  * Private default constructor
	  */
    private YTBIDTextExtractor() {

    }

    /**
		* Retrieve the extensions that can process this TextExtractor
		* @return An array of Strings containing the extensions of files that this TextExtractor can handle
		*/
    public static String[] getExtensions() {
        return new String[] {"ytbid"};
    }

    /**
		* Return an instance of this TextExtractor
		* @return an instance of this TextExtractor
		*/
    public static TextExtractor getInstance() {
        if (instance == null) {
            instance = new YTBIDTextExtractor();
        }
        return instance;
    }

    /**
		* Extracts text from a given file
		* @param file The file where the text is included
		* @return A StringBuffer with the extracted text		 
		*/
	 @Override
    public StringBuffer extractText(File file) {
        //Achieving the youtube id from the given file.
        String youtubeId;
        StringBuffer sbResult = new StringBuffer();
        String text = null;
        try {
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            youtubeId = b.readLine();
            b.close();
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage() + "Current youtube: " + file.getAbsolutePath());
            return null; //Return a null will cause a fuerther invalidation of the instance
        }

		  String apiKey =  Configuration.getSystemConfig().getConfigOption("youtube", "APIKey");
        
        //Extracting and returning the youtube text or error if not available.
        try {
            URL url = new URL("https://www.googleapis.com/youtube/v3/comments?part=snippet&id=" + youtubeId + "&textFormat=html&key=" + apiKey);
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is);
            JsonObject obj = rdr.readObject();
            JsonArray arr = obj.getJsonArray("items");
            if (arr.isEmpty()) {
                logger.error("empty array while processing " + file.getAbsolutePath());
                return null;
            } else {
                text = arr.getJsonObject(0).getJsonObject("snippet").getString("textOriginal");
                //detecting charset with library
                byte[] rawData = text.getBytes();
                CharsetDetector detector = new CharsetDetector();
                detector.setText(rawData);
                CharsetMatch cm = detector.detect();
                logger.warn("Charset guesed: " + cm.getName() + " [confidence=" + cm.getConfidence() + "/100]for " + file.getAbsolutePath() + " Content type: " + text);
                sbResult.append(new String(rawData, Charset.forName(cm.getName())));
                return sbResult;
            }

        } catch (MalformedURLException e) {
            logger.error(e.getMessage() + " while processing " + file.getAbsolutePath());
            return null;
        } catch (IOException e) {
            logger.error(e.getMessage() + " while processing " + file.getAbsolutePath());
            return null;
        }
    }
}