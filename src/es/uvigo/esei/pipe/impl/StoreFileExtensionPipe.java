package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;
import java.util.ArrayList;



/**
 * This pipe adds the length property. 
 * @author Rosalía Laza y Reyes Pavón
 */
public class StoreFileExtensionPipe extends Pipe {
    private String key;
    
    public StoreFileExtensionPipe() {
        key = "extension";
    }
    public StoreFileExtensionPipe(String k) {
        key = k;
    }


    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getName() instanceof String){
            String [] extensions = {".eml", ".sms", ".tsms", ".warc", ".tytb", ".twtid", "ttwt"};
            String value = "";
            String name = (String)carrier.getName();
            int i = 0;
            while(i < extensions.length && !name.endsWith(extensions[i])){
                i++;
            }
            
            if (i < extensions.length){
                 value = extensions[i];
            }
            
            carrier.setProperty(key, value);
        }
        return carrier;
    }
}
