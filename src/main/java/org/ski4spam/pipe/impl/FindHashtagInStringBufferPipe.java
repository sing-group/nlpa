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
import org.bdp4j.util.Pair;

/**
 * This pipe drops hashtags The data of the instance should contain a
 * StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@PropertyComputingPipe()
public class FindHashtagInStringBufferPipe extends Pipe {

    private static final Logger logger = LogManager.getLogger(FindHashtagInStringBufferPipe.class);

    /*  NOTE:
   \p{Punct}-[_] 
	  is equivalent to:
                         !\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~.-
     */
    private static final Pattern hashtagPattern = Pattern.compile("(?:\\s|^|[\"¿¡])(#[^\\p{Cntrl}\\p{Space}!\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~.-]+)[;:?\"!,.]?(?=(?:\\s|$))");

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
     * Indicates if hashtags should be removed from data
     */
    private boolean removeHashtag = true;

    /**
     * The default value for removed hashtags
     */
    public static final String DEFAULT_REMOVE_HASHTAG = "yes";

    /**
     * The default property name to store hashtags
     */
    public static final String DEFAULT_HASHTAG_PROPERTY = "hashtag";

    /**
     * The property name to store hashtags
     */
    private String hashtagProp = DEFAULT_HASHTAG_PROPERTY;

    /**
     * Indicates if hashtag should be removed from data
     *
     * @param removeHashtag True if hashtags should be removed
     */
    @PipeParameter(name = "removeHashtag", description = "Indicates if the hashtags should be removed or not", defaultValue = DEFAULT_REMOVE_HASHTAG)
    public void setRemoveHashtag(String removeHashtag) {
        this.removeHashtag = EBoolean.parseBoolean(removeHashtag);
    }

    /**
     * Indicates if hashtags should be removed
     *
     * @param removeHashtag True if hashtags should be removed
     */
    public void setRemoveHashtag(boolean removeHashtag) {
        this.removeHashtag = removeHashtag;
    }

    /**
     * Checks whether hashtags should be removed
     *
     * @return True if hashtags should be removed
     */
    public boolean getRemoveHashtag() {
        return this.removeHashtag;
    }

    /**
     * Sets the property where hashtags will be stored
     *
     * @param hashtagProp the name of the property for hashtags
     */
    @PipeParameter(name = "hashtagpropname", description = "Indicates the property name to store hashtags", defaultValue = DEFAULT_HASHTAG_PROPERTY)
    public void setHashtagProp(String hashtagProp) {
        this.hashtagProp = hashtagProp;
    }

    /**
     * Retrieves the property name for storing hashtags
     *
     * @return String containing the property name for storing hashtags
     */
    public String getHashtagProp() {
        return this.hashtagProp;
    }

    /**
     * Will return true if s contains hashtags.
     *
     * @param s String to test
     * @return true if string contains hashtag
     */
    public static boolean isHashtag(String s) {
        boolean ret = false;
        if (s != null) {
            ret = hashtagPattern.matcher(s).find();
        }
        return ret;
    }

    /**
     * Construct a StripHashtagFromStringBufferPipe instance with the default
     * configuration value
     */
    public FindHashtagInStringBufferPipe() {
        this(DEFAULT_HASHTAG_PROPERTY, true);
    }

    /**
     * Build a StripHashtagFromStringBufferPipe that stores hashtags of the
     * StringBuffer in the property hashtagProp
     *
     * @param hashtagProp The name of the property to store hashtags
     * @param removeHashtag tells if hashtags should be removed
     */
    public FindHashtagInStringBufferPipe(String hashtagProp, boolean removeHashtag) {
        this.hashtagProp = hashtagProp;
        this.removeHashtag = removeHashtag;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing hashtags, and returns it. This is the method by which all pipes
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

            if (isHashtag(data)) {
                Matcher m = hashtagPattern.matcher(data);

                while (m.find()) {
                    value += m.group(1) + " ";
                    if (removeHashtag) {
                        replacements.push(new Pair<>(m.start(1), m.end(1)));
                    }
                }

                while (!replacements.empty()) {
                    Pair<Integer, Integer> current = replacements.pop();
                    data = (current.getObj1() > 0 ? data.substring(0, current.getObj1()) : "")
                            + //if startindex is 0 do not concatenate
                            (current.getObj2() < (data.length() - 1) ? data.substring(current.getObj2()) : ""); //if endindex=newSb.length()-1 do not concatenate
                }

                if (removeHashtag) {
                    carrier.setData(new StringBuffer(data));
                }
            } else {
                logger.info("hashtag not found for instance " + carrier.toString());
            }

            carrier.setProperty(hashtagProp, value);

        }
        return carrier;
    }
}
