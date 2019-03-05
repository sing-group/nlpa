package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * Pipe that replaces the contactions in the original text
 * Example "i can't" -%gt; "i cannot"
 * @author José Ramón Méndez Reboredo
 */
public class ContractionsFromStringBuffer extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(ContractionsFromStringBuffer.class);

    /**
     * The name of the property where the language is stored
     */
    private String langProp = DEFAULT_LANG_PROPERTY;

    /**
     * A hashset of abbreviations in different languages. NOTE: All JSON files
     * (listed below) containing abbreviations
     *
     */
    private static final HashMap<String, HashMap<String, Pair<Pattern, String>>> htContractions = new HashMap<>();

    static {
        for (String i : new String[]{"/contractions-json/contract.en.json", 
        }) {
            String lang = i.substring(27, 29).toUpperCase();
            try {
                InputStream is = ContractionsFromStringBuffer.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, Pair<Pattern, String>> dict = new HashMap<>();
                for (String abbrev : jsonObject.keySet()) {
                    dict.put(abbrev, new Pair<>(
                            Pattern.compile("(?:[\\p{Space}]|[\"><¡?¿!;:,.'-]|^)(" + Pattern.quote(abbrev) + ")[;:?\"!,.'>-]?(?=(?:[\\p{Space}]|$|>))"),
                            jsonObject.getString(abbrev))); 
                }
                htContractions.put(lang, dict);
            } catch (Exception e) {
                logger.error("Exception processing: " + i + " message " + e.getMessage());
            }

        }

    }

    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Construct a ContractionsFromStringBuffer instance
     */
    public ContractionsFromStringBuffer() {
        this(DEFAULT_LANG_PROPERTY);
    }

    /**
     * Construct a ContractionsFromStringBuffer instance given a language
     * property
     *
     * @param langProp The propertie that stores the language of text
     */
    public ContractionsFromStringBuffer(String langProp) {
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

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {

            String lang = (String) carrier.getProperty(langProp);
            StringBuffer sb = (StringBuffer)carrier.getData();

            HashMap<String,Pair<Pattern,String>> dict = htContractions.get(lang);
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