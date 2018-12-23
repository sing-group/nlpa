package org.ski4spam.pipe.impl;

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.ski4spam.types.SynsetVector;
import org.bdp4j.util.Pair;
import org.ski4spam.util.unmatchedtexthandler.ObfuscationHandler;
import org.ski4spam.util.unmatchedtexthandler.TyposHandler;
import org.ski4spam.util.unmatchedtexthandler.UnmatchedTextHandler;
import org.ski4spam.util.unmatchedtexthandler.UrbanDictionaryHandler;
import org.ski4spam.types.SynsetDictionary;
import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

import org.ski4spam.util.BabelUtils;

import org.bdp4j.pipe.PipeParameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A pipe to compute synsets from text
 *
 * @author Iñaki Velez
 * @author Enaitz Ezpeleta
 * @author José Ramón Méndez
 */
@TransformationPipe()
public class StringBuffer2SynsetVectorPipe extends Pipe {
    /**
     * For loggins purposes
     */
    private static final Logger logger = LogManager.getLogger(StringBuffer2SynsetVectorPipe.class);

    /**
     * An array of UnmatchedTextHandlers to fix incorrect text fragments
     */
    UnmatchedTextHandler vUTH[] = {new UrbanDictionaryHandler(), new TyposHandler(), new ObfuscationHandler()};

    /**
     * The name of the property where the language is stored
     */
    private String langProp = DEFAULT_LANG_PROPERTY;

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
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
        return SynsetVector.class;
    }

    /**
     * Stablish the name of the property where the language will be stored
     *
     * @param langProp The name of the property where the language is stored
     */
    @PipeParameter(name = "langpropname", description = "Indicates the property name to store the language", defaultValue = DEFAULT_LANG_PROPERTY)
    public void setLangProp(String langProp) {
        this.langProp = langProp;
    }

    /**
     * Returns the name of the property in which the language is stored
     *
     * @return the name of the property where the language is stored
     */
    public String getLangProp() {
        return this.langProp;
    }

    /**
     * Create the pipe and initialize the synset dictionary. Please note that
     * the synset dictionary can be achieved by using the corresponding getter.
     *
     */
    public StringBuffer2SynsetVectorPipe() {
        super(new Class<?>[0],new Class<?>[0]);
    }

    /**
     * List of puntuation marks accepted on the beggining of a word
     */
    private static final String acceptedCharOnBeggining = "¿¡[(\"\'";
    private static Pattern acceptedCharOnBegginingPattern = Pattern.compile("^[¿¡\\[\\(\"\'][¿¡\\[\\(\"\']*");
    /**
     * List of puntuation marks accepted on the end of a word
     */
    private static final String acceptedCharOnEnd = ".,!?)];:\"\'";
    private static Pattern acceptedCharOnEndPattern = Pattern.compile("[\\.,!?\\)\\];:<>\"\'][\\.,!?\\)\\];:<>\"\']*$");

    /**
     * List of puntuation marks accepted on the middle of a word
     */
    private static final String acceptedCharOnMiddle = "/-.,;:";
    private static Pattern acceptedCharOnMiddlePattern = Pattern.compile("[\\/\\()\\)\\-\\.,;:<>][\\/\\-\\.,;:<>]*");

    /**
     * A pattern to detect puntuation marks
     */
    private Pattern puntMarkPattern = Pattern.compile("\\p{Punct}");

    /**
     * This method find fagments in text (str) thar are incorrect.
     *
     * @param str The original text
     * @param lang The language of the original text
     * @return A vector of pairs (T,R) where T is the incorrect fragment and R
     * will be the replacement (null now)
     */
    private ArrayList<Pair<String, String>> computeUnmatched(String str, String lang) {
        StringTokenizer st = new StringTokenizer(str, " \t\n\r\u000b\f");

        //The value that will be returned
        ArrayList<Pair<String, String>> returnValue = new ArrayList<Pair<String, String>>();

        while (st.hasMoreTokens()) {
            String current = st.nextToken().trim();

            Matcher matcher = puntMarkPattern.matcher(current);
            if (matcher.find()) { //We found a puntuation mark in the token
                //matcher.start() <- here is the index of the puntuation mark
                //We developed rules checking also the existence of term/terms in Babelnet

                //if do not fit the rules and/or not found in Babelnet
                //    returnValue.add(new Pair<String,String>(current,null));
                //To check the exitence of the term in BabelNet, we will 
                //create a class org.ski4spam.util.BabelNetUtils with  
                //static methods.
                int indexOfPuntMark = matcher.start();
                if (indexOfPuntMark == 0) { //The puntuation symbol is at the beggining
                    if (acceptedCharOnBeggining.indexOf(current.charAt(indexOfPuntMark)) == -1) {
                        returnValue.add(new Pair<String, String>(current, null));
                    } else {
                        Matcher innerMatcher = acceptedCharOnBegginingPattern.matcher(new String(current));
                        if (!BabelUtils.getDefault().isTermInBabelNet(innerMatcher.replaceFirst(""), lang)) {
                            returnValue.add(new Pair<String, String>(current, null));
                        }
                    }
                } else if (indexOfPuntMark == current.length() - 1) { //the puntuation symbol is at the end
                    if (acceptedCharOnEnd.indexOf(current.charAt(indexOfPuntMark)) == -1) {
                        returnValue.add(new Pair<String, String>(current, null));
                    } else {
                        if (!BabelUtils.getDefault().isTermInBabelNet(current.substring(0, indexOfPuntMark), lang)) {
                            returnValue.add(new Pair<String, String>(current, null));
                        }
                    }
                } else { //The puntuation symbol is in the middle
//                    if (acceptedCharOnMiddle.indexOf(current.charAt(indexOfPuntMark)) == -1 || acceptedCharOnEnd.indexOf(current.charAt(indexOfPuntMark)) == -1) {
                    if (acceptedCharOnMiddle.indexOf(current.charAt(indexOfPuntMark)) == -1 && acceptedCharOnEnd.indexOf(current.charAt(indexOfPuntMark)) == -1) {
                        returnValue.add(new Pair<String, String>(current, null));
                    } else {
                        Matcher innerMatcher = acceptedCharOnEndPattern.matcher(new String(current));
                        if (innerMatcher.find(indexOfPuntMark)) {
                            if (!BabelUtils.getDefault().isTermInBabelNet(innerMatcher.replaceFirst(""), lang)) {
                                returnValue.add(new Pair<String, String>(current, null));
                            }
                        } else {
                            //System.out.println("Term is "+current);
                            innerMatcher = acceptedCharOnMiddlePattern.matcher(new String(current));
                            if (innerMatcher.find()) {
                                String firstElement = current.substring(0, innerMatcher.start());
                                String lastElement = current.substring(innerMatcher.end(), current.length());
                                if (!BabelUtils.getDefault().isTermInBabelNet(firstElement, lang)
                                        || (innerMatcher.end() < current.length() - 1 && !BabelUtils.getDefault().isTermInBabelNet(lastElement, lang))) {
                                    returnValue.add(new Pair<String, String>(current, null));
                                }
                            } else {
                                returnValue.add(new Pair<String, String>(current, null));
                            }
                        }
                    }
                }
            } else {
                //We check if the term current exist in babelnet. 
                //if current is not found in Babelnet
                //    returnValue.add(new Pair<String,String>(current,null));
                if (!BabelUtils.getDefault().isTermInBabelNet(current, lang)) {
                    returnValue.add(new Pair<String, String>(current, null));
                }
            }

        }
        return returnValue;
    }

    /**
     * Try to fix terms that are incorrectly written (and are not found in
     * Wordnet) The original text should be fixed according with the
     * replacements made
     *
     * @param originalText The originalText to fix
     * @param unmatched A list of text fragments that should be tryed to fix.
     * The text fragments are in the form of a pair (T,R) where T is the
     * original fragment ant R the replacement (null originally). This method
     * should fill R with the suggested replacement
     * @return A string containing the original text fixed
     */
    private String handleUnmatched(String originalText, List<Pair<String, String>> unmatched, String lang) {
        //Implement the UnmatchedTextHandler interface and three specific implementations that are:
        //+ UrbanDictionaryHandler
        //+ TyposHandler
        //+ ObfuscationHandler
        String returnValue = new String(originalText);

        //The replacement should be done here
        //DONE develop these things (Moncho)
        for (Pair<String, String> current : unmatched) {
            for (int i = 0; current.getObj2() == null && i < vUTH.length; i++) {
                vUTH[i].handle(current, lang);
            }
            if (current.getObj2() != null) {
                returnValue.replace(current.getObj1(), current.getObj2());
            }
        }

        return returnValue;
    }

    /**
     * Create a synsetVector from text
     *
     * @param fixedText The text to transform into a synset vector
     * @param lang The language in which the original text is written
     * @return A vector of synsets. Each synset is represented in a pair (S,T)
     * where S stands for the synset ID and T for the text that matches this
     * synset ID
     */
    private ArrayList<Pair<String, String>> buildSynsetVector(String fixedText, String lang) {
        //Call Babelfy api to transform the string into a vector of sysnsets. 
        //The fisrt string in the pair is the synsetID from babelnet
        //The second string is the matched text
        //The dictionary (dict) should be updated by adding each detected synset in texts.

        //Query Babelnet
        ArrayList<Pair<String, String>> returnValue = BabelUtils.getDefault().buildSynsetVector(fixedText, lang);

        //Update dictionaries
        for (Pair<String, String> current : returnValue) {
            SynsetDictionary.getDictionary().add(current.getObj1());
        }

        return returnValue;
    }

    @Override
    /**
     * Compute synsets from text. This method get data from StringBuffer and
     * process instances:
     * <li>Invalidate instance if the language is not present</li>
     * <li>Get the list of unmatched texts</li>
     * <li>Process this texts to get matches</li>
     * <li>Build a synset vector</li>
     */
    public Instance pipe(Instance carrier) {
        SynsetVector sv = new SynsetVector((StringBuffer) carrier.getData());

        //Invalidate the instance if the language is not present
        //We cannot correctly represent the instance if the language is not present
        if (carrier.getProperty(langProp) == null || ((String) carrier.getProperty(langProp)).equalsIgnoreCase("UND")) {
            logger.error("Instance " + carrier.getName() + " cannot be transformed into a SynsetVector because language could not be determined. It has been invalidated.");
            carrier.invalidate();
            return carrier;
        }
      
        sv.setUnmatchedTexts(computeUnmatched(sv.getOriginalText(), ((String) carrier.getProperty(langProp)).toUpperCase()));

        if (sv.getUnmatchedTexts().size() > 0) {
            sv.setFixedText(handleUnmatched(
                    sv.getOriginalText(),
                    sv.getUnmatchedTexts(),
                    ((String) carrier.getProperty(langProp)).toUpperCase()
            )
            );
        } else {
            sv.setFixedText(sv.getOriginalText());
        }

        sv.setSynsets(buildSynsetVector(sv.getFixedText(), ((String) carrier.getProperty(langProp)).toUpperCase()));

        carrier.setData(sv);

        logger.info("Instance processed: " + carrier.getName());

        return carrier;
    }
}
