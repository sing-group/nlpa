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
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import java.util.Properties;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe adds the polarity of the text as instance property. Possible resuts
 * are included in a 5-levels Likert scale:
 * <ul>
 * <li> 0: "Very Negative"</li>
 * <li> 1: "Negative" </li>
 * <li> 2: "Neutral" </li>
 * <li> 3: "Positive" </li>
 * <li> 4: "Very Positive" </li>
 * </ul>
 * 
 * The polarity is computed using Stanford NLP framework.
 * 
 * @author José Ramón Méndez
 * @author Enaitz Ezpeleta
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class ComputePolarityFromStringBufferPipe extends AbstractPipe {
  /**
   * For logging purposes
   */
  private static final Logger logger = LogManager.getLogger(ComputePolarityFromStringBufferPipe.class);

    /**
     * Initing a StandfordCoreNLP pipeline
     */
    static StanfordCoreNLP pipeline;
    static Properties props;

    static {
        props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");
        props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/englishSR.ser.gz");
        pipeline = new StanfordCoreNLP(props);
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
     * Indicates the datatype expected in the data attribute of an Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * The default property name to store the polarity
     */
    public static final String DEFAULT_POLARITY_PROPERTY = "polarity";

    /**
     * The property name to store the polarity
     */
    private String polProp = DEFAULT_POLARITY_PROPERTY;

    /**
     * Sets the property where the polarity will be stored
     *
     * @param polProp the name of the property for the polarity
     */
    @PipeParameter(name = "polpropname", description = "Indicates the property name to store the polarity", defaultValue = DEFAULT_POLARITY_PROPERTY)
    public void setExtensionProp(String polProp) {
        this.polProp = polProp;
    }

    /**
     * Retrieves the property name for storing the file polarity
     *
     * @return String containing the property name for storing the file polarity
     */
    public String getPolarityProp() {
        return this.polProp;
    }

    /**
     * Default constructor
     */
    public ComputePolarityFromStringBufferPipe() {
        this (DEFAULT_POLARITY_PROPERTY);
    }

    /**
     * Build a StoreFileExtensionPipe that stores the polarity of the file in
     * the property polProp
     *
     * @param polProp The name of the property to store the polarity text 
     */
    public ComputePolarityFromStringBufferPipe(String polProp) {
        super(new Class<?>[0],new Class<?>[0]);

        this.polProp = polProp;
    }

    /**
     * Process an Instance. This method takes an input Instance, calculates its polarity, 
     * and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        //System.out.print("Processing: " + carrier.getName() + " ... ");
        int mainSentiment = 0;
        if (carrier.getData() instanceof StringBuffer) {

            String text = carrier.getData().toString().replaceAll("[^\\p{Space}\\p{Print}]", "");
            //System.out.print("(size: " + text.length() + ") ");
            if (text != null && text.length() > 0) {
                int longest = 0;
                Annotation annotation = pipeline.process(text);
                for (CoreMap sentence : annotation
                        .get(CoreAnnotations.SentencesAnnotation.class)) {
                    Tree tree = sentence
                            .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                    int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                    String partText = sentence.toString();
                    if (partText.length() > longest) {
                        mainSentiment = sentiment;
                        longest = partText.length();
                    }

                }
            }
        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        carrier.setProperty(polProp, mainSentiment);
        //System.out.println("done: polarity=" + mainSentiment + ".");
        return carrier;
    }
}
