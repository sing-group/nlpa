

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelPointer;

public class TestHypernym {
//	Map<String, String> synsetMap; 
	
	public TestHypernym() {
//		synsetMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> searchHypernymsInBabelnet (HashMap<String, String> originalSynsetMap) {
		int ElementsInAnyHypernymPointer, ElementsInHypernymPointer;
		String hypernym;
		
		//First we make a copy of the original Map to work with the copy quiet
		HashMap<String, String> hyperonymizedSynsetMap = new HashMap<String, String>();
		//LA SIGUIENTE LINEA ME DA UN WARNING!!!!
		hyperonymizedSynsetMap = (HashMap) originalSynsetMap.clone();

    	BabelNet bn = BabelNet.getInstance();
    	
		for(Map.Entry<String, String> tupla : hyperonymizedSynsetMap.entrySet())
		{
    		BabelSynset by = bn.getSynset(new BabelSynsetID(tupla.getKey()));
            ElementsInAnyHypernymPointer = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM).size();
            ElementsInHypernymPointer = by.getOutgoingEdges(BabelPointer.HYPERNYM).size();
            if ( (ElementsInHypernymPointer==0) && (ElementsInAnyHypernymPointer==0) )
        		{ 
            	 break;
            	} 
            else 
        		{
            	if (ElementsInHypernymPointer >= 1)
        			{ hypernym = by.getOutgoingEdges(BabelPointer.HYPERNYM).get(0).getBabelSynsetIDTarget().toString(); }
            	else { hypernym = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM).get(0).getBabelSynsetIDTarget().toString(); }
        		}
            tupla.setValue(hypernym);
          }
		
		return hyperonymizedSynsetMap;
	}
	
	
	

	public static void main(String[] args) throws IOException {
    	TestHypernym programa = new TestHypernym();
		
    	String texto = "bn:00015556n bn:00000356n bn:03095983n bn:01808357n bn:00877124n bn:00079972n bn:00032558n " +
    				"bn:00108806a bn:00888759n bn:00060436n bn:00084385v bn:13611274a bn:00114203r " +
    				"bn:00113457a bn:00052907n bn:00061450n";
    	List<String> synsetList = Arrays.asList(texto.split(" "));

    	HashMap<String, String> originalSynsetMap = new HashMap<String, String>();
    	HashMap<String, String> finalSynsetMap = new HashMap<String, String>();
    	
    	//Rellenamos la clave del mapa. El campo valor lo dejamos a null
    	for (String e : synsetList)
		{
		originalSynsetMap.put(e, null);
		}
    	
    	finalSynsetMap = programa.searchHypernymsInBabelnet(originalSynsetMap);
    	//Para imprimir el mapa
		System.out.println("Original:" + originalSynsetMap);
		System.out.println("Ultimo:  " + finalSynsetMap);

    }	
    	
	
	

	
}