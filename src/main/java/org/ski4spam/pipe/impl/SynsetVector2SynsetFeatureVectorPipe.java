/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.util.HashMap;
import java.util.Map;

import org.bdp4j.types.Instance;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.Pipe;
import org.ski4spam.types.SynsetFeatureVector;
import org.ski4spam.types.SynsetVector;
import org.bdp4j.util.Pair;

/**
 * A pipe to transform a SynsetVector wich contains a list of synsets included
 * in a message into a SynsetFeatureVector wich compile togeher duplicated
 * features and assign a score for each feature according with a
 * groupingStrategy. The groupStrategy is one of the following: <ul>
 * <li>SynsetVectorGroupingStrategy.COUNT: indicates the number of times that a
 * synset is observed in the content (ex. 5)</li>
 * <li>SynsetVectorGroupingStrategy.BOOLEAN: Indicates if the synset is observed
 * in the content (1) or not (0) (ex. 0)</li>
 * <li>SynsetVectorGroupingStrategy.FREQUENCY: Indicates the frequency of the
 * synset in the text that is the count of times that the synset is observed
 * divided by the whole amount of synsets.</li>
 * </ul>
 *
 * @author María Novo
 */
public class SynsetVector2SynsetFeatureVectorPipe extends Pipe {
    /**
     * For logging purposes
     */
    public static final String DEFAULT_GROUPTING_STRATEGY = "COUNT";

    /**
     * Dependencies of the type alwaysAfter
     * These dependences indicate what pipes should be  
     * executed before the current one. So this pipe
     * shoudl be executed always after other dependant pipes
     * included in this variable
     */
    final Class<?> alwaysAftterDeps[]={};

    /**
     * Dependencies of the type notAfter
     * These dependences indicate what pipes should not be  
     * executed before the current one. So this pipe
     * shoudl be executed before other dependant pipes
     * included in this variable
     */
    final Class<?> notAftterDeps[]={};

    /**
     * Indicates the group strategy to create the synsetFeatureVector
     */
    private SynsetVectorGroupingStrategy groupStrategy
            = SynsetVectorGroupingStrategy.FREQUENCY;

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return SynsetVector.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return SynsetFeatureVector.class;
    }

    /**
     * Changes the grouping strategy
     *
     * @param groupStrategy The new grouping strategy
     */
    @PipeParameter(name = "groupStrategy", description = "Indicates the group strategy to create the synsetFeatureVector", defaultValue = DEFAULT_GROUPTING_STRATEGY)
    public void setGroupStrategy(String groupStrategy) {
        this.groupStrategy = SynsetVectorGroupingStrategy.valueOf(groupStrategy);
    }

    /**
     * Retrieves the current grouping strategy
     *
     * @return The current grouping strategy
     */
    public SynsetVectorGroupingStrategy getGroupStrategy() {
        return this.groupStrategy;
    }

    /**
     * Creates a SynsetVector2SynsetFeatureVector Pipe using an specific
     * grouping strategy
     *
     * @param groupStrategy The selected grouping strategy
     */
    public SynsetVector2SynsetFeatureVectorPipe(SynsetVectorGroupingStrategy groupStrategy) {
        this.groupStrategy = groupStrategy;
    }

    /**
     * Converts a synsetVector in a synsetFeatureVector, with the synsetId and
     * the number of times that a synsetId appears in synsetVector
     *
     * @param synsetVector
     * @return A synsetFeatureVector with the synsetId and the number of times
     * that a synsetId appears in synsetVector
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

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        Map<String, Double> synsetFeatureVector;
        SynsetFeatureVector synsetFeatureVectorCountMatches = null;

        try {
            SynsetVector synsetVector = (SynsetVector) carrier.getData();

            //SynsetVector synsetVector = (SynsetVector) svTest;
            switch (groupStrategy) {
                case COUNT:
                    /* Generate a synsetFeatureVector with synsetId and synsetId appearance number in synsetVector*/
                    synsetFeatureVectorCountMatches = countMatches(synsetVector);
                    carrier.setData(synsetFeatureVectorCountMatches);

                    break;
                case BOOLEAN:
                    /* Generate a synsetFeatureVector with synsetId and 0/1 if this synsetId is or not in synsetVector*/
                    synsetFeatureVector = new HashMap<>();
                    for (Pair<String, String> pairSV : synsetVector.getSynsets()) {
                        String synsetId = pairSV.getObj1();
                        if (synsetFeatureVector.get(synsetId) == null) {
                            synsetFeatureVector.put(pairSV.getObj1(), 1d);
                        }
                    }
                    ;
                    carrier.setData(new SynsetFeatureVector(synsetFeatureVector));

                    break;
                case FREQUENCY:
                    /* Generate a synsetFeatureVector with synsetId and synsetId appearance frequency in synsetVector*/
                    synsetFeatureVectorCountMatches = countMatches(synsetVector);
                    Map<String, Double> synsets = synsetFeatureVectorCountMatches.getSynsetsFeature();
                    int countSynsets = synsets.size();
                    for (Map.Entry<String, Double> entry : synsets.entrySet()) {
                        Double frequency = entry.getValue() / countSynsets;
                        synsets.put(entry.getKey(), frequency);
                    }
                    carrier.setData(synsetFeatureVectorCountMatches);

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return carrier;
    }
}
