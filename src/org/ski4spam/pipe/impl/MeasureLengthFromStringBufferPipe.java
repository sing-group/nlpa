package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;


/**
 * This pipe adds the length property. 
 * @author Rosalía Laza y Reyes Pavón
 */
@PropertyComputingPipe(inputType = "StringBuffer")
public class MeasureLengthFromStringBufferPipe extends Pipe {
    private String key;
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
        }
                    
        return carrier;
    }
}
