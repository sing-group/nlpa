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
import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.EBoolean;
import org.bdp4j.util.Pair;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonReader;

//import com.vdurmont.emoji.EmojiManager;

/**
 * This pipe finds and eventually drops emojis The data of the instance should
 * contain a StringBuffer
 *
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
     * A hashset of emojis in english
     */
    //Cambiar mapa de Strings a Object porque es una array asociativo
    private static final HashMap<String,HashMap<String,Pair<Pattern, String>>> emojiDictionary = new HashMap<>();
    static{
        for(String i: new String[]{"emoji-data/emoji.en.json"}){
            //Preguntar por el parametro de la línea 81 de AbbreviationFromStringBufferPipe 
            String lang = i.substring(16,19).toUpperCase();
            //usar prints o logger?
            System.out.println("1er char: "+i.charAt(16)+"2o char: "+i.charAt(19)+"Lang= "+lang);
            //logger.info("1er char: "+i.charAt(16)+"2o char: "+i.charAt(19)+"Lang= "+lang);
            try{             
                InputStream is = FindEmojiInStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, Pair<Pattern, String>> dict = new HashMap<>();
                for(String emoji : jsonObject.keySet()){
                    //Duda: como construir el diccionario al tener 2 Strings dependientes de emoji?? --------------------------------
                    dict.put(emoji, new Pair<>(Pattern.compile(Pattern.quote(emoji)),jsonObject.getString(emoji)));
                } 

                emojiDictionary.put(lang,dict);
            }catch(Exception e){
                logger.error("Exception processing: " + i + " message " + e.getMessage());
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
    public static final boolean DEFAULT_REPLACE_EMOJI = true;

    /**
     * The default property name to store emojis
     */
    public static final String DEFAULT_EMOJI_PROPERTY = "emoji";

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
    private boolean replaceEmoji = DEFAULT_REPLACE_EMOJI;

    /**
     * Determines the input type for the data attribute of the Instance processed
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    public boolean isReplaceEmoji() {
        return replaceEmoji;
    }

    public void setReplaceEmoji(boolean replaceEmoji) {
        this.replaceEmoji = replaceEmoji;
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
     * Checks whether emojis should be removed
     *
     * @return True if emojis should be removed
     */
    public boolean getRemoveEmoji() {
        return this.removeEmoji;
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
     * Construct a FindEmojiInStringBufferPipe instance with the default
     * configuration value
     */
    public FindEmojiInStringBufferPipe() {
        this(DEFAULT_EMOJI_PROPERTY, EBoolean.getBoolean(DEFAULT_REMOVE_EMOJI), DEFAULT_LANG_PROPERTY, DEFAULT_REPLACE_EMOJI);
    }

    /**
     * Build a FindEmojiInStringBufferPipe that stores emojis of the
     * StringBuffer in the property emojiProp
     *
     * @param emojiProp The name of the property to store emojis
     * @param removeEmoji tells if emojis should be removed
     * @param replaceEmoji tells if emojis should be replaced by its meaning
     */
    public FindEmojiInStringBufferPipe(String emojiProp, boolean removeEmoji, String langProp, boolean replaceEmoji) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},
        new Class<?>[0]);

        this.emojiProp = emojiProp;
        this.removeEmoji = removeEmoji;
        this.langProp=langProp;
        this.replaceEmoji=replaceEmoji;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing emojis, adds a property and returns it. This is the method by which all pipes
     * are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            //POR DEFECTO  traducir=no, borrar=si y crear todas las propiedades
            //Ver foto pizarra
            String data = carrier.getData().toString();
            String value = "";
            for (String i : EmojiParser.extractEmojis(data)) {
    //Compute polatiry if needed, sumatorio y luego hacer media
                value += (i);   //guardar en propiedad si se especifica que lo haga
            }
            carrier.setProperty(emojiProp, value);

            String lang = (String) carrier.getProperty(langProp);
            HashMap<String, Pair<Pattern, String>> dict = emojiDictionary.get(lang);
            
            
            if(dict==null) return carrier; //When there is not a dictionary for the language
            if(replaceEmoji){
                for(String emoji: dict.keySet()){
                    /*TO DO sustitucion emoji por su significado,
                    necesario arreglar problema con inicializacion del diccionario
                    */
                    //el m.start(1) y m.end(1)  se refieren a la region 1 de la expresion regular
                }
            }else if (removeEmoji) {
                carrier.setData(new StringBuffer(EmojiParser.removeAllEmojis(data)));   //modificar por replace
            }   //else si quiero traducir el emoji
        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
