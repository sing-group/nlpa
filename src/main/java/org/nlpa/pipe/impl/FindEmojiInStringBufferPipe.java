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
import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.EBoolean;

//import com.vdurmont.emoji.EmojiManager;

/**
 * This pipe finds and eventually drops emojis The data of the instance should
 * contain a StringBuffer
 *
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class FindEmojiInStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(FindEmojiInStringBufferPipe.class);
    
    /**
     * The default value for removed emojis
     */
    public static final String DEFAULT_REMOVE_EMOJI = "yes";

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
     * Determines the input type for the data attribute of the Instance
     * processed
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
        this(DEFAULT_EMOJI_PROPERTY, EBoolean.getBoolean(DEFAULT_REMOVE_EMOJI));
    }

    /**
     * Build a FindEmojiInStringBufferPipe that stores emojis of the
     * StringBuffer in the property emojiProp
     *
     * @param emojiProp The name of the property to store emojis
     * @param removeEmoji tells if emojis should be removed
     */
    public FindEmojiInStringBufferPipe(String emojiProp, boolean removeEmoji) {
        super(new Class<?>[0],new Class<?>[0]);

        this.emojiProp = emojiProp;
        this.removeEmoji = removeEmoji;
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

            String data = carrier.getData().toString();
            String value = "";
            for (String i : EmojiParser.extractEmojis(data)) {
                value += (i);
            }
            carrier.setProperty(emojiProp, value);
            if (removeEmoji) {
                carrier.setData(new StringBuffer(EmojiParser.removeAllEmojis(data)));
            }

        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
