package org.ski4spam.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.uniroma1.lcl.babelfy.core.Babelfy;
import it.uniroma1.lcl.babelfy.commons.annotation.SemanticAnnotation;
import it.uniroma1.lcl.jlt.util.Language;

import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetQuery;

import java.util.List;

public class BabelUtils{

	    private static final Logger logger = LogManager.getLogger(BabelUtils.class);	

	    private static BabelUtils bu=null;

	 	 private Babelfy bfy;
	 	 private BabelNet bn;
			 
       private BabelUtils(){
	 		bfy = new Babelfy();
	 		bn = BabelNet.getInstance();       	
       }
		
	    public static BabelUtils getDefault(){
			 if (bu==null) bu=new BabelUtils();
			 return bu;
	    }
		 
		 public boolean isTermInBabelNet(String term, String lang){
			 int resultNo=0;
			 try{
				 /*This is a query to babelfy not babelnet*/
				 /*
			     List<SemanticAnnotation> bfyAnnotations = bfy.babelfy(current, Language.valueOf(lang)); //TODO: compile language from Propoerties
			     logger.info("Babelfy query: " + current + " results: " +  bfyAnnotations.size());
			     if (bfyAnnotations.size()==0)
			        returnValue.add(new Pair<String,String>(current,null));
				  */
				  BabelNetQuery query = new BabelNetQuery.Builder(term)
					 	.from(Language.valueOf(lang)) //TODO: compile language from Propoerties
					 	.build();
				  List<BabelSynset> byl = bn.getSynsets(query);
				  resultNo=byl.size();
		    }catch(Exception e){
				 logger.error("Unable to query Babelfy: "+e.getMessage());
		    }
			 return (resultNo>0);
		 }
}