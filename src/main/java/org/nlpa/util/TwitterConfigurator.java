/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.util;

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
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.LinkedHashMap;
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
     * The default file name where tweet cache will be saved
     */
    public static final String DEFAULT_TWEETS_CACHE_FILE = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "tweetsCache.json";

    /**
     * The file name where tweet cache will be saved
     */
    private static final String outputFile = DEFAULT_TWEETS_CACHE_FILE;

    /**
     * A hashmap with a cache of valid tweets
     */
    private static HashMap<Long, TweetStatus> tweetsCache = new HashMap<>();

    /**
     * A instance of tweet configurator to implement a singleton pattern
     */
    private static TwitterConfigurator twitterConfigurator = null;

    /**
     * A twitterFactory instance
     */
    private static TwitterFactory tf;

    /**
     * A instance of this class to implement a singleton pattern
     */
    private static TwitterConfigurator tc;

    /**
     * The default constructor. Creates a TwitterConfigurator instance.
     */
    private TwitterConfigurator() {
        tweetsCache = new LinkedHashMap<>();
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
    }

    /**
     * Retrieve the tweet cache
     *
     * @return The default cache for tweet
     */
    public static TwitterConfigurator getTwitterData() {
        if (twitterConfigurator == null) {
            twitterConfigurator = new TwitterConfigurator();
        }
        return twitterConfigurator;
    }

    /**
     * Add a text to youtube cache
     *
     * @param createdAt The date of creation
     * @param id The identifier of the tweet
     * @param text The text of the tweet
     * @param lang The language of the tweet
     * @param error Indicates if the tweet is correct
     */
    public void add(Date createdAt, long id, String text, String lang, boolean error) {
        if (!tweetsCache.containsKey(id)) {
            tweetsCache.put(id, new TweetStatus(createdAt, id, text, lang, error));
        }
    }

    /**
     * Determines if a tweet id is included in the cache
     *
     * @param id The identifier of the tweet
     * @return a boolean indicating whether the id is included in the tweet
     * cache or not
     */
    public boolean isIncluded(long id) {
        return tweetsCache.containsKey(id);
    }

    /**
     * Retrieves a TwitterFactory to use it externally
     *
     * @return the TwitterFactory to use it externally
     */
    public TwitterFactory getTwitterFactory() {
        if (tc == null) {
            // If not instanced yet, we do it
            tc = new TwitterConfigurator();
        }

        return tf;
    }

    /**
     * Find the status for a tweet from its tweetId
     *
     * @param tweetId The id of the desired tweet
     * @return The status for the desired tweet
     */
    public Status getStatus(String tweetId) {
        Status toret;

        if (twitterConfigurator.size() == 0) {
            twitterConfigurator.retrieveTweetCache();
        }
        if (this.isIncluded(Long.parseLong(tweetId))) {
            // If already on valid cache
            if (tweetsCache.get(Long.parseLong(tweetId)).getError()) {
                toret = null;
            } else {
                toret = tweetsCache.get(Long.parseLong(tweetId));
            }
        } else {
            // If not on cache
            if (tf == null) {
                tf = getTwitterFactory();
            }
            Twitter twitter = tf.getInstance();

            try {
                toret = twitter.showStatus(Long.parseLong(tweetId));
                this.add(toret.getCreatedAt(), toret.getId(), toret.getText(), toret.getLang(), false);
                saveTwitterCache();
            } catch (TwitterException te) {
                this.add(null, Long.parseLong(tweetId), te.getErrorMessage(), null, true);
                saveTwitterCache();
                logger.error("Tweet error / " + te.getErrorMessage() + " | Current tweet: " + tweetId);
                return null;
            }
        }
        return toret;
    }

    /**
     * Save data to a file
     *
     * @param filename File name where the data is saved
     */
    public void writeToDisk(String filename) {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(filename)) {
            Type statusType = new TypeToken<HashMap<Long, TweetStatus>>() {
            }.getType();
            gson.toJson(tweetsCache, statusType, writer);
        } catch (IOException e) {
            logger.error("[WRITE TO DISK] " + e.getMessage());
        }
    }

    /**
     * Retrieve data from file
     *
     * @param filename File name to retrieve data
     */
    public void readFromDisk(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            tweetsCache = (HashMap<Long, TweetStatus>) input.readObject();
        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
    }

    /**
     * Retrieve data from cache
     */
    public void retrieveTweetCache() {
        BufferedReader bufferedReader = null;
        Gson gson = new Gson();

        try {
            File tweetCacheFile = new File(outputFile);
            if (!tweetCacheFile.exists()) {
                tweetCacheFile.createNewFile();
            }
            bufferedReader = new BufferedReader(new FileReader(tweetCacheFile));
        } catch (IOException ex) {
            logger.error("[RETRIEVE TWEET CACHE] " + ex.getMessage());
        }

        Type type = new TypeToken<HashMap<Long, TweetStatus>>() {
        }.getType();
        HashMap<Long, TweetStatus> tweet = gson.fromJson(bufferedReader, type);

        if (tweet != null) {
            tweet.forEach((k, v) -> tweetsCache.put(k, v));
        }
    }

    /**
     * Save data to cache
     */
    public void saveTwitterCache() {
        try {
            if (this.size() > 0) {
                writeToDisk(outputFile);
            }
        } catch (Exception ex) {
            logger.warn("[SAVE TWITTER CACHE] " + ex.getMessage());
        }
    }

    /**
     * Get the size of the map that contains ths information storage for Twitter
     * cache
     *
     * @return The size of the map that contains ths information storage for
     * Twitter cache
     */
    public int size() {
        return tweetsCache.size();
    }
}
