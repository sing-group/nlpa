package org.ski4spam.pipe.impl;

import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;

/**
 * This pipe modifies the data of an Insatance to lowercase
 * The data should be a StringBuffer
 * @author Rosalía Laza 
 * @author Reyes Pavón
 */
@TransformationPipe()
public class StringBufferToLowerCasePipe extends Pipe {
	
   /**
    * Return the input type included the data attribute of a Instance
    * @return the input type for the data attribute of the Instances processed
    */
    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after processing
     * @return the datatype expected in the data attribute of a Instance after processing
     */
    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }
    
	 /**
		* Build a StringBufferToLowerCasePipe
		*/
    public StringBufferToLowerCasePipe() {
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
        if ( carrier.getData() instanceof StringBuffer){
            StringBuffer newSb=new StringBuffer();
            newSb.append(((StringBuffer)carrier.getData()).toString().toLowerCase());
            carrier.setData(newSb);
        }

        return carrier;
    }
}
