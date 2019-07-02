package org.nlpa.util;

import org.bdp4j.util.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.jlt.util.Language;

import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetQuery;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.BabelSynsetRelation;
import it.uniroma1.lcl.babelnet.data.BabelPointer;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class encapsulates all required information to support Babelfy and
 * Babelnet queryies
 *
 * @author Iñaki Velez de Mendizabal
 */
public class BabelUtils {

    /**
     * For logging purpopses
     */
    private static final Logger logger = LogManager.getLogger(BabelUtils.class);

    /**
     * A instance of this class to implement a singleton pattern
     */
    private static BabelUtils bu = null;

    /**
     * An instance of Babelfy object required to query Babelfy
     */
    private Babelfy bfy;

    /**
     * An instance of BabelNet object required to query BabelNet
     */
    private BabelNet bn;

    /**
     * A stop synset to navigate in Babelnet hierarchy. The synset means entity
     */
    private static String stopSynset = "bn:00031027n";

    /**
     * String limit for babelfy queries
     */
    public static final int MAX_BABELFY_QUERY = 3000;

    /**
     * The private default constructor for this class
     */
    private BabelUtils() {
        bfy = new Babelfy();
        bn = BabelNet.getInstance();
    }

    /**
     * Achieves the default instance of BabelUtils
     *
     * @return a instance of this class
     */
    public static BabelUtils getDefault() {
        if (bu == null) {
            bu = new BabelUtils();
        }
        return bu;
    }

    /**
     * Determines whether a term is included in Babelnet or not
     *
     * @param term The term to check
     * @param lang The language in which the term is written
     * @return true if the term is included in Babelnet ontological dictionary
     */
    public boolean isTermInBabelNet(String term, String lang) {
        if (lang.trim().equalsIgnoreCase("UND")) {
            logger.error("Unable to query Babelnet because language is not found.");
            return false;
        }
        int resultNo = 0;
        try {
            BabelNetQuery query = new BabelNetQuery.Builder(term).from(Language.valueOf(lang)).build();
            List<BabelSynset> byl = bn.getSynsets(query);
            resultNo = byl.size();
        } catch (Exception e) {
            logger.error("Unable to query Babelnet: " + e.getMessage());
        }
        return (resultNo > 0);
    }

    /**
     * Determines whether a synset is included in Babelnet or not
     *
     * @param synsetToCheck The Synset to check
     * @param textToLink The word which corresponds with the Synset in Babelfy.
     * This word is provided only to create a log report.
     * @return true if is possible to obtain information about synset in
     * Babelnet, so the synset is included in Babelnet ontological dictionary.
     */
    public boolean checkSynsetInBabelnet(String synsetToCheck, String textToLink) {
        try {
            //Tray to obtain some information about the synset. If is not possible it generates a exception
            bn.getSynset(new BabelSynsetID(synsetToCheck)).toString();
            return true;

        } catch (Exception e) {
            logger.error("The text [" + textToLink + "] obtained in Babelfy as [" + synsetToCheck + "] does not exists in Babelnet. " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets an hypernym of a synset that is n levels above
     *
     * @param synsetToScale The Synset to search its hypernym.
     * @param levels The number of levels to be scaled.
     * @return a string with the hypernym synset ID
     */
    public String getSynsetHypernymFromLevel(String synsetToScale, int levels) {
        String tmpHypernym = synsetToScale;
        try {
            List<BabelSynsetRelation> elementsInAnyHypernymPointer, elementsInHypernymPointer;
            BabelSynset by;

            for (int l = 0; l < levels; l++) {
                by = bn.getSynset(new BabelSynsetID(tmpHypernym));
                elementsInAnyHypernymPointer = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM);
                elementsInHypernymPointer = by.getOutgoingEdges(BabelPointer.HYPERNYM);
                // If HYPERNYM returns values, it takes first synset and add to tmpHypernym
                if (elementsInHypernymPointer.size() >= 1) {
                    tmpHypernym = elementsInHypernymPointer.get(0).getBabelSynsetIDTarget().toString();
                } // else if ANY_HYPERNYM returns values, it takes first synset and add to tmpHypernym
                else if (elementsInAnyHypernymPointer.size() >= 1) {
                    tmpHypernym = elementsInAnyHypernymPointer.get(0).getBabelSynsetIDTarget().toString();
                }
            }
        } catch (Exception e) {
            logger.error("Hypernym search problem. The synset " + synsetToScale + " does not exists in Babelnet." + e.getMessage());
        }
        return tmpHypernym;
    }

    /**
     * Returns a list with all the hypernyms of a Synset until "entity" element.
     * If synset does not hypernyms, the list only returns the original synset.
     *
     * @param synsetToScale The Synset to search its hypernyms.
     * @return the list with the synset and all of hypernyms
     */
    public List<String> getAllHypernyms(String synsetToScale) {
        List<String> allHypernymsList = new ArrayList<String>();
        try {
            List<BabelSynsetRelation> elementsInAnyHypernymPointer, elementsInHypernymPointer;
            BabelSynset by;
            do {
                //Meto o no meto su hiperonimo si solo tiene uno?
                allHypernymsList.add(synsetToScale);
                by = bn.getSynset(new BabelSynsetID(synsetToScale));
                elementsInAnyHypernymPointer = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM);
                elementsInHypernymPointer = by.getOutgoingEdges(BabelPointer.HYPERNYM);
                // If HYPERNYM returns values, it takes first synset and add to tmpHypernym
                if (elementsInHypernymPointer.size() >= 1) {
                    synsetToScale = elementsInHypernymPointer.get(0).getBabelSynsetIDTarget().toString();
                } // else if ANY_HYPERNYM returns values, it takes first synset and add to tmpHypernym
                else if (elementsInAnyHypernymPointer.size() >= 1) {
                    synsetToScale = elementsInAnyHypernymPointer.get(0).getBabelSynsetIDTarget().toString();
                }

            } while (!synsetToScale.equals(stopSynset) && !allHypernymsList.contains(synsetToScale));
        } catch (Exception e) {
            logger.error("Hypernym search problem. The synset " + synsetToScale + " does not exists in Babelnet." + e.getMessage());
        }
        return allHypernymsList;
    }

    /**
     * Returns a Map with Synsets and their first hypernym from BabelNet. Only
     * builds a pair if synset hypernym exists.
     *
     * @param originalSynsetList The synsets list to obtain hypernyms
     * @return A Map with pairs of synset as key and hypernym as value
     */
    public Map<String, String> getHypernymsFromBabelnet(List<String> originalSynsetList) {
        List<BabelSynsetRelation> elementsInAnyHypernymPointer, elementsInHypernymPointer;
        String hypernym;
        Map<String, String> synsetHypernymMap = new HashMap<>();

        for (String synsetListElement : originalSynsetList) {
            try {
                BabelSynset by = bn.getSynset(new BabelSynsetID(synsetListElement));
                elementsInAnyHypernymPointer = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM);
                elementsInHypernymPointer = by.getOutgoingEdges(BabelPointer.HYPERNYM);
                // If HYPERNYM returns values, it takes first and add to synsetHypernymMap
                if (elementsInHypernymPointer.size() >= 1) {
                    hypernym = elementsInHypernymPointer.get(0).getBabelSynsetIDTarget().toString();
                    synsetHypernymMap.put(synsetListElement, hypernym);
                } // else if ANY_HYPERNYM returns values, it takes first and add to synsetHypernymMap
                else if (elementsInAnyHypernymPointer.size() >= 1) {
                    hypernym = elementsInAnyHypernymPointer.get(0).getBabelSynsetIDTarget().toString();
                    synsetHypernymMap.put(synsetListElement, hypernym);
                }
            } catch (Exception e) {
                logger.error("Hypernym search problem. The synset " + synsetListElement + " does not exists in Babelnet." + e.getMessage());
            }

        }

        return synsetHypernymMap;

    }

    /**
     * Returns true if synsetOnTop if hypernym of synsetToCheck.
     *
     * @param synsetToCheck The Synset to be scaled to try to reach the
     * hypernym.
     * @param synsetOnTop The hypernym
     * @return True if synsetOnTop if hypernym of synsetToCheck.
     */
    public boolean isSynsetHypernymOf(String synsetToCheck, String synsetOnTop) {
        String scaledSynsetToCheck;

        if (synsetToCheck.equals(synsetOnTop)) {
            return false;
        } else {
            boolean isHypernym = false;
            do {
                scaledSynsetToCheck = bu.getSynsetHypernymFromLevel(synsetToCheck, 1);
                if (scaledSynsetToCheck.equals(synsetToCheck)) {
                    return false;
                } else {
                    if (scaledSynsetToCheck.equals(synsetOnTop)) {
                        return true;
                    } else {
                        synsetToCheck = scaledSynsetToCheck;
                    }
                }
            } while (!isHypernym && !synsetToCheck.equals(stopSynset));

            return isHypernym;

        }
    }

    /**
     * Build a list of sysntets from a text
     *
     * @param fixedText The text to be transformed into synsets
     * @param lang The language to identify the synsets
     * @return A vector of synsets. Each synset is represented in a pair (S,T)
     * where S stands for the synset ID and T for the text that matches this
     * synset ID
     */
    public ArrayList<Pair<String, String>> buildSynsetSequence(String fixedText, String lang) {
        // This is an arraylist of entries to check for duplicate results and nGrams
        ArrayList<BabelfyEntry> nGrams = new ArrayList<>();
        List<SemanticAnnotation> bfyAnnotations = new ArrayList<>();
        // boolean solved = false;
        String subtexts[] = new String[0];

        // Split text in 3500 (MAX_BABELFY_QUERY) characters string for querying
        String remain = new String(fixedText);
        List<String> parts = new ArrayList<>();
        while (remain.length() > MAX_BABELFY_QUERY) {
            int splitPos = remain.lastIndexOf('.', MAX_BABELFY_QUERY - 1); // Try to keep phrases in the same part
            if (splitPos == -1) {
                splitPos = remain.lastIndexOf(' ', MAX_BABELFY_QUERY - 1); // but at least try to keep words
            }
            if (splitPos == -1) {
                splitPos = MAX_BABELFY_QUERY - 1; // if this is imposible lets with the max length
            }
            parts.add(remain.substring(0, splitPos + 1));
            remain = remain.substring(splitPos + 1);
        }
        parts.add(remain);
        subtexts = parts.toArray(subtexts);
        // if (subtexts.length>1) {System.out.print("Instance text slitted: original
        // size: "+fixedText.length()+" new zizes "); for (String
        // i:subtexts){System.out.print(i.length()+", ");}; System.out.println();}

        // Text is not splitted
        int currentSubtext = 0;
        while (currentSubtext < subtexts.length) {
            try {
                bfyAnnotations.addAll(bfy.babelfy(subtexts[currentSubtext], Language.valueOf(lang)));
                currentSubtext++;
            } catch (RuntimeException e) {
                if (e.getMessage().equals(
                        "Your key is not valid or the daily requests limit has been reached. Please visit http://babelfy.org.")) {
                    // Wait until 01:01:01 of the next day (just after midnigth)
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.HOUR_OF_DAY, 1); // Wait for an hour and a minute for the actualization of babelcoins
                    c.set(Calendar.MINUTE, 1);
                    c.set(Calendar.SECOND, 1);
                    long midnight = c.getTimeInMillis();

                    long now = System.currentTimeMillis();

                    long millis = midnight - now;
                    long hours = millis / (1000 * 60 * 60);
                    long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
                    // System.out.println("--------------------------------------------------------------------------------------------------------------------------");
                    // System.out.println("INFO: Your key is not valid or the daily requests limit
                    // has been reached. The application will pause for " + hours+"h
                    // "+minutes+"m.");
                    // System.out.println("--------------------------------------------------------------------------------------------------------------------------");
                    logger.info(
                            "Your key is not valid or the daily requests limit has been reached. The application will pause for "
                            + hours + "h " + minutes + "m.");
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException ie) {
                        logger.error("Unable to sleep " + millis + ". " + ie.getMessage());
                    }
                }
            }
        }

        for (SemanticAnnotation annotation : bfyAnnotations) {
            int start = annotation.getCharOffsetFragment().getStart();
            int end = annotation.getCharOffsetFragment().getEnd();
            double score = annotation.getGlobalScore();
            String synsetId = annotation.getBabelSynsetID();
            String text = fixedText.substring(start, end + 1);

            if (nGrams.size() == 0) { // If this anotation is the first i have ever received
                nGrams.add(new BabelfyEntry(start, end, score, synsetId, text));
                continue;
            }

            // This is a sequential search to find previous synsets that are connected with
            // the current one
            int pos = 0;
            BabelfyEntry prevAnot = nGrams.get(pos);
            for (; !(start >= prevAnot.getStartIdx() && end <= prevAnot.getEndIdx())
                    && // The current anotation is
                    // included in other previous
                    // one
                    !(prevAnot.getStartIdx() >= start && prevAnot.getEndIdx() <= end)
                    && // A previous anotation is
                    // included in the current
                    // one
                    pos < nGrams.size() - 1; prevAnot = nGrams.get(pos++))
				;

            if (start >= prevAnot.getStartIdx() && end <= prevAnot.getEndIdx()) { // The current anotation is included
                // in other previous one
                if (start == prevAnot.getStartIdx() && end == prevAnot.getEndIdx() && score > prevAnot.getScore()) {
                    nGrams.set(pos, new BabelfyEntry(start, end, score, synsetId, text));
                }
            } else if (prevAnot.getStartIdx() >= start && prevAnot.getEndIdx() <= end) { // A previous anotation is
                // included in the current
                // one
                nGrams.set(pos, new BabelfyEntry(start, end, score, synsetId, text));
            } else {
                nGrams.add(new BabelfyEntry(start, end, score, synsetId, text)); // it it not related to nothing
                // previous
            }
        }

        // The value that will be returned
        ArrayList<Pair<String, String>> returnValue = new ArrayList<Pair<String, String>>();
        for (BabelfyEntry entry : nGrams) {
            if (checkSynsetInBabelnet(entry.getSynsetId(), entry.getText())) {
                returnValue.add(new Pair<String, String>(entry.getSynsetId(), entry.getText()));
            }

        }
        return returnValue;
    }
}

/**
 * This class is to represent a babelfy Semantic annotation with all relevant
 * attributes to made intensive searches and discard the irrelevant information
 * achieved by Babelfy
 */
class BabelfyEntry {

    private int startIdx;
    private int endIdx;
    private double score;
    private String synsetId;
    private String text;

    /**
     * No args constructor
     */
    public BabelfyEntry() {
    }

    /**
     * Constructor that stablish all attributes of a BabelfyEntry
     *
     * @param endIdx The last index of the entry
     * @param synsetId The synset ID
     * @param score The score
     * @param startIdx The start index of an entry
     * @param text The text of an entry
     */
    public BabelfyEntry(int startIdx, int endIdx, Double score, String synsetId, String text) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.score = score;
        this.synsetId = synsetId;
        this.text = text;
    }

    /**
     * Returns the start index of an entry
     *
     * @return The start index of an entry
     */
    public int getStartIdx() {
        return startIdx;
    }

    /**
     * Stablish the start index of an entry
     *
     * @param startIdx The start index of an entry
     */
    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    /**
     * Return the end index for an entry
     *
     * @return he last index of the entry
     */
    public int getEndIdx() {
        return endIdx;
    }

    /**
     * Stablish the end index for an entry
     *
     * @param endIdx The last index of the entry
     */
    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }

    /**
     * Returns the score of an entry
     *
     * @return the score of an entry
     */
    public double getScore() {
        return score;
    }

    /**
     * Change the score of an entry
     *
     * @param score the score of the entry
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Returns the synsetId for the entry
     *
     * @return the synsetId for the entry
     */
    public String getSynsetId() {
        return synsetId;
    }

    /**
     * Stablish the synsetId for the entry
     *
     * @param synsetID the synsetId for the entry
     */
    public void setSynsetId(String synsetId) {
        this.synsetId = synsetId;
    }

    /**
     * Returns the text for an entrty
     *
     * @return the text for the entry
     */
    public String getText() {
        return this.text;
    }

    /**
     * Stablish the text for an entry
     *
     * @param text The text for the entry
     */
    public void setText(String text) {
        this.text = text;
    }

}
