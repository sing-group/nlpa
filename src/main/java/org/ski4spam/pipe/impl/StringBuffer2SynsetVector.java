package org.ski4spam.pipe.impl;

import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.ski4spam.ia.types.SynsetVector;
import org.ski4spam.util.Pair;
import org.ski4spam.util.unmatchedtexthandler.ObfuscationHandler;
import org.ski4spam.util.unmatchedtexthandler.TyposHandler;
import org.ski4spam.util.unmatchedtexthandler.UnmatchedTextHandler;
import org.ski4spam.util.unmatchedtexthandler.UrbanDictionaryHandler;

import org.ski4spam.util.BabelUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A pipe to compute synsets from text
 *
 * @author Iñaki Velez
 * @author Enaitz Ezpeleta
 * @author José Ramón Méndez
 */
@TransformationPipe()
public class StringBuffer2SynsetVector extends Pipe {
   
	private static final Logger logger = LogManager.getLogger(StringBuffer2SynsetVector.class);

    /**
     * UnmatchedTextHandlers
     */
    UnmatchedTextHandler vUTH[] = {new UrbanDictionaryHandler(), new TyposHandler(), new ObfuscationHandler()};

    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class getOutputType() {
        return SynsetVector.class;
    }
	
	/**
	  * The dictionary to store all sinsets seen in any text
	  */
	HashSet<String> dict;
	
	/**
	  * Create the pipe and initialize the synset dictionary. Please note that the synset dictionary
	  * can be achieved by using the corresponding getter.
	  **/
	public StringBuffer2SynsetVector(){
		dict=new HashSet<String>();

	}
	
	private final String acceptedCharOnBeggining="¿¡";
	private final String acceptedCharOnEnd=".,!?";
	private final String acceptedCharOnMiddle="/-.,";
		
	/**
	  * Create the pipe specifiying an external dictionary.
	  * @param dict The dictionary used to compile the full list of synsets. 
	  */
	public StringBuffer2SynsetVector(HashSet<String> dict){
		this.dict=dict;
	}
	
	public HashSet<String> getDict(){
		return dict;
	}
	
	public void setDict(HashSet<String> dict){
		this.dict=dict;
	}		
	
	private Vector<Pair<String,String>> computeUnmatched(String str, String lang){
		StringTokenizer st=new StringTokenizer(str," \t\n\r\u000b\f");
		
		//The value that will be returned
		Vector<Pair<String,String>> returnValue=new Vector<Pair<String,String>>();
		
		while(st.hasMoreTokens()){
			String current=st.nextToken();
			
			
			Pattern pattern = Pattern.compile("\\p{Punct}");
			Matcher matcher = pattern.matcher(current);
			if (matcher.find()){ //We found a puntuation mark in the token
				 //matcher.start() <- here is the index of the puntuation mark
				 //TODO develop rules checking also the existence of term/terms in Babelnet
				 
				 //if do not fit the rules and/or not found in Babelnet
				 //    returnValue.add(new Pair<String,String>(current,null));
				 
				 //To check the exitence of the term in BabelNet, we will 
				 //create a class org.ski4spam.util.BabelNetUtils with  
				 //static methods.
				 int indexOfPuntMark=matcher.start();
				 if (indexOfPuntMark==0){ //The puntuation symbol is at the beggining
					 if (acceptedCharOnBeggining.indexOf(current.charAt(indexOfPuntMark))==-1){
					 	 returnValue.add(new Pair<String,String>(current,null));
					 }else{
		             if (!BabelUtils.getDefault().isTermInBabelNet(current.substring(1), lang))
							 returnValue.add(new Pair<String,String>(current,null));
					 }
				 }else if (indexOfPuntMark==current.length()-1){ //the puntuation symbol is at the end
					 if (acceptedCharOnEnd.indexOf(current.charAt(indexOfPuntMark))==-1){
					 	 returnValue.add(new Pair<String,String>(current,null));
					 }else{
		             if (!BabelUtils.getDefault().isTermInBabelNet(current.substring(0,indexOfPuntMark-1), lang))
							 returnValue.add(new Pair<String,String>(current,null));
					 }
				 }else{ //The puntuation symbol is in the middle
					 if (acceptedCharOnMiddle.indexOf(current.charAt(indexOfPuntMark))==-1){
					 	 returnValue.add(new Pair<String,String>(current,null));
					 }else{
		             if (!BabelUtils.getDefault().isTermInBabelNet(current.substring(0,indexOfPuntMark-1), lang) ||
 							  !BabelUtils.getDefault().isTermInBabelNet(current.substring(indexOfPuntMark+1,current.length()-1), lang)
							  )
							 returnValue.add(new Pair<String,String>(current,null));
					 }
				 }
			}else{
				 //TODO check if the term current exist in babelnet. 
				 //if current is not found in Babelnet
				 //    returnValue.add(new Pair<String,String>(current,null));
             if (!BabelUtils.getDefault().isTermInBabelNet(current, lang))
					 returnValue.add(new Pair<String,String>(current,null));
			}
			
        }
        return returnValue;
    }
	
	private String handleUnmatched(String originalText,List<Pair<String,String>> unmatched, String lang){
		//Implement the UnmatchedTextHandler interface and three specific implementations that are:
		//+ UrbanDictionaryHandler
		//+ TyposHandler
		//+ ObfuscationHandler
		
		//The replacement should be done here
		//DONE develop these things (Moncho)
		for (Pair<String,String> current:unmatched){
		    for (int i=0;current.getObj2()==null && i<vUTH.length;i++) vUTH[i].handle(current, lang);
            if (current.getObj2()!=null) originalText.replace(current.getObj1(),current.getObj2());
		}		
		
		return originalText;
	}
	
	private Vector<Pair<String,String>> buildSynsetVector(String fixedText, String lang){
		Vector<Pair<String,String>> returnValue=new Vector<Pair<String,String>>();
		
		//Call Babelfy api to transform the string into a vector of sysnsets. 
		//The fisrt string in the pair is the synsetID from babelnet
		//The second string is the matched text
		//The dictionary (dict) should be updated by adding each detected synset in texts.
		//TODO implement the transformation
		
		return returnValue;
	}
	
	@Override
	public Instance pipe(Instance carrier){
		SynsetVector sv=new SynsetVector((StringBuffer)carrier.getData());
		
		sv.setUnmatchedTexts(computeUnmatched(sv.getOriginalText(),((String)carrier.getProperty(GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY)).toUpperCase()));
		
		if(sv.getUnmatchedTexts().size()>0)
		    sv.setFixedText(handleUnmatched(
				 sv.getOriginalText(),
				 sv.getUnmatchedTexts(),
				 ((String)carrier.getProperty(GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY)).toUpperCase()
			   )
		    );
	   else sv.setFixedText(sv.getOriginalText());
		
		sv.setSynsets(buildSynsetVector(sv.getFixedText(),((String)carrier.getProperty(GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY)).toUpperCase()));
		
		carrier.setData(sv);
		
		return carrier;
	}
}
