package org.ski4spam.pipe.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
 * This pipe drops URLs The data of the instance should contain a StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@PropertyComputingPipe()
public class FindUrlInStringBufferPipe extends Pipe {

    private static final Logger logger = LogManager.getLogger(FindUrlInStringBufferPipe.class);
    /*  NOTE:
     \p{Punct}-[.-_] 
		  is equivalent to:
	                         !\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~
     */

    private static List<Pattern> URLPatterns;

    private static final Pattern URLPattern = Pattern.compile("((?:[a-z0-9]+:)(?:\\/\\/|\\/|)?(?:[\\w-]+(?:(?:\\.[\\w-]+)+))(?:[\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?(?=(?:,|;|!|:|\"|\\?|\\s|$)))", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern emailPattern = Pattern.compile("(?:\\s|^|¡)([\\w!#$%&’*+-\\/=?^_`\\{|\\}~“(),:;<>@\\[\\]\"ç]+@[\\[\\w.-:]+([A-Z]{2,4}|\\]))[;:\\?\"!,.]?(?=(?:\\s|$))", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

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
     * Indicates if URLs should be removed
     */
    private boolean removeURL;

    /**
     * The default value for removing @userName
     */
    public static final String DEFAULT_REMOVE_URL = "yes";

    /**
     * The default property name to store @userName
     */
    public static final String DEFAULT_URL_PROPERTY = "URLs";

    /**
     * The property name to store @userName
     */
    private String URLProp = DEFAULT_URL_PROPERTY;

    /**
     * Indicates if URL should be removed
     *
     * @param removeURL True if URL should be removed
     */
    @PipeParameter(name = "removeUrl", description = "Indicates if URL should be removed or not", defaultValue = DEFAULT_REMOVE_URL)
    public void setRemoveURL(String removeURL) {
        this.removeURL = EBoolean.parseBoolean(removeURL);
    }

    /**
     * Indicates if URL should be removed
     *
     * @param removeURL True if URL should be removed
     */
    public void setRemoveURL(boolean removeURL) {
        this.removeURL = removeURL;
    }

    /**
     * Checks whether URL should be removed from data
     *
     * @return True if URL should be removed
     */
    public boolean getRemoveURL() {
        return this.removeURL;
    }

    /**
     * Sets the property where URL will be stored
     *
     * @param URLProp the name of the property for URLs
     */
    @PipeParameter(name = "URLpropname", description = "Indicates the property name to store URL", defaultValue = DEFAULT_URL_PROPERTY)
    public void setURLProp(String URLProp) {
        this.URLProp = URLProp;
    }

    /**
     * Will return true if s contains URL.
     *
     * @param s String to test
     * @return true if string contains URLs
     */
    public static boolean isURL(String s) {
        boolean ret = false;
        if (s != null) {
            Iterator<Pattern> it_p = URLPatterns.iterator();
            while (!ret && it_p.hasNext()) {
                ret = it_p.next().matcher(s).find();
            }
        }
        return ret;
    }

    /**
     * Construct a StripUserNameFromStringBufferPipe instance
     */
    public FindUrlInStringBufferPipe() {
        this(DEFAULT_URL_PROPERTY, true);
    }

    /**
     * Build a FindUrlInStringBufferPipe that stores URLs of the StringBuffer in
     * the property URLProp
     *
     * @param URLProp The name of the property to store @userName
     * @param removeURL tells if URL should be removed
     */

    public FindUrlInStringBufferPipe(String URLProp, boolean removeURL) {
        this.URLProp = URLProp;
        this.removeURL = removeURL;
        URLPatterns = new LinkedList<>();
        URLPatterns.add(emailPattern);
        URLPatterns.add(URLPattern);
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing URL, and returns it. This is the method by which all pipes are
     * eventually run.
     *
     * @param carrier Instance to be processed.
     * @return processed Instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {

            String data = carrier.getData().toString();
            Stack<Pair<Integer, Integer>> replacements = new Stack<>();
            String value = "";

            if (isURL(data)) {

                for (Pattern URLPat : URLPatterns) {
                    Matcher m = URLPat.matcher(data);

                    while (m.find()) {
                        value += m.group(1) + " ";
                        if (removeURL) {
                            replacements.push(new Pair<>(m.start(1), m.end(1)));
                        }
                    }

                    while (!replacements.empty()) {
                        Pair<Integer, Integer> current = replacements.pop();
                        data = (current.getObj1() > 0 ? data.substring(0, current.getObj1()) : "")
                                + //if startindex is 0 do not concatenate
                                (current.getObj2() < (data.length() - 1) ? data.substring(current.getObj2()) : ""); //if endindex=newSb.length()-1 do not concatenate
                    }

                }
                if (removeURL) {
                    carrier.setData(new StringBuffer(data));
                }
            } else {
                logger.info("URL not found for instance " + carrier.toString());
            }
            carrier.setProperty(URLProp, value);
        }
        return carrier;
    }
}
