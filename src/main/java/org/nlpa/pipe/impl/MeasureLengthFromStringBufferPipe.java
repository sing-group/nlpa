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
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

/**
 * This pipe adds the length property that is computed by measuring the length
 * of a stringbuffer included in the data of the Instance
 *
 * @author Rosalía Laza
 * @author Reyes Pavón
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class MeasureLengthFromStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(MeasureLengthFromStringBufferPipe.class);

    /**
     * The default name of the property to store the length of the text
     */
    public static final String DEFAULT_LENGTH_PROPERTY = "length";

    /**
     * The property to store the length of the text
     */
    private String lengthProp = DEFAULT_LENGTH_PROPERTY;

    /**
     * Default constructor. Build a MeasureLengthFromStringBufferPipe that stores the length in the
     * default property ("length")
     */
    public MeasureLengthFromStringBufferPipe() {
        this(DEFAULT_LENGTH_PROPERTY);
    }

    /**
     * Build a MeasureLengthFromStringBufferPipe that stores the length in the
     * property indicated by lengthProp parameter
     *
     * @param lengthProp the name of the property to store the text length
     */
    public MeasureLengthFromStringBufferPipe(String lengthProp) {
        super(new Class<?>[0], new Class<?>[0]);

        this.lengthProp = lengthProp;
    }

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
     * Establish the name of the property to store the lenght of the text
     *
     * @param lengthProp the name of the property to store the lenght of the
     * text
     */
    @PipeParameter(name = "lengthpropname", description = "Indicates the property name to store the length", defaultValue = DEFAULT_LENGTH_PROPERTY)
    public void setLengthProp(String lengthProp) {
        this.lengthProp = lengthProp;
    }

    /**
     * Returns the name of the property to store the length
     *
     * @return the name of the property to store the length
     */
    public String getLengthProp() {
        return this.lengthProp;
    }

    /**
     * Process an Instance. This method takes an input Instance, calculates the
     * lenght of the text, and returns it. This is the method by which all pipes
     * are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            StringBuffer sb = (StringBuffer) carrier.getData();
            int lengthSb = sb.length();
            carrier.setProperty(lengthProp, lengthSb);
        } else {
            carrier.setProperty(lengthProp, "null");
            logger.error("Data should be an StrinBuffer when processing " + carrier.getName() + " but is a " + carrier.getData().getClass().getName());
        }

        return carrier;
    }
}
