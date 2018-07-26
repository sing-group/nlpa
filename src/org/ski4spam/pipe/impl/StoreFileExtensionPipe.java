package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;

import java.io.File;

/**
 * This pipe adds the length property.
 *
 * @author Rosalía Laza y Reyes Pavón
 */
@PropertyComputingPipe(inputType = "File")
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
