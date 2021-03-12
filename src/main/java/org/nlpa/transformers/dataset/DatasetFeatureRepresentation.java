/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.transformers.attribute.Date2MillisTransformer;
import org.bdp4j.transformers.attribute.Enum2IntTransformer;
import org.bdp4j.types.Dataset;
import org.bdp4j.types.DatasetTransformer;
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
    private static final File FILE = new File("outputsyns_file.map");

    /**
     * Combine operator to use when joining attributes
     */
    private Dataset.CombineOperator combineOperator = DEFAULT_OPERATOR;

    /**
     * Map containing synsets as keys and their list of hypernyms as values
     */
    private final static Map<String, List<String>> CACHED_HYPERNYMS = new HashMap<>();

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(eSDRS.class);

    static {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            CACHED_HYPERNYMS.putAll((HashMap<String, List<String>>) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            logger.error("[READ]" + e.getMessage());
        }
    }

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

    /**
     * Constructor
     *
     * @param featuresDataset Source dataset from which attributes is taken
     * @param operator operator to indicate how to combine attribute values
     */
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

    /**
     * Transform a Dataset into another with the same colums as featuresDataset
     * and update values
     *
     * @param dataset (testing dataset)
     * @return the transformed Dataset
     */
    @Override
    protected Dataset transformTemplate(Dataset dataset) { //(testing dataset)
        List<String> featuresDatasetAttributes = featuresDataset.getAttributes();
        List<String> datasetAttributes = dataset.getAttributes();

        createCache(dataset.filterColumnNames("^bn"));
        Object[][] rowsToAdd = new Object[dataset.getInstances().size()][featuresDatasetAttributes.size()];

        for (int i = 0; i < dataset.getInstances().size(); i++) {
            for (int a = 0; a < datasetAttributes.size(); a++) {
                // If featureDataset contains current attribute, set value to transformedDataset
                String attributeName = datasetAttributes.get(a);

                Object datasetAttributeValue = 0;
                if (attributeName.equals("target")) {
                    for (int v = 0; v < dataset.getInstances().get(i).attribute(a).numValues(); v++) {
                        if (Double.parseDouble(dataset.getInstances().get(i).attribute(a).value(v)) == dataset.getInstances().get(i).value(a)) {
                            datasetAttributeValue = dataset.getInstances().get(i).attribute(a).value(v);
                        }
                    }
                } else if (attributeName.equals("id")) {
                    datasetAttributeValue = dataset.getInstances().get(i).stringValue(a);
                } else {
                    datasetAttributeValue = dataset.getInstances().get(i).value(a);
                }
                if (featuresDatasetAttributes.contains(attributeName)) {
                    rowsToAdd = addValue(rowsToAdd, i, attributeName, datasetAttributeValue);
                } else {
                    // If synset does not exists in cache, get hypernyms
                    List<String> hypernyms = CACHED_HYPERNYMS.get(attributeName);
                    for (String hypernym : hypernyms) {
                        if (featuresDatasetAttributes.contains(hypernym)) {
                            rowsToAdd = addValue(rowsToAdd, i, hypernym, datasetAttributeValue);
                            break;
                        }
                    }
                }
            }
        }

        for (Object[] rows : rowsToAdd) {
            for (int i = 0; i < rows.length; i++) {
                if (rows[i] == null) {
                    rows[i] = 0;
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

        return transformedDataset;
    }

    private Object[][] addValue(Object[][] instanceList, int index, String attributeName, Object attributeValue) {

        int currenAttributePosition = featuresDataset.getWekaDataset().attribute(attributeName).index();

        Double currentPositionValue = (Double) instanceList[index][currenAttributePosition];

        if (featuresDataset.getWekaDataset().attribute(attributeName).isNominal()) {
            instanceList[index][currenAttributePosition] = attributeValue.toString();
        } else if (featuresDataset.getWekaDataset().attribute(attributeName).isString()) {
            instanceList[index][currenAttributePosition] = attributeValue.toString();        
        } else {
            instanceList[index][currenAttributePosition] = (currentPositionValue != null && currentPositionValue > 0) ? combineOperator.combine(Double.parseDouble(attributeValue.toString()), currentPositionValue) : Double.parseDouble(attributeValue.toString());
        }

        return instanceList;
    }

    /**
     *
     * Receives a synset list and checks if they are already stored, if not,
     * they get added to the map along with a list of its hypernyms
     *
     * @param cachedHypernyms contains a map with <synsets, hypernyms> already
     * saved in disk
     * @param synsetList list of all the synsets that we want to check if they
     * are already in disk or not
     */
    private void createCache(List<String> synsetList) {
        CachedBabelUtils cachedBabelUtils = new CachedBabelUtils(new HashMap<>(CACHED_HYPERNYMS));

        BabelUtils bUtils = BabelUtils.getDefault();
        for (String synset : synsetList) {
            if (!cachedBabelUtils.existsSynsetInMap(synset)) {
                cachedBabelUtils.addSynsetToCache(synset, bUtils.getAllHypernyms(synset));

                for (String hypernym : cachedBabelUtils.getCachedSynsetHypernymsList(synset)) {
                    if (!cachedBabelUtils.existsSynsetInMap(hypernym)) {
                        cachedBabelUtils.addSynsetToCache(hypernym, bUtils.getAllHypernyms(hypernym));
                    }
                }
            }
        }

        CACHED_HYPERNYMS.clear();
        CACHED_HYPERNYMS.putAll(cachedBabelUtils.getMapOfHypernyms());
        saveCachedHypernyms();
    }

    /**
     *
     * Adds a new synset to the map saved in disk
     *
     * @param cachedHypernyms contains a map with <synsets, hypernyms> already
     * saved in disk
     * @param synset synset that we want to add to disk
     */
    private List<String> addSynsetToCache(String synset) {
        CachedBabelUtils cachedBabelUtils = new CachedBabelUtils(new HashMap<>(CACHED_HYPERNYMS));
        BabelUtils bUtils = BabelUtils.getDefault();
        List<String> hypernyms = null;
        if (!cachedBabelUtils.existsSynsetInMap(synset)) {
            hypernyms = bUtils.getAllHypernyms(synset);
            cachedBabelUtils.addSynsetToCache(synset, hypernyms);
            for (String hypernym : cachedBabelUtils.getCachedSynsetHypernymsList(synset)) {
                if (!cachedBabelUtils.existsSynsetInMap(hypernym)) {
                    cachedBabelUtils.addSynsetToCache(hypernym, bUtils.getAllHypernyms(hypernym));
                }
            }
        }

        CACHED_HYPERNYMS.clear();
        CACHED_HYPERNYMS.putAll(cachedBabelUtils.getMapOfHypernyms());
        return hypernyms;
    }

    /**
     *
     * Saves a map containing synsets and their hypernyms in a FILE
     *
     * @param mapOfHypernyms Map containing every synset (key) and a list of its
     * hypernyms (values)
     */
    private static void saveCachedHypernyms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(CACHED_HYPERNYMS);
        } catch (Exception e) {
            logger.error("[SAVE MAP]" + e.getMessage());
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        try {
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

            originalDataset.setOutputFile("1outputsyns_testJavier_17440.csv");
            originalDataset.generateCSV();

            Dataset[] stratifiedDataset = originalDataset.split(true, 20, 80);
            stratifiedDataset[0].setOutputFile("InitialTestingDataset.csv");
            stratifiedDataset[0].generateCSV();

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
            generalizatedDataset.setOutputFile("1generalizatedTrainingDataset.csv");
            generalizatedDataset.generateCSV();

            System.out.println("Initial Training: " + trainingDataset.getAttributes());
            System.out.println("Instances: " + trainingDataset.getInstances().get(1));
            System.out.println("Training: " + generalizatedDataset.getAttributes());

            DatasetFeatureRepresentation dfr = new DatasetFeatureRepresentation(generalizatedDataset);

            Dataset newDataset = dfr.transform(testingDataset);
            newDataset.setOutputFile("1generalizatedTestingDataset.csv");
            newDataset.generateCSV();

            System.out.println("Testing : " + newDataset.getAttributes());
            testingDataset.generateARFFWithComments(transformersList, "testingDataset.arff");
            generalizatedDataset.generateARFFWithComments(transformersList, "generalizatedDataset.arff");
            newDataset.generateARFFWithComments(transformersList, "newDataset.arff");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
