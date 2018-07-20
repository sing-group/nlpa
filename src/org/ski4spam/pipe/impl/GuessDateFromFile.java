package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;
import org.ski4spam.util.dateextractor.*;

import java.io.File;
import java.util.Hashtable;
import java.util.Date;

/**
 * This pipe reads text and html contents from files
 *
 * @author José Ramón Méndez Reboredo
 */
@PropertyComputingPipe(inputType = "File")
public class GuessDateFromFile extends Pipe {
    private static final Logger logger = LogManager.getLogger(GuessDateFromFile.class);

    Hashtable<String, DateExtractor> htExtractors;
	
	String datePropertyStr="date";
	
	public GuessDateFromFile(){
		init();
	}
	
	public GuessDateFromFile(String datePropertyStr){
		this.datePropertyStr=datePropertyStr;
		init();
	}	

    private void init() {
        htExtractors = new Hashtable<String, DateExtractor>();

        //Add the extractors
        htExtractors.put(EMLDateExtractor.getExtension(), EMLDateExtractor.getInstance());
        htExtractors.put(SMSDateExtractor.getExtension(), SMSDateExtractor.getInstance());
        htExtractors.put(TSMSDateExtractor.getExtension(), TSMSDateExtractor.getInstance());
        htExtractors.put(WARCDateExtractor.getExtension(), WARCDateExtractor.getInstance());
        htExtractors.put(TYTBDateExtractor.getExtension(), TYTBDateExtractor.getInstance());
        htExtractors.put(TWTIDDateExtractor.getExtension(), TWTIDDateExtractor.getInstance());
        htExtractors.put(TTWTDateExtractor.getExtension(), TTWTDateExtractor.getInstance());
    }


    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            String[] extensions = {"eml", "tsms", "sms", "warc", "tytb", "twtid", "ttwt"};
            String extension = "";
            String name = (((File) carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while (i < extensions.length && !name.endsWith(extensions[i])) {
                i++;
            }

            if (i < extensions.length) {
                extension = extensions[i];
            }

            DateExtractor de = htExtractors.get(extension);

            if (de != null) {
                Date d = de.extractDate((File) (carrier.getData()));
                if (d == null) {
                    logger.warn("Invalid date " + carrier.toString() + " due to a fault in parsing.");
                    carrier.setProperty(datePropertyStr,"null");
                }else{
				   carrier.setProperty(datePropertyStr,d);
				}
            } else {
                logger.warn("No parser available for instance " + carrier.toString() + ". Invalidating instance.");
                carrier.setProperty(datePropertyStr,"null");
            }
        }

        return carrier;
    }
}
