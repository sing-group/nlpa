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

        String filePath = "outputsyns.csv";
        CSVDatasetReader jml = new CSVDatasetReader(filePath, transformersList);
        Dataset dataset = jml.loadFile();
//        System.out.println(" ----- DATASET -----");
//        dataset.printLine();

        dataset.generateARFFWithComments(transformersList, "");
        System.out.println(" ----- Arff file with comments generated-----");
        System.out.println("");
        
        /* Creating dataset replacing synsets with hypernonyms*/
        Map<String, String> hyperonymList = new HashMap<>();
        hyperonymList.put("bn:00115153r", "bn:00062164n");//**
        hyperonymList.put("bn:01946589n", "bn:00086370v");
        hyperonymList.put("bn:00093287v", "bn:00062164n");//**
        hyperonymList.put("bn:00079944n", "bn:00062164n");//**
        hyperonymList.put("bn:00095482v", "bn:00062164n");//**
        hyperonymList.put("bn:00024712n", "bn:00000492n");
        hyperonymList.put("bn:00019763n", "bn:00062164n");//**

//        clone.generateARFFWithComments(transformersList, "");
//        clone.replaceColumnNames(hyperonymList, Dataset.COMBINE_SUM);
//        clone.generateARFFWithComments(transformersList, "replaceColumnsNames.arff");
        /**
         * ********************************************
         */

        /*
        Instances data = dataset.getWekaDataset();

        data.deleteStringAttributes();
        data.setClassIndex(data.numAttributes() - 1);

        System.out.println("num data: " + data.numInstances());
        int numInstances = data.numInstances();
        int beginInt = (numInstances * 80) / 100;
        int endInt = numInstances - beginInt;

        Instances trainingData = new Instances(data, 0, beginInt);
        System.out.println("num trainingData: " + trainingData.numInstances());
        Instances testingData = new Instances(data, beginInt, endInt);
        System.out.println("num testingData: " + testingData.numInstances());
        System.out.println("");

        try {
            System.out.println("------------------------------------------");
            System.out.println("--------- Random Forest Classifier ---------");
            System.out.println("------------------------------------------");
            Evaluation rfEvaluation = new Evaluation(testingData);
            RandomForest ramdomForest = new RandomForest();
            ramdomForest.buildClassifier(trainingData);
            rfEvaluation.evaluateModel(ramdomForest, testingData);
            String confusionMatrix = rfEvaluation.toMatrixString("Confusion matrix: ");
            System.out.println("Summary: " + rfEvaluation.toSummaryString());
            System.out.println("------------------------------------------");
            System.out.println(confusionMatrix);
            System.out.println(">> TN: " + rfEvaluation.confusionMatrix()[0][0]);
            System.out.println(">> FP: " + rfEvaluation.confusionMatrix()[0][1]);
            System.out.println(">> FN: " + rfEvaluation.confusionMatrix()[1][0]);
            System.out.println(">> TP: " + rfEvaluation.confusionMatrix()[1][1]);
        } catch (Exception ex) {
            Logger.getLogger(TestWekaOperationsWithDataset.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        try {
            System.out.println("------------------------------------------");
            System.out.println("--------- Bayes Net Classifier ---------");
            System.out.println("------------------------------------------");
            Evaluation bnEvaluation = new Evaluation(testingData);
            BayesNet bayesNet = new BayesNet();
            bayesNet.buildClassifier(trainingData);
            bnEvaluation.evaluateModel(bayesNet, testingData);
            String confusionMatrix = bnEvaluation.toMatrixString("Confusion matrix: ");
            System.out.println("Summary: " + bnEvaluation.toSummaryString());
            System.out.println("------------------------------------------");
            System.out.println(confusionMatrix);
            System.out.println(">> TN: " + bnEvaluation.confusionMatrix()[0][0]);
            System.out.println(">> FP: " + bnEvaluation.confusionMatrix()[0][1]);
            System.out.println(">> FN: " + bnEvaluation.confusionMatrix()[1][0]);
            System.out.println(">> TP: " + bnEvaluation.confusionMatrix()[1][1]);
        } catch (Exception ex) {
            Logger.getLogger(TestWekaOperationsWithDataset.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        try {
            System.out.println("------------------------------------------");
            System.out.println("--------- Naive Bayes Classifier ---------");
            System.out.println("------------------------------------------");
            Evaluation nvEvaluation = new Evaluation(testingData);
            NaiveBayes naiveBayes = new NaiveBayes();
            naiveBayes.buildClassifier(trainingData);
            nvEvaluation.evaluateModel(naiveBayes, testingData);
            String confusionMatrix = nvEvaluation.toMatrixString("Confusion matrix: ");
            System.out.println("Summary: " + nvEvaluation.toSummaryString());
            System.out.println("------------------------------------------");
            System.out.println(confusionMatrix);
            System.out.println(">> TN: " + nvEvaluation.confusionMatrix()[0][0]);
            System.out.println(">> FP: " + nvEvaluation.confusionMatrix()[0][1]);
            System.out.println(">> FN: " + nvEvaluation.confusionMatrix()[1][0]);
            System.out.println(">> TP: " + nvEvaluation.confusionMatrix()[1][1]);
        } catch (Exception ex) {
            Logger.getLogger(TestWekaOperationsWithDataset.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        Evaluation smoEvaluation;
        try {
            System.out.println("------------------------------------------");
            System.out.println("------------ SMO Classifier --------------");
            System.out.println("------------------------------------------");
            smoEvaluation = new Evaluation(testingData);
            SMO smo = new SMO();
            smo.buildClassifier(trainingData);
            smoEvaluation.evaluateModel(smo, testingData);
            String confusionMatrix = smoEvaluation.toMatrixString("Confusion matrix: ");
            System.out.println("Summary: " + smoEvaluation.toSummaryString());
            System.out.println("------------------------------------------");
            System.out.println(confusionMatrix);
            System.out.println(">> TN: " + smoEvaluation.confusionMatrix()[0][0]);
            System.out.println(">> FP: " + smoEvaluation.confusionMatrix()[0][1]);
            System.out.println(">> FN: " + smoEvaluation.confusionMatrix()[1][0]);
            System.out.println(">> TP: " + smoEvaluation.confusionMatrix()[1][1]);

            System.out.println("------------------------------------------");
            System.out.println("---------- K Fold Cross validation (k=10) -------");
            System.out.println("------------------------------------------");
            try {
                int k = 10;
                if (numInstances < k) {
                    k = numInstances;
                }
                Evaluation eval = new Evaluation(data);
                eval.crossValidateModel(new SMO(), data, k, new Random(1));

                confusionMatrix = eval.toMatrixString("Confusion matrix: ");
                System.out.println("Summary: " + eval.toSummaryString());
                System.out.println("------------------------------------------");
                System.out.println(confusionMatrix);
                System.out.println(">> TN: " + eval.confusionMatrix()[0][0]);
                System.out.println(">> FP: " + eval.confusionMatrix()[0][1]);
                System.out.println(">> FN: " + eval.confusionMatrix()[1][0]);
                System.out.println(">> TP: " + eval.confusionMatrix()[1][1]);

            } catch (Exception ex) {
                Logger.getLogger(TestWekaOperationsWithDataset.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            Logger.getLogger(TestWekaOperationsWithDataset.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
         */
    }

}
