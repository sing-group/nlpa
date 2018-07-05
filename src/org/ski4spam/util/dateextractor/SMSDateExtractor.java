package org.ski4spam.util.dateextractor;

import java.io.File;

public class SMSDateExtractor extends DateExtractor {
    static DateExtractor instance = null;

    private SMSDateExtractor() {

    }

    public static String getExtension() {
        return "sms";
    }

    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new SMSDateExtractor();
        }
        return instance;
    }

    public StringBuffer extractDate(File f) {
        return new StringBuffer("This is an example of SMS date");
    }
}