package org.nlpa.pipe.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;

import org.bdp4j.types.Instance;
import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;
import org.nlpa.types.TokenSequence;

/**
 * Stemmer de términos irregulares genérico y abstracto
 *
 * @author José Ramón Méndez Reboredo
 * @since JDK 1.5
 */
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
     * Irregular words by lang
     */
    private final static Map<String, Map<String, String>> LANG_WORD_FILES = new HashMap<>();

    /**
     * Irregular words
     */
    private Map<String, String> irregularWords = new HashMap<>();

    /* Load irregulars words from file */
    static {
        for (String i : new String[]{"/irregular/irregular_es.stm", "/irregular/irregular_en.stm"}) {
            String lang = i.substring(21, 23);
            Map<String, String> irregularWordsList = new HashMap<>();
            try (FileReader fReader = new FileReader(new File(TokenSequenceStemIrregularPipe.class.getResource("/irregular/irregular_" + lang + ".stm").toURI()));
                    BufferedReader bReader = new BufferedReader(fReader);) {
                String var = "";
                while ((var = bReader.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(var, " \n\t");
                    irregularWordsList.put(st.nextToken(), st.nextToken());
                }
            } catch (Exception e) {
                logger.error("[TokenSequencePorterStemmerPipe Load files]: " + e.getMessage());
            }
            LANG_WORD_FILES.put(lang, irregularWordsList);
        }
    }

    /**
     * Default constructor
     */
    public TokenSequenceStemIrregularPipe() {
        super(new Class<?>[]{StringBuffer2TokenSequencePipe.class, GuessLanguageFromStringBufferPipe.class}, new Class<?>[0]);
        this.langProp = DEFAULT_LANG_PROPERTY;
    }

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return TokenSequence.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return TokenSequence.class;
    }
    
    /**
     * Process instances to detect irregular word and change them.
     *
     * @param carrier Instance to be process
     * @return The processed instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        TokenSequence ts = (TokenSequence) carrier.getData();
        TokenSequence ret = new TokenSequence();
        String lang = (String) carrier.getProperty(this.langProp);

        if (lang != null) {
            irregularWords = LANG_WORD_FILES.get(lang.toLowerCase());
            if (irregularWords != null) {
                for (int i = 0; i < ts.size(); i++) {
                    String token = ts.getToken(i);
                    //Si el token es irregular se cambia el texto
                    String changeTxt;
                    if ((changeTxt = irregularWords.get(token)) != null) {
                        System.out.println(token + " - " + changeTxt);
                        token = changeTxt;
                    }
                    ret.add(token);
                }
                carrier.setData(ret);
            }
        }
        return carrier;
    }
}
