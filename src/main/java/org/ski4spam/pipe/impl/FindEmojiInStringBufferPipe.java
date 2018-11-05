package org.ski4spam.pipe.impl;

import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.ski4spam.util.EBoolean;
import org.ski4spam.util.Pair;

//import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

/**
 * This pipe finds and eventually drops emojis The data of the instance should
 * contain a StringBuffer
 *
 * @author José Ramón Méndez
 */
@PropertyComputingPipe()
public class FindEmojiInStringBufferPipe extends Pipe {

    private static final Logger logger = LogManager.getLogger(FindEmojiInStringBufferPipe.class);

    /**
     * Determines the input type for the data attribute of the Instances
     * processed
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
     * Indicates if emojis should be removed from data
     */
    private boolean removeEmoji = true;

    /**
     * The default value for removed emojis
     */
    public static final String DEFAULT_REMOVE_EMOJI = "yes";

    /**
     * The default property name to store emojis
     */
    public static final String DEFAULT_EMOJI_PROPERTY = "emoji";

    /**
     * The property name to store emojis
     */
    private String emojiProp = DEFAULT_EMOJI_PROPERTY;

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
     * Construct a StripEmojiFromStringBufferPipe instance with the default
     * configuration value
     */
    public FindEmojiInStringBufferPipe() {
        this(DEFAULT_EMOJI_PROPERTY, true);
    }

    /**
     * Build a StripEmojiFromStringBufferPipe that stores emojis of the
     * StringBuffer in the property emojiProp
     *
     * @param emojiProp The name of the property to store emojis
     * @param removeEmoji tells if emojis should be removed
     */
    public FindEmojiInStringBufferPipe(String emojiProp, boolean removeEmoji) {
        this.emojiProp = emojiProp;
        this.removeEmoji = removeEmoji;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing emojis, and returns it. This is the method by which all pipes
     * are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
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

        }
        return carrier;
    }
}
