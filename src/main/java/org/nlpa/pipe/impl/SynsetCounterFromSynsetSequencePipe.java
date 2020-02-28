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

import java.util.StringTokenizer;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.nlpa.types.SynsetSequence;

/**
 * This pipe adds the synsetCounter property that is computed by counting the number of synsets
 * of a stringbuffer included in the data of the Instance
 *
 * @author María Novo
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class SynsetCounterFromSynsetSequencePipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SynsetCounterFromSynsetSequencePipe.class);

    /**
     * The default name of the property to store the number of synsets of the text
     */
    public static final String DEFAULT_SYNSET_COUNTER_PROPERTY = "synset-counter";

    /**
     * The property to store the number of synsets in the text
     */
    private String synsetCounterProp = DEFAULT_SYNSET_COUNTER_PROPERTY;

    /**
     * Default constructor. Build a SynsetCounterFromStringBufferPipe that stores the number of synsets in the
     * default property ("synset-counter")
     */
    public SynsetCounterFromSynsetSequencePipe() {
        this(DEFAULT_SYNSET_COUNTER_PROPERTY);
    }

    /**
     * Build a SynsetCounterFromStringBufferPipe that stores the number of synsets in the
     * property indicated by synsetCounterProp parameter
     *
     * @param synsetCounterProp the name of the property to store the number of synsets
     */
    public SynsetCounterFromSynsetSequencePipe(String synsetCounterProp) {
        super(new Class<?>[]{StringBuffer2SynsetSequencePipe.class}, new Class<?>[0]);

        this.synsetCounterProp = synsetCounterProp;
    }

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
        return SynsetSequence.class;
    }

    /**
     * Establish the name of the property to store the number of synsets in the text
     *
     * @param synsetCounterProp the name of the property to store the number of synsets in the
     * text
     */
    @PipeParameter(name = "wordcounterpropname", description = "Indicates the property name to store the number of synsets in the text", defaultValue = DEFAULT_SYNSET_COUNTER_PROPERTY)
    public void setSynsetCounterProp(String synsetCounterProp) {
        this.synsetCounterProp = synsetCounterProp;
    }

    /**
     * Returns the name of the property to store the number of synsets
     *
     * @return the name of the property to store the number of synsets
     */
    public String getSynsetCounterProp() {
        return this.synsetCounterProp;
    }

    /**
     * Process an Instance. This method takes an input Instance, calculates the
     * number of synsets of the text, and returns it. This is the method by which all pipes
     * are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof SynsetSequence) {
            SynsetSequence synsetSequence = (SynsetSequence) carrier.getData();
            int synsetCounter = synsetSequence.getSynsets().size();
            carrier.setProperty(synsetCounterProp, synsetCounter);
        } else {
            carrier.setProperty(synsetCounterProp, 0);
            logger.error("Data should be an SynsetSequence when processing " + carrier.getName() + " but is a " + carrier.getData().getClass().getName());
        }

        return carrier;
    }
}
