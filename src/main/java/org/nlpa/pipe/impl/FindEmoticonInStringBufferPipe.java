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

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.nlpa.util.Trio;
import org.bdp4j.util.EBoolean;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.bdp4j.pipe.Pipe;

/**
 * This pipe finds and eventually drops emoticons The data of the instance
 * should contain a StringBuffer
 *
 * @author José Ramón Méndez
 * @author Rodrigo Currás Ferradás
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class FindEmoticonInStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(FindEmoticonInStringBufferPipe.class);

    /**
     *  A hashset of emoticons in different languages.
     *  The data structure includes polarity and sysnsetID for each emoticon
     */
    private static final HashMap<String, HashMap<String, Trio<Pattern, String, Double>>> emoticonDictionary = new HashMap<>();
    static {
        for (String i : new String[] { "/emoticon-data/emoticonsID.de.json",
                                       "/emoticon-data/emoticonsID.en.json",
                                       "/emoticon-data/emoticonsID.es.json",
                                       "/emoticon-data/emoticonsID.fr.json",
                                       "/emoticon-data/emoticonsID.it.json",
                                       "/emoticon-data/emoticonsID.pt.json",
                                       "/emoticon-data/emoticonsID.ru.json"
                                    }) {
        String lang = i.substring(27, 29).toUpperCase();

        try{

            System.setProperty("file.encoding", "UTF-16LE");
            InputStream is = FindEmoticonInStringBufferPipe.class.getResourceAsStream(i);
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            HashMap<String, Trio<Pattern, String, Double>> dict = new HashMap<>();
            for(String emoticon: jsonObject.keySet()){
                dict.put(emoticon,
                        new Trio<>(Pattern.compile(Pattern.quote(emoticon)),
                                jsonObject.getJsonObject(emoticon).getString("synsetID"),
                                jsonObject.getJsonObject(emoticon).getJsonNumber("polarity").doubleValue()));
            }
            emoticonDictionary.put(lang, dict);

        }catch (Exception e) {
                logger.error("Exception processing: " + i + " message " + e.getMessage());
                e.printStackTrace();
            }                        
        }
    }

    /**
     * The name of the property where the language is stored
     */
    private String langProp = DEFAULT_LANG_PROPERTY;
        
    /**
     * The default value for removed emoticons
     */
    public static final String DEFAULT_REMOVE_EMOTICON = "no";

    /**
     * The default property name to store emoticons
     */
    public static final String DEFAULT_EMOTICON_PROPERTY = "emoticon";

    /**
     * The default value for replaced emoticons
     */
    public static final String DEFAULT_REPLACE_EMOTICON = "yes";

    /**
     * The default value for computing polarity
     */
    public static final String DEFAULT_CALCULATE_POLARITY = "yes";

    /**
     * Indicates if emoticons should be removed from data
     */
    private boolean removeEmoticon = EBoolean.getBoolean(DEFAULT_REMOVE_EMOTICON);

    /**
     * The property name to store emoticons
     */
    private String emoticonProp = DEFAULT_EMOTICON_PROPERTY;

    /**
     * Indicates if emoticons should be replaced 
     */
    private boolean replaceEmoticon = EBoolean.getBoolean(DEFAULT_REPLACE_EMOTICON);

    /**
     * Indicates if polarity should be calculated
     */
    private boolean calculatePolarity = EBoolean.getBoolean(DEFAULT_CALCULATE_POLARITY);

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
     * Indicates if emoticon should be removed from data
     *
     * @param removeEmoticon True if emoticons should be removed
     */
    @PipeParameter(name = "removeEmoticon", description = "Indicates if the emoticons should be removed or not", defaultValue = DEFAULT_REMOVE_EMOTICON)
    public void setRemoveEmoticon(String removeEmoticon) {
        this.removeEmoticon = EBoolean.parseBoolean(removeEmoticon);
    }

    /**
     * Indicates if emoticons should be removed
     *
     * @param removeEmoticon True if emoticons should be removed
     */
    public void setRemoveEmoticon(boolean removeEmoticon) {
        this.removeEmoticon = removeEmoticon;
    }

    @PipeParameter(name="replaceEmoticon", description = "Indicates if emoticons should be replaced or not", defaultValue = DEFAULT_REPLACE_EMOTICON)
    public void setReplaceEmoticon(String replaceEmoticon){
        this.replaceEmoticon = EBoolean.getBoolean(replaceEmoticon);
    }

    /**
     * Indicates if emoticons should be replaced
     * @param replaceEmoticon True if emoticons should be replaced
     */
    public void setReplaceEmoticon(boolean replaceEmoticon){
        this.replaceEmoticon = replaceEmoticon;
    }

    @PipeParameter(name="calculatePolarity", description="Indicates if polarity of emoticons should be calculated or not", defaultValue = DEFAULT_CALCULATE_POLARITY)
    public void setCalculatePolarity(String calculatePolarity){
        this.calculatePolarity = EBoolean.getBoolean(calculatePolarity);
    }

    /**
     * Indicates if emoticon polarity should be calculated
     * @param calculatePolarity True if polarity should be calculated
     */
    public void setCalculatePolarity(boolean calculatePolarity){
        this.calculatePolarity = calculatePolarity;
    }

    /**
     * Checks whether emoticons should be removed
     *
     * @return True if emoticons should be removed
     */
    public boolean getRemoveEmoticon() {
        return this.removeEmoticon;
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
     * Returns the name of the property in which the language is stored
     *
     * @return the name of the property where the language is stored
     */
    public String getLangProp() {
        return this.langProp;
    }

    /**
     * Checks wether emoticons should be replaced
     * 
     * @return True if emoticons should be replaced
     */
    public boolean getReplaceEmoticon(){
        return this.replaceEmoticon;
    }

    /**
     * Checks wether emoticons polarity should be calculated
     * @return True if polarity should be calculated
     */
    public boolean getCalculatePolarity(){
        return this.calculatePolarity;
    }

    /**
     * Sets the property where emoticons will be stored
     *
     * @param emoticonProp the name of the property for emoticons
     */
    @PipeParameter(name = "emoticonpropname", description = "Indicates the property name to store emoticons", defaultValue = DEFAULT_EMOTICON_PROPERTY)
    public void setEmoticonProp(String emoticonProp) {
        this.emoticonProp = emoticonProp;
    }

    /**
     * Retrieves the property name for storing emoticons
     *
     * @return String containing the property name for storing emoticons
     */
    public String getEmoticonProp() {
        return this.emoticonProp;
    }


    /**
     * Default constructor. Construct a FindEmoticonInStringBufferPipe instance with the default
     * configuration value
     */
    public FindEmoticonInStringBufferPipe() {
        this(DEFAULT_EMOTICON_PROPERTY, EBoolean.getBoolean(DEFAULT_REMOVE_EMOTICON), DEFAULT_LANG_PROPERTY,
         EBoolean.getBoolean(DEFAULT_REPLACE_EMOTICON), EBoolean.getBoolean(DEFAULT_CALCULATE_POLARITY));
    }

    /**
     * Build a FindEmoticonInStringBufferPipe that stores emoticons of the
     * StringBuffer in the property emoticonProp
     *
     * @param emoticonProp The name of the property to store emoticons
     * @param removeEmoticon tells if emoticons should be removed
     * @param replaceEmoticon tells if emoticons should be replaced
     * @param calculatePolarity tells if polarity should be calculated
     */
    public FindEmoticonInStringBufferPipe(String emoticonProp, boolean removeEmoticon,
     String lang, boolean replaceEmoticon, boolean calculatePolarity) {
        super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class },new Class<?>[]{FindHashtagInStringBufferPipe.class});

        this.emoticonProp = emoticonProp;
        this.removeEmoticon = removeEmoticon;
        this.replaceEmoticon = replaceEmoticon;
        this.calculatePolarity = calculatePolarity;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing emoticons, adds a property and returns it. This is the method by which all pipes
     * are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {

            String data = carrier.getData().toString();
            String value = "";
            String lang = (String)carrier.getProperty(langProp);

            System.setProperty("file.encoding", "UTF-16LE");
            try (FileOutputStream fw=new FileOutputStream("xxEmoti.txt")){
                fw.write(data.getBytes("UTF-16LE"));
                fw.flush();
            }catch(Exception e){
                System.err.println(e.getMessage());
            }

            HashMap<String, Trio<Pattern, String, Double>> dict = emoticonDictionary.get(lang);

            if (dict == null){
                logger.info("Language " + carrier.getProperty(langProp) + " not supported when processing " + carrier.getName() + " in FindEmoticonInStringBufferPipe");
                carrier.setProperty(emoticonProp, "");
                carrier.setProperty("emoticonPolarity", 0);
                return carrier; // When there is not a dictionary for the language
            }

            StringBuffer sb = (StringBuffer) carrier.getData();
            if(replaceEmoticon){
                for(String emoticon: dict.keySet()){
                    Pattern pat = dict.get(emoticon).getObj1();
                    Matcher match = pat.matcher(sb);
                    int last = 0; 

                    while(match.find(last)){
                        last = match.start(0) + 1;
                        // Now replaces emoticon pattern by its meaning
                        value += emoticon;
                        sb = sb.replace(match.start(0), match.end(0), dict.get(emoticon).getObj2());
                    }

                }
            }else if(removeEmoticon){
                for(String emoticon: dict.keySet()){
                    Pattern pat = dict.get(emoticon).getObj1();
                    Matcher match = pat.matcher(sb);
                    int last = 0; 

                    while(match.find(last)){
                        last = match.start(0) + 1;
                        // Now deletes emoticon pattern from text
                        value += emoticon;
                        sb = sb.replace(match.start(0), match.end(0), "");
                    }

                }
            }

            carrier.setProperty(emoticonProp, value);

            if(calculatePolarity){
                double score = 0;
                int numEmoticons = 0;

                for(String emoticon: dict.keySet()){
                    Pattern emoticonInPattern = dict.get(emoticon).getObj1();
                    Matcher match = emoticonInPattern.matcher(data);
                    int last = 0;
                    while (match.find(last)) {
                        last = match.start(0) + 1;
                        score += dict.get(emoticon).getObj3();
                        numEmoticons++;
                    }

                }
                //Calculate arithmetic mean and store in a property
                Double mean = score / (new Double(numEmoticons));
                carrier.setProperty("emoticonPolarity", mean);
            }
            System.out.println("DATA: ");
            System.out.println(sb.toString());

        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
