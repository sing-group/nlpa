package org.ski4spam.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Handle all functionality of accessing tweets by using Twitter Java API
 *
 * @author Yeray Lage
 */
public class TwitterConfigurator {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TwitterConfigurator.class);

    /**
     * A twitterFactory instance
     */
    private static TwitterFactory tf;

    /**
     * A instance of this class to implement a singleton pattern
     */
    private static TwitterConfigurator tc;

    /**
     * A hashmap with a cache of valid tweets
     */
    private static HashMap<Long, TweetStatus> validTweetsCache = new HashMap<>();

    /**
     * A Hashmap witha a cache of valid tweets
     */
    private static HashMap<Long, ErrorTweet> errorTweetsCache = new HashMap<>();

    /**
     * A file to store valid tweets
     */
    private static File validTweetsCacheFile = new File(System.getProperty("java.io.tmpdir"), "validTweetsCache.json");

    /**
     * A file to store invalid twits
     */
    private static File errorTweetsCacheFile = new File(System.getProperty("java.io.tmpdir"), "errorTweetsCache.json");

    /**
     * Build a TwitterConfigurator instance
     */
    private TwitterConfigurator() {
        //Load twitter configuration using System configuration method
        String consumerKey, consumerSecret, accessToken, accessTokenSecret;
        consumerKey = Configuration.getSystemConfig().getConfigOption("twitter", "ConsumerKey");
        consumerSecret = Configuration.getSystemConfig().getConfigOption("twitter", "ConsumerSecret");
        accessToken = Configuration.getSystemConfig().getConfigOption("twitter", "AccessToken");
        accessTokenSecret = Configuration.getSystemConfig().getConfigOption("twitter", "AccessTokenSecret");

        //Setting up the twitter factory object from the Configuration Builder.
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        tf = new TwitterFactory(cb.build());
        readCachedTweets(); // Read tweets stored in cache
    }

    /**
     * Find the status for a tweet from its tweetId
     *
     * @param tweetId The id of the desired tweet
     * @return The status for the desired tweet
     */
    public static Status getStatus(String tweetId) {
        Status toret = null;
        Gson gson = new Gson();

        if (validTweetsCache.containsKey(Long.parseLong(tweetId))) {
            // If already on valid cache
            toret = validTweetsCache.get(Long.parseLong(tweetId));
        } else if (errorTweetsCache.containsKey(Long.parseLong(tweetId))) {
            // If already on error cache
            ErrorTweet et = errorTweetsCache.get(Long.parseLong(tweetId));
            logger.error("Tweet error / " + et.getError() + " | Current tweet: " + et.getTweetId());
        } else {
            // If not on cache
            if (tf==null) tf=getTwitterFactory();
            
            Twitter twitter = tf.getInstance();
            try {
                toret = twitter.showStatus(Long.parseLong(tweetId));
                validTweetsCache.put(toret.getId(), new TweetStatus(toret.getCreatedAt(), toret.getId(), toret.getText(), toret.getLang()));
                try (Writer writer = new FileWriter(validTweetsCacheFile)) {
                    Type statusType = new TypeToken<HashMap<Long, TweetStatus>>() {
                    }.getType();
                    gson.toJson(validTweetsCache, statusType, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (TwitterException te) {
                errorTweetsCache.put(Long.parseLong(tweetId), new ErrorTweet(Long.parseLong(tweetId), te.getErrorMessage()));
                try (Writer writer = new FileWriter(errorTweetsCacheFile)) {
                    Type statusType = new TypeToken<HashMap<Long, ErrorTweet>>() {
                    }.getType();
                    gson.toJson(errorTweetsCache, statusType, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.error("Tweet error / " + te.getErrorMessage() + " | Current tweet: " + tweetId);
                return null;
            }
        }

        return toret;
    }

    /**
     * Retrieves a TwitterFactory to use it externally
     *
     * @return the TwitterFactory to use it externally
     */
    public static TwitterFactory getTwitterFactory() {
        if (tc == null) {
            // If not instanced yet, we do it
            tc = new TwitterConfigurator();
        }

        return tf;
    }

    /**
     * Read Cached Tweets from a JSON file
     */
    private void readCachedTweets() {
        BufferedReader bufferedReader = null;
        Gson gson = new Gson();

        try {
            File f = errorTweetsCacheFile;
            if (!f.exists()) {
                f.createNewFile();
            }
            bufferedReader = new BufferedReader(new FileReader(f));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type errorType = new TypeToken<HashMap<Long, ErrorTweet>>() {
        }.getType();
        HashMap<Long, ErrorTweet> errorTweet = gson.fromJson(bufferedReader, errorType);
        if (errorTweet != null) {
            errorTweet.forEach((k, v) -> errorTweetsCache.put(k, v));
        }

        try {
            File f = validTweetsCacheFile;
            if (!f.exists()) {
                f.createNewFile();
            }
            bufferedReader = new BufferedReader(new FileReader(f));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type statusType = new TypeToken<HashMap<Long, TweetStatus>>() {
        }.getType();
        HashMap<Long, TweetStatus> status = gson.fromJson(bufferedReader, statusType);
        if (status != null) {
            status.forEach((k, v) -> validTweetsCache.put(k, v));
        }
    }
}
