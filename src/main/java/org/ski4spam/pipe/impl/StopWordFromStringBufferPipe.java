package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.pipe.PipeParameter;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonString;

import java.util.HashMap;
import java.util.LinkedList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe drops stopwords from texts The data of the instance should contain
 * a StringBuffer without HTML Tags
 *
 * @author José Ramón Méndez
 */
@TransformationPipe()
public class StopWordFromStringBufferPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(StopWordFromStringBufferPipe.class);

    /**
     * A HashMap of stopwords in different languages.
	  * Thanks to StopWords-Json Project available at
	  * <a href="https://www.npmjs.com/package/stopwords-json">
	  * https://www.npmjs.com/package/stopwords-json</a>
	  * NOTE: All JSON files (listed below) containing these stopwords
	  * have been compiled from stopwords-json project (see previous link)
     */
    private static final HashMap<String,LinkedList<Pattern>> hmStopWords = new HashMap<>();

    static {
        for (String i : new String[]{"/stopwords-json/af.json", "/stopwords-json/ar.json",
            "/stopwords-json/bg.json", "/stopwords-json/bn.json",
            "/stopwords-json/br.json", "/stopwords-json/ca.json",
            "/stopwords-json/cs.json", "/stopwords-json/da.json",
            "/stopwords-json/de.json", "/stopwords-json/el.json",
            "/stopwords-json/en.json", "/stopwords-json/eo.json",
            "/stopwords-json/es.json", "/stopwords-json/et.json",
            "/stopwords-json/eu.json", "/stopwords-json/fa.json",
            "/stopwords-json/fi.json", "/stopwords-json/fr.json",
            "/stopwords-json/ga.json", "/stopwords-json/gl.json",
            "/stopwords-json/ha.json", "/stopwords-json/he.json",
            "/stopwords-json/hi.json", "/stopwords-json/hr.json",
            "/stopwords-json/hu.json", "/stopwords-json/hy.json",
            "/stopwords-json/id.json", "/stopwords-json/it.json",
            "/stopwords-json/ja.json", "/stopwords-json/ko.json",
            "/stopwords-json/la.json", "/stopwords-json/lv.json",
            "/stopwords-json/mr.json", "/stopwords-json/nl.json",
            "/stopwords-json/no.json", "/stopwords-json/pl.json",
            "/stopwords-json/pt.json", "/stopwords-json/ro.json",
            "/stopwords-json/ru.json", "/stopwords-json/sk.json",
            "/stopwords-json/sl.json", "/stopwords-json/so.json",
            "/stopwords-json/st.json", "/stopwords-json/sv.json",
            "/stopwords-json/sw.json", "/stopwords-json/th.json",
            "/stopwords-json/tr.json", "/stopwords-json/yo.json",
            "/stopwords-json/zh.json", "/stopwords-json/zu.json"}) {

            String lang = i.substring(16, 18).toUpperCase();
            try {
                InputStream is = StopWordFromStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonArray array = rdr.readArray();
                LinkedList<Pattern> currentStopwords=new LinkedList<>();
                for (JsonValue v : array) {
                    currentStopwords.add(
                      Pattern.compile( "(?:\\p{Space}|[\"><¡?¿!;:,.'-]|^)(" + Pattern.quote(((JsonString)v).getString()) + ")[;:?\"!,.'>-]?(?=(?:\\p{Space}|$|>))" )
                    );
                    //System.out.println("Adding: "+lang+" -> "+((JsonString)v).getString());
                }
                hmStopWords.put(lang,currentStopwords);
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
     * Construct a StopWordFromStringBuffer instance
     */
    public StopWordFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY);
    }

    /**
     * Construct a StopWordFromStringBuffer instance given a language property
     *
     * @param langProp The propertie that stores the language of text
     */
    public StopWordFromStringBufferPipe(String langProp) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},new Class<?>[]{AbbreviationFromStringBufferPipe.class});
        
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
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
      if (carrier.getData() instanceof StringBuffer) {
          String lang = (String) carrier.getProperty(langProp);
          StringBuffer sb = (StringBuffer) carrier.getData();
          
          LinkedList<Pattern> setStopwords = hmStopWords.get(lang);
          
          if (setStopwords!=null){
            for (Pattern currentStopword:setStopwords){
                Matcher m = currentStopword.matcher(sb);
                int last=0;
                while (m.find(last)) {
                      last=m.start(1);
                      sb.replace(m.start(1),m.end(1),"");
                }
            }
          }
      }else{
        logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
      }
      return carrier;
    }
}
