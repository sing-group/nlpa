package org.ski4spam.pipe.impl;

import org.bdp4j.ia.types.Instance;
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

    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }
    
	 /**
		* Build a StringBufferToLowerCasePipe
		*/
    public StringBufferToLowerCasePipe() {
    }

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
