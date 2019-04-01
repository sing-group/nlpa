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
import org.bdp4j.util.EBoolean;

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
		* Represents if \n \r and other non printable characters should be escaped
		*/
	 private static Boolean escapeCR=null;

	 /**
		* Chars that should be scapped
		*/
	 private static String charsToScape=null;
	 
    /**
		* The pattern to require quotes
		*/
    private static final Pattern quoteRequiredPattern=Pattern.compile("["+getCSVSep()+getCharsToScape()+"\\n\\r\u0085'\u2028\u2029]");
	 
    /**
     * Escape a CSV String to allow including texts into cells
     *
     * @param str The string to scape
     * @return the scaped string
     */
    public static String escapeCSV(String str) {
        StringBuilder str_scape = new StringBuilder();

        if (str==null || str.length() == 0) {
            str_scape.append(getStrVoidField());
        } else {
				if (quoteRequiredPattern.matcher(str).find()){ //If quote is required
					str_scape.append(getStrQuote());
					str_scape.append(
					   escapeAll(str.replaceAll("[\\p{Cntrl}]", ""))
					);
					str_scape.append(getStrQuote());
				}else{
					str_scape.append(str.replaceAll("[\\p{Cntrl}]", ""));
				}
        }
        return str_scape.toString();
    }
	 
	 /**
		* Escapes CR characters (if required) and quotes
		*/
	 private static String escapeAll(String in){
	 	 StringBuilder strb=new StringBuilder();
		 
		 for(int i=0;i<in.length();i++){
		 	 if (in.charAt(i)=='\n') strb.append(shouldEscapeCRChars()?"\\n":"\n"); 
			 else if (in.charAt(i)=='\r') strb.append(shouldEscapeCRChars()?"\\r":"\r");
			 else if (in.charAt(i)=='\u0085') strb.append(shouldEscapeCRChars()?"\\u0085":"\u0085");
			 else if (in.charAt(i)=='\u2028') strb.append(shouldEscapeCRChars()?"\\u2028":"\u2028");
			 else if (in.charAt(i)=='\u2029') strb.append(shouldEscapeCRChars()?"\\u2029":"\u2029");
			 else if (getCharsToScape().indexOf(in.charAt(i))!=-1 || in.charAt(i)==getStrQuote().charAt(0)) strb.append(getStrQuoteEscapeChar()+in.charAt(i)); 
			 else strb.append(in.charAt(i));
			 //System.out.println("Char: "+in.charAt(i)+"("+getCharsToScape() +": "+ getCharsToScape().indexOf(in.charAt(i)) + ") - "+strb.toString());
		 }
		 //System.exit(0);
		 return strb.toString();
	 }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
    public static String getCSVSep() {
        if (CSVSep == null) {
            CSVSep = Configuration.getSystemConfig().getConfigOption("csv", "CSVSep");
				logger.info("CSV field separator is \""+CSVSep+"\"");
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
			  logger.info("CSV String Quote Character is \""+strQuote+"\"");
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
			  logger.info("CSV Escape Character for Quotes is \""+strQuoteEscapeChar+"\"");
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
			  logger.info("CSV Void field is represented as \""+csvVoidField+"\"");
       }
		 if (csvVoidField == null) {
			 csvVoidField=" ";
		 }
       return csvVoidField;
    }	 
	 
    /**
     * Returns if we should escape carriage returns
     *
     * @return if we should escape carriage returns
     */
    public static boolean shouldEscapeCRChars() {
       if (escapeCR == null) {
           String propVal = Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeCRChars");
			  if (propVal!=null) escapeCR=EBoolean.getBoolean(propVal);
			  logger.info("CSV carriage returns should be escaped: \""+escapeCR.toString()+"\"");
       }
		 if (escapeCR == null) {
			 escapeCR=false;
		 }
       return escapeCR;
    }
	 
    /**
     * Returns if we should escape carriage returns
     *
     * @return if we should escape carriage returns
     */
    public static String getCharsToScape() {
       if (charsToScape == null) {
           charsToScape = Configuration.getSystemConfig().getConfigOption("csv", "CSVEscapeChars");
			  logger.info("CSV chars that should be scapped: \""+charsToScape.toString()+"\"");
       }
		 if (charsToScape == null) {
			 charsToScape="\"";
		 }
       return charsToScape;
    }
		 
}
