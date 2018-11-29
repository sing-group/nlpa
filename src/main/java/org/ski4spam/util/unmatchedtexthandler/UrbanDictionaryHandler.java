package org.ski4spam.util.unmatchedtexthandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.util.Pair;
import static org.ski4spam.pipe.impl.SlangFromStringBufferPipe.getReplacement4SlangTerm;

/**
 * A class to get the match with the specific word from an Urban dictionary
 *
 * @author María Novo
 * @author Rosalía Laza
 * @author Reyes Pavón
 * @author José Ramón Méndez
 */
public class UrbanDictionaryHandler extends UnmatchedTextHandler {

    private static final Logger logger = LogManager.getLogger(UrbanDictionaryHandler.class);

    @Override
    /**
     * Get the matches with the first term of the pair and set it to the second
     * term of the pair.
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
            String matchedString = getReplacement4SlangTerm(originalString, lang);
            if (matchedString != null) {
                text.setObj2(matchedString);
            }
        }
    }

}
