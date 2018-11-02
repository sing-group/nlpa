package org.ski4spam.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.jlt.util.Language;

import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class encapsulates all required information to support Babelfy and
 * Babelnet queryies
 *
 * @author Iñaki Velez
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
		* String limit for babelfy queries
		*/
	 public static final int MAX_BABELFY_QUERY=3000;

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
            BabelNetQuery query = new BabelNetQuery.Builder(term)
                    .from(Language.valueOf(lang))
                    .build();
            List<BabelSynset> byl = bn.getSynsets(query);
            resultNo = byl.size();
        } catch (Exception e) {
            logger.error("Unable to query Babelnet: " + e.getMessage());
        }
        return (resultNo > 0);
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
    public ArrayList<Pair<String, String>> buildSynsetVector(String fixedText, String lang) {
        //This is an arraylist of entries to check for duplicate results and nGrams
        ArrayList<BabelfyEntry> nGrams = new ArrayList<>();
        List<SemanticAnnotation> bfyAnnotations = new ArrayList<>();
        boolean solved = false;
		  String subtexts[]=new String[0];
		  
		  //Split text in 3500 (MAX_BABELFY_QUERY) characters string for querying
		  String remain=new String(fixedText);
		  List<String> parts=new ArrayList<>();
		  while(remain.length()>MAX_BABELFY_QUERY){
				  int splitPos=fixedText.lastIndexOf('.',MAX_BABELFY_QUERY); //Try to keep phrases in the same part
				  if (splitPos==-1) fixedText.lastIndexOf(' ',MAX_BABELFY_QUERY); //but at least try to keep words
				  if (splitPos==-1) splitPos=MAX_BABELFY_QUERY-1;	//if this is imposible lets with the max length
				  			  
				  parts.add(remain.substring(0,splitPos+1));
				  remain=remain.substring(splitPos+1);
		  }
		  parts.add(remain);
		  subtexts=parts.toArray(subtexts);
		  if (subtexts.length>1) System.out.print("Instance text slitted: original size: "+fixedText.length()+" new zizes "); for (String i:subtexts){System.out.print(i.length()+", ");}; System.out.println();
		  
		  //Text is not splitted
		  
		  int currentSubtext=0;
        while (currentSubtext<subtexts.length) {
            try {
                bfyAnnotations.addAll(bfy.babelfy(subtexts[currentSubtext], Language.valueOf(lang)));
					 currentSubtext++;
            } catch (RuntimeException e) {
                if (e.getMessage().equals("Your key is not valid or the daily requests limit has been reached. Please visit http://babelfy.org.")) {
                    //Wait until 01:01:01 of the next day (just after midnigth)
						  Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.HOUR_OF_DAY, 1); //Wait for an hour and a minute for the actualization of babelcoins
                    c.set(Calendar.MINUTE, 1);
                    c.set(Calendar.SECOND, 1);
                    long midnight = c.getTimeInMillis();
						  
                    long now = System.currentTimeMillis();
						  
                    long millis = midnight - now;
                    long hours = millis / (1000 * 60 * 60);
                    long minutes = (millis % (1000 * 60 * 60))/ (1000 * 60);
                    //System.out.println("--------------------------------------------------------------------------------------------------------------------------");
                    //System.out.println("INFO: Your key is not valid or the daily requests limit has been reached. The application will pause for " + hours+"h "+minutes+"m.");
                    //System.out.println("--------------------------------------------------------------------------------------------------------------------------");
                    logger.info("Your key is not valid or the daily requests limit has been reached. The application will pause for " + hours+"h "+minutes+"m.");
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException ie) {
                        logger.error("Unable to sleep "+millis+". " + ie.getMessage());
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

            if (nGrams.size() == 0) { //If this anotation is the first i have ever received
                nGrams.add(new BabelfyEntry(start, end, score, synsetId, text));
                continue;
            }

            //This is a sequential search to find previous synsets that are connected with the current one
            int pos = 0;
            BabelfyEntry prevAnot = nGrams.get(pos);
            for (; !(start >= prevAnot.getStartIdx() && end <= prevAnot.getEndIdx())
                    && //The current anotation is included in other previous one
                    !(prevAnot.getStartIdx() >= start && prevAnot.getEndIdx() <= end)
                    && //A previous anotation is included in the current one
                    pos < nGrams.size() - 1; prevAnot = nGrams.get(pos++));

            if (start >= prevAnot.getStartIdx() && end <= prevAnot.getEndIdx()) { //The current anotation is included in other previous one
                if (start == prevAnot.getStartIdx() && end == prevAnot.getEndIdx() && score > prevAnot.getScore()) {
                    nGrams.set(pos, new BabelfyEntry(start, end, score, synsetId, text));
                }
            } else if (prevAnot.getStartIdx() >= start && prevAnot.getEndIdx() <= end) { //A previous anotation is included in the current one
                nGrams.set(pos, new BabelfyEntry(start, end, score, synsetId, text));
            } else {
                nGrams.add(new BabelfyEntry(start, end, score, synsetId, text)); //it it not related to nothing previous
            }
        }

        //The value that will be returned
        ArrayList<Pair<String, String>> returnValue = new ArrayList<Pair<String, String>>();
        for (BabelfyEntry entry : nGrams) {
            returnValue.add(new Pair<String, String>(entry.getSynsetId(), entry.getText()));
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
