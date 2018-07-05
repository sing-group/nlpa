package org.ski4spam.util.dateextractor;

import java.io.File;

public class TTWTDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private TTWTDateExtractor() {

    }

    public static String getExtension() {
        return "ttwt";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new TTWTDateExtractor();
        }
        return instance;
    }

    public StringBuffer extractDate(File f) {
        return new StringBuffer("This is an example of ttwt date");
    }
}