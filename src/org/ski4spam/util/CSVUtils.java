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
        StringBuilder str_scape = new StringBuilder();
        str_scape.append(StringEscapeUtils.escapeCsv(str.replaceAll(";", "\\;"))).append(CSV_SEP);
        return str_scape.toString();
    }
}
