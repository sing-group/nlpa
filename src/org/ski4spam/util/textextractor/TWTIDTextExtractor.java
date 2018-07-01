package org.ski4spam.util.textextractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TWTIDTextExtractor extends TextExtractor {
    private static final Logger logger = LogManager.getLogger(TWTIDTextExtractor.class);
    private static TextExtractor instance = null;
    private TwitterFactory tf;

    private TWTIDTextExtractor() {
        // Setting up the tokens config based on the .ini file on conf/ folder.
        Wini ini = null;
        try {
            ini = new Wini(new File("conf/configurations.ini"));
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage());
        }

        String consumerKey, consumerSecret, accessToken, accessTokenSecret;
        assert ini != null;
        consumerKey = ini.get("twitter", "ConsumerKey");
        consumerSecret = ini.get("twitter", "ConsumerSecret");
        accessToken = ini.get("twitter", "AccessToken");
        accessTokenSecret = ini.get("twitter", "AccessTokenSecret");

        //Setting up the twitter factory object from the Configuration Builder.
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        tf = new TwitterFactory(cb.build());
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
            return null;
        }

        //Extracting and returning the tweet status text or error if not available.
        try {
            Twitter twitter = tf.getInstance();
            Status status = twitter.showStatus(Long.parseLong(tweetId));
            return new StringBuffer(status.getText());
        } catch (TwitterException te) {
            logger.error("Tweet error / " + te.getErrorMessage() + " | Current tweet: " + file.getAbsolutePath());
            return null;
        }
    }
}