/*
 * BDP4J-sample implements a list of BDP4J (https://github.com/sing-group/bdp4j) 
 * tasks (org.nlpa.pipe.Pipe). These tasks implement common text preprocessing 
 * stages and can be easilly combined to create a BDP4J pipeline for preprocessig 
 * a set of ham/spam SMS messages downloaded from http://www.esp.uem.es/jmgomez/smsspamcorpus/
 *
 * Copyright (C) 2018  Sing Group (University of Vigo)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.nlpa.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.bdp4j.pipe.SharedDataProducer;

/**
 * A tokenSequence implementation
 *
 * @author José Ramón Méndez Reboredo
 */
public class TokenSequence implements Serializable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Strings included in the TokenSequence
     */
    private List<String> tokens = new ArrayList<>();

    /**
     * The separators for tokenising
     */
    public static final String DEFAULT_SEPARATORS = " \t\r\n\f!\"#$%&'()*+,\\-./:;<=>?@[]^_`{|}~";

    /**
     * Default consturctor
     */
    public TokenSequence() {
    }

    /**
     * Constructor with a string that will be tokenized
     *
     * @param toTokenize The string to tokenize
     * @param separators the separators to be used
     */
    public TokenSequence(String toTokenize, String separators) {
        tokens = Collections.list(new StringTokenizer(toTokenize, separators)).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    /**
     * Add a term to the tokenSequence
     *
     * @param t the term (token) to add
     */
    public void add(String t) {
        tokens.add(t);
    }

    /**
     * Build a Feature Vector
     *
     * @return The feature Vector built
     */
    public FeatureVector buildFeatureVector() {
        HashMap<String, Double> retVal = new HashMap<>();

        for (String token : tokens) {
            // Add the token to dictionary
            Dictionary.getDictionary().setEncode(true);
            Dictionary.getDictionary().add(token);            

            // Add the feature to the returnValue
            Double val = retVal.get(token);
            retVal.put(token, (val != null) ? val + 1 : 1);
        }

        return new FeatureVector(retVal);
    }


    /**
     * Get the size of TokenSequence
     *
     * @return The size of TokenSequence
     */
    public int size() {
        return this.tokens.size();
    }

    /**
     * Get the token in the indicated position
     *
     * @param i Token position to get
     * @return The token at the indicated position
     */
    public String getToken(int i) {
        return tokens.get(i);
    }

    /**
     * Add an object to the TokenSequence
     *
     * @param o Object to add
     */
    public void add(Object o) {
        if (o instanceof String) {
            add((String) o);
        } else if (o instanceof TokenSequence) {
            add((TokenSequence) o);
        } else {
            add(o.toString());
        }
    }

    /**
     * Save data to a file
     *
     * @param dir Directory name where the data is saved
     */
    public void writeToDisk(String dir) {
        Dictionary.getDictionary().writeToDisk(dir + System.getProperty("file.separator") + "Dictionary.ser");
    }

}
