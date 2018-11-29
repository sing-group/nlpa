package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.ski4spam.Main;

import java.io.InputStream;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonString;

import java.util.LinkedList;
import org.bdp4j.pipe.PropertyComputingPipe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;
import org.bdp4j.util.Pair;

import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;
import org.ski4spam.util.EBoolean;

/**
 * This pipe drops interjections from texts The data of the instance should
 * contain a StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@PropertyComputingPipe()
public class InterjectionFromStringBufferPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(InterjectionFromStringBufferPipe.class);

    private static final HashMap<String, LinkedList<Pattern>> hmInterjections = new HashMap<>();

    static {
        for (String i : new String[]{"/interjections-json/interj.es.json",
            "/interjections-json/interj.en.json",
            "/interjections-json/interj.ru.json",
            "/interjections-json/interj.de.json",
            "/interjections-json/interj.pt.json",
            "/interjections-json/interj.fr.json",
            "/interjections-json/interj.gl.json",
            "/interjections-json/interj.eu.json"}) {

            String lang = i.substring(27, 29).toUpperCase();
            try {
                InputStream is = Main.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonArray array = rdr.readArray();
                LinkedList<Pattern> setWords = new LinkedList<>();

                for (JsonValue v : array) {
                    setWords.add(Pattern.compile("(?:[\\p{Space}]|^)([¡]?(" + Pattern.quote(((JsonString) v).getString()) + ")[!]?)(?:[\\p{Space}]|$)"));
                }

                hmInterjections.put(lang, setWords);

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
     * Indicates if interjections should be removed from data
     */
    private boolean removeInterjection = false;

    /**
     * The default value for removed interjections
     */
    public static final String DEFAULT_REMOVE_INTERJECTION = "no";

    /**
     * The default property name to store interjections
     */
    public static final String DEFAULT_INTERJECTION_PROPERTY = "interjection";

    /**
     * The property name to store interjections
     */
    private String interjectionProp = DEFAULT_INTERJECTION_PROPERTY;

    /**
     * Indicates if interjection should be removed from data
     *
     * @param removeInterjection False if interjections shouldn't be removed
     */
    @PipeParameter(name = "removeInterjection", description = "Indicates if the interjections should be removed or not", defaultValue = DEFAULT_REMOVE_INTERJECTION)
    public void setRemoveInterjection(String removeInterjection) {
        this.removeInterjection = EBoolean.parseBoolean(removeInterjection);
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
     * Indicates if interjections should be removed
     *
     * @param removeInterjection True if interjecions should be removed
     */
    public void setRemoveInterjection(boolean removeInterjection) {
        this.removeInterjection = removeInterjection;
    }

    /**
     * Checks whether interjections should be removed
     *
     * @return True if interjections should be removed
     */
    public boolean getRemoveInterjection() {
        return this.removeInterjection;
    }

    /**
     * Sets the property where interjections will be stored
     *
     * @param interjectionProp the name of the property for interjections
     */
    @PipeParameter(name = "interjectionpropname", description = "Indicates the property name to store interjections", defaultValue = DEFAULT_INTERJECTION_PROPERTY)
    public void setInterjectionProp(String interjectionProp) {
        this.interjectionProp = interjectionProp;
    }

    /**
     * Retrieves the property name for storing interjections
     *
     * @return String containing the property name for storing interjections
     */
    public String getInterjectionProp() {
        return this.interjectionProp;
    }

    /**
     * Construct a InterjectionFromStringBuffer instance with the default
     * configuration value
     */
    public InterjectionFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY, DEFAULT_INTERJECTION_PROPERTY, false);
    }

    /**
     * Construct a InterjectionFromStringBuffer instance given a language
     * property that stores interjections of the StringBuffer in the property
     * hashtagProp
     *
     * @param langProp The propertie that stores the language of text
     * @param interjectionProp The name of the property to store interjections
     * @param removeInterjection tells if hashtags should be removed
     */
    public InterjectionFromStringBufferPipe(String langProp, String interjectionProp, boolean removeInterjection) {
        this.langProp = langProp;
        this.interjectionProp = interjectionProp;
        this.removeInterjection = removeInterjection;
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
            StringBuffer data = new StringBuffer(carrier.getData().toString());
            String value = "";

            LinkedList<Pattern> setWords = hmInterjections.get(lang);
            if (setWords == null) {
                carrier.setProperty(interjectionProp, value);
                return carrier;
            }

            for (Pattern interej : setWords) {
                Matcher m = interej.matcher(data);

                Stack<Pair<Integer, Integer>> replacements = new Stack<>();

                while (m.find()) {
                    value += m.group(1) + " -- ";
                    if (removeInterjection) replacements.push(new Pair<>(m.start(1), m.end(1)));
                }

                if (removeInterjection){
                   while (!replacements.empty()) {
                       Pair<Integer, Integer> current = replacements.pop();
                       data = data.replace(current.getObj1(),current.getObj2(),"");
                   }
                }
            }

            if (removeInterjection) {
                carrier.setData(data);
            }

            carrier.setProperty(interjectionProp, value);

        } else {
            logger.error("Data should be an StrinBuffer when processing " + carrier.getName() + " but is a " + carrier.getData().getClass().getName());
        }
        return carrier;
    }

}
