package org.ski4spam.util.textextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class GenericTextExtractor extends TextExtractor {
    private static final Logger logger = LogManager.getLogger(GenericTextExtractor.class);

    static TextExtractor instance = null;

    private GenericTextExtractor() {

    }
	
	public static String[] getExtensions(){
		return new String[]{"tsms","ttwt","tytb"};
	}
	
    public static TextExtractor getInstance() {
        if (instance == null) {
            instance = new GenericTextExtractor();
        }
        return instance;
    }

    public StringBuffer extractText(File f) {
        StringBuffer sbResult = new StringBuffer();
        FileInputStream is = null;

        try {
            is = new FileInputStream(f);

            byte contents[] = new byte[is.available()];
            is.read(contents);

            CharsetDetector detector = new CharsetDetector();
            detector.setText(contents);
            CharsetMatch cm = detector.detect();
            logger.info("Charset guessed: " + cm.getName() + "[confidence=" + cm.getConfidence() + "/100] for " + f.getAbsolutePath());
            sbResult.append(new String(contents, Charset.forName(cm.getName())));
            is.close();
        } catch (IOException e) {
            logger.error("IOException caught" + e.getMessage());
			sbResult=null;
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                logger.fatal("IOException caught trying to recover from a previous IO error: " + e.getMessage());
            }
        }
        return sbResult;
    }
}