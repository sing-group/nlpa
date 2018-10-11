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

/**
  * This is a DateExtracfor for twtid files.
  * These files should contain only a tweet Id
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
	  * A instance of TwitterFactory
	  */
    private TwitterFactory tf = TwitterConfigurator.getTwitterFactory();

	/**
	   * The default constructor (converted to private to implement singleton)
	   */
    private TWTIDDateExtractor() {

    }
	 
    /**
		* Retrieve a list of file extensions that can be processed
		* @return an array of file extensions that can be handled with this DateExtractor
		*/
    public static String[] getExtensions() {
        return new String[] {"twtid"};
    }
	 
    /**
		* Retrieve an instance of the current DateExtractor
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

        //Extracting and returning the tweet status date or error if not available.
        Status status = TwitterConfigurator.getStatus(tweetId);
        if (status != null) {
            return status.getCreatedAt();
        } else {
            return null;
        }
    }
}