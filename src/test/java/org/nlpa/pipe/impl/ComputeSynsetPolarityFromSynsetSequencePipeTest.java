package org.nlpa.pipe.impl;
import org.junit.Test;
import org.nlpa.types.SequenceGroupingStrategy;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ComputeSynsetPolarityFromSynsetSequencePipeTest {
	
	public HashMap<String, Double> sentences = new HashMap<String, Double>();
	
	@Test
	public void testNotSynsetSequence() {

		ComputeSynsetPolarityFromSynsetSequencePipe polarityPipe = new ComputeSynsetPolarityFromSynsetSequencePipe();
		Instance instance = new Instance(new StringBuffer("Test text"), "polarity", "Test instance ID", new StringBuffer("Test text"));

		Instance resultInstance = polarityPipe.pipe(instance);
		Object polarity = resultInstance.getProperty("polarity");
		assertNull(polarity);
		
	}
	
	
	@Test(expected = NullPointerException.class)
	public void testNullInstance() {
		Instance instance = null;
		new ComputeSynsetPolarityFromSynsetSequencePipe().pipe(instance);

	}
	
	@Test
	public void testComputePolarity() {
		
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
                new FindEmojiInStringBufferPipe(),
                new FindEmoticonInStringBufferPipe(),
                new StringBufferToLowerCasePipe(), 
                new InterjectionFromStringBufferPipe(),
                new StringBuffer2SynsetSequencePipe(),
                new ComputeSynsetPolarityFromSynsetSequencePipe()
	        });
		
		
		
		Set<String> sentenceKeys = sentences.keySet();
		Iterator<String> sentencesIterator = sentenceKeys.iterator();

		while (sentencesIterator.hasNext()) {
			String sentence = (String) sentencesIterator.next();
			
			Instance instance = new Instance(new StringBuffer(sentence), "polarity", "Test instance ID", new StringBuffer(sentence));

			Instance resultInstance = p.pipe(instance);

			double polarity = (double) resultInstance.getProperty("synsetPolarity");
			assertThat(polarity, is(this.sentences.get(sentence)));
		}
		
		
	}
	
	
	public void addSentences() {

		this.sentences.put("This couch is beautiful and comfortable", 0.69);
		this.sentences.put("Este sofá es bonito y cómodo", 0.75);
		this.sentences.put("Ce canapé est beau et confortable", 0.0);
		this.sentences.put("Questo divano è bello e comodo", 0.0);
		this.sentences.put("Diese Couch ist schön und bequem", 0.63);
		this.sentences.put("Этот диван красивый и удобный", 0.0);
		this.sentences.put("This couch is beautiful and comfortable. This is beautiful, knottiness, effectuality, wiliness", 0.6);
		
	}
	
	
}