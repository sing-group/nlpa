package org.ski4spam.util.unmatchedtexthandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.*;
import org.languagetool.rules.RuleMatch;
import org.ski4spam.Main;
import org.ski4spam.util.Pair;
import pt.tumba.spell.SpellChecker;

/**
 * This class search
 */
public class TyposHandler extends UnmatchedTextHandler {

    private static final Logger logger = LogManager.getLogger(TyposHandler.class);
    private final static Map<String, Class<? extends Language>> LANGUAGE_CLASSES = new HashMap<>();
    private String lang;

    /* Define the list of possible languages to use*/
    static {
        LANGUAGE_CLASSES.put("es", Spanish.class);
        LANGUAGE_CLASSES.put("en", BritishEnglish.class);
        LANGUAGE_CLASSES.put("zh", Chinese.class);
        LANGUAGE_CLASSES.put("nl", Dutch.class);
        LANGUAGE_CLASSES.put("ja", Japanese.class);
        LANGUAGE_CLASSES.put("de", GermanyGerman.class);
        LANGUAGE_CLASSES.put("it", Italian.class);
        LANGUAGE_CLASSES.put("pt", PortugalPortuguese.class);
        LANGUAGE_CLASSES.put("ru", Russian.class);
    }

    /**
     * This method create instance of the language class corresponding with the
     * lang parameter.
     *
     * @param lang
     * @return the Language instance of the corresponding language. If the
     * language doesn't exists, return null.
     */
    private static Language getLanguage(String lang) {
        try {
            Class<? extends Language> languageClass = LANGUAGE_CLASSES.get(lang);

            return languageClass.getConstructor().newInstance();
        } catch (Exception ex) {
            logger.warn("The language " + lang + "doesn't exists in JlanguageTool. " + ex.getMessage());
            return null;
        }
    }

    private static boolean hasLanguage(String lang) {
        return LANGUAGE_CLASSES.containsKey(lang);
    }

    @Override
    public void handle(Pair<String, String> text, String lang) {

        String originalString = text.getObj1();
        String replacementString = text.getObj2();
        this.lang = lang.toLowerCase();
        if (replacementString == null) {
            String matchedString = getJLanguageToolMatch(originalString);
            if (matchedString != null) {
                text.setObj2(matchedString);
            } else {
                matchedString = getJaSpellMatch(originalString);
                if (matchedString != null) {
                    text.setObj2(matchedString);
                }
            }
        }
    }

    /**
     * Return the word that matches with the parameter, using JLanguageTool
     *
     * @param originalString Word to get match in resources files
     * @return String who contains , if exists, the match word. In other case,
     * return null.
     */
    private String getJLanguageToolMatch(String originalString) {
        if (hasLanguage(lang)) {
            JLanguageTool langTool = new JLanguageTool(getLanguage(lang));
            try {
                List<RuleMatch> matches = langTool.check(originalString);

                if (!matches.isEmpty()) {
                    List matchesList = matches.get(0).getSuggestedReplacements();
                    if (!matchesList.isEmpty()) {
                        return matchesList.get(0).toString();
                    }
                }

            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Return the word that matches with the parameter, using JaSpell
     *
     * @param originalString Word to get match in resources files
     * @return String who contains , if exists, the match word. In other case,
     * return null.
     */
    private String getJaSpellMatch(String originalString) {
            try (Reader dictionaryReader = new InputStreamReader(Main.class.getResourceAsStream("/dict/" + lang + ".txt"))) {

                SpellChecker spellCheck = new SpellChecker();
                spellCheck.initialize(dictionaryReader);
                return spellCheck.findMostSimilar(originalString);

            } catch (Exception ex) {
                logger.warn(Main.class.getName() + ". The language doesn't exists. " + ex.getMessage());
                return null;
            }
    }
}
