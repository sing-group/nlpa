/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.types;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A dictionary of Synsets
 *
 * @author María Novo
 * @author José Ramón Méndez Reboredo
 */
public class SynsetDictionary implements Iterable<String> {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SynsetDictionary.class);

    /**
     * The information storage for the dictionary. Only a Hashset of synsetsId
     * is required
     */
    private Set<String> synsetIdsHashSet;

    /**
     * A instance of the Dictionary to implement a singleton pattern
     */
    private static SynsetDictionary dictionary = null;

    /**
     * The default constructor
     */
    private SynsetDictionary() {
        synsetIdsHashSet = new LinkedHashSet<>();
    }

    /**
     * Retrieve the System Dictionary
     *
     * @return The default dictionary for the system
     */
    public static SynsetDictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new SynsetDictionary();
        }
        return dictionary;
    }

    /**
     * Add a synset to dictionarly
     *
     * @param synsetId the new id of synset to add to the dictionary
     */
    public void add(String synsetId) {
        synsetIdsHashSet.add(synsetId);
    }

    /**
     * Determines if a synsetId is included in the dictionary
     *
     * @param synsetId the identifier of the synset to check
     * @return a boolean indicating whether the synsetId is included in the
     * dictionary or not
     */
    public boolean isIncluded(String synsetId) {
        return synsetIdsHashSet.contains(synsetId);

    }

    /**
     * Achieves an iterator to iterate through the stored synsetsIDs
     *
     * @return an iterator
     */
    @Override
    public Iterator<String> iterator() {
        return this.synsetIdsHashSet.iterator();
    }

     /**
     * Returns the size of the dictionary
     * @return the size of the dictionary
     */
    public int size(){
        return this.synsetIdsHashSet.size();
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

            output.writeObject(this.synsetIdsHashSet);
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

            this.synsetIdsHashSet = (LinkedHashSet<String>) input.readObject();
        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
    }
}
