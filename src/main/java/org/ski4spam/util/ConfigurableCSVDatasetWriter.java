/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

import java.io.File;
import java.util.regex.Pattern;
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
     * Disable the access to the default constructor (with no parameters
     */
    private ConfigurableCSVDatasetWriter() {
        this(DEFAULT_CSV_FILE);
    }
   
    /**
     * Construct a ConfigurableCSVDatasetWriter specifiying the file
     * @param csvDataset The file (java.io.File) where the dataset will be stored
     */
    public ConfigurableCSVDatasetWriter(File csvDataset) {
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
    private Pattern quoteRequiredPattern = Pattern.compile("[" + getCSVSep() + getCharsToScape() + "\\n\\r\u0085'\u2028\u2029]");

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
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
    public String getCharsToScape() {
        if (charsToScape == null) {
            charsToScape = Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeChars");
            logger.info("CSV chars that should be scapped: \"" + charsToScape.toString() + "\"");
        }
        if (charsToScape == null) {
            charsToScape = "\"";
        }
        return charsToScape;
    }

}
