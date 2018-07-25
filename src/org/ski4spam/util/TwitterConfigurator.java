package org.ski4spam.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

// This is a Singleton class
public class TwitterConfigurator {
    private static final Logger logger = LogManager.getLogger(TwitterConfigurator.class);
    private static TwitterFactory tf;
    private static TwitterConfigurator tc;
    private static HashMap<Long, TweetStatus> validTweetsCache = new HashMap<>();
    private static HashMap<Long, ErrorTweet> errorTweetsCache = new HashMap<>();

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
        readCachedTweets(); // Read tweets stored in cache
    }

    public static Status getStatus(String tweetId) {
        Status toret = null;
        Gson gson = new Gson();

        if (validTweetsCache.containsKey(Long.parseLong(tweetId))) {
            // If already on valid cache
            System.out.println("Cached as valid.");
            toret = validTweetsCache.get(Long.parseLong(tweetId));
        } else if (errorTweetsCache.containsKey(Long.parseLong(tweetId))) {
            // If already on error cache
            ErrorTweet et = errorTweetsCache.get(Long.parseLong(tweetId));
            System.out.println("Cached as error.");
            logger.error("Tweet error / " + et.getError() + " | Current tweet: " + et.getTweetId());
        } else {
            // If not on cache
            System.out.println("NOT cached.");
            Twitter twitter = tf.getInstance();
            try {
                toret = twitter.showStatus(Long.parseLong(tweetId));
                validTweetsCache.put(toret.getId(), new TweetStatus(toret.getCreatedAt(), toret.getId(), toret.getText(), toret.getLang()));
                try (Writer writer = new FileWriter("src/org/ski4spam/util/validTweetsCache.json")) {
                    Type statusType = new TypeToken<HashMap<Long, TweetStatus>>() {
                    }.getType();
                    gson.toJson(validTweetsCache, statusType, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (TwitterException te) {
                errorTweetsCache.put(Long.parseLong(tweetId), new ErrorTweet(Long.parseLong(tweetId), te.getErrorMessage()));
                try (Writer writer = new FileWriter("src/org/ski4spam/util/errorTweetsCache.json")) {
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

    public static TwitterFactory getTwitterFactory() {
        if (tc == null) {
            // If not instanced yet, we do it
            tc = new TwitterConfigurator();
        }

        return tf;
    }

    // src/org/ski4spam/util/validTweetsCache.json
    private void readCachedTweets() {
        BufferedReader bufferedReader = null;
        Gson gson = new Gson();

        try {
            File f = new File("src/org/ski4spam/util/errorTweetsCache.json");
            if (!f.exists()) f.createNewFile();
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
            File f = new File("src/org/ski4spam/util/validTweetsCache.json");
            if (!f.exists()) f.createNewFile();
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
