package org.ski4spam.pipe.impl;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

import org.bdp4j.pipe.PipeParameter;

/**
 * This pipe adds the length property that is computed by measuring 
 * the length of a stringbuffer included in the data of the Instance
 * @author Rosalía Laza 
 * @author Reyes Pavón
 */
@PropertyComputingPipe()
public class MeasureLengthFromStringBufferPipe extends Pipe {
   
    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class getOutputType() {
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


    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer){
            StringBuffer sb = (StringBuffer)carrier.getData();
            int lengthSb = sb.length();
            carrier.setProperty(lengthProp, lengthSb);
        } else {
        	carrier.setProperty(lengthProp, "null");
        }
                    
        return carrier;
    }
}
