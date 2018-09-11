package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.TransformationPipe;
import org.ski4spam.util.textextractor.*;

import java.io.File;
import java.util.Hashtable;

/**
 * This pipe reads text and html contents from files
 *
 * @author José Ramón Méndez Reboredo
 */
@TransformationPipe()
public class File2StringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(File2StringBufferPipe.class);

    @Override
    public Class getInputType() {
        return File.class;
    }

    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }

    Hashtable<String, TextExtractor> htExtractors;

    public File2StringBufferPipe() {
        htExtractors = new Hashtable<String, TextExtractor>();

        //Add the extractors
        for (String ext:GenericTextExtractor.getExtensions()) htExtractors.put(ext, GenericTextExtractor.getInstance());		
		for (String ext:EMLTextExtractor.getExtensions()) htExtractors.put(ext, EMLTextExtractor.getInstance());
        for (String ext:SMSTextExtractor.getExtensions()) htExtractors.put(ext, SMSTextExtractor.getInstance());
        for (String ext:WARCTextExtractor.getExtensions()) htExtractors.put(ext, WARCTextExtractor.getInstance());
        for (String ext:TWTIDTextExtractor.getExtensions()) htExtractors.put(ext, TWTIDTextExtractor.getInstance());
		for (String ext:YTBIDTextExtractor.getExtensions()) htExtractors.put(ext, YTBIDTextExtractor.getInstance());
    }


    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            String[] extensions = {"eml", "tsms", "sms", "warc", "tytb", "ytbid", "twtid", "ttwt"};
            String extension = "";
            String name = (((File) carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while (i < extensions.length && !name.endsWith(extensions[i])) {
                i++;
            }

            if (i < extensions.length) {
                extension = extensions[i];
            }

            TextExtractor te = htExtractors.get(extension);

            if (te != null) {
                StringBuffer txt = te.extractText((File) (carrier.getData()));
                if (txt == null) {
                    logger.warn("Invalidating instance " + carrier.toString() + " due to a fault in parsing.");
                    carrier.invalidate();
                } else carrier.setData(txt);
            } else {
                logger.warn("No parser available for instance " + carrier.toString() + ". Invalidating instance.");
                carrier.invalidate();
            }
        }

        return carrier;
    }
}
