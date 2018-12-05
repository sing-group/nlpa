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
 * This pipe drops @userName The data of the instance should contain a
 * StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@PropertyComputingPipe()
public class FindUserNameInStringBufferPipe extends Pipe {

    private static final Logger logger = LogManager.getLogger(FindUserNameInStringBufferPipe.class);
    /*  NOTE:
     \p{Punct}-[.-_]
		  is equivalent to:
	                         !\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~
     */

    private static final Pattern userPattern = Pattern.compile("(?:\\s|^|[\"¿¡])(@[^\\p{Cntrl}\\p{Space}!\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~]+)[;:\\?\"!,.]?(?=(?:\\s|$))");

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
     * Indicates if @userName should be removed
     */
    private boolean removeUserName;

    /**
     * The default value for removing @userName
     */
    public static final String DEFAULT_REMOVE_USERNAME = "yes";

    /**
     * The default property name to store @userName
     */
    public static final String DEFAULT_USERNAME_PROPERTY = "@userName";

    /**
     * The property name to store @userName
     */
    private String userNameProp = DEFAULT_USERNAME_PROPERTY;

    /**
     * Indicates if @userName should be removed
     *
     * @param removeUserName True if @userName should be removed
     */
    @PipeParameter(name = "removeUserName", description = "Indicates if @userName should be removed or not", defaultValue = DEFAULT_REMOVE_USERNAME)
    public void setRemoveUserName(String removeUserName) {
        this.removeUserName = EBoolean.parseBoolean(removeUserName);
    }

    /**
     * Indicates if @userName should be removed
     *
     * @param removeUserName True if @UserName should be removed
     */
    public void setRemoveUserName(boolean removeUserName) {
        this.removeUserName = removeUserName;
    }

    /**
     * Checks whether @userName should be removed from data
     *
     * @return True if @userName should be removed
     */
    public boolean getRemoveUserName() {
        return this.removeUserName;
    }

    /**
     * Sets the property where @userName will be stored
     *
     * @param userNameProp the name of the property for @userName
     */
    @PipeParameter(name = "@userNamepropname", description = "Indicates the property name to store @userName", defaultValue = DEFAULT_USERNAME_PROPERTY)
    public void setUserNameProp(String userNameProp) {
        this.userNameProp = userNameProp;
    }

    /**
     * Will return true if s contains @userName.
     *
     * @param s String to test
     * @return true if string contains @userName
     */
    public static boolean isUserName(String s) {
        boolean ret = false;
        if (s != null) {
            ret = userPattern.matcher(s).find();
        }
        return ret;
    }

    /**
     * Construct a StripUserNameFromStringBufferPipe instance
     */
    public FindUserNameInStringBufferPipe() {
        this(DEFAULT_USERNAME_PROPERTY, true);
    }

    /**
     * Build a StripUserNameFromStringBufferPipe that stores @userName of the
     * StringBuffer in the property userNameProp
     *
     * @param userNameProp The name of the property to store @userName
     * @param removeUserName tells if @userName should be removed
     */

    public FindUserNameInStringBufferPipe(String userNameProp, boolean removeUserName) {
        this.userNameProp = userNameProp;
        this.removeUserName = removeUserName;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing @userName, and returns it. This is the method by which all pipes
     * are eventually run.
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

            if (isUserName(data)) {
                Matcher m = userPattern.matcher(data);

                while (m.find()) {
                    value += m.group(1) + " ";
                    if (removeUserName) {
                        replacements.push(new Pair<>(m.start(1), m.end(1)));
                    }
                }
                
                if (removeUserName) {
                    while (!replacements.empty()) {
                        Pair<Integer, Integer> current = replacements.pop();
                        data = (current.getObj1() > 0 ? data.substring(0, current.getObj1()) : "")
                            + //if startindex is 0 do not concatenate
                            (current.getObj2() < (data.length() - 1) ? data.substring(current.getObj2()) : ""); //if endindex=newSb.length()-1 do not concatenate
                    }


                    carrier.setData(new StringBuffer(data));
                }
            } else {
                logger.info("@userName not found for instance " + carrier.toString());
            }
            carrier.setProperty(userNameProp, value);
        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
