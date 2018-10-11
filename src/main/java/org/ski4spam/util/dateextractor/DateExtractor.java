package org.ski4spam.util.dateextractor;

import java.io.File;
import java.util.Date;

/**
  * Extracts the content date from a file (maybe parsing the file to find the date in headers, etc)
  * @author José Ramón Méndez
  */
public abstract class DateExtractor {

	/**
	  * Finds the content date from a file
	  * @param f The file to use to retrieve the content date
	  * @return the date of the content
	  */
    public abstract Date extractDate(File f);

}