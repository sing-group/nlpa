package org.ski4spam.pipe.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.ski4spam.util.TwitterConfigurator;

import twitter4j.Status;
import twitter4j.TwitterFactory;

/**
 * This pipe implements language guessing by using Twitter API
 *
 * @author Yeray Lage Freitas
 */
@PropertyComputingPipe()
public class StoreTweetLangPipe extends Pipe {
	/**
	  * For loging purposes
	  */
    private static final Logger logger = LogManager.getLogger(StoreTweetLangPipe.class);
	
   /**
    * Return the input type included the data attribute of a Instance
    * @return the input type for the data attribute of the Instances processed
    */
    @Override
    public Class getInputType() {
        return File.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after processing
     * @return the datatype expected in the data attribute of a Instance after processing
     */
    @Override
    public Class getOutputType() {
        return File.class;
    }

    private TwitterFactory tf = TwitterConfigurator.getTwitterFactory();

    /**
    * Process an Instance.  This method takes an input Instance,
    * destructively modifies it in some way, and returns it.
    * This is the method by which all pipes are eventually run.
    *
    * @param carrier Instance to be processed.
    * @return Instancia procesada
    */
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
                Status status = TwitterConfigurator.getStatus(tweetId);
                if (status != null) {
                    carrier.setProperty("language", status.getLang());
                    carrier.setProperty("language-reliability", 1.0);
                }
            }
        }

        return carrier;
    }

}
	
		
