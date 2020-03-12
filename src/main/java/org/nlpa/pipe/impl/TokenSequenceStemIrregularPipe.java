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
package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;

import org.bdp4j.types.Instance;
import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;
import org.nlpa.types.Dictionary;
import org.nlpa.types.TokenSequence;

/**
 * A pipe to detect irregular words and get its root from a list of terms
 * contained in text files.
 *
 * @author José Ramón Méndez Reboredo
 * @since JDK 1.5
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class TokenSequenceStemIrregularPipe extends AbstractPipe {

    /**
     * For loggins purposes
     */
    private static final Logger logger = LogManager.getLogger(TokenSequencePorterStemmerPipe.class);

    /**
     * The name of the property where the language is stored
     */
    private final String langProp;

    /**
     * List of irregular words classified by lang
     */
    private final static Map<String, Map<String, String>> LANG_WORD_FILES = new HashMap<>();

    /**
     * List of irregular words ans its root
     */
    private Map<String, String> irregularWords = new HashMap<>();

    /* Load irregulars words from file */
    static {
        for (String i : new String[]{"/irregular/irregular_es.stm", "/irregular/irregular_en.stm"}) {
            String lang = i.substring(21, 23).toUpperCase();
            Map<String, String> irregularWordsList = new HashMap<>();
            try (FileReader fReader = new FileReader(new File(TokenSequenceStemIrregularPipe.class.getResource("/irregular/irregular_" + lang.toLowerCase() + ".stm").toURI()));
                    BufferedReader bReader = new BufferedReader(fReader);) {
                String var = "";
                while ((var = bReader.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(var, " \n\t");
                    irregularWordsList.put(st.nextToken(), st.nextToken());
                }
            } catch (Exception e) {
                logger.error("[TokenSequenceStemIrregularPipe Load files]: " + e.getMessage());
            }
            LANG_WORD_FILES.put(lang, irregularWordsList);
        }
    }

    /**
     * Default constructor. Build a TokenSequenceStemIrregularPipe pipe with the
     * default configuration values
     */
    public TokenSequenceStemIrregularPipe() {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class}, new Class<?>[]{});
        this.langProp = DEFAULT_LANG_PROPERTY;
    }

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return TokenSequence.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance
     * after processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return TokenSequence.class;
    }

    /**
     * Process instances to detect irregular word and destructively replace them
     * with their root.
     *
     * @param carrier Instance to be process
     * @return The processed instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        TokenSequence ts = (TokenSequence) carrier.getData();
        TokenSequence ret = new TokenSequence();
        String lang = (String) carrier.getProperty(this.langProp);
        Dictionary dictionary = Dictionary.getDictionary();
        dictionary.setEncode(true);
        
        if (lang != null) {
            irregularWords = LANG_WORD_FILES.get(lang.toUpperCase());
            if (irregularWords != null) {
                for (int i = 0; i < ts.size(); i++) {
                    String encodeToken = ts.getToken(i);
                    String decodeToken = dictionary.decodeBase64(ts.getToken(i).substring(3));
                    String token = decodeToken;
                    //If the token is irregular, it changes text
                    String changeTxt;
                    if ((changeTxt = irregularWords.get(decodeToken)) != null) {
                        token = changeTxt;
                    }
                    String encodedReplaceToken = "tk:" + dictionary.encodeBase64(token);
                    ret.add(encodedReplaceToken);

                    if (dictionary.getEncode()) {
                        dictionary.replace(encodeToken, encodedReplaceToken);
                    } else {
                        dictionary.replace(decodeToken, token);
                    }
                }
                carrier.setData(ret);
            }
        }
        
        return carrier;
    }
}
