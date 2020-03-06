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

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

import org.bdp4j.util.Pair;

/**
 * A class to represent a sequence of synsets and the associated information
 *
 * @author Iñaki Velez
 * @author Enaitz Ezpeleta
 * @author Maria Novo
 * @author Jose Ramon Mendez
 */
public class SynsetSequence implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The original text
     */
    String originalText = null;

    /**
     * The list of unmatched texts that are represented as Pairs where: + The
     * first element of the pair is the original unmatched text + The second
     * elemento of the pair is the results of parsing the text
     */
    List<Pair<String, String>> unmatchedTexts = new ArrayList<Pair<String, String>>();

    /**
     * The text after fixing unmatched text sections
     */
    String fixedText = null;

    /**
     * The list of detected synsets represented as Pairs where: + The first
     * element of the pair is the synsetId identified by babelfy + The second
     * element of the pair is the portion of the fixedText that matches the
     * synsetId
     */
    List<Pair<String, String>> synsets = new ArrayList<>();

    /**
     * Constructs a SynsetSequence from the original text
     *
     * @param originalText This is the original text parameter
     */
    public SynsetSequence(String originalText) {
        this.originalText = originalText;
    }

    /**
     * Constructs a SynsetSequence from the original text given in a StringBuffer
     *
     * @param originalText StringBuffer that is an object
     */
    public SynsetSequence(StringBuffer originalText) {
        this.originalText = originalText.toString();
    }

    /**
     * Returns the original text
     *
     * @return the original text
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Sets the original text from which the synsets were computed
     *
     * @param originalText the original text
     */
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    /**
     * Gets the list of incorrect fragments of text
     *
     * @return the list of incorrect fragments of text in the form of pairs
     * (T,R) where T is the original fragment and R the replacement suggestion
     * for it (null before computing it)
     */
    public List<Pair<String, String>> getUnmatchedTexts() {
        return unmatchedTexts;
    }

    /**
     * Changes the list of incorrect fragments of the text
     *
     * @param unmatchedTexts The list of incorrect fragments of text in the form
     * of pairs (T,R) where T is the original fragment and R the replacement
     * suggestion for it (null before computing it)
     */
    public void setUnmatchedTexts(List<Pair<String, String>> unmatchedTexts) {
        this.unmatchedTexts = unmatchedTexts;
    }

    /**
     * Returns the fixed text achieved after executing the corrections
     *
     * @return the fixed text
     */
    public String getFixedText() {
        return fixedText;
    }

    /**
     * Changes the fixed text
     *
     * @param fixedText the fixed text
     */
    public void setFixedText(String fixedText) {
        this.fixedText = fixedText;
    }

    /**
     * Achieves a list of synsets in the form (S, T) where S is the synset and T
     * the text that matched the synset
     *
     * @return a list of synsets in the form (S, T) where S is the synset and T
     * the text that matched the synset
     */
    public List<Pair<String, String>> getSynsets() {
        return synsets;
    }

    /**
     * Changes the list of synsets for the current text
     *
     * @param synsets a list of synsets in the form (S, T) where S is the synset
     * and T the text that matched the synset
     */
    public void setSynsets(List<Pair<String, String>> synsets) {
        this.synsets = synsets;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.originalText);
        hash = 17 * hash + Objects.hashCode(this.unmatchedTexts);
        hash = 17 * hash + Objects.hashCode(this.fixedText);
        hash = 17 * hash + Objects.hashCode(this.synsets);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SynsetSequence other = (SynsetSequence) obj;
        if (!Objects.equals(this.originalText, other.originalText)) {
            return false;
        }
        if (!Objects.equals(this.fixedText, other.fixedText)) {
            return false;
        }
//        if (!Objects.equals(this.unmatchedTexts, other.unmatchedTexts)) {
//            System.out.println("unmatchedTexts: " + this.unmatchedTexts + " - " + other.unmatchedTexts);
//            return false;
//        }
        if (!Objects.equals(this.synsets, other.synsets)) {
            return false;
        }
        return true;
    }
}
