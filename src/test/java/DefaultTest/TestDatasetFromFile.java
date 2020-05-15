package DefaultTest;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import org.bdp4j.types.Dataset;
import org.bdp4j.types.Transformer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bdp4j.dataset.CSVDatasetReader;
import org.bdp4j.transformers.attribute.CheckVoidTransformer;
import org.bdp4j.transformers.attribute.Date2MillisTransformer;
import org.bdp4j.transformers.attribute.Enum2IntTransformer;
import org.bdp4j.transformers.attribute.InputScale2OutputScaleTransformer;
import org.bdp4j.transformers.attribute.Url2BinaryTransformer;
import org.bdp4j.types.DatasetStore;
import org.bdp4j.util.Pair;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.RandomForest;
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
public class TestDatasetFromFile {

    /*
    * The main method for the running application
     */
    public static void main(String[] args) {

        try {
            int ntest = 20;
            int ntrain = 80;

            Pair<Double, Double> inputScale = new Pair<>(-1d, 1d);
            Pair<Double, Double> outputScale = new Pair<>(0d, 10d);

            // Parámetro para el transformador Enum2IntTransformer de la propiedad target
            Map<String, Integer> transformList = new HashMap<>();
            transformList.put("ham", 0);
            transformList.put("spam", 1);
            //Se define la lista de transformadores
            Map<String, Transformer> transformersList = new HashMap<>();
            transformersList.put("date", new Date2MillisTransformer());
            transformersList.put("target", new Enum2IntTransformer(transformList));
            transformersList.put("URLs", new Url2BinaryTransformer());
            transformersList.put("interjection", new CheckVoidTransformer());
            transformersList.put("tokenPolarity", new InputScale2OutputScaleTransformer(inputScale, outputScale));
            //transformersList.put("tokenPolarity", new InputScale2OutputScaleTransformer(inputScale, outputScale));

            String hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "" + Calendar.getInstance().get(Calendar.MINUTE) + Calendar.getInstance().get(Calendar.SECOND);
            /*
            System.out.println("-------------------------------------");
            System.out.println("------ SYNSETS CLASSIFICATION -------");
            System.out.println("-------------------------------------");
            Instances testingData;
            Instances trainingData;
            //String filePath = "output/outputsyns_spamassassin.csv";
            //String filePath = "output/results_youtube/outputsyns.csv";
            // String filePath = "output/results_youtube/outputsyns_04042020.csv";
            String filePath = "output/outputsyns_12417.csv";
            CSVDatasetReader jml = new CSVDatasetReader(filePath, transformersList);
            Dataset synsetsDataset = jml.loadFile();
//            synsetsDataset.generateARFFWithComments(transformersList, "youtube-uci-load-file.arff");
            synsetsDataset.generateARFFWithComments(transformersList, "outputsyns_youtube-load-file_" + hora + ".arff");
            // Get train and test sets
            Dataset[] stratifiedDataset = synsetsDataset.split(true, ntest, ntrain);

            List<String> attributesToDelete = new ArrayList<>();
            attributesToDelete.add("id");
            attributesToDelete.add("word-counter");
            attributesToDelete.add("synset-counter");
            attributesToDelete.add("polarity");
            attributesToDelete.add("language-reliability");
            attributesToDelete.add("date");
            attributesToDelete.add("length");
            attributesToDelete.add("URLs");
            attributesToDelete.add("interjection");
            attributesToDelete.add("sit");
            attributesToDelete.add("polarity");
            attributesToDelete.add("synsetPolarity");
            //date, length, URLs, language-reliability, interjection, sit, synsetPolarity

            for (Dataset dataset : stratifiedDataset) {
                dataset.deleteAttributeColumns(attributesToDelete);
            }

            int contAttSynsets = 0;

            for (String att : stratifiedDataset[0].getAttributes()) {
                if (!att.startsWith("bn:")) {
                    System.out.print(att + ", ");
                }
                contAttSynsets++;
            }
            System.out.println("atts: " + contAttSynsets);

            System.out.println("atts: " + stratifiedDataset[0].getAttributes());
            testingData = stratifiedDataset[0].getWekaDataset();
            trainingData = stratifiedDataset[1].getWekaDataset();

            // Set class
            trainingData.setClass(trainingData.attribute("target"));
            testingData.setClass(testingData.attribute("target"));

            // Naive Bayes Evaluation with train-test
            System.out.println("-------- Naive Bayes Multinomial --------");
            Evaluation naiveBayesMultinomialEvaluation = new Evaluation(trainingData);
            AttributeSelectedClassifier attSelect = attributeSelectedClassify(trainingData, new NaiveBayesMultinomial());
            Classifier ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(trainingData);
            naiveBayesMultinomialEvaluation.evaluateModel(ttClassifier, testingData);
            printEvaluation(naiveBayesMultinomialEvaluation, "synsetsNBM_" + hora);

            System.out.println("-------- RandomForest --------");
            Evaluation randomForestEvaluation = new Evaluation(trainingData);
            attSelect = attributeSelectedClassify(trainingData, new RandomForest());
            ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(trainingData);
            randomForestEvaluation.evaluateModel(ttClassifier, testingData);
            printEvaluation(randomForestEvaluation, "synsetsRF_" + hora);

            System.out.println("-------- AdaBoostM1 --------");
            Evaluation adaBoostM1Evaluation = new Evaluation(trainingData);
            attSelect = attributeSelectedClassify(trainingData, new AdaBoostM1());
            ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(trainingData);
            adaBoostM1Evaluation.evaluateModel(ttClassifier, testingData);
            printEvaluation(adaBoostM1Evaluation, "synsetsAdaBoost_" + hora);

            //    SMO Evaluation with train-test //
            System.out.println("-------- SMO --------");
            Evaluation smoEvaluation = new Evaluation(trainingData);
            attSelect = attributeSelectedClassify(trainingData, new SMO()); // 
            ttClassifier = AbstractClassifier.makeCopy(attSelect); //
            ttClassifier.buildClassifier(trainingData); //
            smoEvaluation.evaluateModel(ttClassifier, testingData); //
            printEvaluation(smoEvaluation, "synsetsSMO_" + hora);

            System.out.println("-------- Naive Bayes --------");
            Evaluation naiveBayesEvaluation = new Evaluation(trainingData);
            attSelect = attributeSelectedClassify(trainingData, new NaiveBayes());
            ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(trainingData);
            naiveBayesEvaluation.evaluateModel(ttClassifier, testingData);
            printEvaluation(naiveBayesEvaluation, "synsetsNB_" + hora);

             */

            System.out.println("-------------------------------------");
            System.out.println("------ TOKENS CLASSIFICATION ------");
            System.out.println("-------------------------------------");
            Instances tokensTestingData;
            Instances tokensTrainingData;
            // filePath = "output/outputtoks_youtube_uci.csv";
            //String filePath = "output/outputtoks_spamassassin.csv";
            //String filePath = "output/outputtoks_12417.csv";
            String filePath = "output/outputtoks_193611.csv";
            CSVDatasetReader jml = new CSVDatasetReader(filePath, transformersList);
            Dataset tokensDataset = jml.loadFile();
            tokensDataset.generateARFFWithComments(transformersList, "DSTokens_outputtoks_193611_" + hora + ".arff");
            // Get train and test sets
            Dataset[] tokensStratifiedDataset = tokensDataset.split(true, ntest, ntrain);

            List<String> attributesToDelete = new ArrayList<>();
            attributesToDelete.add("id");
            attributesToDelete.add("language-reliability");
            attributesToDelete.add("date");
            attributesToDelete.add("length");
            //attributesToDelete.add("URLs");
            attributesToDelete.add("interjection");
            attributesToDelete.add("polarity");
            attributesToDelete.add("tokenPolarity");

            for (Dataset dataset : tokensStratifiedDataset) {
                dataset.deleteAttributeColumns(attributesToDelete);
            }

            System.out.println("Attributes");
            for (String att : tokensStratifiedDataset[0].getAttributes()) {
                if (!att.startsWith("tk:")) {
                    System.out.print(att + ", ");
                }
            }
            //System.out.println("atts: " + tokensStratifiedDataset[0].getAttributes());
            tokensTestingData = tokensStratifiedDataset[0].getWekaDataset();
            tokensTrainingData = tokensStratifiedDataset[1].getWekaDataset();

            // Set class
            tokensTrainingData.setClass(tokensTrainingData.attribute("target"));
            tokensTestingData.setClass(tokensTestingData.attribute("target"));

            // Naive Bayes Evaluation with train-test
            System.out.println("-------- Naive Bayes Multinomial --------");
            Evaluation naiveBayesMultinomialEvaluation = new Evaluation(tokensTrainingData);
            AttributeSelectedClassifier attSelect = attributeSelectedClassify(tokensTrainingData, new NaiveBayesMultinomial());
            Classifier ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(tokensTrainingData);
            naiveBayesMultinomialEvaluation.evaluateModel(ttClassifier, tokensTestingData);
            printEvaluation(naiveBayesMultinomialEvaluation, "tokensNBM_" + hora);

            System.out.println("-------- RandomForest --------");
            Evaluation randomForestEvaluation = new Evaluation(tokensTrainingData);
            attSelect = attributeSelectedClassify(tokensTrainingData, new RandomForest());
            ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(tokensTrainingData);
            randomForestEvaluation.evaluateModel(ttClassifier, tokensTestingData);
            printEvaluation(randomForestEvaluation, "tokensRF_" + hora);

            System.out.println("-------- AdaBoostM1 --------");
            Evaluation adaBoostM1Evaluation = new Evaluation(tokensTrainingData);
            attSelect = attributeSelectedClassify(tokensTrainingData, new AdaBoostM1());
            ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(tokensTrainingData);
            adaBoostM1Evaluation.evaluateModel(ttClassifier, tokensTestingData);
            printEvaluation(adaBoostM1Evaluation, "tokensAdaBoost_" + hora);

            //    SMO Evaluation with train-test //
            System.out.println("-------- SMO --------");
            Evaluation smoEvaluation = new Evaluation(tokensTrainingData);
            attSelect = attributeSelectedClassify(tokensTrainingData, new SMO()); // 
            ttClassifier = AbstractClassifier.makeCopy(attSelect); //
            ttClassifier.buildClassifier(tokensTrainingData); //
            smoEvaluation.evaluateModel(ttClassifier, tokensTestingData); //
            printEvaluation(smoEvaluation, "tokensSMO_" + hora);

            System.out.println("-------- Naive Bayes --------");
            Evaluation naiveBayesEvaluation = new Evaluation(tokensTrainingData);
            attSelect = attributeSelectedClassify(tokensTrainingData, new NaiveBayes());
            ttClassifier = AbstractClassifier.makeCopy(attSelect);
            ttClassifier.buildClassifier(tokensTrainingData);
            naiveBayesEvaluation.evaluateModel(ttClassifier, tokensTestingData);
            printEvaluation(naiveBayesEvaluation, "tokensNB_" + hora);
        } catch (Exception ex) {
            Logger.getLogger(TestDatasetFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static AttributeSelectedClassifier attributeSelectedClassify(Instances trainingData, Classifier classifier)
            throws Exception {
        InfoGainAttributeEval igAttEval;
        Ranker ranker;
        igAttEval = new InfoGainAttributeEval();
        ranker = new Ranker();
        ranker.setNumToSelect(1000);
        AttributeSelectedClassifier attSelect = new AttributeSelectedClassifier();
        attSelect.setClassifier(classifier);
        attSelect.setEvaluator(igAttEval);
        attSelect.setSearch(ranker);

        return attSelect;

    }

    private static void printEvaluation(Evaluation evaluation, String type) throws Exception {

        String confusionMatrix = evaluation.toMatrixString("Confusion matrix: ");
        double tn = evaluation.confusionMatrix()[0][0];
        double fp = evaluation.confusionMatrix()[0][1];
        double fn = evaluation.confusionMatrix()[1][0];
        double tp = evaluation.confusionMatrix()[1][1];

        double recall = tp / (tp + fn);
        double precission = tp / (tp + fp);
        double fscore = 2 * ((precission * recall) / (precission + recall));
        double tcr1 = (tn + fp) / (1 * fn + fp);
        double tcr9 = (tn + fp) / (9 * fn + fp);
        double tcr999 = (tn + fp) / (999 * fn + fp);
        StringBuilder textToPrint = new StringBuilder("\r\n");
        textToPrint.append("Summary: ").append(evaluation.toSummaryString()).append("\r\n");
        textToPrint.append("------------------------------------------\r\n");
        textToPrint.append(confusionMatrix).append("\r\n");
        textToPrint.append(">> TN: ").append(tn).append("\r\n");
        textToPrint.append(">> FP: ").append(fp).append("\r\n");
        textToPrint.append(">> FN: ").append(fn).append("\r\n");
        textToPrint.append(">> TP: ").append(tp).append("\r\n");
        textToPrint.append("\r\n");
        textToPrint.append("Recall: ").append(recall).append("\r\n");
        textToPrint.append("Precission: ").append(precission).append("\r\n");
        textToPrint.append("F-Score: ").append(fscore).append("\r\n");
        textToPrint.append("TCR1: ").append(tcr1).append("\r\n");
        textToPrint.append("TCR9: ").append(tcr9).append("\r\n");
        textToPrint.append("TCR999: ").append(tcr999).append("\r\n");

        System.out.println(textToPrint.toString());

        try (FileWriter fw = new FileWriter("testout_" + type + ".txt")) {
            fw.write(textToPrint.toString());
        } catch (Exception ex) {
            System.out.println("[PRINT EVALUATION]: " + ex.getMessage());
        }
    }

}
