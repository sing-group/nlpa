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
package org.nlpa.types;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A dictionary of Strings
 *
 * @author María Novo
 * @author José Ramón Méndez Reboredo
 */
public class Dictionary implements Iterable<String> {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Dictionary.class);

    /**
     * The information storage for the dictionary. Only a Hashset of strings is
     * required
     */
    private Set<String> textHashSet;

    /**
     * Indicates if the text saved is encoded 
     */
    private boolean encode;
    /**
     * A instance of the Dictionary to implement a singleton pattern
     */
    private static Dictionary dictionary = null;

    /**
     * The default constructor
     */
    private Dictionary() {
        textHashSet = new LinkedHashSet<>();
        this.encode = false;
    }

    /**
     * Retrieve the System Dictionary
     *
     * @return The default dictionary for the system
     */
    public static Dictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new Dictionary();
        }
        return dictionary;
    }

    /**
     * Set the value of encode property
     * @param encode True if the text is encoded, false otherwise.
     */
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    /**
     * Get the value of encode property
     * @return The value of encode property
     */
    public boolean getEncode() {
        return this.encode;
    }

    /**
     * Add a string to dictionary
     *
     * @param text The new text to add to the dictionary
     */
    public void add(String text) {
        if (this.encode) {
            textHashSet.add(encodeBase64(text));
        } else {
            textHashSet.add(text);
        }
    }

    /**
     * Determines if a text is included in the dictionary
     *
     * @param text the text to check
     * @return a boolean indicating whether the text is included in the
     * dictionary or not
     */
    public boolean isIncluded(String text) {
        text = (this.encode) ? decodeBase64(text) : text;
        return textHashSet.contains(text);
    }

    /**
     * Achieves an iterator to iterate through the stored text
     *
     * @return an iterator
     */
    @Override
    public Iterator<String> iterator() {
        return this.textHashSet.iterator();
    }

    /**
     * Returns the size of the dictionary
     *
     * @return the size of the dictionary
     */
    public int size() {
        return this.textHashSet.size();
    }

    /**
     * Removes all the elements from the dictionary
     */
    public void clear() {
        this.textHashSet.clear();
    }

    /**
     * Save data to a file
     *
     * @param filename File name where the data is saved
     */
    public void writeToDisk(String filename) {
        try (FileOutputStream outputFile = new FileOutputStream(filename);
                BufferedOutputStream buffer = new BufferedOutputStream(outputFile);
                ObjectOutputStream output = new ObjectOutputStream(buffer);) {

            output.writeObject(this.textHashSet);
            output.flush();
            output.close();
        } catch (Exception ex) {
            logger.error("[WRITE TO DISK] " + ex.getMessage());
        }
    }

    /**
     * Retrieve data from file
     *
     * @param filename File name to retrieve data
     */
    public void readFromDisk(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            this.textHashSet = (LinkedHashSet<String>) input.readObject();
        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
    }

    /**
     * Encode a text to BASE 64
     *
     * @param feat Text to encode
     * @return The text encoded
     */
    private String encodeBase64(String feat) {
        try {
            return Base64.getEncoder().encodeToString(feat.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            logger.warn("[ENCODE BASE 64]: " + ex.getMessage());
            return "";
        }
    }

    /**
     * Decode text from BASE 64
     *
     * @param feat Texto do decode
     * @return The text decoded
     */
    public String decodeBase64(String feat) {
        byte[] decodedBytes = Base64.getDecoder().decode(feat);
        try {
            return new String(decodedBytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.warn("[DECODE BASE 64]: " + ex.getMessage());
            return "";
        }
    }
}
