
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bdp4j.ml.DatasetFromFile;
import org.bdp4j.transformers.Date2MillisTransformer;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.types.*;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author María Novo
 */
public class TestWekaOperationsWithDataset {

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

        String filePath = "outputsyns_old.csv";//Main.class.getResource("/outputsyns.csv").getPath();
        DatasetFromFile jml = new DatasetFromFile(filePath, transformersList);
        Dataset dataset = jml.loadFile();
        System.out.println(" ----- DATASET -----");
        dataset.printLine();
        System.out.println(" ----- Generating arff file with comments -----");
        dataset.generateARFFWithComments(transformersList);

        Instances data = dataset.getWekaDataset();
        data.setClassIndex(data.numAttributes() - 1);

        Instances trainingData = new Instances(data, 0, 14);
        Instances testingData = new Instances(data, 14, 5);

        Evaluation evaluation;
        try {
            evaluation = new Evaluation(trainingData);
            SMO smo = new SMO();
            smo.buildClassifier(data);

            evaluation.evaluateModel(smo, testingData);
            System.out.println(evaluation.toSummaryString());
        } catch (Exception ex) {
            Logger.getLogger(TestWekaOperationsWithDataset.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

}
