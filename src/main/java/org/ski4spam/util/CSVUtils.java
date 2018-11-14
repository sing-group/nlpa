/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

//import org.apache.commons.text.StringEscapeUtils;

import org.ski4spam.util.Configuration;
import java.util.regex.Pattern;

/**
 * Several utilities to create and manage CSV files
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
public class CSVUtils {

    /**
     * The configured CSV Separator
     */
    private static String CSVSep = null;

    /**
     * The Str Sepatator
     */
    private static String strQuote = null;


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

		  
        if (str.length() == 0) {
            str_scape.append(" ");
        } else {
            //str_scape.append((hasCSVSep && !hasStrQuote ? "\"" : "") + StringEscapeUtils.escapeCsv(str.replaceAll("[\\p{Cntrl}]", "")) + (hasCSVSep && !hasStrQuote ? "\"" : ""));
				if (/*hasCSVSep || hasStrQuote || hasLineBreak*/ quoteRequired){
					str_scape.append("\"");
					str_scape.append(
					   str.replaceAll("[\\p{Cntrl}]", "").replaceAll("["+getStrQuote()+"]",getStrQuote()+getStrQuote())
					);
					str_scape.append("\"");
				}else{
					str_scape.append(str.replaceAll("[\\p{Cntrl}]", ""));
				}
        }
        return str_scape.toString();
    }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured separator for CSV files
     */
    public static String getCSVSep() {
        if (CSVSep == null) {
            CSVSep = Configuration.getSystemConfig().getConfigOption("csv", "CSVSep");
        }
        return CSVSep;
    }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured separator for CSV files
     */
    public static String getStrQuote() {
       if (strQuote == null) {
           strQuote = Configuration.getSystemConfig().getConfigOption("csv", "CSVStrQuote");
       }
       return strQuote;
    }
}
