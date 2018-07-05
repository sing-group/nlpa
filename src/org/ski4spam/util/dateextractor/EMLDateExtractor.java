package org.ski4spam.util.dateextractor;

import java.io.File;


public class EMLDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private EMLDateExtractor() {

    }

    public static String getExtension() {
        return "eml";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new EMLDateExtractor();
        }
        return instance;
    }

    public StringBuffer extractDate(File f) {
        return new StringBuffer("This is an example of EML date");
    }
}
