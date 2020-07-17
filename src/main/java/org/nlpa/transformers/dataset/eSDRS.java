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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bdp4j.util.Pair;
import org.nlpa.util.Trio;

/**
 * This class contains all the methods necessary for the reduction of
 * dimensionality of a dataset based on the synsets it contains. This is
 * achieved through the generalization of similar synsets. Two synsets are
 * considered similar if they are not too far away from each other and they both
 * have a similar ham/spam rate (>= 90% or <= 10%)
 *
 * @author Javier Quintas Bergantiño
 */
public class eSDRS extends DatasetTransformer {

    private static final File FILE = new File("outputsyns_file.map");
    private static final Dataset.CombineOperator DEFAULT_OPERATOR = Dataset.COMBINE_SUM;
    private static final int DEFAULT_DEGREE = 3;
    private static final double DEFAULT_MATCH_RATE = 0.99;
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
     * @param matchRate
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

    @Override
    public Dataset transformTemplate(Dataset dataset) {

        long startTime = System.currentTimeMillis();

        //Dataset dataset = dataset;
        logger.info("Dataset loaded");

        //Filter Dataset columns
        List<String> synsetList = dataset.filterColumnNames("^bn:");

        logger.info("Number of synsets in original dataset: " + synsetList.size());

        //Create a FILE that stores all the hypernyms on a map
        createCache(synsetList);
        logger.info("Cache created");

        Map<String, List<String>> synsetsToGeneralize = new HashMap<>();

        //Loop that keeps generalizing while possible
        do {
            keepGeneralizing = false;

            //The synsetList gets sorted by hypernym list size
            Collections.sort(synsetList, (String a, String b) -> CACHED_HYPERNYMS.get(b).size() - CACHED_HYPERNYMS.get(a).size());
            
            logger.info("Synsets sorted");

            logger.info("Start generalizeVertically");
            //Generalize synsets with those that appear on its hypernym list and distance <= maxDegree
            dataset = generalizeVertically(synsetList, dataset);
            logger.info("End generalizeVertically");
 
            logger.info("Start evaluate");
            Map<String, List<String>> evaluationResult = evaluate(dataset);
            synsetsToGeneralize.putAll(evaluationResult);
            logger.info("End evaluate");
            logger.info("Start generalizeHorizontally");
            dataset = generalizeHorizontally(dataset, synsetsToGeneralize);
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

    /**
     *
     * Generalizes synsets with those that appear in its hypernyms list with a
     * distance less or equal to maxDegree
     *
     * @param synsetList the list of synsets in the dataset
     * @param dataset the original dataset
     * @return dataset that had its vertical relationships generalized
     */
    private Dataset generalizeVertically(List<String> synsetList, Dataset dataset) {
        Set<String> usedSynsets = new HashSet<>();

        double inverseMatchRate = 1 - matchRate;

        Trio<String, List<String>, Double>[] synsetsData = synsetList.stream()
                .parallel()
                .map(synset -> new Trio<>(synset, CACHED_HYPERNYMS.get(synset), computeSpamPercentage(synset, dataset)))
                .toArray(Trio[]::new);

        for (int i = 0; i < synsetsData.length - 1; i++) {
            logger.info(i);
            String evaluatedSynset = synsetsData[i].getObj1();
            double evaluatedSynsetSpamPercentage = synsetsData[i].getObj3();

            if (evaluatedSynsetSpamPercentage >= matchRate || evaluatedSynsetSpamPercentage <= inverseMatchRate) {
                List<String> evaluatedSynsetHypernyms = synsetsData[i].getObj2();

                for (int j = i + 1; j < synsetsData.length; j++) {
                    String synset = synsetsData[j].getObj1();
                    List<String> synsetHypernyms = synsetsData[j].getObj2();

                    if (synsetHypernyms.contains(evaluatedSynset) || evaluatedSynsetHypernyms.contains(synset)) {
                        logger.info("contains " + synset + " or contains " + evaluatedSynset);
                        double spamPercentage = synsetsData[j].getObj3();

                        Pair<String, String> pair = new Pair<>(evaluatedSynset, synset);
                        int pairDegree = degreeMap.computeIfAbsent(pair, p -> relationshipDegree(evaluatedSynset, synset, evaluatedSynsetHypernyms, synsetHypernyms));

                        if ((spamPercentage >= matchRate && evaluatedSynsetSpamPercentage >= matchRate) || (spamPercentage <= inverseMatchRate && evaluatedSynsetSpamPercentage <= inverseMatchRate)) {
                            List<String> synsetsToPrint = new ArrayList<>();
                            if (evaluatedSynsetHypernyms.contains(synset) && pairDegree <= maxDegree && pairDegree >= 0) {
                                generalize(synset, evaluatedSynset, usedSynsets, synsetsToPrint, dataset);
                                break;
                            } else if (synsetHypernyms.contains(evaluatedSynset) && pairDegree <= maxDegree && pairDegree >= 0) {
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
            System.out.print("JoinAttributesV " + format.format(new Date()));
            dataset.joinAttributes(listAttributeNameToJoin, hypernym, combineOperator, !synsetIsUsed);
            System.out.println(" - " + format.format(new Date()));

            usedSynsets.add(synset);
        } catch (Exception ex) {
            logger.warn("[GENERALIZE] " + ex.getMessage(), ex);
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
     * @return degree of relationship between both synsets
     *
     */
    private int relationshipDegree(String synset1, String synset2,
            List<String> synset1Hypernyms, List<String> synset2Hypernyms) {
        if (synset1Hypernyms.isEmpty()) {
            return Integer.MIN_VALUE;
        }

        String firstSynset1Hypernym = synset1Hypernyms.get(0);

        if (firstSynset1Hypernym.equals(synset2)) { // synset2 father of synset1
            auxSynsetGeneralizations.put(synset1, firstSynset1Hypernym);
            return 1;
        } else if (synset2Hypernyms.contains(firstSynset1Hypernym)) { // synset2 hypernyms contains father os synset1
            auxSynsetGeneralizations.put(synset1, firstSynset1Hypernym);
            return synset2Hypernyms.indexOf(firstSynset1Hypernym);
        } else {
            return 1 + relationshipDegree(synset1, synset2,
                    synset1Hypernyms.subList(1, synset1Hypernyms.size()),
                    synset2Hypernyms);
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
    private Map<String, List<String>> evaluate(Dataset dataset) {
        Map<String, List<String>> evaluationResult = new HashMap<>();
        Set<String> usedSynsets = new HashSet<>();
        double inverseMatchRate = 1 - matchRate;
        List<String> synsetList = dataset.filterColumnNames("^bn:");

        Trio<String, List<String>, Double>[] synsetsData = synsetList.stream()
                .parallel()
                .map(synset -> new Trio<>(synset, CACHED_HYPERNYMS.get(synset), computeSpamPercentage(synset, dataset)))
                .toArray(Trio[]::new);

        for (int i = 0; i < synsetsData.length - 1; i++) {
            String evaluatedSynset = synsetsData[i].getObj1();
            double evaluatedSynsetSpamPercentage = synsetsData[i].getObj3();

            List<String> synsetsToAddList = new ArrayList<>();

            if ((evaluatedSynsetSpamPercentage >= matchRate || evaluatedSynsetSpamPercentage <= inverseMatchRate) && !usedSynsets.contains(evaluatedSynset)) {

                List<String> evaluatedSynsetHypernyms = synsetsData[i].getObj2();
                for (int j = i + 1; j < synsetsData.length; j++) {
                    String synset = synsetsData[j].getObj1();
                    List<String> synsetHypernyms = synsetsData[j].getObj2();

                    Pair<String, String> pair = new Pair<>(evaluatedSynset, synset);

                    int pairDegree = degreeMap.computeIfAbsent(pair, p -> relationshipDegree(evaluatedSynset, synset, evaluatedSynsetHypernyms, synsetHypernyms));
                    //if (!degreeMap.containsKey(pair)) {
                    // pairDegree = relationshipDegree(evaluatedSynset, synset, evaluatedSynsetHypernyms, synsetHypernyms);
                    //  degreeMap.put(pair, pairDegree);

                    if (pairDegree >= 0 && pairDegree <= maxDegree && !usedSynsets.contains(synset) && !evaluationResult.containsKey(evaluatedSynset)) {
                        double spamPercentage = synsetsData[j].getObj3();
                        if ((spamPercentage >= matchRate && evaluatedSynsetSpamPercentage >= matchRate) || (spamPercentage <= inverseMatchRate && evaluatedSynsetSpamPercentage <= inverseMatchRate)) {
                            //Results from evaluating these synsets
                            if (evaluatedSynsetHypernyms.indexOf(synsetGeneralizations.get(evaluatedSynset)) <= evaluatedSynsetHypernyms.indexOf(auxSynsetGeneralizations.get(evaluatedSynset))) {
                                synsetGeneralizations.put(evaluatedSynset, auxSynsetGeneralizations.get(evaluatedSynset));
                            }
                            usedSynsets.add(synset);
                            synsetsToAddList.add(synset);

                            keepGeneralizing = true;
                        }
                    }
                    //}
                }
                usedSynsets.add(evaluatedSynset);
            }
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
    private Dataset generalizeHorizontally(Dataset dataset, Map<String, List<String>> synsetsToGeneralize) {

        List<String> listAttributeNameToJoin;
        for (String synset : synsetsToGeneralize.keySet()) {
            List<String> synsetList = dataset.filterColumnNames("^bn:");

            listAttributeNameToJoin = new ArrayList<>(synsetsToGeneralize.get(synset));
            String newAttributeName = synsetGeneralizations.get(synset);

            listAttributeNameToJoin.add(synset);

            System.out.print("JoinAttributesH " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND));
            dataset.joinAttributes(listAttributeNameToJoin, newAttributeName, combineOperator, synsetList.contains(newAttributeName));
            System.out.println(" - " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND));

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
