

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelPointer;

public class TestHypernym {
	List<String> synsetList;
	
	public TestHypernym() {
		synsetList = new ArrayList<String>();
	}
	
	public List<String> GetHypernymsFromBabelnet (List<String> originalSynsetList) {
		int ElementsInAnyHypernymPointer, ElementsInHypernymPointer;
		String hypernym;
		List<String> hypernymListToReturn = new ArrayList<String>();
    	BabelNet bn = BabelNet.getInstance();
    	
    	for (String synsetListElement : originalSynsetList)
    	{
    		BabelSynset by = bn.getSynset(new BabelSynsetID(synsetListElement));
            ElementsInAnyHypernymPointer = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM).size();
            ElementsInHypernymPointer = by.getOutgoingEdges(BabelPointer.HYPERNYM).size();
            if ( (ElementsInHypernymPointer==0) && (ElementsInAnyHypernymPointer==0) )
        		{ hypernym = by.getID().toString(); } 
            else 
        		{
            	if (ElementsInHypernymPointer >= 1)
        			{ hypernym = by.getOutgoingEdges(BabelPointer.HYPERNYM).get(0).getBabelSynsetIDTarget().toString(); }
            	else { hypernym = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM).get(0).getBabelSynsetIDTarget().toString(); }
        		}
            hypernymListToReturn.add(hypernym);
            
    	}
		return hypernymListToReturn;
	}
	
	
	

	public static void main(String[] args) throws IOException {
		TestHypernym lista = new TestHypernym();
		
    	String texto = "bn:00015556n bn:00000356n bn:03095983n bn:01808357n bn:00877124n bn:00079972n bn:00032558n " +
    				"bn:00108806a bn:00888759n bn:00060436n bn:00084385v bn:13611274a bn:00114203r " +
    				"bn:00113457a bn:00052907n bn:00061450n";
    	List<String> synsetList = Arrays.asList(texto.split(" "));
    	List<String> resultado;
    	resultado = lista.GetHypernymsFromBabelnet(synsetList);
    	
    	for (String e : synsetList)
    		{
    		System.out.print(e + " ");
    		}
    	System.out.println();

    	for (String e : resultado)
		{
		System.out.print(e + " ");
		}



    }	
    	
	
	

	
}