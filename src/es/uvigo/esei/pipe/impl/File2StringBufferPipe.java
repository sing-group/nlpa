package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;

import  es.uvigo.esei.util.*;

import java.io.File;
import java.util.Hashtable;
	
/**
 * This pipe reads text and html contents from files
 * @author José Ramón Méndez Reboredo
 */
public class File2StringBufferPipe extends Pipe {
	
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
            String [] extensions = {"eml", "sms", "tsms", "warc", "tytb", "twtid", "ttwt"};
            String value = "";
            String name = ((String)carrier.getName()).toLowerCase();
            int i = 0;
            while(i < extensions.length && !name.endsWith(extensions[i])){
                i++;
            }
            
            if (i < extensions.length){
                 value = extensions[i];
            }			 
			 
			 TextExtractor te=htExtractors.get(value);
			 if(te!=null) carrier.setData(te.extractText((File)(carrier.getData())));
		}

        return carrier;
    }
}
