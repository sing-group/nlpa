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
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.Pipe;
import org.ski4spam.ia.types.SynsetFeatureVector;
import org.ski4spam.ia.types.SynsetVector;
import org.ski4spam.util.Pair;

/**
 * A pipe to transform a SynsetVector wich contains a list of synsets included in a message
 * into a SynsetFeatureVector wich compile togeher duplicated features and assign a score
 * for each feature according with a groupingStrategy. The groupStrategy is one of the 
 * following: <ul>
 * <li>SynsetVectorGroupingStrategy.COUNT: indicates the number of times that a synset is observed in the content (ex. 5)</li> 
 * <li>SynsetVectorGroupingStrategy.BOOLEAN: Indicates if the synset is observed in the content (1) or not (0) (ex. 0)</li>
 * <li>SynsetVectorGroupingStrategy.FREQUENCY: Indicates the frequency of the synset in the text that is the count 
 * of times that the synset is observed divided by the whole amount of synsets.</li>
 * </ul>
 * @author María Novo
 */
public class SynsetVector2SynsetFeatureVector extends Pipe {

	 public static final String DEFAULT_GROUPTING_STRATEGY="FREQUENCY";
	 
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

    /**
		* Changes the grouping strategy
		* @param groupStrategy The new grouping strategy
		*/
    @PipeParameter(name = "groupStrategy", description = "Indicates the group strategy to create the synsetFeatureVector", defaultValue=DEFAULT_GROUPTING_STRATEGY)
    public void setGroupStrategy(String groupStrategy) {
        this.groupStrategy = SynsetVectorGroupingStrategy.valueOf(groupStrategy);
    }

    /**
		* Retrieves the current grouping strategy
		* @return The current grouping strategy
		*/
    public SynsetVectorGroupingStrategy getGroupStrategy() {
        return this.groupStrategy;
    }

    /**
		* Creates a SynsetVector2SynsetFeatureVector Pipe using an specific grouping strategy
		* @param groupStrategy The selected grouping strategy
		*/
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

        return new SynsetFeatureVector(synsetFeatureVector);
    }

    @Override
    public Instance pipe(Instance carrier) {
        Map<String, Double> synsetFeatureVector;
        List<Pair<String, Double>> sfv = new ArrayList<>();

        try {
            SynsetVector synsetVector = (SynsetVector)carrier.getData();

            //SynsetVector synsetVector = (SynsetVector) svTest;
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
						  int countSynsets = synsets.size();
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