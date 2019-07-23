/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.util.dateextractor;

import java.io.File;
import java.util.Date;

/**
 * This dataextractor returns null. This is useful to process files from which
 * the date is imposible to extract
 *
 * @author José Ramón Méndez
 */
public class NullDateExtractor extends DateExtractor {

    /**
     * An instance to implement a singleton pattern
     */
    static DateExtractor instance = null;

    /**
     * The default constructor (converted to private to implement singleton)
     */
    private NullDateExtractor() {

    }

    /**
     * Retrieve a list of file extensions that can be processed
     *
     * @return an array of file extensions that can be handled with this
     * DateExtractor
     */
    public static String[] getExtensions() {
        return new String[]{"ttwt", "sms", "tsms", "tytb"};
    }

    /**
     * Retrieve an instance of the current DateExtractor
     *
     * @return an instance of the current DateExtractor
     */
    public static DateExtractor getInstance() {
        if (instance == null) {
            instance = new NullDateExtractor();
        }
        return instance;
    }

    /**
     * Finds the content date from a file
     *
     * @param f The file to use to retrieve the content date
     * @return the date of the content
     */
    @Override
    public Date extractDate(File f) {
        return null;
    }
}
