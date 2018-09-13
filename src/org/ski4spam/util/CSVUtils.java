/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author María Novo
 */
public class CSVUtils {
    
    public static final String CSV_SEP = ";";
    
    public static String escapeCsv(String str){
        String str_scape = new String();
        str_scape = StringEscapeUtils.escapeCsv(str.replaceAll(";", "\\;")) + CSV_SEP;
        return str_scape;
    }
}
