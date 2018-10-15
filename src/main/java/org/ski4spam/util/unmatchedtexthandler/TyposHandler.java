package org.ski4spam.util.unmatchedtexthandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.languagetool.JLanguageTool;
import org.languagetool.language.Russian;
import org.languagetool.Language;
import org.languagetool.language.*;
import org.languagetool.rules.RuleMatch;
import org.ski4spam.Main;
import org.ski4spam.util.Pair;
import pt.tumba.spell.SpellChecker;

/**
 * Search words/text matches with two different tools:
 * <li>JLanguageTool:&nbsp;A tool that offers spell and grammar checking.</li>
 * <li>JaSpell:&nbsp;This tool is a Java spelling checking package./li>
 */
public class TyposHandler extends UnmatchedTextHandler {

    private static final Logger logger = LogManager.getLogger(TyposHandler.class);
    private final static Map<String, Class<? extends Language>> LANGUAGE_CLASSES = new HashMap<>();

    /* Define the list of possible languages to use*/
    static {

        LANGUAGE_CLASSES.put("es", Spanish.class);
        LANGUAGE_CLASSES.put("en", BritishEnglish.class);
//        LANGUAGE_CLASSES.put("en", AmericanEnglish.class);
//        LANGUAGE_CLASSES.put("en", CanadianEnglish.class);
//        LANGUAGE_CLASSES.put("en", AustralianEnglish.class);
//        LANGUAGE_CLASSES.put("en", NewZealandEnglish.class);
//        LANGUAGE_CLASSES.put("en", SouthAfricanEnglish.class);
        LANGUAGE_CLASSES.put("zh", Chinese.class);
        LANGUAGE_CLASSES.put("nl", Dutch.class);
        LANGUAGE_CLASSES.put("ja", Japanese.class);
        LANGUAGE_CLASSES.put("de", GermanyGerman.class);
        LANGUAGE_CLASSES.put("it", Italian.class);
        LANGUAGE_CLASSES.put("pt", PortugalPortuguese.class);
        LANGUAGE_CLASSES.put("ru", Russian.class);
        LANGUAGE_CLASSES.put("bs", BosnianSerbian.class);
        LANGUAGE_CLASSES.put("br", Breton.class);
        LANGUAGE_CLASSES.put("ca", Catalan.class);
        LANGUAGE_CLASSES.put("sr", CroatianSerbian.class);
        LANGUAGE_CLASSES.put("eo", Esperanto.class);
        LANGUAGE_CLASSES.put("gl", Galician.class);
        LANGUAGE_CLASSES.put("el", Greek.class);
        LANGUAGE_CLASSES.put("sr", JekavianSerbian.class);
        LANGUAGE_CLASSES.put("sr", MontenegrinSerbian.class);
        LANGUAGE_CLASSES.put("fa", Persian.class);
        LANGUAGE_CLASSES.put("pl", Polish.class);
        LANGUAGE_CLASSES.put("ro", Romanian.class);
        LANGUAGE_CLASSES.put("sr", Serbian.class);
        LANGUAGE_CLASSES.put("sk", Slovak.class);
        LANGUAGE_CLASSES.put("uk", Ukrainian.class);
        LANGUAGE_CLASSES.put("ca", ValencianCatalan.class);
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
        try {

            String originalString = text.getObj1();
            String replacementString = text.getObj2();
            //originalString = originalString.replaceAll("\\.()¿?!¡;,<>\"", "");

            lang = lang.toLowerCase();
            if (replacementString == null) {
                String matchedString = getJLanguageToolMatch(originalString, lang);
                
                if (matchedString != null) {
                    text.setObj2(matchedString);
                } else {
                    matchedString = getJaSpellMatch(originalString, lang);
                    if (matchedString != null) {
                        text.setObj2(matchedString);
                        System.out.println("JaSpellMatch: " + text.getObj1() + " - " + text.getObj2());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR " + Main.class.getName() + ". " + ex.getMessage());
        }
    }

    /**
     * Return the word that matches with the parameter, using JLanguageTool
     *
     * @param originalString Word to get match in resources files
     * @return String who contains , if exists, the match word. In other case,
     * return String. If there aren't matches, return null.
     */
    private String getJLanguageToolMatch(String originalString, String lang) {
        try {
            if (hasLanguage(lang)) {
                JLanguageTool langTool = new JLanguageTool(getLanguage(lang));
                try {
                    List<RuleMatch> matches = langTool.check(originalString, false, JLanguageTool.ParagraphHandling.NORMAL);
                    originalString = originalString.replaceAll("\\p{Punct}","");
                    if (!matches.isEmpty()) {
                        /* Valorar si compensa perder el tiempo en la comprobación */
//                        if (matches.get(0).getMessage().equals("This sentence does not start with an uppercase letter")){
//                            return originalString;
//                        }
                        
                        List matchesList = matches.get(0).getSuggestedReplacements();
                        if (!matchesList.isEmpty()) {
                            System.out.println("JLanguageToolMatch: " +lang.toUpperCase()+" >>"+ originalString + "<>" + matchesList.get(0).toString().toLowerCase());
                            return matchesList.get(0).toString().toLowerCase();
                        }
                    } else {
                        return originalString;
                    }

                } catch (IOException ex) {
                    logger.warn(Main.class.getName() + ". " + ex.getMessage());
                }
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError ex) {
            logger.error("ERROR " + Main.class.getName() + ". getJLanguageToolMatch: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Return the word that matches with the parameter, using JaSpell
     *
     * @param originalString Word to get match in resources files
     * @return String who contains , if exists, the match word. In other case,
     * return String. If there aren't matches, return null.
     */
    private String getJaSpellMatch(String originalString, String lang) {
        try (Reader dictionaryReader = new InputStreamReader(Main.class.getResourceAsStream("/dict/" + lang + ".txt"))) {

            SpellChecker spellCheck = new SpellChecker();
            spellCheck.initialize(dictionaryReader);
            return spellCheck.findMostSimilar(originalString);

        } catch (Exception ex) {
            logger.error("ERROR " + Main.class.getName() + ". getJaSpellMatch: " + ex.getMessage());
            return null;
        }
    }
}
