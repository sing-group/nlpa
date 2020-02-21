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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.nlpa.types.TokenSequence;

import org.bdp4j.util.Pair;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;
import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;
import org.nlpa.types.Dictionary;
import org.nlpa.types.Rule;

/**
 * Stemmer according to Porter's algorithm. Reduces a word to its root using
 * Porter's algorithm.
 *
 * @author José Ramón Méndez Reboredo
 * @since JDK 1.5
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class TokenSequencePorterStemmerPipe extends AbstractPipe {

    /**
     * For loggins purposes
     */
    private static final Logger logger = LogManager.getLogger(TokenSequencePorterStemmerPipe.class);

    /**
     * The name of the property where the language is stored
     */
    private final String langProp;

    /**
     * Stemming rules by lang
     */
    private final static Map<String, List<Pair<String, List<Rule>>>> RULES_FILES = new HashMap<>();

    /**
     * A list with Stemming rules
     */
    private List<Pair<String, List<Rule>>> rules = new ArrayList<>();

    /* Steeming rules from file */
    static {
        for (String i : new String[]{"/porter/porter.en.stm",
            "/porter/porter.es.stm"
        }) {

            List<Pair<String, List<Rule>>> currentRulesFile = new ArrayList<>();
            String lang = i.substring(15, 17).toUpperCase();

            try (Reader iStream = new FileReader(new File(TokenSequencePorterStemmerPipe.class.getResource("/porter/porter." + lang.toLowerCase() + ".stm").toURI()));
                    BufferedReader bReader = new BufferedReader(iStream)) {
                String line;
                StringTokenizer sTokenizer;
                while ((line = bReader.readLine()) != null) {
                    //Delete comment if exists
                    if (line.contains("#")) {
                        line = line.substring(0, line.indexOf("#"));
                    }

                    //If line is not null after delete comment
                    if (!line.trim().equals("")) {
                        sTokenizer = new StringTokenizer(line);
                        String tokens[] = new String[5];
                        final int STEP = 0;
                        final int TERM = 1;
                        final int TERM_CHANGE = 2;
                        final int MIN_ROOT_SIZE = 3;
                        final int COND = 4;

                        int cuentaTokens = 0;
                        while (sTokenizer.hasMoreTokens() && cuentaTokens < 5) {
                            tokens[cuentaTokens] = sTokenizer.nextToken();
                            cuentaTokens++;
                        }

                        if (cuentaTokens == 5) { //Si has leido 5 tokens
                            //If change = "*", it is empty string
                            if (tokens[TERM_CHANGE].trim().equals("*")) {
                                tokens[TERM_CHANGE] = "";
                            }
                            if (tokens[TERM].trim().equals("*")) {
                                tokens[TERM] = "";
                            }

                            //Add to rules list
                            Rule rule = null;
                            try {
                                rule = new Rule(
                                        tokens[TERM],
                                        tokens[TERM_CHANGE],
                                        Integer.parseInt(tokens[MIN_ROOT_SIZE]),
                                        Integer.parseInt(tokens[COND])
                                );
                            } catch (NumberFormatException nfe) {
                                logger.warn(nfe.getMessage());
                            }

                            // If numeric files have been processed correctly
                            if (rule != null) {
                                //Add rule to the corresponding list

                                List<Rule> step = null;

                                if (RULES_FILES.get(lang) != null) {
                                    currentRulesFile = RULES_FILES.get(lang);
                                }
                                for (int ind = 0; ind < currentRulesFile.size() && step == null; ind++) {
                                    step = currentRulesFile.get(ind).getObj1().trim().equals(tokens[STEP].trim())
                                            ? currentRulesFile.get(ind).getObj2() : null;
                                }

                                //Add new step
                                if (step == null) {
                                    step = new ArrayList<>();
                                    currentRulesFile.add(new Pair<>(tokens[STEP].trim(), step));
                                }

                                //Add rule to step
                                step.add(rule);
                            }
                        }
                    }
                }
                RULES_FILES.put(lang, currentRulesFile);
            } catch (Exception ex) {
                logger.warn("[TokenSequencePorterStemmerPipe Load rules ] " + ex.getMessage());
            }
        }
    }

    /**
     * Default constructor. Build a TokenSequencePorterStemmerPipe pipe with the
     * default configuration values
     */
    public TokenSequencePorterStemmerPipe() {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class}, new Class<?>[]{TokenSequenceStemIrregularPipe.class});
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
     * Process instances and apply steemer to them, destructively modify them
     * and replace with its root.
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
            //Apply stemmer to each word
            for (int i = 0; i < ts.size(); i++) {
                String token = new String(Base64.getDecoder().decode(ts.getToken(i).substring(3)));
                String tokenRoot = extractRoot(token, lang.toLowerCase());
                if (!tokenRoot.equals("")) {
                    ret.add("tk:" + new String(Base64.getEncoder().encode(tokenRoot.getBytes())));
                    if (dictionary.getEncode()) {
                        dictionary.replace("tk:" + dictionary.encodeBase64(token), "tk:" + dictionary.encodeBase64(tokenRoot));
                    } else {
                        dictionary.replace(token, tokenRoot);
                    }
                } else {
                    ret.add("tk:" + new String(Base64.getEncoder().encode(token.getBytes())));
                }
            }
        }
        carrier.setData(ret);

        return carrier;
    }

    /**
     * Check if the given letter a vowel
     *
     * @param c letter to check
     * @return True if the letter is a vowel, false otherwise.
     */
    private boolean isVowel(char c) {
        return (c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u') || (c == 'á') || (c == 'é') || (c == 'í') || (c == 'ó') || (c == 'ú');
    }

    /**
     * Calculate the number of syllables of a word
     *
     * @param word Word to process
     * @return Numer of syllabes of a word
     */
    private int wordSize(String word) {
        int result = 0;
        int state = 0;
        int i = 0;
        while (i < word.length()) {
            switch (state) {
                case 0:
                    state = (isVowel(word.charAt(i))) ? 1 : 2;
                    break;
                case 1:
                    state = (isVowel(word.charAt(i))) ? 1 : 2;
                    if (state == 2) {
                        result++;
                    }
                    break;
                case 2:
                    state = (isVowel(word.charAt(i)) || ('y' == word.charAt(i))) ? 1 : 2;
                    break;
            }
            i++;
        }
        return result;
    }

    /**
     * Check if the word contains a vowel
     *
     * @param word Word to check
     * @return True if the word contains a vowel, false otherwise.
     */
    private boolean containsVowel(String word) {
        if (word.equals("")) {
            return false;
        }
        int i;
        boolean cond1, cond2;
        String vocales = "aeiouáéíóú";

        cond1 = isVowel(word.charAt(0));
        i = 1;
        cond2 = false;
        while ((i < word.length()) && (!cond2)) {
            if (vocales.indexOf(word.charAt(i)) != -1) {
                cond2 = true;
            } else {
                i++;
            }
        }
        return (cond1 || cond2);
    }

    /**
     * Check if word ends with <Consonant><Vowel><Consonant>
     *
     * @param word Word to check
     * @return True if word ends with CVC, false otherwise.
     */
    private boolean endsWithCVC(String word) {

        String C1 = "aeiouáéíóúwxy";
        String C2 = "aeiouáéíóúy";
        String C3 = "aeiouáéíóú";

        int l = word.length();

        if (l < 3) {
            return false;
        }

        l--;
        return ((C1.indexOf(word.charAt(l)) == -1)
                && (C2.indexOf(word.charAt(l - 1)) != -1)
                && (C3.indexOf(word.charAt(l - 2)) == -1));
    }

    /**
     * Check if you have to delete an "e" to apply a rule
     *
     * @param word Word to delete an "e"
     * @return True if you have to delete an "e". False otherwise.
     */
    private boolean removeAnE(String word) {
        return ((wordSize(word) == 1) && !endsWithCVC(word));
    }

    /**
     * Apply a pass over strings with a set of rules
     *
     * @param rules Set of rules applied
     * @return String with partial result
     */
    private String replaceEnd(List<Rule> rules, String processWord) {
        String ending;
        String tmp_ch = "";
        int i = 0;

        String returnValue = processWord;
        while (i < rules.size()) {
            // check if the root word matched with some root rule

            if (returnValue.length() - rules.get(i).getOldSuffix().length() >= 0) {
                ending = returnValue.substring(returnValue.length() - rules.get(i).getOldSuffix().length());

                if (!ending.equals("")) {
                    if (rules.get(i).getOldSuffix().equals(ending)) {
                        if (returnValue.endsWith(rules.get(i).getOldSuffix())) {
                            tmp_ch = ending;
                            returnValue = returnValue.substring(0, returnValue.length() - rules.get(i).getOldSuffix().length()); // elimina el sufijo a la palabra
                            if (rules.get(i).getMinRootSize() < wordSize(returnValue)) {   // si el tamaño de la raíz permite el cambio
                                if ((rules.get(i).getCondition() == -1) || ((rules.get(i).getCondition() == 1) && (containsVowel(returnValue)))
                                        || ((rules.get(i).getCondition() == 2) && (removeAnE(returnValue)))) { // si hay que aplicar alguna condición
                                    returnValue += rules.get(i).getNewSuffix();
                                    break;
                                }
                            }
                            ending = tmp_ch; // Restore suffix
                            returnValue += ending;
                        }
                    }
                }
            }

            i++;
        }
        return returnValue;
    }

    /**
     * Removes all non-vocal or consonant characters
     *
     * @param str String to clean
     * @return Cleaned string
     */
    private String cleanUpText(String str) {
        int last = str.length();

        String ret = "";

        for (int i = 0; i < last; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) || isVowel(str.charAt(i))) {
                ret += str.charAt(i);
            }
        }
        return ret;
    }

    /**
     * Perform word stemming, using the porter algorithm with 4 steps of
     * stemming
     *
     * @param word Word to process
     * @return String after steamming
     */
    private String stem(String word, String lang) {

        String ret = word;

        rules = RULES_FILES.get(lang);
        if (rules != null) {
            //Process rules
            for (int i = 0; i < rules.size(); i++) {
                String original = ret;

                ret = replaceEnd(rules.get(i).getObj2(), ret);
                if (!ret.equals(original)) {
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Remove accents in current word
     *
     * @param word Word to remove accents
     * @return String without accents
     */
    private String removeAccents(String word) {
        String tmp = word;

        tmp = tmp.replace('á', 'a');
        tmp = tmp.replace('é', 'e');
        tmp = tmp.replace('í', 'i');
        tmp = tmp.replace('ó', 'o');
        tmp = tmp.replace('ú', 'u');
        return tmp;
    }

    /**
     * Extract root from the given word
     *
     * @param word Word from which the root is extracted
     * @param lang Language of the text
     * @return String with the extracted root.
     */
    public String extractRoot(String word, String lang) {
        String returnValue = word;
        String langUp = lang.toUpperCase();
        returnValue = returnValue.toLowerCase();
        returnValue = cleanUpText(returnValue);

        if (RULES_FILES.containsKey(langUp)) {
            returnValue = stem(returnValue, langUp);
        }
        removeAccents(returnValue);
        return returnValue;
    }
}
