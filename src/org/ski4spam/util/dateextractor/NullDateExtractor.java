package org.ski4spam.util.dateextractor; 
 
import java.io.File; 
 
import java.util.Date; 
 
public class NullDateExtractor extends DateExtractor { 
    static DateExtractor instance = null; 
 
    private NullDateExtractor() { 
 
    } 
 
    public static String[] getExtensions() { 
        return new String[] {"ttwt","sms","tsms","tytb"}; 
    } 
 
    public static DateExtractor getInstance() { 
        if (instance == null) { 
            instance = new NullDateExtractor(); 
        } 
        return instance; 
    } 
 
    public Date extractDate(File f) { 
        return null; 
    } 
}