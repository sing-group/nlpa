package org.nlpa.pipe.impl;
import org.junit.Ignore;
import org.junit.Test;
import org.nlpa.types.FeatureVector;
import org.nlpa.types.SequenceGroupingStrategy;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ComputePolarityFromSynsetPipeTest {
	
	public HashMap<String, Double> sentences = new HashMap<String, Double>();
	
	@Test
	public void testComputePositivePolarity() {
		
		addSentences();

		AbstractPipe p = new SerialPipes(new AbstractPipe[]{
				new TargetAssigningFromPathPipe(),
                new StoreFileExtensionPipe(), 
                new GuessDateFromFilePipe(), 
                new File2StringBufferPipe(),
                new MeasureLengthFromStringBufferPipe(),
                new FindUrlInStringBufferPipe(),
                new StripHTMLFromStringBufferPipe(),
                new MeasureLengthFromStringBufferPipe("length_after_html_drop"), 
                new GuessLanguageFromStringBufferPipe(),
                new StringBufferToLowerCasePipe(), 
                new InterjectionFromStringBufferPipe(),
                new StopWordFromStringBufferPipe(),
                new TeeCSVFromStringBufferPipe("output.csv", true), 
                new StringBuffer2SynsetSequencePipe(),
                new SynsetSequence2FeatureVectorPipe(SequenceGroupingStrategy.COUNT),
                new ComputePolarityFromSynsetPipe()
	        });
		
		StringBuffer sentence = new StringBuffer();
		Instance instance = new Instance(sentence, "polarity", "Test instance ID", sentence);
		
		Set<String> featureKeys = sentences.keySet();
		Iterator<String> iterator = featureKeys.iterator();

		while (iterator.hasNext()) {
			String feature = (String) iterator.next();
			sentence = new StringBuffer();
			sentence.append(feature);
			
			instance.setData(sentence);

			Instance resultInstance = p.pipe(instance);

			double polarity = (double) resultInstance.getProperty("synsetPolarity");
			assertThat(polarity, is(this.sentences.get(feature)));
		}
		
		
	}
	
	
	public void addSentences() {
		this.sentences.put("This couch is beautiful and comfortable", 0.46);
		
	}
	
	
}