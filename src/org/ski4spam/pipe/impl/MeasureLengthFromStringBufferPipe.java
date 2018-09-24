package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;


/**
 * This pipe adds the length property. 
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

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public MeasureLengthFromStringBufferPipe() {
        key = "length";
    }

    public MeasureLengthFromStringBufferPipe(String k) {
        key = k;
    }


    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer){
            StringBuffer sb = (StringBuffer)carrier.getData();
            int lengthSb = sb.length();
            carrier.setProperty(key, lengthSb);
        } else {
        	carrier.setProperty(key, "null");
        }
                    
        return carrier;
    }
}
