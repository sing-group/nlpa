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
import javax.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.EBoolean;
import org.nlpa.util.Trio;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonReader;



/**
 * This pipe finds and eventually drops emojis The data of the instance should
 * contain a StringBuffer
 *
 * @author José Ramón Méndez
 * @author Rodrigo Currás Ferradás
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class FindEmojiInStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(FindEmojiInStringBufferPipe.class);

    /**
     * A hashset of emojis in different languages
     */
    private static final HashMap<String, HashMap<String, Trio<Pattern, String, Double>>> emojiDictionary = new HashMap<>();
    static {
        for (String i : new String[] { "/emoji-data/emojisID.de.json",
                                       "/emoji-data/emojisID.en.json",
                                       "/emoji-data/emojisID.es.json",
                                       "/emoji-data/emojisID.fr.json",
                                       "/emoji-data/emojisID.it.json",
                                       "/emoji-data/emojisID.pt.json",
                                       "/emoji-data/emojisID.ru.json"
                                    }) {
            String lang = i.substring(21, 23).toUpperCase();

            try {
                System.setProperty("file.encoding", "UTF-16LE");
                InputStream is = FindEmojiInStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, Trio<Pattern, String, Double>> dict = new HashMap<>();
                for (String emoji : jsonObject.keySet()) {
                    dict.put(emoji, new Trio<>(Pattern.compile(Pattern.quote(emoji)), jsonObject.getJsonObject(emoji).getString("synsetID"),
                           jsonObject.getJsonObject(emoji).getJsonNumber("polarity").doubleValue()));
                }

                emojiDictionary.put(lang, dict);

            } catch (Exception e) {
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
     * The default value for removed emojis
     */
    public static final String DEFAULT_REMOVE_EMOJI = "no";

    /**
     * The default value for replace emoji
     */
    public static final String DEFAULT_REPLACE_EMOJI = "yes";

    /**
     * The default property name to store emojis
     */
    public static final String DEFAULT_EMOJI_PROPERTY = "emoji";

    /**
     * The default value for calculate polarity
     */
    public static final String DEFAULT_CALCULATE_POLARITY = "yes";

    /**
     * Indicates if emojis should be removed from data
     */
    private boolean removeEmoji = EBoolean.getBoolean(DEFAULT_REMOVE_EMOJI);

    /**
     * The property name to store emojis
     */
    private String emojiProp = DEFAULT_EMOJI_PROPERTY;

    /**
     * The property that indicates if emojis are replaced by its meaning
     */
    private boolean replaceEmoji = EBoolean.getBoolean(DEFAULT_REPLACE_EMOJI);

    /**
     * The property that indicates if polarity from emojis should be calculated
     */
    private boolean calculatePolarity = EBoolean.getBoolean(DEFAULT_CALCULATE_POLARITY);

    /**
     * Determines the input type for the data attribute of the Instance processed
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
     *         processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates if emoji should be removed from data
     *
     * @param removeEmoji True if emojis should be removed
     */
    @PipeParameter(name = "removeEmoji", description = "Indicates if the emojis should be removed or not", defaultValue = DEFAULT_REMOVE_EMOJI)
    public void setRemoveEmoji(String removeEmoji) {
        this.removeEmoji = EBoolean.parseBoolean(removeEmoji);
    }

    /**
     * Indicates if emojis should be removed
     *
     * @param removeEmoji True if emojis should be removed
     */
    public void setRemoveEmoji(boolean removeEmoji) {
        this.removeEmoji = removeEmoji;
    }
    
    /**
     * Checks whether emojis should be removed
     *
     * @return True if emojis should be removed
     */
    public boolean getRemoveEmoji() {
        return this.removeEmoji;
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
     * Sets the property where emojis will be stored
     *
     * @param emojiProp the name of the property for emojis
     */
    @PipeParameter(name = "emojipropname", description = "Indicates the property name to store emojis", defaultValue = DEFAULT_EMOJI_PROPERTY)
    public void setEmojiProp(String emojiProp) {
        this.emojiProp = emojiProp;
    }

    /**
     * Retrieves the property name for storing emojis
     *
     * @return String containing the property name for storing emojis
     */
    public String getEmojiProp() {
        return this.emojiProp;
    }

    /**
     * Indicates if emojis should be replaced
     * 
     * @return True if emojis should be replaced
     */
    public boolean getReplaceEmoji() {
        return replaceEmoji;
    }

    /**
     * Indicates if emoji should be removed from data
     *
     * @param removeEmoji True if emojis should be removed
     */
    @PipeParameter(name = "replaceEmoji", description = "Indicates if the emojis should be removed or not", defaultValue = DEFAULT_REPLACE_EMOJI)
    public void setReplaceEmoji(String replaceEmoji) {
        this.replaceEmoji = EBoolean.parseBoolean(replaceEmoji);
    }
    
    /**
     * Indicates if emojis should be replaced
     *
     * @param replaceEmoji True if emojis should be repplaced
     */
    public void setReplaceEmoji(boolean replaceEmoji) {
        this.replaceEmoji = replaceEmoji;
    }

    @PipeParameter(name="calculatePolarity", description="Indicates if polarity of emoticons should be calculated or not", defaultValue = DEFAULT_CALCULATE_POLARITY)
    public void setCalculatePolarity(String calculatePolarity){
        this.calculatePolarity = EBoolean.getBoolean(calculatePolarity);
    }

    /**
     * Indicates if emoji polarity should be calculated
     * @param calculatePolarity True if polarity should be calculated
     */
    public void setCalculatePolarity(boolean calculatePolarity){
        this.calculatePolarity = calculatePolarity;
    }
    
    /**
     * @return True if polarity should be calculated
     */
    public boolean getCalculatePolarity() {
        return calculatePolarity;
    }

    /**
     * Construct a FindEmojiInStringBufferPipe instance with the default
     * configuration value
     */
    public FindEmojiInStringBufferPipe() {
        this(DEFAULT_EMOJI_PROPERTY, EBoolean.getBoolean(DEFAULT_REMOVE_EMOJI), DEFAULT_LANG_PROPERTY,
                EBoolean.getBoolean(DEFAULT_REPLACE_EMOJI), EBoolean.getBoolean(DEFAULT_CALCULATE_POLARITY));
    }

    /**
     * Build a FindEmojiInStringBufferPipe that stores emojis of the StringBuffer in
     * the property emojiProp
     *
     * @param emojiProp    The name of the property to store emojis
     * @param removeEmoji  tells if emojis should be removed
     * @param langProp The language of the text
     * @param replaceEmoji tells if emojis should be replaced by its meaning
     * @param calculatePolarity tells if emoji polarity should be calculated
     */
    public FindEmojiInStringBufferPipe(String emojiProp, boolean removeEmoji, String langProp,
            boolean replaceEmoji, boolean calculatePolarity) {
        super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class }, new Class<?>[0]);

        this.emojiProp = emojiProp;
        this.removeEmoji = removeEmoji;
        this.langProp = langProp;
        this.replaceEmoji = replaceEmoji;
        this.calculatePolarity = calculatePolarity;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing emojis, adds a property and returns it. This is the method by which
     * all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            String data = carrier.getData().toString();

            System.setProperty("file.encoding", "UTF-16LE");
            try (FileOutputStream fw=new FileOutputStream("xx.txt")){
                fw.write(data.getBytes("UTF-16LE"));
                fw.flush();
            }catch(Exception e){
                System.err.println(e.getMessage());
            }

            String value = "";

            String lang = (String) carrier.getProperty(langProp);
            HashMap<String, Trio<Pattern, String, Double>> dict = emojiDictionary.get(lang);

           
            if (dict == null){
                logger.info("Language " + carrier.getProperty(langProp) + " not supported when processing " + carrier.getName() + " in FindEmojiInStringBufferPipe");
                carrier.setProperty(emojiProp, "");
            	carrier.setProperty("emojiPolarity", 0); 
                return carrier; // When there is not a dictionary for the language
            }
            
            
            StringBuffer sb = (StringBuffer) carrier.getData();
            if (replaceEmoji) {
                for (String emoji : dict.keySet()) {

                    Pattern pat = dict.get(emoji).getObj1();
                    Matcher match = pat.matcher(sb);
                    int last = 0;
                    while (match.find(last)) {
                        last = match.start(0) + 1;
                        // Now replaces emoji pattern by its meaning
                        value += emoji;
                        sb = sb.replace(match.start(0), match.end(0), dict.get(emoji).getObj2());      
                    }

                    

                }
            } else if (removeEmoji) {
                System.out.println("REMOVE");
                for (String emoji : dict.keySet()) {
                    Pattern pat = dict.get(emoji).getObj1();
                    Matcher match = pat.matcher(sb);
                    int last = 0;
                    while (match.find(last)) {
                        value += emoji;
                        last = match.start(0) + 1;
                        // Now deletes emojis
                        sb = sb.replace(match.start(0), match.end(0), "");
                    }
                }
            }
            carrier.setProperty(emojiProp, value);

            if (calculatePolarity) {
                double score = 0;
                int numEmojis = 0;
                for (String emoji : dict.keySet()) {
                    Pattern emojiInPattern = dict.get(emoji).getObj1();
                    Matcher match = emojiInPattern.matcher(data);
                    int last = 0;
                    while (match.find(last)) {
                        last = match.start(0) + 1;
                        score += dict.get(emoji).getObj3();
                        numEmojis++;
                        System.out.println("score=" + score);
                    }
                    
                }
                //Calculate arithmetic mean and store in a property
                Double mean = score / (new Double(numEmojis));
                carrier.setProperty("emojiPolarity", mean);
            }

            try (FileOutputStream fw2=new FileOutputStream("xxResult.txt")){
                fw2.write(sb.toString().getBytes("UTF-16LE"));
                fw2.flush();
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
            
        } else {
            logger.error("Data should be an StrinBuffer when processing " + carrier.getName() + " but is a "
                    + carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
