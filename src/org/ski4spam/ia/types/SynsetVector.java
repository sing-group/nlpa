package org.ski4spam.ia.types;

import java.util.Vector;
import org.ski4spam.util.Pair;

public class SynsetVector {
	/**
	  * The original text
	  */
    String originalText=null;
	
	/**
	  * The vector of unmatched texts that are represented as Pairs where:
	  * + The first element of the pair is the original unmatched text
	  * + The second elemento of the pair is the results of parsing the text
	  */	
	Vector<Pair<String,String>> unmatchedTexts=new Vector<Pair<String,String>>();
	

	/**
	  * The text after fixing unmatched text sections
      */
    String fixedText=null;


    /**
	  * The vector of detected synsets represented as Pairs where:
	  * + The first element of the pair is the synsetId identified by babelfy
      * + The second element of the pair is the porttion of the fixedText that matches the synsetId
	  */
	Vector<Pair<String,String>> synsets=new Vector<Pair<String,String>>();
	
	/**
	  * Default constructor. Please note that it was avoided by declaring it private.
	  */
	private SynsetVector(){
    }
	
	/**
	  * Constructs a SynsetVector from the original text
	  * @param originalText This is the original text parameter
	  */
	public SynsetVector(String originalText) {
		this.originalText=originalText;
    }
	
	/**
	  * Constructs a SynsetVector from the original text given in a StringBuffer
	  * @param originalText StringBuffer that is an object
	  */
	public SynsetVector(StringBuffer originalText){
		this.originalText=originalText.toString();
    }

	//TODO Implement getters and setters.
	
}