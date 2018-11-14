package org.ski4spam.types;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import org.bdp4j.util.Pair;

/**
 * A class to represent a vector of synsets and the asofiated information
 *
 * @author Iñaki Velez
 * @author Enaitz Ezpeleta
 * @author Maria Novo
 * @author Jose Ramon Mendez
 */
public class SynsetVector implements Serializable {

    /**
     * The original text
     */
    String originalText = null;

    /**
     * The vector of unmatched texts that are represented as Pairs where: + The
     * first element of the pair is the original unmatched text + The second
     * elemento of the pair is the results of parsing the text
     */
    List<Pair<String, String>> unmatchedTexts = new ArrayList<Pair<String, String>>();

    /**
     * The text after fixing unmatched text sections
     */
    String fixedText = null;

    /**
     * The vector of detected synsets represented as Pairs where: + The first
     * element of the pair is the synsetId identified by babelfy + The second
     * element of the pair is the portion of the fixedText that matches the
     * synsetId
     */
    List<Pair<String, String>> synsets = new ArrayList<Pair<String, String>>();

    /**
     * Default constructor. Please note that it was avoided by declaring it
     * private.
     */
    private SynsetVector() {
    }

    /**
     * Constructs a SynsetVector from the original text
     *
     * @param originalText This is the original text parameter
     */
    public SynsetVector(String originalText) {
        this.originalText = originalText;
    }

    /**
     * Constructs a SynsetVector from the original text given in a StringBuffer
     *
     * @param originalText StringBuffer that is an object
     */
    public SynsetVector(StringBuffer originalText) {
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
     * the text that matched the synsets
     *
     * @return a list of synsets in the form (S, T) where S is the synset and T
     * the text that matched the synsets
     */
    public List<Pair<String, String>> getSynsets() {
        return synsets;
    }

    /**
     * Changes the list of synsets for the current text
     *
     * @param synsets a list of synsets in the form (S, T) where S is the synset
     * and T the text that matched the synsets
     */
    public void setSynsets(List<Pair<String, String>> synsets) {
        this.synsets = synsets;
    }

}