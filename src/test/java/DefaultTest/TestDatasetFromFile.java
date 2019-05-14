package DefaultTest;


import java.util.HashMap;
import java.util.Map;
import org.bdp4j.dataset.CSVDatasetReader;
import org.bdp4j.transformers.Date2MillisTransformer;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.types.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author María Novo
 */
public class TestDatasetFromFile {

    /*
    * The main method for the running application
    */
    public static void main(String[] args) {

        // Parámetro para el transformador Enum2IntTransformer de la propiedad target
        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target", new Enum2IntTransformer(transformList));

        String filePath = "outputsyns.csv";//Main.class.getResource("/outputsyns.csv").getPath();
        CSVDatasetReader jml = new CSVDatasetReader(filePath, transformersList);
        Dataset dataset =  jml.loadFile();
        System.out.println(" ----- DATASET -----");
        dataset.printLine();
        System.out.println(" ----- Generating arff file with comments -----");
        dataset.generateARFFWithComments(transformersList, "");
        
        
    }

}
