package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;
import org.ski4spam.util.TwitterConfigurator;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.ski4spam.pipe.TransformationPipe;

/**
 * This pipe implements language guessing by using Twitter API
 *
 * @author Yeray Lage Freitas
 */
//@PropertyComputingPipe(inputType = "File")
@TransformationPipe(inputType = "File", outputType = "File")
public class StoreTweetLangPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(StoreTweetLangPipe.class);

    private TwitterFactory tf = TwitterConfigurator.getTwitterFactory();

    
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            if (carrier.getProperty("extension") == "twtid") { // For using this just for tweets
                String tweetId;
                File file = (File) carrier.getData();
                //Achieving the tweet id from the given file.
                try {
                    FileReader f = new FileReader(file);
                    BufferedReader b = new BufferedReader(f);
                    tweetId = b.readLine();
                    b.close();
                } catch (IOException e) {
                    logger.error("IO Exception caught / " + e.getMessage() + "Current tweet: " + file.getAbsolutePath());
                    return carrier;
                }

                //Extracting and returning the tweet status date or error if not available.
                try {
                    Twitter twitter = tf.getInstance();
                    Status status = twitter.showStatus(Long.parseLong(tweetId));
                    carrier.setProperty("language", status.getLang());
                    carrier.setProperty("language-reliability", 1.0);
                } catch (TwitterException te) {
                    logger.error("Tweet error at lang guess / " + te.getErrorMessage() + " | Current tweet: " + file.getAbsolutePath());
                    return carrier;
                }
            }
        }

        return carrier;
    }

}
	
		
