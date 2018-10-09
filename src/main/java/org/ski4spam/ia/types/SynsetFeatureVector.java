/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.ia.types;

import java.io.Serializable;
import java.util.Map;

/**
 * A vector of synset-based features 
 * @author Maria Novo
 */
public class SynsetFeatureVector implements Serializable {
	 /**
		* A map of synsets together with its values
		*/
    private Map<String, Double> synsetFeature;
    
	 /**
		* Builds a SynsetFeatureVector from a Map (String,Double) where the 
		* string represents the synsetID and the double contains the value
		* for such feature
		* @param synsetFeature The synsetFeature Map used to build the SynsetFeatureVector
		*/
    public SynsetFeatureVector(Map<String, Double> synsetFeature) {
        this.synsetFeature = synsetFeature;
    }

    /**
		* Retrieves the map that connects each synset found in the text with the 
		* feature value
		* @return a Map that connects each feature (synsetId) with the value for it
		*/
    public Map<String, Double> getSynsetsFeature() {
        return synsetFeature;
    }
    
	 /**
		* Gets the size (number of properties) of the current SynsetFeatureVector
		* @return the size of the current SynsetFeatureVector
		*/
    public int getSize(){
        return synsetFeature.size();
    }
	 
    /**
     * Checks for the value stored for the synset synsetId
     * @param synsetId The target synset 
     * @return The value asociated to synsetId, which represents the frequency of appearance of the synsetId
     */
    public double getFrequencyValue(String synsetId){
        if (synsetFeature.containsKey(synsetId)){
            for (Map.Entry<String, Double> entry : synsetFeature.entrySet()) {
               if (entry.getKey() == synsetId){
                return entry.getValue();
               }
            }
        }
        return -1;
    }

}
