/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;

/**
 * A pipe to transform a SynsetSequence which contains a list of synsets
 * included in a message into a FeatureVector which compiles together duplicated
 * features and assign a score for each feature according with a
 * groupingStrategy. The groupStrategy is one of the following: <ul>
 * <li>SequenceGroupingStrategy.COUNT: indicates the number of times that a
 * synset is observed in the content (ex. 5)</li>
 * <li>SequenceGroupingStrategy.BOOLEAN: Indicates if the synset is observed in
 * the content (1) or not (0) (ex. 0)</li>
 * <li>SequenceGroupingStrategy.FREQUENCY: Indicates the frequency of the synset
 * in the text that is the count of times that the synset is observed divided by
 * the whole amount of synsets.</li>
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
    private static final Logger logger = LogManager.getLogger(SynsetSequence2FeatureVectorPipe.class);

    /**
     * The default value for the the grouping strategy
     */
    public static final String DEFAULT_GROUPING_STRATEGY = "COUNT";

    /**
     * Indicates the grouping strategy to create the synsetFeatureVector
     */
    private SequenceGroupingStrategy groupStrategy = SequenceGroupingStrategy.FREQUENCY;

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
     * Indicates the datatype expected in the data attribute of an Instance
     * after processing
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
     * Default constructor. Creates a SynsetVector2FeatureVector Pipe using the
     * default grouping strategy
     */
    public SynsetSequence2FeatureVectorPipe() {
        this(SequenceGroupingStrategy.valueOf(DEFAULT_GROUPING_STRATEGY));
    }

    /**
     * Creates a SynsetVector2FeatureVector Pipe using a specific grouping
     * strategy
     *
     * @param groupStrategy The selected grouping strategy
     */
    public SynsetSequence2FeatureVectorPipe(SequenceGroupingStrategy groupStrategy) {
        super(new Class<?>[0], new Class<?>[0]);

        this.groupStrategy = groupStrategy;
    }

    /**
     * Converts a SynsetSequence in a FeatureVector, with the synsetId and the
     * number of times that a synsetId appears in SynsetSequence
     *
     * @param synsetVector
     * @return A FeatureVector with the synsetId and the number of times that a
     * synsetId appears in SynsetSequence
     */
    private FeatureVector countMatches(SynsetSequence synsetVector) {

        Map<String, Double> synsetFeatureVector = new HashMap<>();

        try {
            synsetVector.getSynsets().stream().map((pairSV) -> pairSV.getObj1()).forEachOrdered((synsetId) -> {
                if (synsetFeatureVector.get(synsetId) == null) {
                    synsetFeatureVector.put(synsetId, 1d);
                } else {
                    Double appearanceNumber = synsetFeatureVector.get(synsetId);
                    synsetFeatureVector.put(synsetId, appearanceNumber + 1d);
                }
            });
        } catch (Exception e) {
            logger.warn("[COUNT MATCHES]" + e.getMessage());
        }

        return new FeatureVector(synsetFeatureVector);
    }

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * transforming data from SynsetSequence to FeatureVector and returns it.
     * This is the method by which all pipes are eventually run.
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
                        if (synsetId != null) {
                            if (synsetFeatureVector.get(synsetId) == null) {
                                synsetFeatureVector.put(synsetId, 1d);
                            }
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
                        String entryKey = entry.getKey();
                        if (entryKey != null) {
                            Double frequency = entry.getValue() / countSynsets;
                            synsets.put(entryKey, frequency);
                        }
                    }
                    carrier.setData(synsetFeatureVectorCountMatches);

                    break;
            }

        } catch (Exception e) {
            logger.warn("[PIPE]: " + e.getMessage());
        }
        return carrier;
    }
}
