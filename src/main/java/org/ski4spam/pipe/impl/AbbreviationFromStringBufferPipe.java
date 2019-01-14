package org.ski4spam.pipe.impl;

import org.bdp4j.util.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.pipe.PipeParameter;
import org.ski4spam.Main;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe drops stopwords from texts The data of the instance should contain
 * a StringBuffer without HTML Tags
 *
 * @author Reyes Pavón Rial
 * @author Rosalía Laza Fidalgo
 */
@TransformationPipe()
public class AbbreviationFromStringBufferPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(AbbreviationFromStringBufferPipe.class);

    /**
     * A hashset of abbreviations in different languages. NOTE: All JSON files
     * (listed below) containing abbreviations
     *
     */
    private static final HashMap<String, HashMap<String, Pair<Pattern, String>>> htAbbreviations = new HashMap<>();

    static {
        for (String i : new String[]{"/abbreviations-json/abbrev.es.json", 
                                     "/abbreviations-json/abbrev.en.json",
                                     "/abbreviations-json/abbrev.fr.json",
                                     "/abbreviations-json/abbrev.gl.json",
                                     "/abbreviations-json/abbrev.eu.json",
                                     "/abbreviations-json/abbrev.ru.json"}) {

            String lang = i.substring(27, 29).toUpperCase();
            try {
                InputStream is = Main.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, Pair<Pattern, String>> dict = new HashMap<>();
                for (String abbrev : jsonObject.keySet()) {
                    dict.put(abbrev, new Pair<>(
                            Pattern.compile("(?:[\\p{Space}]|^)(" + Pattern.quote(abbrev) + ")(?:[\\p{Space}]|$)"),
                            jsonObject.getString(abbrev)));
                }
                htAbbreviations.put(lang, dict);
            } catch (Exception e) {
                System.out.println("Exception processing: " + i + " message " + e.getMessage());
            }

        }

    }

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
        return StringBuffer.class;
    }

    /**
     * Construct a AbbreviationFromStringBuffer instance
     */
    public AbbreviationFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY);
    }

    /**
     * Construct a AbbreviationFromStringBuffer instance given a language
     * property
     *
     * @param langProp The propertie that stores the language of text
     */
    public AbbreviationFromStringBufferPipe(String langProp) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},new Class<?>[0]);

        this.langProp = langProp;
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
     * Process an Instance. This method takes an input Instance, modifies it
     * extending abbreviations, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {

             String lang = (String) carrier.getProperty(langProp);
             StringBuffer sb = (StringBuffer)carrier.getData();

             HashMap<String,Pair<Pattern,String>> dict = htAbbreviations.get(lang);
             if (dict==null) return carrier; //When there is not a dictionary for the language

             for(String abbrev:dict.keySet()){
                  Pattern p=dict.get(abbrev).getObj1();
                  Matcher m = p.matcher(sb);
                  int last = 0;
                  while (m.find(last)){
                     last = m.start(1);
                     sb = sb.replace(m.start(1), m.end(1), dict.get(abbrev).getObj2());
                  }
             }
        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
