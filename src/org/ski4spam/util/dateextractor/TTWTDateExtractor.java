package org.ski4spam.util.dateextractor;

import java.io.File;

import java.util.Date;

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

    public Date extractDate(File f) {
        return null;
    }
}