/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import com.google.common.cache.Cache;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bdp4j.transformers.attribute.Date2MillisTransformer;
import org.bdp4j.transformers.attribute.Enum2IntTransformer;
import org.bdp4j.types.Dataset;
import org.bdp4j.types.DatasetTransformer;
import org.bdp4j.types.Instance;
import org.bdp4j.types.Transformer;
import org.nlpa.util.BabelUtils;
import org.nlpa.util.CachedBabelUtils;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * This class represents a dataset according to the features of another
 *
 * @author María Novo
 */
public class DatasetFeatureRepresentation extends DatasetTransformer {

    /**
     * Dataset with the features which are going to be represented in another
     * Dataset (training dataset)
     */
    private Dataset featuresDataset;
    private static final Dataset.CombineOperator DEFAULT_OPERATOR = Dataset.COMBINE_SUM;

    /**
     * Combine operator to use when joining attributes
     */
    private Dataset.CombineOperator combineOperator = DEFAULT_OPERATOR;

    /**
     * Get featuresDataset attribute value
     *
     * @return featuresDataset attributte value
     */
    public Dataset getFeaturesDataset() {
        return featuresDataset;
    }

    /**
     * Establish value to featuresDataset attribute
     *
     * @param featuresDataset the new value that featuresDataset will have
     */
    public void setFeaturesDataset(Dataset featuresDataset) {
        this.featuresDataset = featuresDataset;
    }

    /**
     * Get combineOperator attribute value
     *
     * @return combineOperator variable value
     */
    public Dataset.CombineOperator getCombineOperator() {
        return combineOperator;
    }

    /**
     * Establish combineOperator to maxDegree attribute
     *
     * @param combineOperator the new value that combineOperator will have
     */
    public void setCombineOperator(Dataset.CombineOperator combineOperator) {
        this.combineOperator = combineOperator;
    }

    public DatasetFeatureRepresentation(Dataset featuresDataset, Dataset.CombineOperator operator) {
        this.featuresDataset = featuresDataset;
        this.combineOperator = operator;
    }

    /**
     * Defaul constructor
     *
     * @param featuresDataset Source dataset from which attributes is taken
     */
    public DatasetFeatureRepresentation(Dataset featuresDataset) {
        this(featuresDataset, DEFAULT_OPERATOR);
    }

    private Object[][] addValue(Object[][] instanceList, int index, String attributeName, Object attributeValue) {
        //System.out.println("attribute: " + attributeName + " -- " + "attributeValue: " + attributeValue);

        int currenAttributePosition = featuresDataset.getWekaDataset().attribute(attributeName).index();
        Double currentPositionValue = (Double) instanceList[index][currenAttributePosition];
        if (featuresDataset.getWekaDataset().attribute(attributeName).isNominal()) {
            instanceList[index][currenAttributePosition] = attributeValue.toString();
        } else {
            instanceList[index][currenAttributePosition] = (currentPositionValue != null && currentPositionValue > 0) ? combineOperator.combine(Double.parseDouble(attributeValue.toString()), currentPositionValue) : Double.parseDouble(attributeValue.toString());
        }

        return instanceList;
    }

    /**
     * Transform a Dataset into another with the same colums as featuresDataset
     * and update values
     *
     * @param dataset (testing dataset)
     * @return the transformed Dataset
     */
    @Override
    protected Dataset transformTemplate(Dataset dataset) { //(testing dataset)
        CachedBabelUtils cacheBUtils = new CachedBabelUtils();

        List<String> featuresDatasetAttributes = featuresDataset.getAttributes();
        List<String> datasetAttributes = dataset.getAttributes();
        BabelUtils butils = BabelUtils.getDefault();
        Object[][] rowsToAdd = new Object[dataset.getInstances().size()][featuresDatasetAttributes.size()];

        for (int i = 0; i < dataset.getInstances().size(); i++) {
            for (int a = 0; a < datasetAttributes.size(); a++) {
                // If featureDataset contains current attribute, set value to transformedDataset
                String attributeName = datasetAttributes.get(a);
                Object datasetAttributeValue = null;
                if (attributeName.equals("target")) {
                    for (int v = 0; v < dataset.getInstances().get(i).attribute(a).numValues(); v++) {
                        if (Double.parseDouble(dataset.getInstances().get(i).attribute(a).value(v)) == dataset.getInstances().get(i).value(a)) {
                            datasetAttributeValue = dataset.getInstances().get(i).attribute(a).value(v);
                        }
                    }
                } else {
                    datasetAttributeValue = dataset.getInstances().get(i).value(a);
                }

                if (featuresDatasetAttributes.contains(attributeName)) {
                    rowsToAdd = addValue(rowsToAdd, i, attributeName, datasetAttributeValue);
                } else {
                    // If synset does not exists in cache, get hypernyms
                    List<String> hypernyms;
                    if (!cacheBUtils.existsSynsetInMap(attributeName)) {
                        hypernyms = butils.getAllHypernyms(attributeName);
                        cacheBUtils.addSynsetToCache(attributeName, hypernyms);
                        //System.out.println("attributeName: " +  attributeName + " >> " + hypernyms);
                    } else {
                        hypernyms = cacheBUtils.getCachedSynsetHypernymsList(attributeName);
                        //System.out.println("attributeName caché: " +  attributeName + " >> " + hypernyms);
                    }

                    for (String hypernym : hypernyms) {
                        if (featuresDatasetAttributes.contains(hypernym)) {
                            rowsToAdd = addValue(rowsToAdd, i, hypernym, datasetAttributeValue);
                            System.out.println("synset : " + attributeName + " - hypernym: " + hypernym);
                            break;
                        }
                    }
                }
            }
        }

        ArrayList<Attribute> attributes = new ArrayList<>();
        Instances featureInstances = featuresDataset.getWekaDataset();

        for (int i = 0; i < featuresDataset.numAttributes(); i++) {
            attributes.add(featureInstances.attribute(i));
        }

        Dataset transformedDataset = new Dataset("Transformed dataset", attributes, 0);
        transformedDataset.addRows(rowsToAdd);

        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target", new Enum2IntTransformer(transformList));
        
        transformedDataset.generateARFFWithComments(transformersList, "transformedDataset.arff");
        //dataset = transformedDataset;
        return transformedDataset;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target", new Enum2IntTransformer(transformList));

        BufferedReader reader = new BufferedReader(new FileReader("outputsyns_testJavier_17440.arff"));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
        Instances data = arff.getData();

        Dataset originalDataset = new Dataset(data);

        Dataset[] stratifiedDataset = originalDataset.split(true, 20, 80);

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
        attributesToDelete.add("allCaps");
        attributesToDelete.add("compressionRatio");
        attributesToDelete.add("interjectionRatio");
        attributesToDelete.add("NERDATE");
        attributesToDelete.add("NERMONEY");
        attributesToDelete.add("NERNUMBER");
        attributesToDelete.add("NERADDRESS");
        attributesToDelete.add("NERLOCATION");

        for (Dataset dataset : stratifiedDataset) {
            dataset.deleteAttributeColumns(attributesToDelete);
        }
        Dataset testingDataset = stratifiedDataset[0];
        Dataset trainingDataset = stratifiedDataset[1];

        eSDRS gESDRS = new eSDRS();
        Dataset generalizatedDataset = gESDRS.transform(trainingDataset);
        DatasetFeatureRepresentation dfr = new DatasetFeatureRepresentation(generalizatedDataset);

        Dataset newDataset = dfr.transform(testingDataset);
        
        testingDataset.generateARFFWithComments(transformersList, "testingDataset.arff");
        generalizatedDataset.generateARFFWithComments(transformersList, "generalizatedDataset.arff");
        newDataset.generateARFFWithComments(transformersList, "newDataset.arff");
                

    }

}
