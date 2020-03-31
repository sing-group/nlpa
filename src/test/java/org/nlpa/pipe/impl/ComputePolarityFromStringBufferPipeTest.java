package org.nlpa.pipe.impl;

import org.junit.Test;
import org.nlpa.pipe.impl.ComputePolarityFromStringBufferPipe;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class ComputePolarityFromStringBufferPipeTest {

	public HashMap<String, Double> sentences = new HashMap<String, Double>();
	
	@Test
	public void testNotStringBufferData() {

		ComputePolarityFromStringBufferPipe polarityPipe = new ComputePolarityFromStringBufferPipe();
		Instance instance = new Instance(new Integer(4), "polarity", "Test instance ID", new Integer(4));

		Instance resultInstance = polarityPipe.pipe(instance);
		Object polarity = resultInstance.getProperty("polarity");
		assertNull(polarity);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullInstance() {

		Instance instance = null;
		new ComputePolarityFromStringBufferPipe().pipe(instance);

	}
		
	
	@Test
	public void testComputePolarity() {
		
		addSentences();

		AbstractPipe p = new SerialPipes(new AbstractPipe[]{new TargetAssigningFromPathPipe(),
	            new StoreFileExtensionPipe(), 
	            new GuessDateFromFilePipe(), 
	            new File2StringBufferPipe(),
	            new MeasureLengthFromStringBufferPipe(),
	            new FindUrlInStringBufferPipe(),
	            new StripHTMLFromStringBufferPipe(),
	            new MeasureLengthFromStringBufferPipe("length_after_html_drop"), 
	            new GuessLanguageFromStringBufferPipe(),
	            new ContractionsFromStringBufferPipe(),
	            new AbbreviationFromStringBufferPipe(),
	            new SlangFromStringBufferPipe(),
	            new StringBufferToLowerCasePipe(),
	            new ComputePolarityFromStringBufferPipe()
	        });
				
		 
		Set<String> sentencesKeys = sentences.keySet();
		Iterator<String> iterator = sentencesKeys.iterator();

		while (iterator.hasNext()) {

			String text = (String) iterator.next();
			double sentencePolarity = sentences.get(text);
			
			Instance instance = new Instance(new StringBuffer(text), "polarity", "Test instance ID", new StringBuffer(text));

			Instance resultInstance = p.pipe(instance);

			double instancePolarity = (double) resultInstance.getProperty("polarity");
			assertThat("Sentence <" + text + ">", instancePolarity, is(equalTo(sentencePolarity)));
		}

	}
	
	public void addSentences() {

		// Test different dictionaries
		this.sentences.put("This couch is beautiful and comfortable", 0.99);
		this.sentences.put("Este sofá es bonito y cómodo", 0.0);
		this.sentences.put("Ce canapé est beau et confortable", 0.06);
		this.sentences.put("Questo divano è bello e comodo", 0.0);
		this.sentences.put("Diese Couch ist schön und bequem", 0.24);
		this.sentences.put("Этот диван красивый и удобный", 0.0);

		//Test negative words
		this.sentences.put("Esto no es muy difícil", 0.75);
		this.sentences.put("Esto no es difícil", 0.75);
		
		//Test booster words
		this.sentences.put("Esto no es muy difícil", 1.0);
		this.sentences.put("This is very difficult", -0.89);
		this.sentences.put("Esto no es difícil", 0.75);
		this.sentences.put("Esto es difícil", -0.75);
		this.sentences.put("Esto es muy muy difícil", -1.0);
		
		//Test ngrams
		this.sentences.put("Esto es el argumento de un numero complejo y es muy difícil", -1.0);
		this.sentences.put("Esto es un argumento en general", 0.00);
		this.sentences.put("Una pelicula sin sentimiento y sin alegría", -0.75);
		
		//Test text with several sentences
		this.sentences.put("This isn't difficult. This couch is beautiful and comfortable", 0.89);
		this.sentences.put("This isn't difficult. This is beautiful, knottiness, effectuality, wiliness", 1.0);
		
		//Test weird text
		this.sentences.put(". . ", 0.0);
		this.sentences.put("Este móvil es bonito pero funciona bien", 0.0);

	
	}
	
}