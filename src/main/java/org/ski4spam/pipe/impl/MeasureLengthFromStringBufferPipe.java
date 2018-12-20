package org.ski4spam.pipe.impl;

import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

import org.bdp4j.pipe.PipeParameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This pipe adds the length property that is computed by measuring
 * the length of a stringbuffer included in the data of the Instance
 * @author Rosalía Laza
 * @author Reyes Pavón
 */
@PropertyComputingPipe()
public class MeasureLengthFromStringBufferPipe extends Pipe {

   /**
	 * For logging purposes
	 */
    private static final Logger logger = LogManager.getLogger(MeasureLengthFromStringBufferPipe.class);

    /**
     * Dependencies of the type alwaysAfter
     * These dependences indicate what pipes should be  
     * executed before the current one. So this pipe
     * shoudl be executed always after other dependant pipes
     * included in this variable
     */
    final Class<?> alwaysAftterDeps[]={};

    /**
     * Dependencies of the type notAfter
     * These dependences indicate what pipes should not be  
     * executed before the current one. So this pipe
     * shoudl be executed before other dependant pipes
     * included in this variable
     */
    final Class<?> notAftterDeps[]={};

   /**
    * Return the input type included the data attribute of a Instance
    * @return the input type for the data attribute of the Instances processed
    */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }


    /**
     * Indicates the datatype expected in the data attribute of a Instance after processing
     * @return the datatype expected in the data attribute of a Instance after processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

	 /**
		* The default name of the property to store the length of the text
		*/
	 public static final String DEFAULT_LENGTH_PROPERTY="length";

    /**
		* The property to store the length of the text
		*/
    private String lengthProp=DEFAULT_LENGTH_PROPERTY;

    /**
		* Stablish the name of the property to sotre the lenght of the text
		* @param lengthProp the name of the property to sotre the lenght of the text
		*/
	 @PipeParameter(name = "lengthpropname", description = "Indicates the property name to store the length", defaultValue=DEFAULT_LENGTH_PROPERTY)
    public void setLengthProp(String lengthProp) {
        this.lengthProp = lengthProp;
    }

	 /**
		* Returns the name of the property to store the length
		* @return the name of the property to store the length
		*/
    public String getLengthProp() {
        return this.lengthProp;
    }

    /**
		* Build a MeasureLengthFromStringBufferPipe that stores the length in the
		* default property ("length")
		*/
    public MeasureLengthFromStringBufferPipe() {
    }

    /**
		* Build a MeasureLengthFromStringBufferPipe that stores the length in the
		* property indicated by lengthProp parameter
		* @param lengthProp the name of te property to store the text length
		*/
    public MeasureLengthFromStringBufferPipe(String lengthProp) {
        this.lengthProp = lengthProp;
    }

    /**
    * Process an Instance.  This method takes an input Instance,
    * destructively modifies it in some way, and returns it.
    * This is the method by which all pipes are eventually run.
    *
    * @param carrier Instance to be processed.
    * @return Instancia procesada
    */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer){
            StringBuffer sb = (StringBuffer)carrier.getData();
            int lengthSb = sb.length();
            carrier.setProperty(lengthProp, lengthSb);
        } else {
        	carrier.setProperty(lengthProp, "null");
        	logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }

        return carrier;
    }
}
