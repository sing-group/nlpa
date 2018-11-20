package org.ski4spam.util.unmatchedtexthandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.Main;
import org.bdp4j.util.Pair;
import java.util.HashMap;

/**
 * A class to get the match with the specific word from an Urban dictionary
 *
 * @author María Novo
 */
public class UrbanDictionaryHandler extends UnmatchedTextHandler {
	 
    private static final Logger logger = LogManager.getLogger(UrbanDictionaryHandler.class);
	 private HashMap<String, HashMap<String,String>> urbanDictionaries=new HashMap<>();

    @Override
    /**
     * Get the matches with the first term of the pair and set it to the second term of the pair.
     *
     * @param text Is a pair with the original text and the word that matches
     * with the text. If the second one doesn't exists, the value is null
     * @param lang The language of the original string
     * @return String
     */
    public void handle(Pair<String, String> text, String lang) {    
        String originalString = text.getObj1();
        String replacementString = text.getObj2();
        if (replacementString == null) {
            String matchedString = getMatch(originalString,lang);
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
    private String getMatch(String originalString, String lang) {
		if (urbanDictionaries.get(lang)==null){
        URL url = Main.class.getResource("/urbandictionary/messages_" + lang.toUpperCase() + ".properties");
		  HashMap<String,String> dict=new HashMap<>();
        if (url != null) {
            File resourceFile = new File(url.getPath());
            if (resourceFile.exists()) {
                if (resourceFile.canRead()) {
                    try (FileReader resourceFileReader = new FileReader(resourceFile);) {
                        BufferedReader buffer = new BufferedReader(resourceFileReader);
                        String linea;
                        while ((linea = buffer.readLine()) != null) {
									 if (linea.contains("#")) linea=linea.substring(0,linea.indexOf("#")-1);
                            if (linea.contains("=")) {
                                String[] matches = linea.split("=");
										  dict.put(matches[0],matches[1]);
                            }
                        }
                    } catch (IOException ex) {
                        logger.error(ex.getMessage());
                        return null;
                    }
                } else {
                    return null;
                }
            }

        }
		  urbanDictionaries.put(lang,dict);
		}
      
		return urbanDictionaries.get(lang).get(originalString);
    }

}
