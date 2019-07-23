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
package org.nlpa.util.dateextractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nlpa.util.TwitterConfigurator;
import twitter4j.Status;

/**
 * This is a DateExtracfor for twtid files. These files should contain only a
 * tweet Id
 *
 * @author Yeray Lage
 */
public class TWTIDDateExtractor extends DateExtractor {

    /**
     * For loging purposes
     */
    private static final Logger logger = LogManager.getLogger(TWTIDDateExtractor.class);

    /**
     * An instance to implement a singleton pattern
     */
    private static DateExtractor instance = null;

    /**
     * The default constructor (converted to private to implement singleton)
     */
    private TWTIDDateExtractor() {

    }

    /**
     * Retrieve a list of file extensions that can be processed
     *
     * @return an array of file extensions that can be handled with this
     * DateExtractor
     */
    public static String[] getExtensions() {
        return new String[]{"twtid"};
    }

    /**
     * Retrieve an instance of the current DateExtractor
     *
     * @return an instance of the current DateExtractor
     */
    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new TWTIDDateExtractor();
        }
        return instance;
    }

    /**
     * Finds the content date from a file
     *
     * @param file The file to use to retrieve the content date
     * @return the date of the content
     */
    @Override
    public Date extractDate(File file) {
        String tweetId;

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
        try {
            //Extracting and returning the tweet status date or error if not available.
            Status status = TwitterConfigurator.getTwitterData().getStatus(tweetId);
            if (status != null) {
                return status.getCreatedAt();
            } else {
                return null;
            }

        } catch (Exception ex) {
            logger.error("Exception caught / " + ex.getMessage() + "Current tweet: " + file.getAbsolutePath());
            return null;
        }
    }
}
