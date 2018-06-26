package org.ski4spam.pipe.impl;

import org.ski4spam.util.textextractor.TWTIDTextExtractor;
import org.ski4spam.util.textextractor.WARCTextExtractor;
import org.ski4spam.util.textextractor.TTWTTextExtractor;
import org.ski4spam.util.textextractor.SMSTextExtractor;
import org.ski4spam.util.textextractor.EMLTextExtractor;
import org.ski4spam.util.textextractor.TYTBTextExtractor;
import org.ski4spam.util.textextractor.TSMSTextExtractor;
import org.ski4spam.util.textextractor.TextExtractor;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;


import java.io.File;
import java.util.Hashtable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This pipe reads text and html contents from files
 * @author José Ramón Méndez Reboredo
 */
public class File2StringBufferPipe extends Pipe {
	private static final Logger logger = LogManager.getLogger(File2StringBufferPipe.class);
		
	Hashtable<String,TextExtractor> htExtractors;

    public File2StringBufferPipe() {
		htExtractors=new Hashtable<String,TextExtractor>();
		
		//Add the extractors
		htExtractors.put(EMLTextExtractor.getExtension(),EMLTextExtractor.getInstance());
		htExtractors.put(SMSTextExtractor.getExtension(),SMSTextExtractor.getInstance());
		htExtractors.put(TSMSTextExtractor.getExtension(),TSMSTextExtractor.getInstance());
		htExtractors.put(WARCTextExtractor.getExtension(),WARCTextExtractor.getInstance());
		htExtractors.put(TYTBTextExtractor.getExtension(),TYTBTextExtractor.getInstance());
		htExtractors.put(TWTIDTextExtractor.getExtension(),TWTIDTextExtractor.getInstance());
		htExtractors.put(TTWTTextExtractor.getExtension(),TTWTTextExtractor.getInstance());
    }


    @Override
    public Instance pipe(Instance carrier) {
        if ( carrier.getData() instanceof File){
            String [] extensions = {"eml", "tsms", "sms", "warc", "tytb", "twtid", "ttwt"};
            String extension = "";
            String name = (((File)carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while(i < extensions.length && !name.endsWith(extensions[i])){
                i++;
            }
            
            if (i < extensions.length){
                 extension = extensions[i];
            }		 
			 
			 TextExtractor te=htExtractors.get(extension);
			 
			 if(te!=null){
				 StringBuffer txt=te.extractText((File)(carrier.getData()));
				 if (txt==null){
				     logger.warn("Invalidating instance "+carrier.toString()+" due to a fault in parsing.");
					 carrier.invalidate();
			     } else carrier.setData(txt);
			 } else {
				 logger.warn("No parser available for instance "+carrier.toString()+". Invalidating instance.");
			     carrier.invalidate();
			 }
		}

        return carrier;
    }
}
