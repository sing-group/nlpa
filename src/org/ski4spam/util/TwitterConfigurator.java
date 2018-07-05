package org.ski4spam.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;

// This is a Singleton class
public class TwitterConfigurator {
    private static final Logger logger = LogManager.getLogger(TwitterConfigurator.class);
    private static TwitterFactory tf;
    private static TwitterConfigurator tc;

    private TwitterConfigurator() {
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

    public static TwitterFactory getTwitterFactory() {
        if (tc == null) {
            // If not instanced yet, we do it
            tc = new TwitterConfigurator();
        }

        return tf;
    }
}
