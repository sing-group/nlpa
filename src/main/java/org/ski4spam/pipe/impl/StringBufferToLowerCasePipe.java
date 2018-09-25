package org.ski4spam.pipe.impl;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;

//import org.w3c.tidy.Tidy;
/**
 * This pipe modifies data to lowercase
 * @author Rosalía Laza y Reyes Pavón
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
