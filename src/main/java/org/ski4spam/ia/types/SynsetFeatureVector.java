/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.ia.types;

import java.io.Serializable;
import java.util.Map;


/**
 *
 * @author María Novo
 */
public class SynsetFeatureVector implements Serializable {
    private Map<String, Double> synsetFeature;
    
    public SynsetFeatureVector(Map<String, Double> synsetFeature) {
        this.synsetFeature = synsetFeature;
    }

    public Map<String, Double> getSynsetsFeature() {
        return synsetFeature;
    }
    
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
