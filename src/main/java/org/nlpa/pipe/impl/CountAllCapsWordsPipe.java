package org.nlpa.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;

import com.google.auto.service.AutoService;

/**
 * This pipe count the number of words with all caps.
 *
 * @author Patricia Martin Perez
 */
@PropertyComputingPipe
@AutoService(Pipe.class)
public class CountAllCapsWordsPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CountAllCapsWordsPipe.class);

    /**
     * The default property name where the number of all caps words will be stored
     */
    public static final String DEFAULT_ALL_CAPS_PROPERTY = "allCaps";

    /**
     * The name of the property where the number of all caps words is stored
     */
    private String allCapsProperty = DEFAULT_ALL_CAPS_PROPERTY;

    /**
     * Construct a CountAllCapsWordsPipe instance
     *
     */
    public CountAllCapsWordsPipe() {
        super(new Class<?>[0], new Class<?>[0]);

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
     * Indicates the datatype expected in the data attribute of an Instance
     * after processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Establish the name of the property where the number of all caps words will be stored
     *
     * @param allCapsProperty The name of the property where the number of all caps words
     */
    @PipeParameter(name = "allCapsProperty", description = "Indicates the property name to store the number of all caps words", defaultValue = DEFAULT_ALL_CAPS_PROPERTY)
    public void setAllCapsProperty(String allCapsProperty) {
        this.allCapsProperty = allCapsProperty;
    }

    /**
     * Retrieves the property name for storing the number of all caps words
     *
     * @return String containing the property name for storing the number of all caps words
     */
    public String getAllCapsProperty() {
        return allCapsProperty;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
        	int allCaps = 0;
            String text = carrier.getData().toString();
            String[] words = text.split(" ");
            words = cleanWords(words); 
            
            for(String word : words) {
            	int uppercase = 0;
            	for(int i=0; i<word.length(); i++) {
            		if(Character.isUpperCase(word.charAt(i))) {
            			uppercase++;
            		}
            	}
            	if(uppercase != 0 && uppercase == word.length()) {
            		allCaps++;
            	}
            }
            
            carrier.setProperty(allCapsProperty, allCaps);
            
        } else {
            logger.error("Data should be a StringBuffer when processing " + carrier.getName() + " but is a "
                    + carrier.getData().getClass().getName());
        }

        return carrier;
    }

    
	/**
	 * Remove symbols from text.
	 *
	 * @param words the array of words to clean
	 * 
	 * @return the words already cleaning
	 */
	public String[] cleanWords(String[] words) {
		String[] cleanWords = new String[words.length]; 

		String pattern = "[^\\p{L}\\p{Nd}]+";
		int cont = 0;
		for(String word : words) {
			cleanWords[cont] = word.replaceAll(pattern, "");
			cont++;
		}

		return cleanWords;
	}
}
