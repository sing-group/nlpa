/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.nlpa.types.SequenceGroupingStrategy;
import com.google.auto.service.AutoService;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.nlpa.types.FeatureVector;
import org.nlpa.types.SynsetSequence;

import java.util.HashMap;
import java.util.Map;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;

/**
 * A pipe to transform a SynsetSequence which contains a list of synsets included
 in a message into a FeatureVector which compiles together duplicated
 features and assign a score for each feature according with a
 groupingStrategy. The groupStrategy is one of the following: <ul>
 * <li>SequenceGroupingStrategy.COUNT: indicates the number of times that a
 synset is observed in the content (ex. 5)</li>
 * <li>SequenceGroupingStrategy.BOOLEAN: Indicates if the synset is observed
 in the content (1) or not (0) (ex. 0)</li>
 * <li>SequenceGroupingStrategy.FREQUENCY: Indicates the frequency of the
 synset in the text that is the count of times that the synset is observed
 divided by the whole amount of synsets.</li>
 * </ul>
 *
 * @author María Novo
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class SynsetSequence2FeatureVectorPipe extends AbstractPipe {
    /**
     * For logging purposes
     */
    public static final String DEFAULT_GROUPING_STRATEGY = "COUNT";

    /**
     * Indicates the group strategy to create the synsetFeatureVector
     */
    private SequenceGroupingStrategy groupStrategy
            = SequenceGroupingStrategy.FREQUENCY;

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return SynsetSequence.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return FeatureVector.class;
    }

    /**
     * Changes the grouping strategy
     *
     * @param groupStrategy The new grouping strategy
     */
    @PipeParameter(name = "groupStrategy", description = "Indicates the group strategy to create the synsetFeatureVector", defaultValue = DEFAULT_GROUPING_STRATEGY)
    public void setGroupStrategy(String groupStrategy) {
        this.groupStrategy = SequenceGroupingStrategy.valueOf(groupStrategy);
    }

    /**
     * Retrieves the current grouping strategy
     *
     * @return The current grouping strategy
     */
    public SequenceGroupingStrategy getGroupStrategy() {
        return this.groupStrategy;
    }

    /**
     * Creates a SynsetVector2FeatureVector Pipe using the default
     * grouping strategy
     */
    public SynsetSequence2FeatureVectorPipe(){
        this(SequenceGroupingStrategy.valueOf(DEFAULT_GROUPING_STRATEGY));
    }

    /**
     * Creates a SynsetVector2FeatureVector Pipe using a specific
     * grouping strategy
     *
     * @param groupStrategy The selected grouping strategy
     */
    public SynsetSequence2FeatureVectorPipe(SequenceGroupingStrategy groupStrategy) {
        super(new Class<?>[0],new Class<?>[0]);

        this.groupStrategy = groupStrategy;
    }

    /**
     * Converts a SynsetSequence in a FeatureVector, with the synsetId and
     * the number of times that a synsetId appears in SynsetSequence
     *
     * @param synsetVector
     * @return A FeatureVector with the synsetId and the number of times
     * that a synsetId appears in SynsetSequence
     */
    private FeatureVector countMatches(SynsetSequence synsetVector) {

        Map<String, Double> synsetFeatureVector = new HashMap<>();

        try {
            for (Pair<String, String> pairSV : synsetVector.getSynsets()) {
                String synsetId = pairSV.getObj1();

                if (synsetFeatureVector.get(synsetId) == null) {
                    synsetFeatureVector.put(synsetId, 1d);
                } else {
                    Double appearanceNumber = synsetFeatureVector.get(synsetId);
                    synsetFeatureVector.put(synsetId, appearanceNumber + 1d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return new FeatureVector(synsetFeatureVector);
    }

    /**
     * Process an Instance. This method takes an input Instance, 
     * modifies its list of synsets in a feature vector, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        Map<String, Double> synsetFeatureVector;
        FeatureVector synsetFeatureVectorCountMatches = null;

        try {
            SynsetSequence synsetVector = (SynsetSequence) carrier.getData();

            //SynsetVector synsetVector = (SynsetSequence) svTest;
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
                    carrier.setData(new FeatureVector(synsetFeatureVector));

                    break;
                case FREQUENCY:
                    /* Generate a synsetFeatureVector with synsetId and synsetId appearance frequency in synsetVector*/
                    synsetFeatureVectorCountMatches = countMatches(synsetVector);
                    Map<String, Double> synsets = synsetFeatureVectorCountMatches.getFeatures();
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
