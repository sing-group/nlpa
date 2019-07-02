/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.util.Pair;

/**
 *
 * @author María Novo
 */
public class YouTubeConfigurator {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(YouTubeConfigurator.class);

    /**
     * The default file name where youttube cache will be saved
     */
    public static final String DEFAULT_OUTPUT_FILE = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "youtubecache.txt";

    /**
     * The file name where youttube cache will be saved
     */
    private static final String outputFile = DEFAULT_OUTPUT_FILE;

    /**
     * The information storage for the youtube cache. Only a youtube id is
     * required
     */
    private Map<String, Pair<Date, String>> youTubeDataInstances;

    /**
     * A instance of youtube configurator to implement a singleton pattern
     */
    private static YouTubeConfigurator youTubeData = null;

    /**
     * The default constructor
     */
    private YouTubeConfigurator() {
        youTubeDataInstances = new LinkedHashMap<>();
         
    }

    /**
     * Retrieve the youtube cache
     *
     * @return The default cache for youtube
     */
    public static YouTubeConfigurator getYouTubeData() {
        if (youTubeData == null) {
            youTubeData = new YouTubeConfigurator();
        }
        return youTubeData;
    }

    /**
     * Add a text to youtube cache
     *
     * @param youTubeId the id to add to youtube cache
     * @param youTubeText text to add to youtube cache
     * @param youTubeDate date to add to youtube cache
     *
     */
    public void add(String youTubeId, String youTubeText, Date youTubeDate) {
        Pair<Date, String> youTubeDataPair = youTubeDataInstances.get(youTubeId);
        if (youTubeDataPair == null) {
            Pair<Date, String> pair = new Pair<>(youTubeDate, youTubeText);
            youTubeDataInstances.put(youTubeId, pair);
        }
    }

    /**
     * Determines if a youTube id is included in the cache and has an associated
     * text
     *
     * @param youTubeId the identifier of the youtube entry to check
     * @return a boolean indicating whether the text is included in the youtube
     * cache or not
     */
    public boolean isIncluded(String youTubeId) {
        return youTubeDataInstances.containsKey(youTubeId);
    }

    /**
     * Determines if a youTube id is included in the cache and has an associated
     * text
     *
     * @param youTubeId the identifier of the youtube entry to check
     * @return a boolean indicating whether the text is included in the youtube
     * cache or not
     */
    public String isIncludedText(String youTubeId) {
        if (youTubeDataInstances.containsKey(youTubeId)) {
            return youTubeDataInstances.get(youTubeId).getObj2();
        }
        return null;
    }

    /**
     * Determines if a youTube id is included in the cache and has an associated
     * date
     *
     * @param youTubeId the identifier of the youtube entry to check
     * @return a boolean indicating whether the date is included in the youtube
     * cache or not
     */
    public Date isIncludedDate(String youTubeId) {
        if (youTubeDataInstances.containsKey(youTubeId)) {
            return youTubeDataInstances.get(youTubeId).getObj1();
        }
        return null;
    }

    /**
     * Save data to a file
     *
     * @param filename File name where the data is saved
     */
    public void writeToDisk(String filename) {
        try (FileOutputStream outputFile = new FileOutputStream(filename);
                BufferedOutputStream buffer = new BufferedOutputStream(outputFile);
                ObjectOutputStream output = new ObjectOutputStream(buffer);) {

            output.writeObject(this.youTubeDataInstances);
            output.flush();
            output.close();
        } catch (Exception ex) {
            logger.error("[WRITE TO DISK] " + ex.getMessage());
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

            this.youTubeDataInstances = (Map<String, Pair<Date, String>>) input.readObject();
        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
    }

    /**
     * Retrieve data from cache
     */
    public void retrieveYoutubeCache() {
        try {
            if (this.size() == 0) {
                File youtubeCacheFile = new File(outputFile);
                if (youtubeCacheFile.exists()) {
                    this.readFromDisk(outputFile);
                }
            }
        } catch (Exception ex) {
            logger.warn("[RETRIEVE YOUTUBE CACHE ] " + ex.getMessage());
        }
    }

    /**
     * Save data to cache
     */
    public void saveYoutubeCache() {
        try {
            if (this.size() > 0) {
                writeToDisk(outputFile);
            }
        } catch (Exception ex) {
            logger.warn("[SAVE YOUTUBE CACHE ] " + ex.getMessage());
        }
    }

    public int size() {
        return this.youTubeDataInstances.size();
    }
}
