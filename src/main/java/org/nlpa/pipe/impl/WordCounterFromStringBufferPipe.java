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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

/**
 * This pipe adds the wordCounter property that is computed by counting the
 * number of words of a stringbuffer included in the data of the Instance
 *
 * @author María Novo
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class WordCounterFromStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(WordCounterFromStringBufferPipe.class);

    /**
     * The default name of the property to store the number of words of the text
     */
    public static final String DEFAULT_WORD_COUNTER_PROPERTY = "word-counter";

    /**
     * The default value of the regex to split the text
     */
    public static final String DEFAULT_REGEX_VALUE = "([\\W\\s]+)";

    /**
     * The property to store the number of words in the text
     */
    private String wordCounterProp = DEFAULT_WORD_COUNTER_PROPERTY;

    /**
     * The property to store the regex
     */
    private String regex = DEFAULT_REGEX_VALUE;

    /**
     * Default constructor. Build a WordCounterFromStringBufferPipe that stores
     * the umber of words in the default property ("word-counter")
     */
    public WordCounterFromStringBufferPipe() {
        this(DEFAULT_WORD_COUNTER_PROPERTY);

    }

    /**
     * Build a WordCounterFromStringBufferPipe that stores the number of words
     * in the property indicated by wordCounterProp parameter
     *
     * @param wordCounterProp the name of the property to store the number of
     * words
     */
    public WordCounterFromStringBufferPipe(String wordCounterProp) {
        super(new Class<?>[0], new Class<?>[0]);

        this.wordCounterProp = wordCounterProp;
    }

    /**
     * Build a WordCounterFromStringBufferPipe that stores the number of words
     * in the property indicated by wordCounterProp parameter
     *
     * @param wordCounterProp the name of the property to store the number of
     * words
     * @param regex The regex used to identify spaces
     */
    public WordCounterFromStringBufferPipe(String wordCounterProp, String regex) {
        super(new Class<?>[0], new Class<?>[0]);

        this.regex = regex;
        this.wordCounterProp = wordCounterProp;
    }

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
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
        return StringBuffer.class;
    }

    /**
     * Establish the name of the property to store the number of words in the
     * text
     *
     * @param wordCounterProp the name of the property to store the number of
     * words in the text
     */
    @PipeParameter(name = "wordcounterpropname", description = "Indicates the property name to store the number of words in the text", defaultValue = DEFAULT_WORD_COUNTER_PROPERTY)
    public void setWordCounterProp(String wordCounterProp) {
        this.wordCounterProp = wordCounterProp;
    }

    /**
     * Returns the name of the property to store the number of words
     *
     * @return the name of the property to store the number of words
     */
    public String getWordCounterProp() {
        return this.wordCounterProp;
    }

    /**
     * Establish the regex used to split words
     *
     * @param regex the regex used to split words text
     */
    @PipeParameter(name = "regex", description = "Indicates the regex used to split words", defaultValue = DEFAULT_REGEX_VALUE)
    public void setRegexProp(String regex) {
        this.regex = regex;
    }

    /**
     * Returns the regex used to split words
     *
     * @return the regex used to split words
     */
    public String getRegexProp() {
        return this.regex;
    }

    /**
     * Process an Instance. This method takes an input Instance, calculates the
     * number of words of the text, and returns it. This is the method by which
     * all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            StringBuffer sb = (StringBuffer) carrier.getData();
            String[] words = sb.toString().split(this.regex);

            carrier.setProperty(wordCounterProp, words.length);
        } else {
            carrier.setProperty(wordCounterProp, 0);
            logger.error("Data should be an StringBuffer when processing " + carrier.getName() + " but is a " + carrier.getData().getClass().getName());
        }

        return carrier;
    }
}
