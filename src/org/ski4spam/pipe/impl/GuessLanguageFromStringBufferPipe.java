package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.text.TextObjectFactory;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.DetectedLanguage;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This pipe implements language guessing by using language detector library shared at https://github.com/optimaize/language-detector
 * @author José Ramón Méndez Reboredo
 */
public class GuessLanguageFromStringBufferPipe extends Pipe {
	private static final Logger logger = LogManager.getLogger(GuessLanguageFromStringBufferPipe.class);
	
	String langProp="language";
	
	String langRealiabilityProp="language-reliability";
	
	LanguageDetector languageDetector;

    /**
     * Inits language detecting
     */
    private void init(){
		try{
  		    //load all languages:
		    List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

		    //build language detector:
		    languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
		        .withProfiles(languageProfiles)
		        .build();
			
		}catch(IOException e){
			logger.fatal("Language detector profiles could not be loaded");
			System.exit(0);
		}
    }
	
	public GuessLanguageFromStringBufferPipe(){
		init();
	}
	
	public GuessLanguageFromStringBufferPipe(String langProp, String langRealiabilityProp){
		this.langProp=langProp;
		this.langRealiabilityProp=langRealiabilityProp;
		init();
	}
	
    @Override
    public Instance pipe(Instance carrier) {
        if ( carrier.getData() instanceof StringBuffer){
			//query:
			List<DetectedLanguage> langList=languageDetector.getProbabilities((StringBuffer)(carrier.getData()));
			
			LdLocale bestlang=null;
			Double prob=0d;
			
			for (DetectedLanguage lang:langList){
				logger.info(carrier.toString()+" -> "+lang.toString());
				if (lang.getProbability()>prob){
					bestlang=lang.getLocale();
					prob=lang.getProbability();
				}
		    }
			
			carrier.setProperty(langProp,bestlang.getLanguage());
			carrier.setProperty(langRealiabilityProp,prob);
		}
		
		return carrier;
	}
	
}
	
		
