package org.ski4spam.pipe.impl;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

/**
 * This pipe implements language guessing by using language detector library shared at https://github.com/optimaize/language-detector
 *
 * @author José Ramón Méndez Reboredo
 */
@PropertyComputingPipe()
public class GuessLanguageFromStringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(GuessLanguageFromStringBufferPipe.class);

    public static String DEFAULT_LANG_PROPERTY="language";
	 public static String DEFAULT_LANG_RELIABILITY_PROPERTY="language-reliability";

    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }

    private String langProp = DEFAULT_LANG_PROPERTY;
    private String langReliabilityProp = DEFAULT_LANG_RELIABILITY_PROPERTY;
    private LanguageDetector languageDetector; 
    
    public void setLangProp(String langProp){
        this.langProp = langProp;
    }
    
    public String getLangProp(){
        return this.langProp;
    }
   
    public void setLangReliabilityProp(String langReliabilityProp){
        this.langReliabilityProp = langReliabilityProp;
    }
    
    public String getLangReliabilityProp(){
        return this.langReliabilityProp;
    }
   
    public GuessLanguageFromStringBufferPipe() {
        init();
    }

    public GuessLanguageFromStringBufferPipe(String langProp, String langReliabilityProp) {
        this.langProp = langProp;
        this.langReliabilityProp = langReliabilityProp;
        init();
    }

    /**
     * Inits language detecting
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

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            if (carrier.getProperty("extension") != "twtid") { // For not using this with tweets
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
                    carrier.setProperty(langProp, bestlang.getLanguage());
                } else {
                    carrier.setProperty(langProp, "");
                }
                carrier.setProperty(langReliabilityProp, prob);
            }
        }

        return carrier;
    }

}
	
		
