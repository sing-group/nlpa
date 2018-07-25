package org.ski4spam.util.textextractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.util.TwitterConfigurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TWTIDTextExtractor extends TextExtractor {
    private static final Logger logger = LogManager.getLogger(TWTIDTextExtractor.class);
    private static TextExtractor instance = null;

    private TWTIDTextExtractor() {

    }

    public static String getExtension() {
        return "twtid";
    }

    public static TextExtractor getInstance() {
        if (instance == null) {
            instance = new TWTIDTextExtractor();
        }
        return instance;
    }

    public StringBuffer extractText(File file) {
        //Achieving the tweet id from the given file.
        String tweetId;
        try {
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            tweetId = b.readLine();
            b.close();
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage() + "Current tweet: " + file.getAbsolutePath());
            return null; //Return a null will cause a fuerther invalidation of the instance
        }

        //Extracting and returning the tweet status text or error if not available.
        try {
            return new StringBuffer(TwitterConfigurator.getStatus(tweetId).getText());
        } catch (NullPointerException e) {
            return null;
        }
    }
}