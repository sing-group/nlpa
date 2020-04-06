package org.nlpa.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.nlpa.types.SynsetSequence;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * This pipe detects the sentiment polarity of a synset sequence using lexicon and adds
 * the polarity as an instance property. The polarity score is within the range
 * [-1.0, 1.0].
 * 
 * @author Patricia Martin Perez
 */
@PropertyComputingPipe
public class ComputeSynsetPolarityFromSynsetSequencePipe extends AbstractPipe {

	/**
	 * For logging purposes
	 */
	private static final Logger logger = LogManager.getLogger(ComputeSynsetPolarityFromSynsetSequencePipe.class);
	
	/**
	 * Total sum of the 3 polarities scores: Positive + Negative + Objective
	 */
	private static final double TOTAL_POLARITY_SCORE = 1.0;

	/**
	 * A hashset of polarities.
	 */
	private static final HashMap<String, double[]> htPolarities = new HashMap<>();

	static {
			try {
				InputStream is = ComputeSynsetPolarityFromSynsetSequencePipe.class.getResourceAsStream("/lexicon-json/lexicon_babelnet.json");
				JsonReader jsonReader = Json.createReader(is);
				JsonObject jsonObject = jsonReader.readObject();
				jsonReader.close();

				for (String word : jsonObject.keySet()) {
					double[] doubleArray = new double[2];
					JsonArray jsonArray = (JsonArray) jsonObject.get(word);
					JsonValue posValue = jsonArray.get(0);
					JsonValue negValue = jsonArray.get(1);
					doubleArray[0] = new Double(posValue.toString());
					doubleArray[1] = new Double(negValue.toString());
					htPolarities.put(word, doubleArray);
				}

			} catch (Exception e) {
				logger.error("Exception processing: synset lexicon message " + e.getMessage());
			}

	}

	/**
	 * The default property name where the polarity will be stored
	 */
	public static final String DEFAULT_POLARITY_PROPERTY = "synsetPolarity";

	/**
	 * The name of the property where the polarity is stored
	 */
	private String polarityProp = DEFAULT_POLARITY_PROPERTY;

	/**
	 * Construct a ComputePolarityFromLexicon instance
	 *
	 */
	public ComputeSynsetPolarityFromSynsetSequencePipe() {
		super(new Class<?>[0], new Class<?>[0]);

	}

	/**
	 * Return the input type included the data attribute of an Instance
	 *
	 * @return the input type for the data attribute of the Instance processed
	 */
	@Override
	public Class<?> getInputType() {
		return SynsetSequence.class;
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
		return SynsetSequence.class;
	}


	@Override
	public Instance pipe(Instance carrier) {
		if (carrier.getData() instanceof SynsetSequence) {

			SynsetSequence data = (SynsetSequence) carrier.getData();

			HashMap<String, double[]> dict = htPolarities;
			// When there is not a lexicon
			if (dict == null) {
				carrier.setProperty(polarityProp, 0.0);
				return carrier;
			}
			
			double polarity = computePolarity(data, dict);
			double polarityDecimalFormat = (double) Math.round(polarity * 100) / 100;
			carrier.setProperty(polarityProp, polarityDecimalFormat);

		} else {
			logger.error("Data should be a SynsetSequence when processing " + carrier.getName() + " but is a "
					+ carrier.getData().getClass().getName());
		}

		return carrier;
	}
	
	
	/**
	 * Compute the polarity of a synset sequence. For each synset gets its polarity.
	 *
	 * @param data which contains the synset sequence
	 * @param dict the lexicon
	 * 
	 * @return polarity of the synset sequence
	 */
	private double computePolarity(SynsetSequence data, HashMap<String, double[]> dict) {
		double totalPolarityScore = 0.0;
		double polarityScore = 0.0;
		int words = 0;
		
		List<Pair<String, String>> synsetsData = data.getSynsets();

		Iterator<Pair<String,String>> synsets = synsetsData.iterator();
	     while(synsets.hasNext()){
	    	 Pair<String, String> pair = synsets.next();
	    	 double[] polarity = dict.get(pair.getObj1());
	    	 
	    	 if (polarity != null) {
	 
	    		 polarityScore = getScorePolarity(polarity[0], polarity[1]);
	    		 if(polarityScore != 0) {
	    			 words++;
	    		 }
	    		 
	    		 totalPolarityScore += polarityScore;
			}
	    	 
	    	 
	     }
	     
		if(words > 0) {
			totalPolarityScore = totalPolarityScore/words;
		}
		
		return totalPolarityScore;
		
	}
	
	/**
	 * Get the polarity of a synset. Considering positive, negative and neutral 
	 * scores, the highest score is choosen.
	 *
	 * @param posScore positive score of the synset
	 * @param negScore negative score of the synset
	 * 
	 * @return polarity of the synset
	 */
	private double getScorePolarity(double posScore, double negScore) {
		double polarityScore = 0;
		// The objectivity score
		double objScore = ComputeSynsetPolarityFromSynsetSequencePipe.TOTAL_POLARITY_SCORE - (posScore + negScore);

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


}
