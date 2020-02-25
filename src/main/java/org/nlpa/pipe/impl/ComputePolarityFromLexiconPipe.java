package org.nlpa.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * This pipe detects the sentiment polarity of the text using lexicons and adds
 * the polarity as an instance property. The polarity score is within the range
 * [-1.0, 1.0]. The property that stores the language of text has to exist.
 * 
 * @author Patricia Martin Perez
 */
@PropertyComputingPipe
public class ComputePolarityFromLexiconPipe extends AbstractPipe {

	/**
	 * For logging purposes
	 */
	private static final Logger logger = LogManager.getLogger(ComputePolarityFromLexiconPipe.class);
	
	/**
	 * Number of words that are modified by the word of negation that precede them
	 */
	private static final int NEGATION_WORDS_COUNT = 3;

	/**
	 * A hashset of polarities in different languages. NOTE: All JSON files (listed
	 * below) containing the lexicon
	 *
	 */
	private static final HashMap<String, HashMap<String, double[]>> htPolarities = new HashMap<>();

	static {
		for (String i : new String[] { "/lexicon-json/lexicon.en.json" }) {

			String lang = i.substring(22, 24).toUpperCase();
			try {
				InputStream is = ComputePolarityFromLexiconPipe.class.getResourceAsStream(i);
				JsonReader jsonReader = Json.createReader(is);
				JsonObject jsonObject = jsonReader.readObject();
				jsonReader.close();
				HashMap<String, double[]> dict = new HashMap<>();


				for (String word : jsonObject.keySet()) {
					double[] doubleArray = new double[2];
					JsonArray jsonArray = (JsonArray) jsonObject.get(word);
					JsonValue posValue = jsonArray.get(0);
					JsonValue negValue = jsonArray.get(1);
					doubleArray[0] = new Double(posValue.toString());
					doubleArray[1] = new Double(negValue.toString());
					dict.put(word, doubleArray);
				}

				dict.toString();
				htPolarities.put(lang, dict);
			} catch (Exception e) {
				logger.error("Exception processing: " + i + " message " + e.getMessage());
			}

		}

	}

	/**
	 * The default property name where the polarity will be stored
	 */
	public static final String DEFAULT_POLARITY_PROPERTY = "polarity";

	/**
	 * The name of the property where the language is stored
	 */
	private String langProp = DEFAULT_LANG_PROPERTY;
	/**
	 * The name of the property where the polarity is stored
	 */
	private String polarityProp = DEFAULT_POLARITY_PROPERTY;

	/**
	 * Construct a ComputePolarityFromLexicon instance
	 *
	 */
	public ComputePolarityFromLexiconPipe() {
		super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class, ContractionsFromStringBufferPipe.class,
			AbbreviationFromStringBufferPipe.class, SlangFromStringBufferPipe.class}, new Class<?>[0]);

	}

	/**
	 * Return the input type included the data attribute of an Instance
	 *
	 * @return the input type for the data attribute of the Instance processed
	 */
	@Override
	public Class<?> getInputType() {

		return StringBuffer.class;
	}

	/**
	 * Indicates the datatype expected in the data attribute of an Instance after
	 * processing
	 *
	 * @return the datatype expected in the data attribute of an Instance after
	 *         processing
	 */
	@Override
	public Class<?> getOutputType() {

		return StringBuffer.class;
	}

	/**
	 * Establish the name of the property where the language will be stored
	 *
	 * @param langProp The name of the property where the language is stored
	 */
	@PipeParameter(name = "langpropname", description = "Indicates the property name to store the language", defaultValue = DEFAULT_LANG_PROPERTY)
	public void setLangProp(String langProp) {
		this.langProp = langProp;
	}

	@Override
	public Instance pipe(Instance carrier) {
		if (carrier.getData() instanceof StringBuffer) {

			String lang = (String) carrier.getProperty(langProp);
			StringBuffer data = (StringBuffer) carrier.getData();

			HashMap<String, double[]> dict = htPolarities.get(lang);
			// When there is not a lexicon for the language
			if (dict == null) {
				carrier.setProperty(polarityProp, 2);
				return carrier;
			}

			double polarity = calculatePolarity(data.toString(), dict);
			double polarityDecimalFormat = (double) Math.round(polarity * 100) / 100;
			carrier.setProperty(polarityProp, polarityDecimalFormat);

		} else {
			logger.error("Data should be an StringBuffer when processing " + carrier.getName() + " but is a "
					+ carrier.getData().getClass().getName());
		}

		return carrier;
	}
	
	/**
	 * Calculate the polarity of a text. For each sentence gets the polarity of the words.
	 *
	 * @param data the text to calculate the polarity
	 * @param dict the lexicon based on the language of the text
	 * 
	 * @return polarity of the text
	 */
	private double calculatePolarity(String data, HashMap<String, double[]> dict) {
		double totalPolarity = 0.0d;
		double weightSentence = 0.0d;
		double totalWeightSentence = 0.0d;
		double polaritySentence = 0.0d;
		
		// Remove symbols from text
		String pattern = "[^a-zA-Z0-9.\\s]";
		String text = data.replaceAll(pattern, "");
	
		// Calculate the polarity for each sentence
		String[] sentences = {text};
		if (text.contains(".")) {
			sentences = text.split("\\.");
		} else {
			sentences[0] = text;
		}

		
		for (String sentence : sentences) {
			
			double polarityScore = 0.0d;
			double totalPolarityScore = 0.0d;
			int wordNum = 0;
			int negationWordNum = 0;
			boolean isNegation = false;
			String[] words = sentence.split(" ");

			for (String word : words) {

				if (checkNegationWord(word)) {
					isNegation = true;
					negationWordNum = 0;
				} else {
					double[] polarity = dict.get(word);
					
					if (polarity != null) {
						wordNum++;
						negationWordNum++;

						if (isNegation) {
							if (this.NEGATION_WORDS_COUNT == negationWordNum) {
								totalPolarityScore += polarityScore * (-1);
								isNegation = false;
							} else {
								polarityScore += getScorePolarity(polarity[0], polarity[1]);
							}

						} else {
							totalPolarityScore += getScorePolarity(polarity[0], polarity[1]);
							polarityScore = 0;
						}

					}
				}

			}
			
			if(polarityScore > 0) {
				totalPolarityScore += polarityScore * (-1);
			}
			
			if (wordNum > 0) { 
				
				weightSentence = wordNum / 5.0;
				totalWeightSentence += weightSentence;
				
				polaritySentence += (weightSentence * totalPolarityScore);
				
				totalPolarity = polaritySentence / totalWeightSentence;
			} else { 
				// if there is no word in the text that has polarity
				totalPolarity = 2;
			}

		}

		return totalPolarity;
	}
	
	
	/**
	 * Get the polarity of a word. Considering positive, negative and neutral 
	 * scores, the highest score is choosen.
	 *
	 * @param posScore positive score of the word
	 * @param negScore negative score of the word
	 * 
	 * @return polarity of the word
	 */
	private double getScorePolarity(double posScore, double negScore) {
		double polarityScore = 0;
		// The objectivity score
		double objScore = 1 - (posScore + negScore);

		if (objScore > (posScore + negScore)) {
			// Neutral
			polarityScore = 0;
		} else if (posScore > negScore) {
			// Positive
			polarityScore = posScore;
		} else {
			// Negative
			polarityScore = negScore * (-1);
		}

		return polarityScore;
	}
	
	
	/**
	 * Checks if the word is a negation word.
	 *
	 * @param word
	 * 
	 * @return true or false if the word is use for negation.
	 */
	private boolean checkNegationWord(String word) {
		//TODO add negative words for all languages
		Vector<String> negationWords = new Vector<>();
		negationWords.add("no");
		negationWords.add("not");
		negationWords.add("n't");
		negationWords.add("never");
		if (negationWords.contains(word)) {
			return true;
		}

		return false;
	}
	
}
