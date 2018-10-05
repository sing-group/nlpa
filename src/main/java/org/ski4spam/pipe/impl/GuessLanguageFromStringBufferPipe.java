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

import org.bdp4j.pipe.PipeParameter;

/**
 * This pipe implements language guessing by using language detector library shared at https://github.com/optimaize/language-detector
 *
 * @author José Ramón Méndez Reboredo
 */
@PropertyComputingPipe()
public class GuessLanguageFromStringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(GuessLanguageFromStringBufferPipe.class);

    /**
		* The default property name where the language will be stored
		*/
    public final static String DEFAULT_LANG_PROPERTY="language";
	 
	 /**
		* The default property name where the language guessing reliability is stored
		*/
	 public final static String DEFAULT_LANG_RELIABILITY_PROPERTY="language-reliability";

    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }

    /**
		* The name of the property to store the language
		*/
    private String langProp = DEFAULT_LANG_PROPERTY;
	 
	 /**
		* The name of the property to store the realiability of the language guessing
		*/
    private String langReliabilityProp = DEFAULT_LANG_RELIABILITY_PROPERTY;
	 
	 /**
		* A language detector to guess the language
		*/
    private LanguageDetector languageDetector; 
    
	 /**
		* Stablish the name of the property where the language will be stored
		* @param langProp The name of the property where the language is stored
		*/
	 @PipeParameter(name = "langpropname", description = "Indicates the property name to store the language", defaultValue=DEFAULT_LANG_PROPERTY)
    public void setLangProp(String langProp){
        this.langProp = langProp;
    }
    
	 /**
		* Returns the name of the property in which the language is stored
		* @return the name of the property where the language is stored
	  */
    public String getLangProp(){
        return this.langProp;
    }
   
	 /**
		* Store the property name for the reliability of the guessing
		* @param langReliabilityProp The property name for storing the reliability
		*/
	 @PipeParameter(name = "realiabilitypropname", description = "Indicates the property name to store the reliability", defaultValue=DEFAULT_LANG_RELIABILITY_PROPERTY)
    public void setLangReliabilityProp(String langReliabilityProp){
        this.langReliabilityProp = langReliabilityProp;
    }
    
	 /**
		* Returns the reliability of the language guessing
		* @return the reliability of the language guessing
		*/
    public String getLangReliabilityProp(){
        return this.langReliabilityProp;
    }
   
	 /**
		* The default constructor for the language guessing pipe
		*/
    public GuessLanguageFromStringBufferPipe() {
        init();
    }

    public GuessLanguageFromStringBufferPipe(String langProp, String langReliabilityProp) {
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
                    carrier.setProperty(langProp, bestlang.getLanguage().toUpperCase());
                } else {
                    carrier.setProperty(langProp, "");
                }
                carrier.setProperty(langReliabilityProp, prob);
            }
        }

        return carrier;
    }

}
	
		
