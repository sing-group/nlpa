import java.util.HashMap;
import java.util.Map;

import org.bdp4j.ml.DatasetFromFile;
import org.bdp4j.transformers.Date2MillisTransformer;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.types.Dataset;
import org.bdp4j.types.Transformer;

import weka.core.Instances;

public class TestWekaClassifiers {

	public static void main(String[] args) {
		
		 // Parámetro para el transformador Enum2IntTransformer de la propiedad target
        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target", new Enum2IntTransformer(transformList));

        String filePath = "outputsyns.csv";
        DatasetFromFile jml = new DatasetFromFile(filePath, transformersList);
        // Este sería nuestro dataset, generado a partir del fichero outputsyns.csv. Con él puedes usar todos los métodos de la clase Dataset que está en 
        // bdp4j/types/Dataset.java
        Dataset dataset = jml.loadFile();
        System.out.println(" ----- DATASET -----");
        dataset.printLine();
        
        // Con este código, se genera un archivo arff a partir del dataset anterior
        System.out.println(" ----- Generating arff file -----");
        dataset.generateARFFWithComments(transformersList);
        
        // Con este código, se obtiene el dataset con el que tienes que trabajar en WEKA,
        // a partir de nuestro dataset
        Instances wekaDataset = dataset.getWekaDataset();
        
        /* A partir de aquí, con datasetWeka, es donde tienes que meter el código para ejecutar los clasificadores */
        
        
        /* Una vez realizadas las pruebas con los clasificadores, partiendo del dataset sin escalar, para hacer la generalización de synsets, lo más sencillo sería que usases nuestro dataset inicial "dataset", y 
         * cuando hayas terminado, lo convirtieses de nuevo en un dataset de WEKA (usando dataset.getWekaDataset()), para poder ejecutar de nuevo los clasificadores sobre él */
        

	}

}
