package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.ski4spam.util.dateextractor.*;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

/**
 * This pipe finds the content Date from different formats
 *
 * @author José Ramón Méndez Reboredo
 */
@PropertyComputingPipe()
public class GuessDateFromFilePipe extends AbstractPipe {
    /**
     * The default name for the date property
     */
    public static final String DEFAULT_DATE_PROPERTY = "date";

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(GuessDateFromFilePipe.class);

    @Override
    public Class<?> getInputType() {
        return File.class;
    }

    @Override
    public Class<?> getOutputType() {
        return File.class;
    }

    /**
     * A collection of DateExtractors
     */
    private static HashMap<String, DateExtractor> htExtractors;

    static {
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

    /**
     * The property where the date is being stored
     */
    String dateProp = DEFAULT_DATE_PROPERTY;

    /**
     * Set the the property where the date will be stored
     *
     * @param dateProp the name of the property for the date
     */
    @PipeParameter(name = "datepropname", description = "Indicates the property name to store the date", defaultValue = DEFAULT_DATE_PROPERTY)
    public void setDateProp(String dateProp) {
        this.dateProp = dateProp;
    }

    /**
     * Retrieves the name of the property to store the Date
     *
     * @return the name of the property to store the Date
     */
    public String getDateProp() {
        return this.dateProp;
    }

    /**
     * Create a GuessDateFromFile which stores the date of the content in the
     * property "date"
     */
    public GuessDateFromFilePipe() {
        this(DEFAULT_DATE_PROPERTY);
    }

    /**
     * Create a GuessDateFromFile which stores the date of the content in the
     * property indicated by dateProp
     *
     * @param dateProp The name of the property to store the date
     */
    public GuessDateFromFilePipe(String dateProp) {
        super(new Class<?>[0],new Class<?>[0]);

        this.dateProp = dateProp;
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
            try {
                if (de != null) {
                    Date d = de.extractDate((File) (carrier.getData()));
                    if (d == null) {
                        logger.warn("Invalid date " + carrier.toString() + " due to a fault in parsing.");
                        carrier.setProperty(dateProp, "null");
                    } else {
                        carrier.setProperty(dateProp, d);
                    }
                } else {
                    logger.warn("No parser available for instance " + carrier.toString() + ". Invalidating instance.");
                    carrier.setProperty(dateProp, "null");
                }
            } catch (Exception ex) {
                System.out.println("date " + de + " -- " + carrier.getData());
                ex.printStackTrace();
            }
        }

        return carrier;
    }
}
