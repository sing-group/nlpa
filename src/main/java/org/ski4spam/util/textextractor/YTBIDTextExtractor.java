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
import org.ini4j.Wini;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;


public class YTBIDTextExtractor extends TextExtractor {
    private static final Logger logger = LogManager.getLogger(YTBIDTextExtractor.class);
    static TextExtractor instance = null;

    private YTBIDTextExtractor() {

    }

    public static String[] getExtensions() {
        return new String[] {"ytbid"};
    }

    public static TextExtractor getInstance() {
        if (instance == null) {
            instance = new YTBIDTextExtractor();
        }
        return instance;
    }

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
        // Setting up the tokens config based on the .ini file on conf/ folder.
        Wini ini = null;
        try {
            ini = new Wini(new File("conf/configurations.ini"));
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage());
        }
        
        String apiKey;
        assert ini != null;
        apiKey = ini.get("youtube", "APIKey");
        
        
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