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
