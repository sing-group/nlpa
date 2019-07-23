/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.util.unmatchedtexthandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.util.Pair;
import static org.nlpa.pipe.impl.SlangFromStringBufferPipe.getReplacement4SlangTerm;

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
     */
    public void handle(Pair<String, String> text, String lang) {
        String originalString = text.getObj1();
        String replacementString = text.getObj2();
        if (replacementString == null) {
            String matchedString = getReplacement4SlangTerm(originalString, lang);
            if (matchedString != null) {
                logger.info("Sucessfull match for string "+ matchedString);
                text.setObj2(matchedString);
            }
        }
    }

}
