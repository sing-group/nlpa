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
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import java.io.IOException;
import java.util.List;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe implements language guessing by using language detector library
 * shared at https://github.com/optimaize/language-detector
 *
 * @author José Ramón Méndez Reboredo
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class GuessLanguageFromStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(GuessLanguageFromStringBufferPipe.class);

    /**
     * The default property name where the language will be stored
     */
    public final static String DEFAULT_LANG_PROPERTY = "language";

    /**
     * The default property name where the language guessing reliability is
     * stored
     */
    public final static String DEFAULT_LANG_RELIABILITY_PROPERTY = "language-reliability";

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
     * The name of the property to store the language
     */
    private String langProp = DEFAULT_LANG_PROPERTY;

    /**
     * The name of the property to store the realiability of the language
     * guessing
     */
    private String langReliabilityProp = DEFAULT_LANG_RELIABILITY_PROPERTY;

    /**
     * A language detector to guess the language
     */
    private LanguageDetector languageDetector;

    /**
     * Establish the name of the property where the language will be stored
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
     * Store the property name for the reliability of the guessing
     *
     * @param langReliabilityProp The property name for storing the reliability
     */
    @PipeParameter(name = "realiabilitypropname", description = "Indicates the property name to store the reliability", defaultValue = DEFAULT_LANG_RELIABILITY_PROPERTY)
    public void setLangReliabilityProp(String langReliabilityProp) {
        this.langReliabilityProp = langReliabilityProp;
    }

    /**
     * Returns the reliability of the language guessing
     *
     * @return the reliability of the language guessing
     */
    public String getLangReliabilityProp() {
        return this.langReliabilityProp;
    }

    /**
     * The default constructor for the language guessing pipe
     */
    public GuessLanguageFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY, DEFAULT_LANG_RELIABILITY_PROPERTY);
    }

    /**
     * The constructor for the language guessing pipe
     * @param langProp The property name to store language
     * @param langReliabilityProp The property name to store the reliability of the language guessing
     */
    public GuessLanguageFromStringBufferPipe(String langProp, String langReliabilityProp) {
        super(new Class<?>[]{StripHTMLFromStringBufferPipe.class}, new Class<?>[0]);

        this.langProp = langProp;
        this.langReliabilityProp = langReliabilityProp;
        init();
    }

    /**
     * Inits language detecting subsystem
     */
    private void init() {
        try {
            //load all languages:
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

            //build language detector:
            languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(languageProfiles)
                    .build();

        } catch (IOException e) {
            logger.fatal("Language detector profiles could not be loaded");
            System.exit(0);
        }
    }

    /**
     *
     * Process an Instance. This method takes an input Instance, finds out the language, 
     * adds an instance property for language and its reliability, and returns it. 
     * This is the method by which all  pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            //if (carrier.getProperty("extension") != "twtid") { // For not using this with tweets
            //query:
            List<DetectedLanguage> langList = languageDetector.getProbabilities((StringBuffer) (carrier.getData()));

            LdLocale bestlang = null;
            Double prob = 0d;

            for (DetectedLanguage lang : langList) {
                logger.info(carrier.toString() + " -> " + lang.toString());
                if (lang.getProbability() > prob) {
                    bestlang = lang.getLocale();
                    prob = lang.getProbability();
                }
            }

            if (bestlang != null) { // In case of emojis bestLang is null
                carrier.setProperty(langProp, bestlang.getLanguage().toUpperCase());
                carrier.setProperty(langReliabilityProp, prob);
            } else {
                //see ISO 639-3:2007
                carrier.setProperty(langProp, "UND");
                carrier.setProperty(langReliabilityProp, 0);
            }

            //}
        } else {
            logger.error("Data should be an StrinBuffer when processing " + carrier.getName() + " but is a " + carrier.getData().getClass().getName());
        }
        return carrier;
    }

}
