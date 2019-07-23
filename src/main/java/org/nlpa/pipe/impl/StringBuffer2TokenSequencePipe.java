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

import com.google.auto.service.AutoService;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;
import org.nlpa.types.Dictionary;
import org.nlpa.types.TokenSequence;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SharedDataProducer;

import static org.nlpa.types.TokenSequence.DEFAULT_SEPARATORS;

/**
 * This pipe modifies the data of an Instance transforming it from StringBuffer
 * to TokenSequence
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class StringBuffer2TokenSequencePipe extends AbstractPipe implements SharedDataProducer {

    /**
     * The separators used to tokenize
     */
    private String separators = DEFAULT_SEPARATORS;

    /**
     * Default constructor. Creates a StringBuffer2TokenSequencePipe Pipe
     *
     */
    public StringBuffer2TokenSequencePipe() {
        super(new Class<?>[]{StringBufferToLowerCasePipe.class}, new Class<?>[0]);
    }

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
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
        return TokenSequence.class;
    }

    /**
     * Stablish the separators used for tokenising
     *
     * @param separators The separators
     */
    @PipeParameter(name = "separators", description = "Indicates separators used to identify tokens", defaultValue = DEFAULT_SEPARATORS)
    public void setSeparators(String separators) {
        this.separators = separators;
    }

    /**
     * Returns a String with the characters used as token separators
     *
     * @return the separators used to tokenize
     */
    public String getSeparators() {
        return this.separators;
    }

    /**
     * Compute tokens from text. This method get data from StringBuffer and
     * process instances to get tokens
     *
     * @param carrier The instance to be processed
     * @return The processed instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            String data = (carrier.getData().toString());
            TokenSequence tokenSequence = new TokenSequence(data, separators);
            carrier.setData(tokenSequence);

            Dictionary.getDictionary().setEncode(true);
            for (int i = 0; i < tokenSequence.size(); i++) {
                Dictionary.getDictionary().add(tokenSequence.getToken(i));
            }
        }
        return carrier;
    }

    /**
     * Save data to a file
     *
     * @param dir Directory name where the data is saved
     */
    @Override
    public void writeToDisk(String dir) {
        Dictionary.getDictionary().writeToDisk(dir + System.getProperty("file.separator") + "Dictionary.ser");
    }
}
