/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

import org.apache.commons.text.StringEscapeUtils;

import org.ski4spam.util.Configuration;

/**
 * Several utilities to create and manage CSV files
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
	 private static String strSep ="\"";
    
	 /**
		* Escape a CSV String to allow including texts into cells
		* @param str The string to scape
		* @return the scaped string
		*/
    public static String escapeCSV(String str){
        StringBuilder str_scape = new StringBuilder();
		  boolean hasCSVSep=(str.indexOf(getCSVSep())!=-1);
		  boolean hasStrSep=(str.indexOf(getStrSep())!=-1);
		  if (str.length()==0) 
			  str_scape.append(" ").append( getCSVSep() );
        else str_scape.append((hasCSVSep&&!hasStrSep?"\"":"")+StringEscapeUtils.escapeCsv(str)+(hasCSVSep&&!hasStrSep?"\"":"")).append(getCSVSep());
        return str_scape.toString();
    }
	 
	 /**
		* Returns the CSV separator configured
		* @return the configured separator for CSV files
		*/
	 public static String getCSVSep(){
		 if (CSVSep==null){
			 CSVSep=Configuration.getSystemConfig().getConfigOption("csv", "CSVSep");
		 }
		 return CSVSep;
	 }
	 
	 /**
		* Returns the CSV separator configured
		* @return the configured separator for CSV files
		*/
	 public static String getStrSep(){
		 return strSep;
	 }	 
}
