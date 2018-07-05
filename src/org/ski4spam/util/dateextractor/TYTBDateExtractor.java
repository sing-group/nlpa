package org.ski4spam.util.dateextractor;

import java.io.File;

public class TYTBDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private TYTBDateExtractor() {

    }

    public static String getExtension() {
        return "tytb";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new TYTBDateExtractor();
        }
        return instance;
    }

    public StringBuffer extractDate(File f) {
        return new StringBuffer("This is an example of TYTB date");
    }
}