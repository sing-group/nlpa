package DefaultTest;
import org.junit.Ignore;
import org.junit.Test;
import org.nlpa.pipe.impl.ComputePolarityFromLexiconPipe;
import org.bdp4j.types.Instance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ComputePolarityFromLexiconPipeUnitTest {
	
	
	@Ignore
	@Test
	public void testComputePositivePolarity() {
		StringBuffer positiveSentence = new StringBuffer();
		positiveSentence.append("This is beautiful and useful");
		
		Instance inst = new Instance(positiveSentence, "polarity", 1, 1);
		
		inst.setProperty("language", "EN");
		ComputePolarityFromLexiconPipe pol = new ComputePolarityFromLexiconPipe();

		Instance carrier = pol.pipe(inst);
		
		double polarity = (double) carrier.getProperty("polarity");
		assertThat(polarity, is(0.782));
		
		
	}
	
	@Test
	public void testComputeNegativePolarity() {
		StringBuffer str = new StringBuffer();
		str.append("This is no beautiful and useful");
		
		Instance inst = new Instance(str, "polarity", 1, 1);
		
		inst.setProperty("language", "EN");
		ComputePolarityFromLexiconPipe pol = new ComputePolarityFromLexiconPipe();	
		
		Instance carrier = pol.pipe(inst);
		double polarity = (double) carrier.getProperty("polarity");
		
		assertThat(polarity, is(-0.782));
	}
	
}