/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

//import org.apache.commons.text.StringEscapeUtils;

import org.ski4spam.util.Configuration;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Several utilities to create and manage CSV files
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
public class CSVUtils {
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CSVUtils.class);
	
    /**
     * The configured CSV Separator
     */
    private static String CSVSep = null;

    /**
     * The String Quote delimiter
     */
    private static String strQuote = null;

    /**
     * The Char to Escape String Quote delimiters
     */
    private static String strQuoteEscapeChar = null;
	 
	 /**
		* The representation of a CSV VOID FIELD
		*/
	 private static String csvVoidField = null;

    /**
		* The pattern to require quotes
		*/
    private static final Pattern quoteRequiredPattern=Pattern.compile("["+getCSVSep()+getStrQuote()+"\\n\\r\u0085'\u2028\u2029]");
	 
    /**
     * Escape a CSV String to allow including texts into cells
     *
     * @param str The string to scape
     * @return the scaped string
     */
    public static String escapeCSV(String str) {
        StringBuilder str_scape = new StringBuilder();
        //boolean hasCSVSep = (str.indexOf(getCSVSep()) != -1);
        //boolean hasStrQuote = (str.indexOf(getStrQuote()) != -1);
		  //boolean hasLineBreak = (str.indexOf("\n") != -1)||(str.indexOf("\r") != -1);
		  boolean quoteRequired=quoteRequiredPattern.matcher(str).find();

		  
        if (str==null || str.length() == 0) {
            str_scape.append(getStrVoidField());
        } else {
            //str_scape.append((hasCSVSep && !hasStrQuote ? "\"" : "") + StringEscapeUtils.escapeCsv(str.replaceAll("[\\p{Cntrl}]", "")) + (hasCSVSep && !hasStrQuote ? "\"" : ""));
				if (/*hasCSVSep || hasStrQuote || hasLineBreak*/ quoteRequired){
					str_scape.append(getStrQuote());
					str_scape.append(
					   str.replaceAll("[\\p{Cntrl}]", "").
							 replaceAll("["+getStrQuote()+"]",getStrQuoteEscapeChar()+getStrQuote())
					);
					str_scape.append(getStrQuote());
				}else{
					str_scape.append(str.replaceAll("[\\p{Cntrl}]", ""));
				}
        }
        return str_scape.toString();
    }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
    public static String getCSVSep() {
        if (CSVSep == null) {
            CSVSep = Configuration.getSystemConfig().getConfigOption("csv", "CSVSep");
				logger.error("CSV field separator is \""+CSVSep+"\"");
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
    public static String getStrQuote() {
       if (strQuote == null) {
           strQuote = Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuote");
			  logger.error("CSV String Quote Character is \""+strQuote+"\"");
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
    public static String getStrQuoteEscapeChar() {
       if (strQuoteEscapeChar == null) {
           strQuoteEscapeChar = Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuoteEscapeChar");
			  logger.error("CSV Escape Character for Quotes is \""+strQuoteEscapeChar+"\"");
       }
		 if (strQuoteEscapeChar == null) {
			 strQuoteEscapeChar="\"";
		 }
       return strQuoteEscapeChar;
    }
	 
    /**
     * Returns the representation for a CSV void field
     *
     * @return the representation for a CSV void field
     */
    public static String getStrVoidField() {
       if (csvVoidField == null) {
           csvVoidField = Configuration.getSystemConfig().getConfigOption("csv", "CSVVoidField");
			  logger.error("CSV Void field is represented as \""+csvVoidField+"\"");
       }
		 if (csvVoidField == null) {
			 csvVoidField=" ";
		 }
       return csvVoidField;
    }	 
		 
}
