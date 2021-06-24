/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template FILE, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import java.io.BufferedReader;
import org.bdp4j.types.Dataset;
import org.bdp4j.types.DatasetTransformer;
import org.nlpa.util.CachedBabelUtils;

import org.nlpa.util.BabelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.nlpa.transformers.dataset.tree.WekaSynsetInstance;
import org.nlpa.transformers.dataset.tree.SynsetNode;
import org.nlpa.transformers.dataset.tree.SynsetNodePath;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSink;

/**
 * This class contains all the methods necessary for the reduction of
 * dimensionality of a dataset based on the synsets it contains. This is
 * achieved through the generalization of similar synsets. Two synsets are
 * considered similar if they are not too far away from each other and they both
 * have a similar ham/spam rate
 *
 * @author María Novo Lourés
 */
public class eSDRS extends DatasetTransformer {

    private static final File FILE = new File("outputsyns_file.map");
    private static final Dataset.CombineOperator DEFAULT_OPERATOR = Dataset.COMBINE_SUM;
    private static final int DEFAULT_DEGREE = 2;
    private static final double DEFAULT_REQUIRED_SIMILARITY = 0.90;
    private static final Datatype DEFAULT_DATATYPE = Datatype.FREQUENCY;

    private static final Comparator<SynsetNode> SYNSET_NODE_COMPARATOR_BY_DEGREE
            = (nodeA, nodeB) -> {
                int cmp = Integer.compare(nodeA.getDegree(), nodeB.getDegree());
                if (cmp == 0) {
                    return -nodeA.getReferenceSynset().compareTo(nodeB.getReferenceSynset());
                } else {
                    return cmp;
                }
            };

    public static enum Datatype {
        FREQUENCY,
        BINARY,
        APPEARENCES_NUMBER
    }

    private Datatype datatype = DEFAULT_DATATYPE;
    /**
     * Max relationship degree that we will admit
     */
    private int maxDegree = DEFAULT_DEGREE;

    /**
     * Max rate that we will considered
     */
    private double requiredSimilarity = DEFAULT_REQUIRED_SIMILARITY;

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

    private boolean stop = false;

    static {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            CACHED_HYPERNYMS.putAll((HashMap<String, List<String>>) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            logger.error("[READ]" + e.getMessage());
        }
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     * @param operator the operator used to combine attributes
     * @param matchRate the match rate
     */
    public eSDRS(int degree, Dataset.CombineOperator operator, double matchRate) {
        this.maxDegree = degree;
        this.combineOperator = operator;
        this.requiredSimilarity = matchRate;
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     * @param operator the operator used to combine attributes
     */
    public eSDRS(int degree, Dataset.CombineOperator operator) {
        this(degree, operator, DEFAULT_REQUIRED_SIMILARITY);
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     */
    public eSDRS(int degree) {
        this(degree, DEFAULT_OPERATOR, DEFAULT_REQUIRED_SIMILARITY);
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param operator the operator used to combine attributes
     */
    public eSDRS(Dataset.CombineOperator operator) {
        this(DEFAULT_DEGREE, operator, DEFAULT_REQUIRED_SIMILARITY);
    }

    /**
     * Default constructor.
     *
     */
    public eSDRS() {
        this(DEFAULT_DEGREE, DEFAULT_OPERATOR, DEFAULT_REQUIRED_SIMILARITY);
    }

    /**
     * Get maxDegree attribute value
     *
     * @return maxDegree variable value
     */
    public int getMaxDegree() {
        return maxDegree;
    }

    /**
     * Establish value to maxDegree attribute
     *
     * @param maxDegree the new value that maxDegree will have
     */
    public void setMaxDegree(int maxDegree) {
        this.maxDegree = maxDegree;
    }

    /**
     * Get requiredSimilarity attribute value
     *
     * @return requiredSimilarity variable value
     */
    public double getRequiredSimilarity() {
        return requiredSimilarity;
    }

    /**
     * Establish value to requiredSimilarity attribute
     *
     * @param matchRate the new value that requiredSimilarity will have
     */
    public void setRequiredSimilarity(double matchRate) {
        this.requiredSimilarity = matchRate;
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
     * Get the data type of dataset
     *
     * @return the data type of dataset
     */
    public Datatype getDatatype() {
        return datatype;
    }

    /**
     * Estabilish the data type of dataset
     *
     * @param datatype type of dataset information
     */
    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    @Override
    public Dataset transformTemplate(Dataset dataset) {
        long start = System.currentTimeMillis();
        logger.info("Dataset loaded");
        Serializable target = 1d;
        dataset.filterColumns("id|^bn|target");

        createCache(dataset.filterColumnNames("^bn"));

        logger.info("Start Building map");
        Map<String, SynsetNode> synsetNodeMap = new HashMap<>();
        for (int i = 0; i < dataset.getInstances().size(); i++) {
            Instance instance = dataset.getInstances().get(i);

            for (int j = 1; j < dataset.numAttributes() - 1; j++) {
                double synsetValue = instance.value(j);

                if (datatype.equals(Datatype.APPEARENCES_NUMBER)) {
                    if (synsetValue >= 1) {
                        String synsetId = instance.attribute(j).name();
                        if (synsetNodeMap.containsKey(synsetId)) {
                            synsetNodeMap.get(synsetId).addInstance(new WekaSynsetInstance(instance));
                        } else {
                            SynsetNode node = new SynsetNode(synsetId);
                            node.addInstance(new WekaSynsetInstance(instance));
                            synsetNodeMap.put(synsetId, node);
                            List<String> allSynsetHypernyms = CACHED_HYPERNYMS.get(synsetId);
                            List<String> synsetHypernyms = allSynsetHypernyms.subList(1, allSynsetHypernyms.size());

                            for (String hypernym : synsetHypernyms) {
                                if (synsetNodeMap.containsKey(hypernym)) {
                                    synsetNodeMap.get(hypernym).addChild(node);
                                } else {
                                    SynsetNode child = node;
                                    node = new SynsetNode(hypernym, null);
                                    node.addChild(child);
                                    synsetNodeMap.put(hypernym, node);
                                }
                            }
                        }
                    }
                } else {
                    if (synsetValue > 0) {
                        String synsetId = instance.attribute(j).name();
                        if (synsetNodeMap.containsKey(synsetId)) {
                            synsetNodeMap.get(synsetId).addInstance(new WekaSynsetInstance(instance));
                        } else {
                            SynsetNode node = new SynsetNode(synsetId);
                            node.addInstance(new WekaSynsetInstance(instance));
                            synsetNodeMap.put(synsetId, node);

                            List<String> allSynsetHypernyms = CACHED_HYPERNYMS.get(synsetId);
                            List<String> synsetHypernyms = allSynsetHypernyms.subList(1, allSynsetHypernyms.size());

                            for (String hypernym : synsetHypernyms) {
                                if (synsetNodeMap.containsKey(hypernym)) {
                                    SynsetNode hypernymNode = synsetNodeMap.get(hypernym);
                                    hypernymNode.addChild(node);
                                    node = hypernymNode;

                                } else {
                                    SynsetNode child = node;
                                    node = new SynsetNode(hypernym, null);
                                    node.addChild(child);
                                    synsetNodeMap.put(hypernym, node);
                                }
                            }
                        }

                    }
                }

            }
        }

        logger.info("End building map. Map size: " + synsetNodeMap.size());

        Set<SynsetNode> treeSet = synsetNodeMap.values().stream()
                .filter(node -> node.getParent() == null)
                .collect(Collectors.toSet());

        while (!stop) {
            stop = true;

            // ----- GENERARALIZACIÓN VERTICAL ----
            logger.info("Start Vertically generalization");
            long startV = System.currentTimeMillis();

            treeSet.forEach(tree -> generalizeVertically(tree, target));
//            for (SynsetNode tree : treeSet) {
//                System.out.println(generalizeVertically(tree, target).toStringDeep());
            logger.info("End Vertically generalization. Execution time: " + (System.currentTimeMillis() - startV) + " ms");

            // ----- GENERARALIZACIÓN HORIZONTAL ----
            logger.info("Start Horizontally generalization");
            long startH = System.currentTimeMillis();
            treeSet.forEach(tree -> generalizeHorizontally(tree, target));
//            for (SynsetNode tree : treeSet) {
//                System.out.print(generalizeHorizontally(tree, target).toStringDeep());
//            }
            logger.info("End Horizontally generalization. Execution time: " + (System.currentTimeMillis() - startH) + " ms");
        }

        // ----- GENERAR DATASET ----
        logger.info("Number of features before generalization: " + (dataset.numAttributes() - 2));
        Dataset generalizedDataset = generateDataset(dataset, treeSet);
        logger.info("Number of features after generalization: " + (dataset.numAttributes() - 2));
        logger.info("Execution time (eSDRS algorithm): " + (System.currentTimeMillis() - start) + " ms");

        return generalizedDataset;
    }

    /**
     * Returns true if the indicated rate is ham
     *
     * @param spamRate
     * @return true if indicated rate is ham
     */
    private boolean isHam(double spamRate) {
        return spamRate <= 1d - this.requiredSimilarity;
    }

    /**
     * Returns true if the indicated rate is spam
     *
     * @param spamRate
     * @return true if indicated rate is spam
     */
    private boolean isSpam(double spamRate) {
        return spamRate >= this.requiredSimilarity;
    }

    /**
     * Returns true if the indicated rate is neither ham nor spam.
     *
     * @param spamRate
     * @return true if the indicated rate is neither ham nor spam.
     */
    private boolean isNone(double spamRate) {
        return !isHam(spamRate) && !isSpam(spamRate);
    }

    /**
     * Grouping synsets that are connected by hypernym relations
     *
     * @param tree Tree that contains synsets and its hypernyms connected
     * @param target Target used to generalize synsets
     * @return The generalized tree
     */
    public SynsetNode generalizeVertically(SynsetNode tree, Serializable target) {
        TreeSet<SynsetNode> treeNodes = new TreeSet<>(SYNSET_NODE_COMPARATOR_BY_DEGREE);
        treeNodes.addAll(tree.getDescendantAndSelf());

        SynsetNode currentNode;

        while ((currentNode = treeNodes.pollLast()) != null) {
            if (!currentNode.hasInstances() || !currentNode.hasParent()) {
                continue;
            }
            double currentFrequency = currentNode.getTargetFrequency(target);

            if (isNone(currentFrequency)) {
                continue;
            }

            boolean currentIsHam = isHam(currentFrequency);
            boolean currentIsSpam = isSpam(currentFrequency);

            DoublePredicate hasSameClass = frequency
                    -> (currentIsHam && isHam(frequency)) || (currentIsSpam && isSpam(frequency));

            SynsetNode parent = currentNode.getParent();
            if (parent.hasInstances()) {
                if (hasSameClass.test(parent.getTargetFrequency(target))
                        && hasSameClass.test(parent.getCombinedTargetFrequency(target, currentNode))) {
                    parent.generalize(currentNode);
                    if (parent.hasSynsets() && currentNode.hasSynsets()) {
                        stop = false;
                    }
                }
            } else {
                if (currentNode.getFirstAncestorWithInstancesDegree() <= maxDegree) {
                    SynsetNode ancestorWithInstances = currentNode.getFirstAncestorWithInstances();
                    if (ancestorWithInstances != null && hasSameClass.test(ancestorWithInstances.getTargetFrequency(target))) {
                        parent.generalize(currentNode);
                        if (parent.hasSynsets() && currentNode.hasSynsets()) {
                            stop = false;
                        }
                    }
                }
            }
        }
        tree.prune();

        return tree;
    }

    /**
     * Grouping synsets with a common hypernym
     *
     * @param tree Tree that contains synsets and its hypernyms connected
     * @param target Target used to generalize synsets
     * @return The generalized tree
     */
    private SynsetNode generalizeHorizontally(SynsetNode tree, Serializable target) {
        TreeSet<SynsetNode> treeNodes = new TreeSet<>(SYNSET_NODE_COMPARATOR_BY_DEGREE);
        treeNodes.addAll(tree.getDescendantAndSelf());

        SynsetNode currentNode;

        while ((currentNode = treeNodes.pollLast()) != null) {
            if (!currentNode.hasInstances() || !currentNode.hasSynsets() || !currentNode.hasParent()) {
                continue;
            }

            double currentFrequency = currentNode.getTargetFrequency(target);

            if (isNone(currentFrequency)) {
                continue;
            }
            boolean currentIsHam = isHam(currentFrequency);
            boolean currentIsSpam = isSpam(currentFrequency);
            DoublePredicate hasSameClass = frequency
                    -> (currentIsHam && isHam(frequency)) || (currentIsSpam && isSpam(frequency));

            SynsetNode parent = currentNode.getParent();
            List<SynsetNodePath> paths = parent.getPathToDescendants(maxDegree - 1, Collections.singleton(currentNode));

            Collections.sort(paths, (pathA, pathB) -> -Integer.compare(pathA.getLength(), pathB.getLength()));

            for (SynsetNodePath path : paths) {
                SynsetNode last = path.getLast();
                if (last.hasInstances() && hasSameClass.test(last.getTargetFrequency(target))) {
                    if (path.allMatch(node -> !node.hasInstances() || hasSameClass.test(node.getTargetFrequency(target)))) {
                        SynsetNodePath candidate = path.prepend(currentNode);
                        SynsetNode root = path.getRoot();
                        List<SynsetNode> pathWithoutRoot = candidate.getPathWithoutRoot();

                        double combinedFrequency = root.getCombinedTargetFrequency(target, pathWithoutRoot);
                        if (hasSameClass.test(combinedFrequency)) {
                            root.generalize(pathWithoutRoot);
                            if (root.hasSynsets()) {
                                stop = false;
                            }
                        }
                    }
                }
            }
        }
        tree.prune();
        return tree;
    }

    private Dataset generateDataset(Dataset dataset, Set<SynsetNode> treeSet) {

        try {
            Set<SynsetNode> setGeneralizedNodes = treeSet.stream().filter(node -> node.countSynsets() > 1).collect(Collectors.toSet());

            TreeSet<SynsetNode> treeSetGeneralizedNodes = new TreeSet<>(SYNSET_NODE_COMPARATOR_BY_DEGREE.reversed());
            treeSetGeneralizedNodes.addAll(setGeneralizedNodes);

            SynsetNode currentNode;
            logger.info("Generate dataset");
            while ((currentNode = treeSetGeneralizedNodes.pollLast()) != null) {
                double targetFrequency = currentNode.getTargetFrequency(1d);
                if (targetFrequency < this.requiredSimilarity && targetFrequency >= 1 - this.requiredSimilarity) {
                    System.out.print("This node: " + currentNode.getSynsets() + " has wrong generalization. Its target frequency is: " + targetFrequency);
                }
                List<String> synsetsList = currentNode.getSynsets();
                List<String> listAttributeToJoin = new ArrayList();

                Iterator<String> synsetListIterator = synsetsList.iterator();
                String hypernyn = synsetListIterator.next();
                while (synsetListIterator.hasNext()) {
                    listAttributeToJoin.add(synsetListIterator.next());
                }
                dataset.joinAttributes(listAttributeToJoin, hypernyn, Dataset.COMBINE_SUM, true);
            }
            logger.info("END genetateDataset");
        } catch (Exception ex) {
            System.out.println("[GENERATE DATASET]: " + ex.getMessage());
        }
        return dataset;
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

    public static void main(String[] args) {
        BufferedReader reader = null;
        try {
            String spamAssassinArff = "FREQ_spamassassin_corpus-nlpa_classif_10491.arff";
            String youTubeArff = "FREQ_youtube-uci_corpus-nlpa_classif_152225.arff";
            String javierArff = "outputsyns_testJavier_17440.arff";
            String javierArffMini = "outputsyns_testJavierMini.arff";

            String fileToProcess = spamAssassinArff;
            String corpus = (fileToProcess.contains("youtube") ? "YT" : (fileToProcess.contains("spamassassin") ? "SA" : "test"));
            System.out.println("FILE TO PROCESS: " + fileToProcess);
            long startTime = System.currentTimeMillis();

//            eSDRS esdrsNew = new eSDRS(2, DEFAULT_OPERATOR, 0.80);
//            eSDRS esdrsNew = new eSDRS(2, DEFAULT_OPERATOR, 0.85);
//            eSDRS esdrsNew = new eSDRS(2, DEFAULT_OPERATOR, 0.90);
//            eSDRS esdrsNew = new eSDRS(2, DEFAULT_OPERATOR, 0.95);
//            eSDRS esdrsNew = new eSDRS(3, DEFAULT_OPERATOR, 0.80);
//            eSDRS esdrsNew = new eSDRS(3, DEFAULT_OPERATOR, 0.85);
//            eSDRS esdrsNew = new eSDRS(3, DEFAULT_OPERATOR, 0.90);
//            eSDRS esdrsNew = new eSDRS(3, DEFAULT_OPERATOR, 0.95);
//            eSDRS esdrsNew = new eSDRS(4, DEFAULT_OPERATOR, 0.80);
//            eSDRS esdrsNew = new eSDRS(4, DEFAULT_OPERATOR, 0.85);
//            eSDRS esdrsNew = new eSDRS(4, DEFAULT_OPERATOR, 0.90);
            eSDRS esdrsNew = new eSDRS(4, DEFAULT_OPERATOR, 0.95);

            reader = new BufferedReader(new FileReader(fileToProcess));
            if (fileToProcess.equals("outputsyns_testJavier_17440.arff")) {
                esdrsNew.setDatatype(Datatype.APPEARENCES_NUMBER);
            }
            System.out.println("MD: " + esdrsNew.getMaxDegree() + " - RS: " + esdrsNew.getRequiredSimilarity());
            System.out.println("DATA TYPE: " + esdrsNew.getDatatype().toString());
            ArffLoader.ArffReader arff;
            try {
                arff = new ArffLoader.ArffReader(reader);
                Instances data = arff.getData();
                Dataset dataset = new Dataset(data);
                Dataset generalizedDataset = esdrsNew.transformTemplate(dataset);
                int rs = (int) (esdrsNew.getRequiredSimilarity() * 100);
                String filename = corpus + "_MD" + esdrsNew.getMaxDegree() + "_RS0" + rs + "_THEO" + startTime;
                generalizedDataset.setOutputFile(filename + ".csv");
                generalizedDataset.generateCSV();

                DataSink sink = new DataSink(filename + ".arff");
                sink.write(generalizedDataset.getWekaDataset());
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(eSDRS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(eSDRS.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();

            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(eSDRS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
