package org.ski4spam.pipe.impl;

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
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bdp4j.util.Pair;
import java.util.Collection;

import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe drops stopwords from texts The data of the instance should contain
 * a StringBuffer without HTML Tags
 *
 * @author Reyes Pavón Rial
 * @author Rosalía Laza Fidalgo
 */
@TransformationPipe()
public class SlangFromStringBufferPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SlangFromStringBufferPipe.class);

    /**
     * A hashmap of slangs in different languages.
     * NOTE: All JSON files (listed below) containing slangs
     *
     */
    private static final HashMap<String, HashMap<String, SlangEntry>> hmSlangs = new HashMap<>();

    static {
        for (String i : new String[]{"/slangs-json/slang.en.json","/slangs-json/slang.es.json" }) {

            String lang = i.substring(19, 21).toUpperCase();
            try {
                InputStream is = Main.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, SlangEntry> dict=new HashMap<>();
                for(String slang:jsonObject.keySet()){
                    dict.put(slang,
							               new SlangEntry(
								                   Pattern.compile( "(?:[\\p{Space}]|^)(" + Pattern.quote(slang) + ")(?:[\\p{Space}]|$)"),
                                   jsonObject.getString(slang))
						                 );
                }
                hmSlangs.put(lang,dict);
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
    public Class getInputType() {
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
    public Class getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Construct a SlangFromStringBuffer instance
     */
    public SlangFromStringBufferPipe() {
    }

    /**
     * Construct a SlangFromStringBuffer instance given a language property
     *
     * @param langProp The propertie that stores the language of text
     */
    public SlangFromStringBufferPipe(String langProp) {
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
     * Process an Instance. This method takes an input Instance,
     * modifies it extending langs, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * LLAMARLO ANTES DE QUITAR MAYÚSCULAS *****************
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            String lang = (String) carrier.getProperty(langProp);

            HashMap<String, SlangEntry> dict = hmSlangs.get(lang);
            if (dict==null) return carrier; //If dict is not available for the language of the texts

            Collection<SlangEntry> dictEntries=dict.values();
            StringBuffer sb = new StringBuffer(carrier.getData().toString());

            for(SlangEntry slang:dictEntries){
               Pattern p=slang.getWordPattern();
               Matcher m = p.matcher(sb);
               int last=0;
               while (m.find(last)){
                      sb = sb.replace(m.start(1), m.end(1), slang.getReplacement());
                      last=m.start(1);
               }
            }
           carrier.setData(sb);
        }
        return carrier;
    }

    /**
      * Find the replacement for a SlangTerm
      * @param slangTerm The term written in SlangTerm
      * @param lang The language used for slang
      * @return The traduction of the slang
      */
    public static String getReplacement4SlangTerm(String slangTerm, String lang){
      HashMap<String, SlangEntry> dict = hmSlangs.get(lang);
      if (dict==null) return null;
      SlangEntry entry=dict.get(slangTerm);
      if (entry==null) return null;
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
	  * A pattern that is automatically compued from the word to quickly find the slang entry
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
	* @return the replacement value
	*/
	public String getReplacement() {
		return replacement;
	}

	/**
	* Sets new value of replacement
	* @param the replacement value
	*/
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	/**
	* Returns value of wordPattern
	* @return the pattern to detect the slang
	*/
	public Pattern getWordPattern() {
		return wordPattern;
	}

	/**
	* Sets new value of wordPattern
	* @param the pattern to detect the slang
	*/
	public void setWordPattern(Pattern wordPattern) {
		this.wordPattern = wordPattern;
	}

  /**
   * Override default constructor to make it private
   */
	private SlangEntry() {
	}
}
