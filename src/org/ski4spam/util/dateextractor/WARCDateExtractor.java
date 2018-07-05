package org.ski4spam.util.dateextractor;

import java.io.File;

public class WARCDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private WARCDateExtractor() {

    }

    public static String getExtension() {
        return "warc";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new WARCDateExtractor();
        }
        return instance;
    }

    public StringBuffer extractDate(File f) {
        return new StringBuffer("This is an example of WARC date");
    }
}