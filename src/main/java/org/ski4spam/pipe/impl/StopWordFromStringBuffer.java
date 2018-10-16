package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.pipe.PipeParameter;
import org.ski4spam.Main;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonString;

import java.util.HashSet;
import java.util.StringTokenizer;

import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe drops stopwords from texts The data of the instance should contain
 * a StringBuffer without HTML Tags
 *
 * @author José Ramón Méndez
 */
@TransformationPipe()
public class StopWordFromStringBuffer extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(StopWordFromStringBuffer.class);

    /**
     * A hashset of stopwords in different languages thanks to
     * https://www.npmjs.com/package/stopwords-json
     */
    private static final HashSet<String> htStopwords = new HashSet<>();

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
                InputStream is = Main.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonArray array = rdr.readArray();

                for (JsonValue v : array) {
                    htStopwords.add(lang + ((JsonString)v).getString());
                    //System.out.println("Adding: "+lang+((JsonString)v).getString());
                }
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
     * Construct a StopWordFromStringBuffer instance
     */
    public StopWordFromStringBuffer() {
    }

    /**
     * Construct a StopWordFromStringBuffer instance given a language property
     *
     * @param langProp The propertie that stores the language of text
     */
    public StopWordFromStringBuffer(String langProp) {
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
            StringBuffer newSb = new StringBuffer();
            String lang = (String) carrier.getProperty(langProp);
            String data = carrier.getData().toString();
            StringTokenizer st = new StringTokenizer(data, " \t\n\r\u000b\f");
            while (st.hasMoreTokens()) {
                String current = st.nextToken();
					 String currentFixed=(new String (current)).replaceAll("[^a-zA-Z0-9]", "");
					 //System.out.print("Replacing "+current+" codified as (" + currentFixed +") by searching for "+lang+currentFixed+"...");
                if (!htStopwords.contains(lang + currentFixed)) {
                    newSb.append(current + " ");
						 //System.out.println("kept");						  
                }/*else{
						 System.out.println("dropped");
                }*/
            }
            carrier.setData(newSb);
        }

        return carrier;
    }
}
