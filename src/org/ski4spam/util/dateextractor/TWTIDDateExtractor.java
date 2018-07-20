package org.ski4spam.util.dateextractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.util.TwitterConfigurator;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Date;

public class TWTIDDateExtractor extends DateExtractor {
    private static final Logger logger = LogManager.getLogger(TWTIDDateExtractor.class);
    private static DateExtractor instance = null;
    private TwitterFactory tf = TwitterConfigurator.getTwitterFactory();

    private String tweetId;

    private TWTIDDateExtractor() {

    }

    public static String getExtension() {
        return "twtid";
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
        try {
            Twitter twitter = tf.getInstance();
            Status status = twitter.showStatus(Long.parseLong(tweetId));
            return status.getCreatedAt();
        } catch (TwitterException te) {
            logger.error("Tweet error at date extraction / " + te.getErrorMessage() + " | Current tweet: " + file.getAbsolutePath());
            return null;
        }
    }
}