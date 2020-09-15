/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template FILE, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import org.bdp4j.types.Dataset;
import org.bdp4j.types.DatasetTransformer;
import org.nlpa.util.CachedBabelUtils;

import org.nlpa.util.BabelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.logging.Level;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bdp4j.util.Pair;
import org.nlpa.util.Trio;

/**
 * This class contains all the methods necessary for the reduction of
 * dimensionality of a dataset based on the synsets it contains. This is
 * achieved through the generalization of similar synsets. Two synsets are
 * considered similar if they are not too far away from each other and they both
 * have a similar ham/spam rate (&gt;= 90% or &lt;= 10%)
 *
 * @author Javier Quintas Bergantiño
 */
public class eSDRS extends DatasetTransformer {

    private static final File FILE = new File("outputsyns_file.map");
    private static final Dataset.CombineOperator DEFAULT_OPERATOR = Dataset.COMBINE_SUM;
    private static final int DEFAULT_DEGREE = 3;
    private static final double DEFAULT_MATCH_RATE = 0.99;
    private static final int DEFAULT_MAX_THREADS = 120;
    /**
     * Expression needed to generalize synsets
     */
    private static final String GENERAL_EXPRESSION = "( %s > 0) ? 1 : 0";

    /**
     * Map used to save a synset and and its corresponding hypernym
     * generalisation
     */
    private Map<String, String> synsetGeneralizations = new HashMap<>();
    private Map<String, String> auxSynsetGeneralizations = new HashMap<>();

    /**
     * Determines if the number of threads is limited
     */
    private boolean limitMaxThreads = false;

    /**
     * Max number of threads
     */
    private int maxThreads = DEFAULT_MAX_THREADS;

    /**
     * Max relationship degree that we will admit
     */
    private int maxDegree = DEFAULT_DEGREE;

    /**
     * Max rate that we will considered
     */
    private double matchRate = DEFAULT_MATCH_RATE;

    /**
     * Whether ".csv" and ".arff" files should be generated or not
     */
    private boolean generateFiles;

    /**
     * Combine operator to use when joining attributes
     */
    private Dataset.CombineOperator combineOperator = DEFAULT_OPERATOR;

    /**
     * Map containing synsets as keys and their list of hypernyms as values
     */
    private final static Map<String, List<String>> CACHED_HYPERNYMS = new HashMap<>();

    /**
     * Boolean variable needed to determine if the algorithm should keep
     * generalizing
     */
    private boolean keepGeneralizing;

    /**
     * Auxiliar map used to save the relationship degree between two synsets
     */
    private static Map<Pair<String, String>, Integer> degreeMap = new HashMap<>();
    //private static Map<String, List<String>> toGeneralizePrint = new HashMap<>();

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
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     * @param operator the operator used to combine attributes
     * @param generate decides whether output files will be generated or not
     * @param matchRate the match rate
     */
    public eSDRS(int degree, Dataset.CombineOperator operator, boolean generate, double matchRate) {
        this.maxDegree = degree;
        this.combineOperator = operator;
        this.generateFiles = generate;
        this.matchRate = matchRate;
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     * @param operator the operator used to combine attributes
     * @param generate decides whether output files will be generated or not
     */
    public eSDRS(int degree, Dataset.CombineOperator operator, boolean generate) {
        this(degree, operator, generate, DEFAULT_MATCH_RATE);
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     * @param operator the operator used to combine attributes
     */
    public eSDRS(int degree, Dataset.CombineOperator operator) {
        this(degree, operator, true, DEFAULT_MATCH_RATE);
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param degree the max degree that will be allowed
     */
    public eSDRS(int degree) {
        this(degree, DEFAULT_OPERATOR, true, DEFAULT_MATCH_RATE);
    }

    /**
     *
     * Constructor that lets you customize execution options
     *
     * @param operator the operator used to combine attributes
     */
    public eSDRS(Dataset.CombineOperator operator) {
        this(DEFAULT_DEGREE, operator, true, DEFAULT_MATCH_RATE);
    }

    /**
     * Default constructor.
     *
     */
    public eSDRS() {
        this(DEFAULT_DEGREE, DEFAULT_OPERATOR, true, DEFAULT_MATCH_RATE);
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
     * Get matchRate attribute value
     *
     * @return matchRate variable value
     */
    public double getMatchRate() {
        return matchRate;
    }

    /**
     * Establish value to matchRate attribute
     *
     * @param matchRate the new value that matchRate will have
     */
    public void setMatchRate(double matchRate) {
        this.matchRate = matchRate;
    }

    /**
     * Get generateFiles attribute value
     *
     * @return generateFiles variable value
     */
    public boolean isGenerateFiles() {
        return generateFiles;
    }

    /**
     * Establish value to generateFiles attribute
     *
     * @param generateFiles the new value that generateFiles will have
     */
    public void setGenerateFiles(boolean generateFiles) {
        this.generateFiles = generateFiles;
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
     * Get Threads attribute value
     *
     * @return Threads variable value
     */
    public int getMaxThreads() {
        return maxThreads;
    }

    /**
     * Establish value to maxThreads attribute
     *
     * @param maxThreads the new value that maxThreads will have
     */
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    /**
     * Get limitMaxThreads attribute value
     *
     * @return limitMaxThreads variable value
     */
    public boolean isLimitMaxThreads() {
        return limitMaxThreads;
    }

    /**
     * Establish value to limitMaxThreads attribute
     *
     * @param limitMaxThreads the new value that limitMaxThreads will have
     */
    public void setLimitMaxThreads(boolean limitMaxThreads) {
        this.limitMaxThreads = limitMaxThreads;
    }

    @Override
    public Dataset transformTemplate(Dataset dataset) {

        long startTime = System.currentTimeMillis();

        if (limitMaxThreads) {
            logger.info("MAX THREADS: " + maxThreads);
        } else {
            logger.info("THREADS not limited");
        }
        logger.info("Dataset loaded");

        //Filter Dataset columns
        List<String> synsetList = dataset.filterColumnNames("^bn:");

        logger.info("Number of synsets in original dataset: " + synsetList.size());

        //Create a FILE that stores all the hypernyms on a map
        createCache(synsetList);
        logger.info("Cache created");

        Map<String, Set<String>> synsetsToGeneralize = new HashMap<>();

        //Loop that keeps generalizing while possible
        do {
            keepGeneralizing = false;

            //The synsetList gets sorted by hypernym list size
            Collections.sort(synsetList, (String a, String b) -> CACHED_HYPERNYMS.get(b).size() - CACHED_HYPERNYMS.get(a).size());

            logger.info("Synsets sorted");

            logger.info("Start generalizeVertically");
            try {
                //Generalize synsets with those that appear on its hypernym list and distance <= maxDegree
                dataset = generalizeVertically(synsetList, dataset);
            } catch (Exception ex) {
                logger.error("[GENERALIZE VERTICALLY]" + ex);
                ex.printStackTrace();
            }
            logger.info("End generalizeVertically");

            logger.info("Start evaluate");
            Map<String, Set<String>> evaluationResult;
            try {
                evaluationResult = evaluate(dataset);
                synsetsToGeneralize.putAll(evaluationResult);
            } catch (Exception ex) {
                logger.error("[EVALUATE]" + ex);
                ex.printStackTrace();
            }

            logger.info("End evaluate");
            logger.info("Start generalizeHorizontally");
            try {
                dataset = generalizeHorizontally(dataset, synsetsToGeneralize);
            } catch (Exception ex) {
                logger.error("[GENERALIZE HORIZONTALLY]" + ex);
                ex.printStackTrace();
            }
            logger.info("End generalizeHorizontally");
            synsetList = dataset.filterColumnNames("^bn:");

            synsetsToGeneralize.clear();

        } while (keepGeneralizing);

        if (generateFiles) {
            Calendar fecha = Calendar.getInstance();
            int ano = fecha.get(Calendar.YEAR);
            int mes = fecha.get(Calendar.MONTH) + 1;
            int dia = fecha.get(Calendar.DAY_OF_MONTH);
            int hora = fecha.get(Calendar.HOUR_OF_DAY);
            int minuto = fecha.get(Calendar.MINUTE);
            int segundo = fecha.get(Calendar.SECOND);
            String currenDate = ano + mes + dia + "_" + hora + minuto + segundo;

            dataset.setOutputFile(currenDate + "relationshipDegree_" + maxDegree + ".csv");
            dataset.generateCSV();
            dataset.generateARFFWithComments(null, currenDate + "relationshipDegree_" + maxDegree + ".arff");
        }

        long endTime = System.currentTimeMillis();
        logger.info("Execution time in milliseconds: " + (endTime - startTime));

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
     * Obtains the hypernyms of a synset
     *
     * @param synset the synset from which we want to get its hypernyms
     * @return list of its hypernyms
     */
    private List<String> getHypernyms(String synset) {
        if (CACHED_HYPERNYMS.containsKey(synset)) {
            return CACHED_HYPERNYMS.get(synset);
        } else {
            return addSynsetToCache(synset);
        }
    }

    private double computeSpamPercentage(String evaluatedSynset, Dataset dataset) {

        String evaluatedSynsetExpression = String.format(GENERAL_EXPRESSION, evaluatedSynset);

        Map<String, Integer> evaluatedSynsetResult = dataset.evaluateColumns(evaluatedSynsetExpression,
                Integer.class,
                new String[]{evaluatedSynset},
                new Class[]{double.class},
                "target");
        double ham = evaluatedSynsetResult.get("0").doubleValue();
        double spam = evaluatedSynsetResult.get("1").doubleValue();

        return (spam / (ham + spam));
    }

    private Trio<String, List<String>, Double>[] getSynsetsDataInParallel(List<String> synsetList, Dataset dataset) {
        return synsetList.stream()
                .parallel()
                .map(synset -> new Trio<>(synset, CACHED_HYPERNYMS.get(synset), computeSpamPercentage(synset, dataset)))
                .toArray(Trio[]::new);
    }

    Trio<String, List<String>, Double>[] getSynsetsData(List<String> synsetList, Dataset dataset) throws ExecutionException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(maxThreads);
        try {
            return forkJoinPool.submit(() -> getSynsetsDataInParallel(synsetList, dataset))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("ERROR getSynsetsData: " + e.getMessage());
        } finally {
            forkJoinPool.shutdown();
        }
        return null;
    }

    /**
     *
     * Generalizes synsets with those that appear in its hypernyms list with a
     * distance less or equal to maxDegree
     *
     * @param synsetList the list of synsets in the dataset
     * @param dataset the original dataset
     * @return dataset that had its vertical relationships generalized
     */
    private Dataset generalizeVertically(List<String> synsetList, Dataset dataset) throws ExecutionException {
        Set<String> usedSynsets = new HashSet<>();
        Trio<String, List<String>, Double>[] synsetsData;
        if (limitMaxThreads) {
            synsetsData = getSynsetsData(synsetList, dataset);
        } else {
            synsetsData = synsetList.stream()
                    .parallel()
                    .map(synset -> new Trio<>(synset, CACHED_HYPERNYMS.get(synset), computeSpamPercentage(synset, dataset)))
                    .toArray(Trio[]::new);
        }
        for (int i = 0; i < synsetsData.length - 1; i++) {
            String evaluatedSynset = synsetsData[i].getObj1();

            List<String> evaluatedSynsetHypernyms = synsetsData[i].getObj2();

            for (int j = i + 1; j < synsetsData.length; j++) {
                String synset = synsetsData[j].getObj1();
                List<String> synsetHypernyms = synsetsData[j].getObj2();

                if (synsetHypernyms.contains(evaluatedSynset) || evaluatedSynsetHypernyms.contains(synset)) {
                    Pair<String, String> pair = new Pair<>(evaluatedSynset, synset);
                    HypernymAndDegree hypernymAndDegree = relationshipDegree(evaluatedSynset, synset, evaluatedSynsetHypernyms, synsetHypernyms, synsetList, synsetsData);
                    if (hypernymAndDegree != null) {
                        int pairDegree = degreeMap.computeIfAbsent(pair, p -> hypernymAndDegree.getDegree());
                        String hypernym = hypernymAndDegree.getHypernym();
                        List<String> synsetsToPrint = new ArrayList<>();
                        if (pairDegree <= maxDegree && pairDegree > 0) {
                            if (!evaluatedSynset.equals(hypernym) && hypernym != null) {
                                auxSynsetGeneralizations.put(evaluatedSynset, hypernym);
                            }
                            if (evaluatedSynsetHypernyms.contains(synset)) {
                                generalize(synset, evaluatedSynset, usedSynsets, synsetsToPrint, dataset);
                                break;
                            } else if (synsetHypernyms.contains(evaluatedSynset)) {
                                generalize(evaluatedSynset, synset, usedSynsets, synsetsToPrint, dataset);
                                keepGeneralizing = true;
                                break;
                            }

                        }
                    }
                }
            }
        }

        return dataset;
    }

    private void generalize(String hypernym, String synset, Set<String> usedSynsets, List<String> synsetsToPrint, Dataset dataset) {
        try {
            boolean synsetIsUsed = usedSynsets.contains(hypernym);

            synsetGeneralizations.put(synset, hypernym);
            synsetsToPrint.add(hypernym);

            List<String> listAttributeNameToJoin = new ArrayList<>();
            listAttributeNameToJoin.add(synset);
            listAttributeNameToJoin.add(hypernym);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
            String before = format.format(new Date());
            dataset.joinAttributes(listAttributeNameToJoin, hypernym, combineOperator, !synsetIsUsed);
            System.out.println("V: " + hypernym + ": " + listAttributeNameToJoin + ".  From " + before + " to " + format.format(new Date()));

            usedSynsets.add(synset);
        } catch (Exception ex) {
            logger.warn("[GENERALIZE] " + ex.getMessage(), ex);
        }
    }

    private static class HypernymAndDegree {

        private String hypernym;
        private int degree;

        public HypernymAndDegree(String hypernym, int degree) {
            this.hypernym = hypernym;
            this.degree = degree;
        }

        public String getHypernym() {
            return hypernym;
        }

        public int getDegree() {
            return degree;
        }
    }

    /**
     *
     * Determines the relationship degree between two synsets
     *
     * @param synset1 synset that we want to evaluate
     * @param synset2 synset that we want to evaluate
     * @param synset1Hypernyms list containing all the hypernyms of synset1
     * @param synset2Hypernyms list containing all the hypernyms of synset2
     *
     * @return hypernym and degree of relationship between both synsets
     *
     */
    private HypernymAndDegree relationshipDegree(
            String synset1, String synset2,
            List<String> synset1Hypernyms, List<String> synset2Hypernyms, List<String> synsetList,
            Trio<String, List<String>, Double>[] synsetsData
    ) {
        String hypernym = null;
        List<String> synsetPath = null;

        if (synset1.equals(synset2)) {
            return new HypernymAndDegree(synset1, 0);
        } else if (synset2Hypernyms.contains(synset1)) {
            hypernym = synset1;
            synsetPath = synset2Hypernyms.subList(0, synset2Hypernyms.indexOf(synset1) + 1);
        } else if (synset1Hypernyms.contains(synset2)) {
            hypernym = synset2;
            synsetPath = synset1Hypernyms.subList(0, synset1Hypernyms.indexOf(synset2) + 1);
        } else {
            common_hypernym:
            for (int i = 1; i < synset1Hypernyms.size(); i++) {
                String hypernym1 = synset1Hypernyms.get(i);
                for (int j = 1; j < synset2Hypernyms.size(); j++) {
                    String hypernym2 = synset2Hypernyms.get(j);

                    if (hypernym1.equals(hypernym2)) {
                        hypernym = hypernym1;
                        synsetPath = new ArrayList<>();
                        synsetPath.addAll(synset1Hypernyms.subList(0, i + 1));
                        synsetPath.addAll(synset2Hypernyms.subList(0, j));
                        break common_hypernym;
                    }
                }
            }
        }

        if (synsetPath == null || hypernym == null) {
            return null;
        } else {
            ToDoubleFunction<String> getSpamPercentage = synset -> {
                int index = synsetList.indexOf(synset);
                return index >= 0 ? synsetsData[index].getObj3() : Double.NaN;
            };

            double synset1SpamPercentage = getSpamPercentage.applyAsDouble(synset1);

            final double inverseMatchRate = 1 - matchRate;
            if (synset1SpamPercentage < matchRate && synset1SpamPercentage > inverseMatchRate) {
                return null;
            }

            Predicate<String> hasSameClassAsSynset1 = synset -> {
                double spamPercentage = getSpamPercentage.applyAsDouble(synset);

                return Double.isNaN(spamPercentage)
                        || (synset1SpamPercentage >= matchRate && spamPercentage >= matchRate)
                        || (synset1SpamPercentage <= inverseMatchRate && spamPercentage <= inverseMatchRate);
            };

            for (String synset : synsetPath.subList(1, synsetPath.size())) {
                if (!hasSameClassAsSynset1.test(synset)) {
                    return null;
                }
            }

            return new HypernymAndDegree(hypernym, synsetPath.size() - 1);
        }
    }

    /**
     *
     * Function that evaluates more complex relations between two synsets and
     * decides if they should be generalized
     *
     * @param dataset the original dataset that we are working with
     * @param synsetList list of all the synsets in the dataset
     * @param cachedHypernyms map containing every synset and its hypernyms
     * (previously read from disk)
     * @return map containing a synset as key and a list of synsets that should
     * be generalized with it as value
     *
     */
    private Map<String, Set<String>> evaluate(Dataset dataset) throws ExecutionException {
        Map<String, Set<String>> evaluationResult = new HashMap<>();
        Set<String> usedSynsets = new HashSet<>();
        List<String> synsetList = dataset.filterColumnNames("^bn:");
        Trio<String, List<String>, Double>[] synsetsData;
        if (limitMaxThreads) {
            synsetsData = getSynsetsData(synsetList, dataset);
        } else {
            synsetsData = synsetList.stream()
                    .parallel()
                    .map(synset -> new Trio<>(synset, CACHED_HYPERNYMS.get(synset), computeSpamPercentage(synset, dataset)))
                    .toArray(Trio[]::new);
        }

        System.out.println("synsetsData.length: " + synsetsData.length);
        for (int i = 0; i < synsetsData.length - 1; i++) {
            String evaluatedSynset = synsetsData[i].getObj1();
            Set<String> synsetsToAddList = new HashSet<>();
            if (!usedSynsets.contains(evaluatedSynset)) {
                List<String> evaluatedSynsetHypernyms = synsetsData[i].getObj2();
                for (int j = i + 1; j < synsetsData.length; j++) {
                    String synset = synsetsData[j].getObj1();
                    List<String> synsetHypernyms = synsetsData[j].getObj2();
                    Pair<String, String> pair = new Pair<>(evaluatedSynset, synset);

                    HypernymAndDegree hypernymandDegree = relationshipDegree(evaluatedSynset, synset, evaluatedSynsetHypernyms, synsetHypernyms, synsetList, synsetsData);
                    if (hypernymandDegree != null) {
                        int pairDegree = degreeMap.computeIfAbsent(pair, p -> hypernymandDegree.getDegree());
                        String hypernym = hypernymandDegree.getHypernym();
                        if (pairDegree > 0 && pairDegree <= maxDegree && !usedSynsets.contains(synset) && !evaluationResult.containsKey(evaluatedSynset)) {
                            auxSynsetGeneralizations.put(evaluatedSynset, hypernym);
                            String auxEvaluatedSynset = auxSynsetGeneralizations.get(evaluatedSynset);

                            if ((evaluatedSynsetHypernyms.indexOf(synsetGeneralizations.get(evaluatedSynset)) <= evaluatedSynsetHypernyms.indexOf(auxEvaluatedSynset)) && auxEvaluatedSynset != null) {
                                synsetGeneralizations.put(evaluatedSynset, auxEvaluatedSynset);
                            }
                            usedSynsets.add(synset);
                            synsetsToAddList.add(synset);

                            keepGeneralizing = true;
                        }
                    }
                }
            }
            usedSynsets.add(evaluatedSynset);
            if (synsetsToAddList.size() > 0) {
                evaluationResult.put(evaluatedSynset, synsetsToAddList);
            }
        }
        return evaluationResult;
    }

    /**
     *
     * Function used to reduce the number of synsets on the original dataset
     *
     * @param dataset the original dataset that we want to reduce
     * @param synsetsToGeneralize map of synsets that we will be generalizing
     *
     * @return the dataset modified and reduced according to the generalizable
     * synsets
     */
    private Dataset generalizeHorizontally(Dataset dataset, Map<String, Set<String>> synsetsToGeneralize) {

        List<String> listAttributeNameToJoin;
        for (String synset : synsetsToGeneralize.keySet()) {
            List<String> synsetList = dataset.filterColumnNames("^bn:");

            listAttributeNameToJoin = new ArrayList<>(synsetsToGeneralize.get(synset));
            String newAttributeName = synsetGeneralizations.get(synset);

            listAttributeNameToJoin.add(synset);

            if (newAttributeName != null && !listAttributeNameToJoin.isEmpty()) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
                String before = format.format(new Date());
                dataset.joinAttributes(listAttributeNameToJoin, newAttributeName, combineOperator, synsetList.contains(newAttributeName));
                System.out.println("H: " + newAttributeName + ": " + listAttributeNameToJoin + ".  From " + before + " to " + format.format(new Date()));
            }
        }
        logger.info("[generalizeHorizontally] Number of features after reducing: " + dataset.filterColumnNames("^bn:").size());
        return dataset;
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

}
