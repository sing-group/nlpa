
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

	public TestHypernym() {

	}

	public Map<String, String> GetHypernymsFromBabelnet(List<String> originalSynsetList) {
		int elementsInAnyHypernymPointer, elementsInHypernymPointer;
		String hypernym;
		Map<String, String> synsetHypernymMap = new HashMap<String, String>();
		BabelNet bn = BabelNet.getInstance();

		for (String synsetListElement : originalSynsetList) {
			BabelSynset by = bn.getSynset(new BabelSynsetID(synsetListElement));
			elementsInAnyHypernymPointer = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM).size();
			elementsInHypernymPointer = by.getOutgoingEdges(BabelPointer.HYPERNYM).size();
			// Si hay listado de hiperónimos en la relación HYPERNYM cogemos el primero de la
			// lista y metemos la pareja (synset original , hyperónimo) en el mapa
			if (elementsInHypernymPointer >= 1) {
				hypernym = by.getOutgoingEdges(BabelPointer.HYPERNYM).get(0).getBabelSynsetIDTarget().toString();
				synsetHypernymMap.put(synsetListElement, hypernym);
			}
			// Si no hay nada en la relación HIPERNYM miramos en ANY_HYPERNYM y si hay listado de hiperónimos,
			// cogemos el primero de la lista y metemos la pareja (synset original , hyperónimo) en el mapa
			else if (elementsInAnyHypernymPointer >= 1) {
				hypernym = by.getOutgoingEdges(BabelPointer.ANY_HYPERNYM).get(0).getBabelSynsetIDTarget().toString();
				synsetHypernymMap.put(synsetListElement, hypernym);
			}
			//Si no se cumple ninguna de las dos condiciones anteriores, pasamos al siguiente elemento de la lista
		}

		return synsetHypernymMap;

	}

	public static void main(String[] args) throws IOException {

		String textoParaTest = "bn:00015556n bn:00000356n bn:03095983n bn:01808357n bn:00877124n bn:00079972n bn:00032558n "
				+ "bn:00108806a bn:00888759n bn:00060436n bn:00084385v bn:13611274a bn:00114203r "
				+ "bn:00113457a bn:00052907n bn:00061450n";
		List<String> synsetListParaTest = Arrays.asList(textoParaTest.split(" "));

		Map<String, String> salida;
		TestHypernym lista = new TestHypernym();
		salida = lista.GetHypernymsFromBabelnet(synsetListParaTest);
		System.out.println(synsetListParaTest);
		System.out.println(salida);

	}

}