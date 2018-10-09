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

/** 
  * This class encapsulates all required information to support Babelfy and Babelnet queryies
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
			* The private default constructor for this class
			*/
       private BabelUtils(){
	 		bfy = new Babelfy();
	 		bn = BabelNet.getInstance();       	
       }
		
		 /**
			* Achieves the default instance of BabelUtils
			* @return a instance of this class 
			*/
	    public static BabelUtils getDefault(){
			 if (bu==null) bu=new BabelUtils();
			 return bu;
	    }
		 
		 /**
			* Determines whether a term is included in Babelnet or not
			* @param term The term to check
			* @param lang The language in which the term is written
			* @return true if the term is included in Babelnet ontological dictionary
			*/
		 public boolean isTermInBabelNet(String term, String lang){
			 if (lang.trim().equalsIgnoreCase("UND")) {
				 logger.error("Unable to query Babelnet because language is not found.");
				 return false;
			 }
			 int resultNo=0;
			 try{
				  BabelNetQuery query = new BabelNetQuery.Builder(term)
					 	.from(Language.valueOf(lang)) 
					 	.build();
				  List<BabelSynset> byl = bn.getSynsets(query);
				  resultNo=byl.size();
		    }catch(Exception e){
				 logger.error("Unable to query Babelnet: "+e.getMessage());
		    }
			 return (resultNo>0);
		 }
		 
		 /**
			* Build a list of sysntets from a text
			* @param fixedText The text to be transformed into synsets
			* @param lang The language to identify the synsets
			* @return A vector of synsets. Each synset is represented in a pair (S,T) where 
			          S stands for the synset ID and T for the text that matches this 
			          synset ID
			*/
		 public ArrayList<Pair<String,String>> buildSynsetVector(String fixedText, String lang){
 			//The value that will be returned
 			ArrayList<Pair<String,String>> returnValue=new ArrayList<Pair<String,String>>();

			 if (lang.trim().equalsIgnoreCase("UND")) {
				 logger.error("Unable to query Babelfy because language is not found.");
				 return returnValue;
			 }			 
				
			List<SemanticAnnotation> bfyAnnotations = bfy.babelfy(fixedText, Language.valueOf(lang));
				for (SemanticAnnotation annotation : bfyAnnotations) {
					/*If is necesary to split the input text the disambiguated word is frag
				    String frag = inputText.substring(annotation.getCharOffsetFragment().getStart(),
				        annotation.getCharOffsetFragment().getEnd() + 1); */
					returnValue.add(new Pair<String,String>(annotation.getBabelSynsetID(),
						fixedText.substring(annotation.getCharOffsetFragment().getStart(),
										        annotation.getCharOffsetFragment().getEnd() + 1)
						));
				}
			return returnValue;

				    
				}
			 

}
