package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;

import java.io.Reader;
import java.io.Serializable;
import java.io.IOException;
import java.io.Writer;

//import org.w3c.tidy.Tidy;
/**
 * This pipe modifies data to lowercase
 * @author Rosalía Laza y Reyes Pavón
 */
public class StringBufferToLowerCasePipe extends Pipe {

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
