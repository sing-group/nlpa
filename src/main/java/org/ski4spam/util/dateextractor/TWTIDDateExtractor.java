package org.ski4spam.util.dateextractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.util.TwitterConfigurator;

import twitter4j.Status;
import twitter4j.TwitterFactory;

public class TWTIDDateExtractor extends DateExtractor {
    private static final Logger logger = LogManager.getLogger(TWTIDDateExtractor.class);
    private static DateExtractor instance = null;
    private TwitterFactory tf = TwitterConfigurator.getTwitterFactory();

    private String tweetId;

    private TWTIDDateExtractor() {

    }

    public static String[] getExtensions() {
        return new String[] {"twtid"};
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new TWTIDDateExtractor();
        }
        return instance;
    }

    public Date extractDate(File file) {
        //Achieving the tweet id from the given file.
        try {
            FileReader f = new FileReader(file);
            BufferedReader b = new BufferedReader(f);
            tweetId = b.readLine();
            b.close();
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage() + "Current tweet: " + file.getAbsolutePath());
            return null;
        }

        //Extracting and returning the tweet status date or error if not available.
        Status status = TwitterConfigurator.getStatus(tweetId);
        if (status != null) {
            return status.getCreatedAt();
        } else {
            return null;
        }
    }
}