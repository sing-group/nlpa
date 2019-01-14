import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.BabelSynsetRelation;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.util.Language;

public class TestHypernym {

    public static void main(String[] args) throws IOException {
    	String texto = "bn:00015556n bn:00000356n bn:03095983n bn:01808357n bn:00877124n bn:00079972n bn:00032558n " +
    				"bn:00108806a bn:00888759n bn:00060436n bn:00084385v bn:13611274a bn:00114203r " +
    				"bn:00113457a bn:00052907n bn:00061450n";
    	String texto2= "bn:00050198n bn:00096817a bn:00086682v bn:00025320n bn:00032654n"
    			+ " bn:00082188v bn:16732289n bn:00092424v bn:00092443v bn:00101180a bn:00058164n "
    			+ "bn:00090018v bn:00084848v bn:00093934v bn:00087976v bn:00083145v "
    			+ "bn:00093288v bn:00091875v bn:00076373n bn:00077549n bn:00098275a "
    			+ "bn:03675874n";
    	String spam1="bn:00085756v bn:00077462n bn:00001875n";
    	BabelNet bn = BabelNet.getInstance();
    	BabelSynset tmpby;
    	//BabelSynset by = bn.getSynset(new BabelSynsetID("bn:00015556n"));
    	List<String> bsList = new ArrayList<String>();
    	String[] temp;
    	String separador = " ";
    	temp = texto.split(separador);
    	for (int i = 0; i<temp.length; i++)
    		{
    		BabelSynset by = bn.getSynset(new BabelSynsetID(temp[i]));
    		System.out.println("\n#################### Synset=" + temp[i] + " "
    							+ by.getMainSense(Language.EN).get().getFullLemma() + " #################");
    		
    		System.out.println("--------------ANY_HYPERNYM-------------------");
    		for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM))
    			{
    			tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
    			System.out.println(by.getID()
    			+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                + "Puntero: " +edge.getPointer()+" - "
                + edge.getBabelSynsetIDTarget() + " "
                + tmpby.getMainSense(Language.EN).get().getFullLemma());
    			}
    		
    		/* System.out.println("--------------ALSO_SEE-------------------");
    		for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.ALSO_SEE))
				{
    			tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
    			System.out.println(by.getID()
    					+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
    					+ edge.getPointer()+" - "
    					+ edge.getBabelSynsetIDTarget() + " "
    					+ tmpby.getMainSense(Language.EN).get().getFullLemma());
				}

    		System.out.println("------------------ANY_HOLONYM-------------------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.ANY_HOLONYM)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
            
            */System.out.println("------------------HYPERNYM-------------------------------" );
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.HYPERNYM)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}

            /*System.out.println("------------------ATTRIBUTE-------------------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.ATTRIBUTE)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
            
            System.out.println("------------------CAUSE-------------------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.CAUSE)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
            
            System.out.println("------------------DERIVATIONALLY_RELATED-------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.DERIVATIONALLY_RELATED)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
            
            System.out.println("------------------GLOSS_DISAMBIGUATED-------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.GLOSS_DISAMBIGUATED)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
            
            System.out.println("------------------TOPIC-------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.TOPIC)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
            
            */System.out.println("------------------WIBI_HYPERNYM-------------------" + "\n\n");
            for(BabelSynsetRelation edge : by.getOutgoingEdges(BabelPointer.WIBI_HYPERNYM)) 
            	{
            	tmpby = bn.getSynset(new BabelSynsetID(edge.getBabelSynsetIDTarget().toString()));
                System.out.println(by.getID()
                	+"\t" + by.getMainSense(Language.EN).get().getFullLemma()+" - "
                    + edge.getPointer()+" - "
                    + edge.getBabelSynsetIDTarget() + " "
                    + tmpby.getMainSense(Language.EN).get().getFullLemma());
            	}
    		}
    }	
    	
}