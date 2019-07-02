package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.bdp4j.util.EBoolean;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe finds and eventually drops emoticons The data of the instance
 * should contain a StringBuffer
 *
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class FindEmoticonInStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(FindEmoticonInStringBufferPipe.class);

    /**
     * Pattern for detecting emoticons
     */
    private static final Pattern emoticonPattern = Pattern.compile("(\\:\\w+\\:|\\<[\\/\\\\]?3|[\\(\\)\\\\\\D|\\*\\$][\\-\\^]?[\\:\\;\\=]|[\\:\\;\\=B8][\\-\\^]?[3DOPp\\@\\$\\*\\\\\\)\\(\\/\\|])(?=\\s|[\\!\\.\\?]|$)");

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
     * Indicates if emoticons should be removed from data
     */
    private boolean removeEmoticon = true;

    /**
     * The default value for removed emoticons
     */
    public static final String DEFAULT_REMOVE_EMOTICON = "yes";

    /**
     * The default property name to store emoticons
     */
    public static final String DEFAULT_EMOTICON_PROPERTY = "emoticon";

    /**
     * The property name to store emoticons
     */
    private String emoticonProp = DEFAULT_EMOTICON_PROPERTY;

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

    /**
     * Checks whether emoticons should be removed
     *
     * @return True if emoticons should be removed
     */
    public boolean getRemoveEmoticon() {
        return this.removeEmoticon;
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
     * Will return true if s contains emoticons.
     *
     * @param s String to test
     * @return true if string contains emoticon
     */
    public static boolean isEmoticon(String s) {
        boolean ret = false;
        if (s != null) {
            ret = emoticonPattern.matcher(s).find();
        }
        return ret;
    }

    /**
     * Construct a StripEmoticonFromStringBufferPipe instance with the default
     * configuration value
     */
    public FindEmoticonInStringBufferPipe() {
        this(DEFAULT_EMOTICON_PROPERTY, true);
    }

    /**
     * Build a StripEmoticonFromStringBufferPipe that stores emoticons of the
     * StringBuffer in the property emoticonProp
     *
     * @param emoticonProp The name of the property to store emoticons
     * @param removeEmoticon tells if emoticons should be removed
     */
    public FindEmoticonInStringBufferPipe(String emoticonProp, boolean removeEmoticon) {
        super(new Class<?>[0],new Class<?>[]{FindHashtagInStringBufferPipe.class});

        this.emoticonProp = emoticonProp;
        this.removeEmoticon = removeEmoticon;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing emoticons, and returns it. This is the method by which all pipes
     * are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {

            String data = carrier.getData().toString();
            Stack<Pair<Integer, Integer>> replacements = new Stack<>();

            String value = "";

            if (isEmoticon(data)) {
                Matcher m = emoticonPattern.matcher(data);

                while (m.find()) {
                    value += m.group(1) + " ";
                    if (removeEmoticon) {
                        replacements.push(new Pair<>(m.start(1), m.end(1)));
                    }
                }

                while (!replacements.empty()) {
                    Pair<Integer, Integer> current = replacements.pop();
                    data = (current.getObj1() > 0 ? data.substring(0, current.getObj1()) : "")
                            + //if startindex is 0 do not concatenate
                            (current.getObj2() < (data.length() - 1) ? data.substring(current.getObj2()) : ""); //if endindex=newSb.length()-1 do not concatenate
                }

                if (removeEmoticon) {
                    carrier.setData(new StringBuffer(data));
                }
            } else {
                logger.info("Emoticon not found for instance " + carrier.toString());
            }

            carrier.setProperty(emoticonProp, value);

        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
