/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.ia.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.ski4spam.util.Pair;

/**
 *
 * @author María Novo
 */
public class SynsetFeatureVector {
    private List<Pair<String, Double>> synsetFeature;
    
    public SynsetFeatureVector(List<Pair<String, Double>> synsetFeature) {
        this.synsetFeature = synsetFeature;
    }

    public List<Pair<String, Double>> getSynsetsFeature() {
        return synsetFeature;
    }
    
    public int getSize(){
        return synsetFeature.size();
    }

}
