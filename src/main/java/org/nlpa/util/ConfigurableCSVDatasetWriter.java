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

import java.io.File;
//import java.util.regex.Pattern;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bdp4j.util.CSVDatasetWriter;
import org.bdp4j.util.EBoolean;

/**
 * Configurable CSV Dataset.
 * The CSV parameters are configured through system configuration 
 * (config/configurations.ini by default)
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
public class ConfigurableCSVDatasetWriter extends CSVDatasetWriter {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(ConfigurableCSVDatasetWriter.class);

    /**
     * Default constructor. Disable the access to the default constructor (with no parameters
     */
    private ConfigurableCSVDatasetWriter() {
        //this(DEFAULT_CSV_FILE);
    }
   
    /**
     * Construct a ConfigurableCSVDatasetWriter specifiying the file
     * @param csvDataset The file (java.io.File) where the dataset will be stored
     */
    public ConfigurableCSVDatasetWriter(File csvDataset) {
        this();
        CSVSep = Configuration.getSystemConfig().getConfigOption("csv", "CSVSep");
        strQuote = Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuote");
        strQuoteEscapeChar =  Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuoteEscapeChar");
        csvVoidField = Configuration.getSystemConfig().getConfigOption("csv", "CSVVoidField");
        escapeCR = EBoolean.parseBoolean(Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeCRChars"));
        charsToScape = Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeChars");
        this.csvDataset=csvDataset;
    }
    
    /**
     * Construct a ConfigurableCSVDatasetWriter specifiying the path to CSV file to be created
     * @param csvDatasetPath The path to the CSV file that is being created
     */
    public ConfigurableCSVDatasetWriter(String csvDatasetPath) {
        this(new File(csvDatasetPath));
    }
    
    /**
     * Pattern to determine if quotation is required
     */    
    //private Pattern quoteRequiredPattern = Pattern.compile("[" + getCSVSep() + getCharsToScape() + "\\n\\r\u0085'\u2028\u2029]");

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
    @Override
    public String getCSVSep() {
        if (CSVSep == null) {
            CSVSep = Configuration.getSystemConfig().getConfigOption("csv", "CSVSep");
            logger.info("CSV field separator is \"" + CSVSep + "\"");
        }
        if (CSVSep == null) {
            CSVSep = ",";
        }
        return CSVSep;
    }
    
    /**
     * Returns the CSV String Quote Character configured
     *
     * @return the configured String Quote Character for CSV files
     */
    @Override
    public  String getStrQuote() {
        if (strQuote == null) {
            strQuote = Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuote");
            logger.info("CSV String Quote Character is \"" + strQuote + "\"");
        }
        if (strQuote == null) {
            strQuote = "\"";
        }
        return strQuote;
    }

    /**
     * Returns the CSV Escape Character for Quotes configured
     *
     * @return the CSV Escape Character for Quotes configured
     */
    @Override
    public String getStrQuoteEscapeChar() {
        if (strQuoteEscapeChar == null) {
            strQuoteEscapeChar = Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuoteEscapeChar");
            logger.info("CSV Escape Character for Quotes is \"" + strQuoteEscapeChar + "\"");
        }
        if (strQuoteEscapeChar == null) {
            strQuoteEscapeChar = "\"";
        }
        return strQuoteEscapeChar;
    }

    /**
     * Returns the representation for a CSV void field
     *
     * @return the representation for a CSV void field
     */
    @Override
    public  String getStrVoidField() {
        if (csvVoidField == null) {
            csvVoidField = Configuration.getSystemConfig().getConfigOption("csv", "CSVVoidField");
            logger.info("CSV Void field is represented as \"" + csvVoidField + "\"");
        }
        if (csvVoidField == null) {
            csvVoidField = " ";
        }
        return csvVoidField;
    }

    /**
     * Returns if we should escape carriage returns
     *
     * @return if we should escape carriage returns
     */
    @Override
    public boolean shouldEscapeCRChars() {
        if (escapeCR == null) {
            String propVal = Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeCRChars");
            if (propVal != null) {
                escapeCR = EBoolean.getBoolean(propVal);
            }
            logger.info("CSV carriage returns should be escaped: \"" + escapeCR.toString() + "\"");
        }
        if (escapeCR == null) {
            escapeCR = false;
        }
        return escapeCR;
    }

    /**
     * Returns if we should escape carriage returns
     *
     * @return if we should escape carriage returns
     */
    @Override
    public String getCharsToScape() {
        if (charsToScape == null) {
            charsToScape = Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeChars");
            logger.info("CSV chars that should be scapped: \"" + charsToScape + "\"");
        }
        if (charsToScape == null) {
            charsToScape = "\"";
        }
        return charsToScape;
    }

}
