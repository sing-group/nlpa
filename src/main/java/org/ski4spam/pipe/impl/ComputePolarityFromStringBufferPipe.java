package org.ski4spam.pipe.impl;

import java.io.File;
import java.util.Properties;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

import org.bdp4j.pipe.PipeParameter;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;


/**
 * This pipe adds the polarity of the text as instance property.
 *
 * @author José Ramón Méndez 
 */
@PropertyComputingPipe()
public class ComputePolarityFromStringBufferPipe extends Pipe {
	/**
	  * Initing a StandfordCoreNLP pipeline
	  */
	static StanfordCoreNLP pipeline;
	static Properties props;
	static {
		props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}

   /**
    * Return the input type included the data attribute of a Instance
    * @return the input type for the data attribute of the Instances processed
    */
    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after processing
     * @return the datatype expected in the data attribute of a Instance after processing
     */
    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }

    /**
		* The default property name to store the polarity
		*/
    public static final String DEFAULT_POLARITY_PROPERTY="polarity";

    /**
		* The property name to store the polarity
		*/
    private String polProp=DEFAULT_POLARITY_PROPERTY;

	 /**
		* Sets the property where the polarity will be stored
		* @param polProp the name of the property for the polarity
		*/
	 @PipeParameter(name = "polpropname", description = "Indicates the property name to store the polarity", defaultValue=DEFAULT_POLARITY_PROPERTY)    
    public void setExtensionProp(String polProp) {
        this.polProp = polProp;
    }
	 
	 /**
		* Retrieves the property name for storing the file polarity
		* @return String containing the property name for storing the file polarity
		*/
    public String getPolarityProp(){
        return this.polProp;
    }

    /**
		* Default constructor
		*/
    public ComputePolarityFromStringBufferPipe() {
    }

    /**
		* Build a StoreFileExtensionPipe that stores the polarity of the file in the property polProp
		* @param polProp The name of the property to extore the file polarity
		*/
    public ComputePolarityFromStringBufferPipe(String polProp) {
        this.polProp = polProp;
    }

    /**
    * Process an Instance.  This method takes an input Instance,
    * destructively modifies it in some way, and returns it.
    * This is the method by which all pipes are eventually run.
    *
    * @param carrier Instance to be processed.
    * @return Instancia procesada
    */
    @Override
    public Instance pipe(Instance carrier) {
		  System.out.print("Processing: "+carrier.getName()+" ... ");
		  int mainSentiment = 0;
        if (carrier.getData() instanceof StringBuffer) {

			String text=carrier.getData().toString().replaceAll("[^\\p{Space}\\p{Print}]","");
			System.out.print("(size: "+text.length()+") ");			
	  		if (text != null && text.length() > 0) {
	  			int longest = 0;
	  			Annotation annotation = pipeline.process(text);
	  			for (CoreMap sentence : annotation
	  					.get(CoreAnnotations.SentencesAnnotation.class)) {
	  				Tree tree = sentence
	  						.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
	  				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	  				String partText = sentence.toString();
	  				if (partText.length() > longest) {
	  					mainSentiment = sentiment;
	  					longest = partText.length();
	  				}

	  			}
	  		}
        }
        carrier.setProperty(polProp, mainSentiment);		  
		  System.out.println("done: polarity="+mainSentiment+".");
        return carrier;
    }
}
