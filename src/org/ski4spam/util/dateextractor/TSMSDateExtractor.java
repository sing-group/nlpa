package org.ski4spam.util.dateextractor;

import java.io.File;

public class TSMSDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private TSMSDateExtractor() {

    }

    public static String getExtension() {
        return "ttsms";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new TSMSDateExtractor();
        }
        return instance;
    }

    public StringBuffer extractDate(File f) {
        return new StringBuffer("This is an example of TSMS date");
    }
}