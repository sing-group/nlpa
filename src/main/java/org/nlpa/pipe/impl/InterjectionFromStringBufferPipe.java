/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.EBoolean;

import javax.json.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bdp4j.pipe.Pipe;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe drops (or not) interjections from texts and adds an Instance property. 
 * The data of the instance should contain a StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class InterjectionFromStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(InterjectionFromStringBufferPipe.class);

    /**
     * Hashmap containing interjections
     */
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
                InputStream is = InterjectionFromStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonArray array = rdr.readArray();
                LinkedList<Pattern> setWords = new LinkedList<>();

                for (JsonValue v : array) {
                    setWords.add(Pattern.compile("(?:[\\p{Space}]|[\"><¡?¿!;:,.'-]|^)([¡]*(" + Pattern.quote(((JsonString) v).getString()) + ")[!]*)[;:?\"!,.'>-]?(?=(?:[\\p{Space}]|$|>))"));
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
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
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
     * Establish the name of the property where the language will be stored
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
     * @param removeInterjection True if interjections should be removed
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
     * interjectionProp
     *
     * @param langProp The propertie that stores the language of text
     * @param interjectionProp The name of the property to store interjections
     * @param removeInterjection tells if interjection should be removed
     */
    public InterjectionFromStringBufferPipe(String langProp, String interjectionProp, boolean removeInterjection) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},new Class<?>[0]);
        
        this.langProp = langProp;
        this.interjectionProp = interjectionProp;
        this.removeInterjection = removeInterjection;
    }

    /**
     * Process an Instance. This method takes an input Instance, 
     * drops interjections, adds an instance property, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            String lang = (String) carrier.getProperty(langProp);
            StringBuffer sb = (StringBuffer) carrier.getData();
            String value = "";

            LinkedList<Pattern> setWords = hmInterjections.get(lang);
            if (setWords != null) {
               for (Pattern interej :setWords){
                    Matcher m = interej.matcher(sb);
                    int last=0;
                    
                    if (m.find(last)){
                       value += m.group(1) + " -- "; 
                       if (removeInterjection){
                           while (m.find(last)){
                                last=m.start(1);
                                sb.replace(m.start(1),m.end(1),"");
                           }
                       }                       
                    }
                }
            }
            carrier.setProperty(interjectionProp, value);
        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }

}
