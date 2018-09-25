package org.ski4spam.pipe.impl;

import java.io.File;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

/**
 * This pipe adds the length property.
 *
 * @author Rosalía Laza y Reyes Pavón
 */
@PropertyComputingPipe()
public class StoreFileExtensionPipe extends Pipe {

    @Override
    public Class getInputType() {
        return File.class;
    }

    @Override
    public Class getOutputType() {
        return File.class;
    }

    private String key;

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey(){
        return this.key;
    }

    public StoreFileExtensionPipe() {
        key = "extension";
    }

    public StoreFileExtensionPipe(String k) {
        key = k;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            String[] extensions = {"eml", "tsms", "sms", "warc", "ytbid", "tytb", "twtid", "ttwt"};
            String value = "";
            String name = (((File) carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while (i < extensions.length && !name.endsWith(extensions[i])) {
                i++;
            }

            if (i < extensions.length) {
                value = extensions[i];
            }

            carrier.setProperty(key, value);
        }
        return carrier;
    }
}
