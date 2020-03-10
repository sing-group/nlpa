package org.nlpa.pipe.impl;

import org.junit.Test;
import org.nlpa.pipe.impl.ComputePolarityFromLexiconPipe;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ComputePolarityFromLexiconPipeUnit {

	public HashMap<String, Double> sentences = new HashMap<String, Double>();
	
	
	@Test
	public void testComputePositivePolarity() {
		
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
	            new ComputePolarityFromLexiconPipe()
	        });
		
		
		StringBuffer sentence = new StringBuffer();
		Instance instance = new Instance(sentence, "polarity", "Test instance ID", sentence);
		
		 
		Set<String> sentencesKeys = sentences.keySet();
		Iterator<String> iterator = sentencesKeys.iterator();

		while (iterator.hasNext()) {
			String text = (String) iterator.next();
			sentence = new StringBuffer();
			sentence.append(text);

			instance.setData(sentence);

			Instance resultInstance = p.pipe(instance);

			double polarity = (double) resultInstance.getProperty("polarity");
			assertThat(polarity, is(sentences.get(text)));
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
		
		//Test ngrams
		this.sentences.put("Esto es el argumento de un numero complejo y es muy difícil", -0.75);
		this.sentences.put("Esto es un argumento en general", 0.00);
		this.sentences.put("Una pelicula sin sentimiento y sin alegría", -0.75);
		
		//Test text with several sentences
		//TODO

	}
	
}