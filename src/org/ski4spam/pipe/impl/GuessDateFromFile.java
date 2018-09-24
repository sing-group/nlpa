package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;
import org.ski4spam.util.dateextractor.*;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;

/**
 * This pipe reads text and html contents from files
 *
 * @author José Ramón Méndez Reboredo
 */
@PropertyComputingPipe()
public class GuessDateFromFile extends Pipe {

    private static final Logger logger = LogManager.getLogger(GuessDateFromFile.class);

    @Override
    public Class getInputType() {
        return File.class;
    }

    @Override
    public Class getOutputType() {
        return File.class;
    }

    Hashtable<String, DateExtractor> htExtractors;

    String datePropertyStr = "date";
    
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
        htExtractors = new Hashtable<String, DateExtractor>();

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
