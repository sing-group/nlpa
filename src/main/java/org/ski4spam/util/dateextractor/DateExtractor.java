package org.ski4spam.util.dateextractor;

import java.io.File;
import java.util.Date;

public abstract class DateExtractor {

    public abstract Date extractDate(File f);

}