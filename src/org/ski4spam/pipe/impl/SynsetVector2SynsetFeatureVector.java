/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.ia.types.SynsetVector;
import org.ski4spam.ia.types.SynsetFeatureVector;
import org.ski4spam.pipe.ParameterPipe;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.TransformationPipe;
import org.ski4spam.util.Pair;

/**
 *
 * @author María Novo
 */
public class SynsetVector2SynsetFeatureVector extends Pipe {

    private List<Pair<String, Double>> synsetFeatureVector;
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
     *
     * @param carrier
     * @return
     */
    private SynsetFeatureVector countMatches(SynsetVector synsetVector) {
        List<Pair<String, Double>> synsetFeatureVector = new ArrayList<>();

        try {
            synsetVector.getSynsets().forEach((pairSV) -> {
                Pair pSFV;
                if (synsetFeatureVector.isEmpty()) {
                    pSFV = new Pair(pairSV.getObj1().toString(), 1.0);
                    synsetFeatureVector.add(pSFV);
                    countSynsets++;
                } else {
                    Boolean found = false;
                    int i = 0;
                    while (i < synsetFeatureVector.size() && !found) {
                        Double appearanceNumber = synsetFeatureVector.get(i).getObj2();
                        if ((synsetFeatureVector.get(i).getObj1()).equals(pairSV.getObj1())) {
                            synsetFeatureVector.get(i).setObj2(appearanceNumber + 1);
                            found = true;
                        } else {
                            pSFV = new Pair(pairSV.getObj1().toString(), 1.0);
                            synsetFeatureVector.add(pSFV);
                            found = true;
                        }
                        if (found) {
                            countSynsets++;
                        }
                        i++;
                    };
                }
            });

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return new SynsetFeatureVector(synsetFeatureVector);
    }

    @Override
    public Instance pipe(Instance carrier) {

        List<Pair<String, Double>> sfv = new ArrayList<Pair<String, Double>>();

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
//            SynsetVector synsetVector = (SynsetVector)carrier.getData();

            SynsetVector synsetVector = (SynsetVector) svTest;
            switch (groupStrategy) {
                case COUNT:
                    countMatches(synsetVector);
                    break;
                case BOOLEAN:
                    synsetVector.getSynsets().forEach((pairSV) -> {
                        Pair pSFV;
                        if (sfv.isEmpty()) {
                            pSFV = new Pair(pairSV.getObj1().toString(), 1.0);
                            sfv.add(pSFV);
                        } else {
                            Boolean found = false;
                            int i = 0;
                            while (i < sfv.size() && !found) {
                                if ((sfv.get(i).getObj1()).equals(pairSV.getObj1())) {
                                    found = true;
                                }
                                i++;
                            };
                            if (!found) {
                                pSFV = new Pair(pairSV.getObj1().toString(), 1.0);
                                sfv.add(pSFV);
                                found = true;
                            }
                        }
                    });
                    break;
                case FREQUENCY:
                    SynsetFeatureVector synsetFeatureVectorCountMatches = countMatches(synsetVector);

                    Double frequency;
                    for (Pair<String, Double> synsetsFeaturePair : synsetFeatureVectorCountMatches.getSynsetsFeature()) {
                        frequency = synsetsFeaturePair.getObj2() / countSynsets;
                        synsetsFeaturePair.setObj2(frequency);
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return carrier;
    }
}
