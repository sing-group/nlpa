package org.ski4spam.pipe.impl;

import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.TransformationPipe;

import java.io.IOException;
import java.util.List;

/**
 * This pipe implements language guessing by using language detector library shared at https://github.com/optimaize/language-detector
 *
 * @author José Ramón Méndez Reboredo
 */
public class GuessLanguageFromStringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(GuessLanguageFromStringBufferPipe.class);

    private String langProp = "language";

    private String langReliabilityProp = "language-reliability";

    private LanguageDetector languageDetector;

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

    @TransformationPipe(inputType = "StringBuffer", outputType = "StringBuffer")
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
	
		
