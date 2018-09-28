/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.ParameterPipe;
import org.bdp4j.pipe.Pipe;
import org.ski4spam.ia.types.SynsetFeatureVector;
import org.ski4spam.ia.types.SynsetVector;
import org.ski4spam.util.Pair;

/**
 *
 * @author María Novo
 */
public class SynsetVector2SynsetFeatureVector extends Pipe {

    private Map<String, Double> synsetFeatureVector;
    private SynsetVector synsetVector;
    private int countSynsets = 0;
    /**
     * Indicates the group strategy to create the synsetFeatureVector
     */
    private SynsetVectorGroupingStrategy groupStrategy
            = SynsetVectorGroupingStrategy.FREQUENCY;

    @Override
    public Class getInputType() {
        return SynsetVector.class;
    }

    @Override
    public Class getOutputType() {
        return SynsetFeatureVector.class;
    }

    @ParameterPipe(name = "groupStrategy", description = "Indicates the group strategy to create the synsetFeatureVector")
    public void setGroupStrategy(SynsetVectorGroupingStrategy groupStrategy) {
        this.groupStrategy = groupStrategy;
    }

    public SynsetVectorGroupingStrategy getGroupStrategy() {
        return this.groupStrategy;
    }

    public SynsetVector2SynsetFeatureVector() {
    }

    public SynsetVector2SynsetFeatureVector(SynsetVectorGroupingStrategy groupStrategy) {
        this.groupStrategy = groupStrategy;
    }

    /**
     * Converts a synsetVector in a synsetFeatureVector, with the synsetId and the number of times that a synsetId appears in synsetVector
     * @param synsetVector
     * @return A synsetFeatureVector with the synsetId and the number of times that a synsetId appears in synsetVector
     */
    private SynsetFeatureVector countMatches(SynsetVector synsetVector) {
        Map<String, Double> synsetFeatureVector = new HashMap<>();

        try {
            for (Pair<String, String> pairSV : synsetVector.getSynsets()) {
                String synsetId = pairSV.getObj1();
                
                if (synsetFeatureVector.get(synsetId) == null) {
                    synsetFeatureVector.put(pairSV.getObj1(), 1d);
                } else {
                    Double appearanceNumber = synsetFeatureVector.get(synsetId);
                    synsetFeatureVector.put(synsetId, appearanceNumber + 1d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        countSynsets = synsetFeatureVector.size();
        return new SynsetFeatureVector(synsetFeatureVector);
    }

    @Override
    public Instance pipe(Instance carrier) {

        List<Pair<String, Double>> sfv = new ArrayList<>();

        /**
         * *****************************************
         */
        /*create a example of synsetvector*/
        SynsetVector svTest = new SynsetVector("el perro ladra");
        List<Pair<String, String>> synsetsTest = new ArrayList<Pair<String, String>>();
        Pair p1 = new Pair("bn:21565421", "el");
        Pair p2 = new Pair("bn:54554548", "perro");
        Pair p3 = new Pair("bn:78248598", "ladra");
        Pair p4 = new Pair("bn:21565421", "la");
        synsetsTest.add(p1);
        synsetsTest.add(p4);
        synsetsTest.add(p2);
        synsetsTest.add(p3);
        svTest.setSynsets(synsetsTest);
        /**
         * *****************************************
         */

        try {
//          SynsetVector synsetVector = (SynsetVector)carrier.getData();

            SynsetVector synsetVector = (SynsetVector) svTest;
            switch (groupStrategy) {
                case COUNT:
                    /* Generate a synsetFeatureVector with synsetId and synsetId appearance number in synsetVector*/
                    countMatches(synsetVector);
                    break;
                case BOOLEAN:
                    /* Generate a synsetFeatureVector with synsetId and 0/1 if this synsetId is or not in synsetVector*/
                        synsetFeatureVector = new HashMap<>();
                        for (Pair<String, String> pairSV : synsetVector.getSynsets()) {
                            String synsetId = pairSV.getObj1();
                            if (synsetFeatureVector.get(synsetId) == null) {
                                synsetFeatureVector.put(pairSV.getObj1(), 1d);
                            }
                        };

                    break;
                case FREQUENCY:
                    /* Generate a synsetFeatureVector with synsetId and synsetId appearance frequency in synsetVector*/
                    SynsetFeatureVector synsetFeatureVectorCountMatches = countMatches(synsetVector);
                    Map<String, Double> synsets = synsetFeatureVectorCountMatches.getSynsetsFeature();
                    for (Map.Entry<String, Double>  entry: synsets.entrySet()) {
                       Double frequency = entry.getValue() / countSynsets;
                       synsets.put(entry.getKey(), frequency);   
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return carrier;
    }
}