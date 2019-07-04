package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bdp4j.pipe.Pipe;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe detects slang terms in text and replace by its traduction using dictionaries (Json files). 
 * The property that stores the language of text has to exist.
 *
 * @author Reyes Pavón Rial
 * @author Rosalía Laza Fidalgo
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class SlangFromStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SlangFromStringBufferPipe.class);

    /**
     * A hashmap of slangs in different languages. NOTE: All JSON files (listed
     * below) containing slangs
     *
     */
    private static final HashMap<String, HashMap<String, SlangEntry>> hmSlangs = new HashMap<>();

    static {
        for (String i : new String[]{"/slangs-json/slang.en.json", "/slangs-json/slang.es.json",
            "/slangs-json/slang.gl.json", "/slangs-json/slang.fr.json",
            "/slangs-json/slang.pt.json"}) {

            String lang = i.substring(19, 21).toUpperCase();
            try {
                InputStream is = SlangFromStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, SlangEntry> dict = new HashMap<>();
                for (String slang : jsonObject.keySet()) {
                    dict.put(slang, new SlangEntry(Pattern.compile("(?:[\\p{Space}]|[\"><¡?¿!;:,.']|^)(" + Pattern.quote(slang) + ")[;:?\"!,.'>]?(?=(?:[\\p{Space}]|$|>))", Pattern.CASE_INSENSITIVE),
                            jsonObject.getString(slang)));
                }
                hmSlangs.put(lang, dict);
            } catch (Exception e) {
                logger.error("Exception processing: " + i + " message " + e.getMessage());
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
     * Construct a SlangFromStringBuffer instance
     */
    public SlangFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY);
    }

    /**
     * Construct a SlangFromStringBuffer instance given a language property
     *
     * @param langProp The propertie that stores the language of text
     */
    public SlangFromStringBufferPipe(String langProp) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class}, new Class<?>[0]);

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
     * extending langs, and returns it. This is the method by which all pipes
     * are eventually run.
     *
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            String lang = (String) carrier.getProperty(langProp);
            StringBuffer data = (StringBuffer) carrier.getData();
            HashMap<String, SlangEntry> dict = hmSlangs.get(lang);
            if (dict == null) {
                return carrier; //If dict is not available for the language of the texts
            }
            Collection<SlangEntry> dictEntries = dict.values();
            for (SlangEntry slang : dictEntries) {
                Matcher m = slang.getWordPattern().matcher(data);
                int last = 0;
                while (m.find(last)) {
                    last = m.start(1);
                    data = data.replace(m.start(1), m.end(1), slang.getReplacement());
                }
            }
        }
        return carrier;
    }

    /**
     * Find the replacement for a SlangTerm
     *
     * @param slangTerm The term written in SlangTerm
     * @param lang The language used for slang
     * @return The traduction of the slang
     */
    public static String getReplacement4SlangTerm(String slangTerm, String lang) {
        HashMap<String, SlangEntry> dict = hmSlangs.get(lang);
        if (dict == null) {
            return null;
        }
        SlangEntry entry = dict.get(slangTerm);
        if (entry == null) {
            return null;
        }
        return entry.getReplacement();
    }
}

/**
 * Entry for slang
 */
class SlangEntry {

    /**
     * The replacement string for the slang
     */
    private String replacement;

    /**
     * A pattern that is automatically computed from the word to quickly find the
     * slang entry
     */
    private Pattern wordPattern;

    /**
     * Default SlangEntry constructor
     */
    public SlangEntry(Pattern wordPattern, String replacement) {
        super();
        this.replacement = replacement;
        this.wordPattern = wordPattern;
    }

    /**
     * Returns value of replacement
     *
     * @return the replacement value
     */
    public String getReplacement() {
        return replacement;
    }

    /**
     * Sets new value of replacement
     *
     * @param replacement the replacement value
     */
    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    /**
     * Returns value of wordPattern
     *
     * @return the pattern to detect the slang
     */
    public Pattern getWordPattern() {
        return wordPattern;
    }

    /**
     * Sets new value of wordPattern
     *
     * @param wordPattern the pattern to detect the slang
     */
    public void setWordPattern(Pattern wordPattern) {
        this.wordPattern = wordPattern;
    }
}
