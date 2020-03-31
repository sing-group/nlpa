package org.nlpa.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;

import com.google.auto.service.AutoService;

/**
 * This pipe saves the text in a property before doing any transformation to the
 * text
 *
 * @author Patricia Martin Perez
 */
@PropertyComputingPipe
@AutoService(Pipe.class)
public class SaveOriginalTextPropertyPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SaveOriginalTextPropertyPipe.class);

    /**
     * The default property name where the original text will be stored
     */
    public static final String DEFAULT_ORIGINAL_TEXT_PROPERTY = "originalText";

    /**
     * The name of the property where the original text is stored
     */
    private String originalTextProperty = DEFAULT_ORIGINAL_TEXT_PROPERTY;

    /**
     * Construct a SaveOriginalTextPropertyPipe instance
     *
     */
    public SaveOriginalTextPropertyPipe() {
        super(new Class<?>[0], new Class<?>[0]);

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
     * Establish the name of the property where the original text will be stored
     *
     * @param originalTextProperty The name of the property where the original
     * text is stored
     */
    @PipeParameter(name = "originalTextProperty", description = "Indicates the property name to store the original text", defaultValue = DEFAULT_ORIGINAL_TEXT_PROPERTY)
    public void setOriginalTextProperty(String originalTextProperty) {
        this.originalTextProperty = originalTextProperty;
    }

    /**
     * Retrieves the property name for storing the original text
     *
     * @return String containing the property name for storing the original text
     */
    public String getOriginalTextProperty() {
        return originalTextProperty;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            carrier.setProperty(originalTextProperty, carrier.getData().toString());
        } else {
            logger.error("Data should be an StringBuffer when processing " + carrier.getName() + " but is a "
                    + carrier.getData().getClass().getName());
        }

        return carrier;
    }

}
