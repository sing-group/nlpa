package org.ski4spam.pipe.impl;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.ski4spam.util.textextractor.EMLTextExtractor;
import org.ski4spam.util.textextractor.GenericTextExtractor;
import org.ski4spam.util.textextractor.SMSTextExtractor;
import org.ski4spam.util.textextractor.TWTIDTextExtractor;
import org.ski4spam.util.textextractor.TextExtractor;
import org.ski4spam.util.textextractor.WARCTextExtractor;
import org.ski4spam.util.textextractor.YTBIDTextExtractor;

/**
 * This pipe reads text and html contents from different file formats
 * @author José Ramón Méndez Reboredo
 */
@TransformationPipe()
public class File2StringBufferPipe extends Pipe {
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(File2StringBufferPipe.class);

    /**
     * Dependencies of the type alwaysAfter
     * These dependences indicate what pipes should be  
     * executed before the current one. So this pipe
     * shoudl be executed always after other dependant pipes
     * included in this variable
     */
    final Class<?> alwaysAftterDeps[]={};

    /**
     * Dependencies of the type notAfter
     * These dependences indicate what pipes should not be  
     * executed before the current one. So this pipe
     * shoudl be executed before other dependant pipes
     * included in this variable
     */
    final Class<?> notAftterDeps[]={};

    @Override
    public Class<?> getInputType() {
        return File.class;
    }

    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
		* A collection of Textextractors to extract the text
		*/
    private static HashMap<String, TextExtractor> htExtractors;

    static{
       htExtractors = new HashMap<>();

       //Add the extractors
       for (String ext:GenericTextExtractor.getExtensions()) htExtractors.put(ext, GenericTextExtractor.getInstance());		
	    for (String ext:EMLTextExtractor.getExtensions()) htExtractors.put(ext, EMLTextExtractor.getInstance());
       for (String ext:SMSTextExtractor.getExtensions()) htExtractors.put(ext, SMSTextExtractor.getInstance());
       for (String ext:WARCTextExtractor.getExtensions()) htExtractors.put(ext, WARCTextExtractor.getInstance());
       for (String ext:TWTIDTextExtractor.getExtensions()) htExtractors.put(ext, TWTIDTextExtractor.getInstance());
	    for (String ext:YTBIDTextExtractor.getExtensions()) htExtractors.put(ext, YTBIDTextExtractor.getInstance());    	
    }
	 
    /**
		* Default constructor for the class
		*/
    public File2StringBufferPipe() {
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
