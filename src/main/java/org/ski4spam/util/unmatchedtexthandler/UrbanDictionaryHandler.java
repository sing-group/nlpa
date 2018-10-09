package org.ski4spam.util.unmatchedtexthandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.Main;
import org.ski4spam.util.Pair;

public class UrbanDictionaryHandler extends UnmatchedTextHandler {

    private String lang;
     private static final Logger logger = LogManager.getLogger(UrbanDictionaryHandler.class);

    @Override
    public void handle(Pair<String, String> text, String lang) {    
        String originalString = text.getObj1();
        String replacementString = text.getObj2();
        this.lang = lang;
        if (replacementString == null) {
            String matchedString = getMatch(originalString);
            if (matchedString != null) {
                text.setObj2(matchedString);
            }
        }
    }

    /**
     * Return the word that matches with the parameter
     *
     * @param originalString Word to get match in resources files
     * @return String
     */
    private String getMatch(String originalString) {
        URL url = Main.class.getResource("/urbandictionary/messages_" + lang.toUpperCase() + ".properties");
        if (url != null) {
            File resourceFile = new File(url.getPath());
            if (resourceFile.exists()) {
                if (resourceFile.canRead()) {
                    try (FileReader resourceFileReader = new FileReader(resourceFile);) {
                        BufferedReader buffer = new BufferedReader(resourceFileReader);
                        String linea;
                        while ((linea = buffer.readLine()) != null) {
                            if (linea.contains(originalString)) {
                                String[] matches = linea.split("=");
                                if (matches[0] == null ? originalString == null : matches[0].equals(originalString)) {
                                    return matches[1];
                                }
                            }
                        }
                    } catch (IOException ex) {
                        logger.error(UrbanDictionaryHandler.class.getName() + ". " + ex.getMessage());
                        return null;
                    }
                } else {
                    return null;
                }
            }

        }
        return null;
    }

}