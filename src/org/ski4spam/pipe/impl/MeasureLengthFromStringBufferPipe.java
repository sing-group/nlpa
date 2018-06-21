package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;



/**
 * This pipe adds the length property. 
 * @author Rosalía Laza y Reyes Pavón
 */
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
