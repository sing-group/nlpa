package org.ski4spam.util.dateextractor;

import java.io.File;

import java.util.Date;

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

    public Date extractDate(File f) {
        return new Date();
    }
}