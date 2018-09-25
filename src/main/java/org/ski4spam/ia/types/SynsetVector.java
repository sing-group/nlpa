package org.ski4spam.ia.types;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import org.ski4spam.util.Pair;

/**
 * A class to represent a vector of synsets and the asofiated information
 *
 * @author Iñaki Velez
 * @author Enaitz Ezpeleta
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
     * element of the pair is the porttion of the fixedText that matches the
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

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public List<Pair<String, String>> getUnmatchedTexts() {
        return unmatchedTexts;
    }

    public void setUnmatchedTexts(List<Pair<String, String>> unmatchedTexts) {
        this.unmatchedTexts = unmatchedTexts;
    }

    public String getFixedText() {
        return fixedText;
    }

    public void setFixedText(String fixedText) {
        this.fixedText = fixedText;
    }

    public List<Pair<String, String>> getSynsets() {
        return synsets;
    }

    public void setSynsets(List<Pair<String, String>> synsets) {
        this.synsets = synsets;
    }

}
