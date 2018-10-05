package org.ski4spam.pipe.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.ski4spam.util.dateextractor.DateExtractor;
import org.ski4spam.util.dateextractor.EMLDateExtractor;
import org.ski4spam.util.dateextractor.NullDateExtractor;
import org.ski4spam.util.dateextractor.TWTIDDateExtractor;
import org.ski4spam.util.dateextractor.WARCDateExtractor;
import org.ski4spam.util.dateextractor.YTBIDDateExtractor;

import org.bdp4j.pipe.PipeParameter;

/**
 * This pipe finds the content Date from different formats
 * @author José Ramón Méndez Reboredo
 */
@PropertyComputingPipe()
public class GuessDateFromFile extends Pipe {
	/**
	  * The default name for the date property
	  */
	 public static final String DEFAULT_DATE_PROPERTY="date";
	
    private static final Logger logger = LogManager.getLogger(GuessDateFromFile.class);

    @Override
    public Class getInputType() {
        return File.class;
    }

    @Override
    public Class getOutputType() {
        return File.class;
    }

    /**
		* A collection of DateExtractors
		*/
    HashMap<String, DateExtractor> htExtractors;

    /**
		* The property where the date is being stored
		*/
    String datePropertyStr = DEFAULT_DATE_PROPERTY;
    
	 /**
		* Sthe the property where the date will be stored
		* @param datePropertyStr the name of the property for the date
		*/
	 @PipeParameter(name = "datepropname", description = "Indicates the property name to store the date", defaultValue=DEFAULT_DATE_PROPERTY)
    public void setDatePropertyStr(String datePropertyStr){
        this.datePropertyStr = datePropertyStr;
    }
	 
    public String getDatePropertyStr(){
        return this.datePropertyStr;
    }
    
    public GuessDateFromFile() {
        init();
    }

    public GuessDateFromFile(String datePropertyStr) {
        this.datePropertyStr = datePropertyStr;
        init();
    }

    private void init() {
        htExtractors = new HashMap<>();

        //Add the extractors
        for (String ext : EMLDateExtractor.getExtensions()) {
            htExtractors.put(ext, EMLDateExtractor.getInstance());
        }
        for (String ext : WARCDateExtractor.getExtensions()) {
            htExtractors.put(ext, WARCDateExtractor.getInstance());
        }
        for (String ext : TWTIDDateExtractor.getExtensions()) {
            htExtractors.put(ext, TWTIDDateExtractor.getInstance());
        }
        for (String ext : YTBIDDateExtractor.getExtensions()) {
            htExtractors.put(ext, YTBIDDateExtractor.getInstance());
        }
        for (String ext : NullDateExtractor.getExtensions()) {
            htExtractors.put(ext, NullDateExtractor.getInstance());
        }
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            String[] extensions = {"eml", "tsms", "sms", "warc", "ytbid", "tytb", "twtid", "ttwt"};
            String extension = "";
            String name = (((File) carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while (i < extensions.length && !name.endsWith(extensions[i])) {
                i++;
            }

            if (i < extensions.length) {
                extension = extensions[i];
            }

            DateExtractor de = htExtractors.get(extension);

            if (de != null) {
                Date d = de.extractDate((File) (carrier.getData()));
                if (d == null) {
                    logger.warn("Invalid date " + carrier.toString() + " due to a fault in parsing.");
                    carrier.setProperty(datePropertyStr, "null");
                } else {
                    carrier.setProperty(datePropertyStr, d);
                }
            } else {
                logger.warn("No parser available for instance " + carrier.toString() + ". Invalidating instance.");
                carrier.setProperty(datePropertyStr, "null");
            }
        }

        return carrier;
    }
}
