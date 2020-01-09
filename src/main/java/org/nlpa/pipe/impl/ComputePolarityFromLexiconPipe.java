package org.nlpa.pipe.impl;

import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;


/**
 * This pipe detects the sentiment polarity of the text using lexicons and adds the polarity as an instance property.
 * The polarity score is within the range [-1.0, 1.0].
 * The property that stores the language of text has to exist.
 * 
 * @author Patricia Martin Perez
 */
@PropertyComputingPipe
public class ComputePolarityFromLexiconPipe extends AbstractPipe {
	
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
		
		super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class}, new Class<?>[0]);
		
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
     * processing
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
		StringBuffer data = (StringBuffer) carrier.getData();
		
		
		carrier.setProperty(polarityProp, 0d);
		System.out.println("Polaridad");
		return carrier;
	}
	


}
