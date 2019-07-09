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
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;
import org.nlpa.types.Dictionary;
import org.nlpa.types.TokenSequence;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SharedDataProducer;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * A pipe to compute tokens from text
 *
 * @author María Novo 
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class StringBuffer2TokenSequencePipe extends AbstractPipe implements SharedDataProducer {

    /**
     * For loggins purposes
     */
    //private static final Logger logger = LogManager.getLogger(StringBuffer2TokenSequencePipe.class);

    /**
     * The name of the property where the language is stored
     */
    private String langProp = DEFAULT_LANG_PROPERTY;

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
     * Stablish the name of the property where the language will be stored
     *
     * @param langProp The name of the property where the language is stored
     */
    @PipeParameter(name = "langpropname", description = "Indicates the property name to store the language", defaultValue = DEFAULT_LANG_PROPERTY)
    public void setLangProp(String langProp) {
        this.langProp = langProp;
    }

    /**
     * Returns the name of the property in which the language is stored
     *
     * @return the name of the property where the language is stored
     */
    public String getLangProp() {
        return this.langProp;
    }

    /**
     * Create the pipe and initialize the dictionary. Please note that
     * the dictionary can be achieved by using the corresponding getter.
     *
     */
    public StringBuffer2TokenSequencePipe() {
        super(new Class<?>[]{StringBufferToLowerCasePipe.class}, new Class<?>[0]);
    }
    
    @Override
    /**
     * Compute tokens from text. This method get data from StringBuffer and
     * process instances:
     * <li>Invalidate instance if the language is not present</li>
     * <li>Process this texts to get tokens</li>
     */
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            String data = (carrier.getData().toString());
            TokenSequence tokenSequence = new TokenSequence(data, TokenSequence.DEFAULT_SEPARATORS);
            carrier.setData(tokenSequence);
            for (int i = 0; i < tokenSequence.size(); i++) {
                Dictionary.getDictionary().setEncode(true);
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
