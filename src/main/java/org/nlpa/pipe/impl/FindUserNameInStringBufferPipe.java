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
import org.bdp4j.util.Pair;
import org.bdp4j.util.EBoolean;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe drops @userName. The data of the instance should contain a
 * StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class FindUserNameInStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(FindUserNameInStringBufferPipe.class);

    /*  NOTE:
     \p{Punct}-[.-_]
		  is equivalent to:
	                         !\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~
     */
    /**
     * Pattern to detect a user
     */
    private static final Pattern userPattern = Pattern.compile("(?:\\s|^|[\"><¡?¿!;:,.'-])(@[^\\p{Cntrl}\\p{Space}!\"#$%&'()*+\\\\,\\/:;<=>?@\\[\\]^`{|}~]+)[;:?\"!,.'>-]?(?=(?:\\s|$|>))");

    /**
     * The default value for removing @userName
     */
    public static final String DEFAULT_REMOVE_USERNAME = "yes";

    /**
     * The default property name to store @userName
     */
    public static final String DEFAULT_USERNAME_PROPERTY = "@userName";

    /**
     * Indicates if @userName should be removed
     */
    private boolean removeUserName = EBoolean.getBoolean(DEFAULT_REMOVE_USERNAME);

    /**
     * The property name to store @userName
     */
    private String userNameProp = DEFAULT_USERNAME_PROPERTY;

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
     * Indicates the datatype expected in the data attribute of an Instance
     * after processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

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
     * Retrieves the property name for @username
     *
     * @return String containing the property name for @username
     */
    public String getUserNameProp() {
        return this.userNameProp;
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
     * Default construct. Construct a FindUserNameInStringBufferPipe instance
     */
    public FindUserNameInStringBufferPipe() {
        this(DEFAULT_USERNAME_PROPERTY, EBoolean.getBoolean(DEFAULT_REMOVE_USERNAME));
    }

    /**
     * Build a FindUserNameInStringBufferPipe that stores @userName of the
     * StringBuffer in the property userNameProp
     *
     * @param userNameProp The name of the property to store @userName
     * @param removeUserName tells if @userName should be removed
     */
    public FindUserNameInStringBufferPipe(String userNameProp, boolean removeUserName) {
        super(new Class<?>[0], new Class<?>[0]);

        this.userNameProp = userNameProp;
        this.removeUserName = removeUserName;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing @userName, adds a property and returns it. This is the method by
     * which all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
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
        } else {
            logger.error("Data should be an StrinBuffer when processing " + carrier.getName() + " but is a " + carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
