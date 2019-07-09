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

package org.nlpa.pipe.impl;

import org.nlpa.util.dateextractor.WARCDateExtractor;
import org.nlpa.util.dateextractor.NullDateExtractor;
import org.nlpa.util.dateextractor.YTBIDDateExtractor;
import org.nlpa.util.dateextractor.EMLDateExtractor;
import org.nlpa.util.dateextractor.DateExtractor;
import org.nlpa.util.dateextractor.TWTIDDateExtractor;
import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe finds the content Date from different formats
 *
 * @author José Ramón Méndez Reboredo
 */
@AutoService(Pipe.class)
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
        super(new Class<?>[0], new Class<?>[0]);

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
                logger.error("[GUESS DATE FROM FILE PIPE ] " + ex.getMessage());
             }
        }

        return carrier;
    }
}
