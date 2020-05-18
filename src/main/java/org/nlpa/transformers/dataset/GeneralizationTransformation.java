/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import org.bdp4j.types.Dataset;
import org.bdp4j.types.DatasetTransformer;
import java.util.stream.Collectors;
import org.nlpa.util.CachedBabelUtils;

import org.bdp4j.dataset.CSVDatasetReader;
import org.bdp4j.types.Transformer;
import org.nlpa.util.BabelUtils;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelPointer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedWriter;
import java.util.*;

import java.util.logging.Level; 
import java.util.logging.Logger;

import javafx.util.*;
import java.util.*;

/**
 *
 * @author Javier Quintas Bergantiño
 */
public class GeneralizationTransformation extends DatasetTransformer {

    static BabelNet bn = BabelNet.getInstance();
    //Map used to save the hypernyms to which synset pairs will generalize
    static Map<String, String> generalizeTo = new HashMap<String, String>(); 
    static Map<String, String> auxGeneralizeTo = new HashMap<String, String>();

    //Max relationship degree that we will admit 
    static int maxDegree = 5;

    //Map containing synsets as keys and their list of hypernyms as values
    static Map<String, List<String>> cachedHypernyms;

    //Boolean variable needed to determine if the algorithm should keep generalizing
    static boolean keepGeneralizing;

    //Auxiliar map used to save the relationship degree between two synsets
    static Map<Pair<String, String>, Integer> degreeMap = new HashMap<Pair<String, String>, Integer>();
    static Map<String, List<String>> toGeneralizePrint = new HashMap<String, List<String>>();
    static Logger logger = Logger.getLogger(GeneralizationTransformation.class.getName());

    public GeneralizationTransformation() {
    }

    @Override
    protected Dataset transformTemplate(Dataset dataset) {

        long startTime = System.currentTimeMillis();
        
        Dataset originalDataset = dataset;        
        System.out.println("Dataset cargado");

        //Filter Dataset columns
        List<String> synsetList = originalDataset.filterColumnNames("^bn:");

        logger.log(Level.INFO, "Original synset: "+ synsetList.size());

        //Create a file that stores all the hypernyms on a map
        cachedHypernyms = read(); 
        createCache(cachedHypernyms, synsetList);  
        cachedHypernyms = read();

        Map<String, List<String>> toGeneralize = new HashMap<String, List<String>>();

        //Loop that keeps generalizing while possible
        do{
            keepGeneralizing = false;

            //The synsetlist gets sorted by hypernym list size
            String[] arr = quickSort(synsetList.toArray(new String[0]), 0, synsetList.toArray().length - 1);
            synsetList = Arrays.asList(arr);
            logger.log(Level.INFO, "Synsets sorted");

            //Generalize synsets with those that appear on its hypernym list and distance <= maxDegree
            originalDataset = generalizeDirectly(synsetList, originalDataset);
            synsetList = originalDataset.filterColumnNames("^bn:");
            logger.log(Level.INFO, "Synsets generalized directly");

            
            toGeneralize.putAll(evaluate(originalDataset, synsetList, cachedHypernyms));
            for(String s: toGeneralize.keySet())
                toGeneralizePrint.put(s, toGeneralize.get(s));
            System.out.println();
            
            originalDataset = generalize(originalDataset, toGeneralize);
            logger.log(Level.INFO, "Synsets generalizados");

            
            synsetList = originalDataset.filterColumnNames("^bn:");

            toGeneralize.clear();

        }while(keepGeneralizing);


        long endTime = System.currentTimeMillis();
        logger.log(Level.INFO, "Execution time in milliseconds: " + (endTime - startTime));
        

        return originalDataset;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    /**
     * 
     * ESP: Recibe una lista de synsets y comprueba si están guardados en disco, si no, los añade al mapa con una lista de sus respectivos hiperónimos
     * ENG: Receives a synset list and checks if they are already stored, if not, they get added to the map along with a list of its hypernyms
     * 
     * @param cachedHypernyms contains a map with <synsets, hypernyms> already saved in disk
     * @param synsetList list of all the synsets that we want to check if they are already in disk or not
     */
    public static void createCache(Map<String, List<String>> cachedHypernyms, List<String> synsetList){
        CachedBabelUtils cachedBabelUtils = new CachedBabelUtils(cachedHypernyms);

        for(String s: synsetList){
            if(!cachedBabelUtils.existsSynsetInMap(s)){
                cachedBabelUtils.addSynsetToCache(s, BabelUtils.getDefault().getAllHypernyms(s));
                logger.log(Level.INFO, "Adding "+ s);

                for(String h: cachedBabelUtils.getCachedSynsetHypernymsList(s)){
                    if(!cachedBabelUtils.existsSynsetInMap(h)){
                        cachedBabelUtils.addSynsetToCache(h, BabelUtils.getDefault().getAllHypernyms(h));
                    }
                }
            }
        }

        saveMap(cachedBabelUtils.getMapOfHypernyms());
		//System.out.println("Saved elements: " + cachedBabelUtils.getMapOfHypernyms().size());
        
    }


    /**
     * 
     * ESP: Añade un synset nuevo al mapa guardado en disco
     * ENG: Adds a new synset to the map saved in disk
     * 
     * @param cachedHypernyms contains a map with <synsets, hypernyms> already saved in disk
     * @param synset synset that we want to add to disk
     */
    public static void addNewCachedSynset(Map<String, List<String>> cachedHypernyms, String synset){
        CachedBabelUtils cachedBabelUtils = new CachedBabelUtils(cachedHypernyms);

        if(!cachedBabelUtils.existsSynsetInMap(synset)){
            cachedBabelUtils.addSynsetToCache(synset, BabelUtils.getDefault().getAllHypernyms(synset));
            logger.log(Level.INFO, "Adding "+ synset);

            for(String h: cachedBabelUtils.getCachedSynsetHypernymsList(synset)){
                if(!cachedBabelUtils.existsSynsetInMap(h)){
                    cachedBabelUtils.addSynsetToCache(h, BabelUtils.getDefault().getAllHypernyms(h));
                }
            }
        }

        saveMap(cachedBabelUtils.getMapOfHypernyms());
    }


    /**
     * 
     * ESP: algoritmo de ordenación
     * ENG: sorting algorithm
     * 
     * @param arr the array to sort
     * @param begin the index from which we will be sorting
     * @param end the index of the final element we will be sorting
     * @return
     */
    public static String[] quickSort(String[] arr, int begin, int end){
        if(begin < end){
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
        return arr;
    }


    /**
     * 
     * ESP: función necesaria para el correcto funcionamiento de quicksort
     * ENG: essential function needed for quicksort algorithm
     */
    private static int partition(String[] arr, int begin, int end){
        String pivot = arr[end];
        int i = begin - 1;

        for(int j = begin; j < end; j++){
            if(cachedHypernyms.get(arr[j]).size() >= cachedHypernyms.get(pivot).size()){
                i++;

                String swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        String swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;
        
        return i+1;
    }


    /**
     * 
     * ESP: Obtener los hiperónimos de un synset
     * ENG: Obtains the hypernyms of a synset
     * 
     * @param synset the synset from which we want to get its hypernyms
     * @return list of its hypernyms
     */
    private static List<String> getHypernyms(String synset){
        List<String> toRet;
        
        if(cachedHypernyms.keySet().contains(synset)){    
            toRet = cachedHypernyms.get(synset);
        }else{
            addNewCachedSynset(cachedHypernyms, synset);
            cachedHypernyms = read();
            toRet = cachedHypernyms.get(synset);
        }

        return toRet;
    }


    /**
     * 
     * ESP: Generaliza aquellos synsets con otros que esten en su lista de hiperónimos y con una distancia límite indicada por maxDegree
     * ENG: Generalizes synsets with those that appear in its hypernyms list with a distance less or equal to maxDegree
     * 
     * @param synsetList the list of synsets in the dataset
     * @param originalDataset the original dataset
     */
    public static Dataset generalizeDirectly(List<String> synsetList, Dataset originalDataset){
        List<String> usedSynsets = new ArrayList<String>();
        for(String s1: synsetList){
            int index = synsetList.indexOf(s1);

            //Evaluate ham/spam %
            String expressionS1 = "(" + s1 + " >= 1) ? 1 : 0";
            Map<String, Integer> result1 = originalDataset.evaluateColumns(expressionS1,
                int.class, 
                new String[]{s1}, 
                new Class[]{double.class}, 
                "target");
            float ham1 = (float) result1.get("0");
            float spam1 = (float) result1.get("1");
            float percentage1 = (spam1/(ham1 + spam1));
            logger.log(Level.INFO, "Synset 1: " + s1 + " -> " + result1 + " -> " + percentage1);

            if(percentage1 >= 0.99 || percentage1 <= 0.01){
                for(String s2: synsetList.subList(index + 1, synsetList.size())){
                    //Get hypernym list of both synsets
                    List<String> s1Hypernyms = getHypernyms(s1);
                    List<String> s2Hypernyms = getHypernyms(s2);

                    if(s2Hypernyms.contains(s1) || s1Hypernyms.contains(s2)){
                        String expressionS2 = "(" + s2 + " >= 1) ? 1 : 0";
                
                        Map<String, Integer> result2 = originalDataset.evaluateColumns(expressionS2,
                            int.class, 
                            new String[]{s2}, 
                            new Class[]{double.class}, 
                            "target");
                        
                        float ham2 = (float) result2.get("0");
                        float spam2 = (float) result2.get("1");
                        float percentage2 = (spam2/(ham2 + spam2));

                        Pair<String, String> pair = new Pair<String, String>(s1, s2);

                        if(!degreeMap.containsKey(pair)){
                            int degree = relationshipDegree(s1, s2, s1Hypernyms, s2Hypernyms); 
                            degreeMap.put(pair, degree);
                        }

                        if((percentage2 >= 0.99 && percentage1 >= 0.99) || (percentage2 <= 0.01 && percentage1 <= 0.01)){
                            if(s1Hypernyms.contains(s2) && degreeMap.get(pair) <= maxDegree && degreeMap.get(pair) >= 0){
                                List<String> listAttributeNameToJoin = new ArrayList<String>();
                                Boolean aux = usedSynsets.contains(s2);
                                logger.log(Level.INFO, "Synset 1: " + s1 + " -> " + result1 + " Synset 2: " + s2 + " -> " + result2);
                                generalizeTo.put(s1, s2);
                                
                                List<String> auxPrint = new ArrayList<String>();
                                auxPrint.add(s2);
                                toGeneralizePrint.put(s1, auxPrint);
                                
                                listAttributeNameToJoin.add(s1);
                                listAttributeNameToJoin.add(s2);

                                originalDataset.joinAttributes(listAttributeNameToJoin, s2, Dataset.COMBINE_SUM, !aux);

                                if(!usedSynsets.contains(s1))
                                    usedSynsets.add(s1);

                                break; 
                            }
                            else if(s2Hypernyms.contains(s1) && degreeMap.get(pair) <= maxDegree && degreeMap.get(pair) >= 0){
                                List<String> listAttributeNameToJoin = new ArrayList<String>();
                                Boolean aux = usedSynsets.contains(s1);
                                logger.log(Level.INFO, "Synset 1: " + s1 + " -> " + result1 + " Synset 2: " + s2 + " -> " + result2);
                                generalizeTo.put(s2, s1);

                                List<String> auxPrint = new ArrayList<String>();
                                auxPrint.add(s1);
                                toGeneralizePrint.put(s2, auxPrint);
                                
                                listAttributeNameToJoin.add(s2);                                
                                listAttributeNameToJoin.add(s1);

                                originalDataset.joinAttributes(listAttributeNameToJoin, s1, Dataset.COMBINE_SUM, !aux);
                                
                                keepGeneralizing = true;

                                if(!usedSynsets.contains(s2))
                                    usedSynsets.add(s2);
                                
                                break;
                            }
                        }
                    }
                        
                }
            }
        }
        System.out.println("");
        return originalDataset;
    }


    /**
     * 
     * ESP: Determina el grado de parentesco entre dos synsets
     * ENG: Determines the relationship degree between two synsets
     * 
     * @param synset1 synset that we want to evaluate
     * @param synset2 synset that we want to evaluate
     * @param s1Hypernyms list containing all the hypernyms of synset1
     * @param s2Hypernyms list containing all the hypernyms of synset2
     * 
     * @return degree of relationship between both synsets
     * 
     */
    public static int relationshipDegree(String synset1, String synset2, 
                                        List<String> s1Hypernyms, List<String> s2Hypernyms)
    {
        if (s1Hypernyms.size() == 0)
            return Integer.MIN_VALUE;

        String s1 = s1Hypernyms.get(0);

        if (s1 == synset2){
            //if(generalizeTo.get(synset1) == null)
                auxGeneralizeTo.put(synset1, s1);

            return 1;
        } else if (s2Hypernyms.contains(s1)) {
            //if(generalizeTo.get(synset1) == null)
                auxGeneralizeTo.put(synset1, s1);

            return s2Hypernyms.indexOf(s1); 
        } else {

            return 1 + relationshipDegree(synset1, synset2, 
                                        s1Hypernyms.subList(1, s1Hypernyms.size()),
                                        s2Hypernyms);
        }
    }


    /**
     * 
     * ESP: Función que evalúa relaciones más complejas entre dos synsets y decide si deben ser generalizados
     * ENG: Function that evaluates more complex relations between two synsets and decides if they should be generalized
     * 
     * @param originalDataset the original dataset that we are working with
     * @param synsetList list of all the synsets in the dataset
     * @param cachedHypernyms map containing every synset and its hypernyms (previously read from disk)
     * 
     */
    public static Map<String, List<String>> evaluate(Dataset originalDataset, List<String> synsetList, Map<String, List<String>> cachedHypernyms){
        Map<String, List<String>> finalResult = new HashMap<String, List<String>>();
        List<String> usedSynsets = new ArrayList<String>();
        for(String s1: synsetList){
 
            //We get the index of the synset
            int index = synsetList.indexOf(s1);

            String expressionS1 = "(" + s1 + " >= 1) ? 1 : 0";
            Map<String, Integer> result1 = originalDataset.evaluateColumns(expressionS1,
                int.class, 
                new String[]{s1}, 
                new Class[]{double.class}, 
                "target");
            float ham1 = (float) result1.get("0");
            float spam1 = (float) result1.get("1");
            float percentage1 = (spam1/(ham1 + spam1));

            List<String> s2List = new ArrayList<String>();
            
            if((percentage1 >= 0.99 || percentage1 <= 0.01) && !usedSynsets.contains(s1)){
                logger.log(Level.INFO, "Synset 1: " + s1 + " -> " + result1 + " -> " + percentage1);
                //Iterate through a sublist of the original synset that contains only from the next synset to s1 onwards
                for (String s2: synsetList.subList(index + 1, synsetList.size())){
                    
                    List<String> s1Hypernyms = getHypernyms(s1);
                    List<String> s2Hypernyms = getHypernyms(s2);

                    Pair<String, String> pair = new Pair<String, String>(s1, s2);

                    if(!degreeMap.containsKey(pair)){
                        int degree = relationshipDegree(s1, s2, s1Hypernyms, s2Hypernyms); 
                        degreeMap.put(pair, degree);
                    }

                    if (degreeMap.get(pair) >= 0 && degreeMap.get(pair) <= maxDegree && !usedSynsets.contains(s2) && !finalResult.containsKey(s1)) {
                        String expressionS2 = "(" + s2 + " >= 1) ? 1 : 0";
        
                        Map<String, Integer> result2 = originalDataset.evaluateColumns(expressionS2,
                            int.class, 
                            new String[]{s2}, 
                            new Class[]{double.class}, 
                            "target");
                        
                        float ham2 = (float) result2.get("0");
                        float spam2 = (float) result2.get("1");
                        float percentage2 = (spam2/(ham2 + spam2));  
                        if((percentage2 >= 0.99 && percentage1 >= 0.99) || (percentage2 <= 0.01 && percentage1 <= 0.01)){
                            //Results from evaluating these synsets
                            logger.log(Level.INFO, "Synset 1: " + s1 + " -> " + result1 + " Synset 2: " + s2 + " -> " + result2);


                            if(s1Hypernyms.indexOf(generalizeTo.get(s1)) <= s1Hypernyms.indexOf(auxGeneralizeTo.get(s1)))
                                generalizeTo.put(s1, auxGeneralizeTo.get(s1)); 
                                
                            usedSynsets.add(s2);
                            s2List.add(s2);

                            keepGeneralizing = true;
                        }
                    }
                }
                usedSynsets.add(s1);
            }
            if (s2List.size() > 0){
                finalResult.put(s1, s2List);
            }
        }
        logger.log(Level.INFO, "\n");
        return finalResult;
    }

    /**
     * ESP: función utilizada para reducir los synsets del dataset original
     * ENG: used to reduce the number of synsets on the original dataset
     * 
     * @param originalDataset the original dataset that we want to reduce
     * @param toGeneralize map of synsets that we will be generalizing
     * 
     * @return the originalDataset modified and reduced according to the generalizable synsets
     */
    public static Dataset generalize(Dataset originalDataset, Map<String, List<String>> toGeneralize){
        
        for (String s1 : toGeneralize.keySet()){

            List<String> listAttributeNameToJoin = new ArrayList<String>();
            listAttributeNameToJoin.addAll(toGeneralize.get(s1));
            String newAttributeName = generalizeTo.get(s1);

            logger.log(Level.INFO, "Nuevo nombre de atributo: "+ newAttributeName);

            listAttributeNameToJoin.add(s1);

            logger.log(Level.INFO, "List to reduce: "+ listAttributeNameToJoin);

            List<String> synsetList = originalDataset.filterColumnNames("^bn:");
            originalDataset.joinAttributes(listAttributeNameToJoin, newAttributeName, Dataset.COMBINE_SUM, synsetList.contains(newAttributeName));
            
            logger.log(Level.INFO, "\n");
        }

        logger.log(Level.INFO, "Number of features after reducing: "+ originalDataset.filterColumnNames("^bn:").size());

        return originalDataset;
    }


    /** 
     * 
     * ESP: Lee desde disco un archivo ".map" de tipo <String, List<String>>
     * ENG: Reads a ".map" file from disk with type <String, List<String>> 
     *
     * @return A map containing every synset (key) and a list of its hypernyms (values)
     */
	public static Map<String , List<String>> read() {
        try{
            //Poner aquí el nombre del fichero a cargar. Extensión ".map"
             File toRead=new File("outputsyns_youtube_old.map");
             if (!toRead.exists()){
                return new HashMap<>();
             }
             FileInputStream fis=new FileInputStream(toRead);
             ObjectInputStream ois=new ObjectInputStream(fis);
 
             HashMap<String,List<String>> mapInFile=(HashMap<String,List<String>>)ois.readObject();
 
             ois.close();
             fis.close();
             //print All data in MAP
             return mapInFile;
        }catch(Exception e){}
            return null;
    }

    /**
     * 
     * ESP: Para guardar el mapa de los synsets y sus hiperónimos en el disco
     * ENG: Saves a map containing synsets and their hypernyms in a file
     * 
     * @param mapOfHypernyms Map containing every synset (key) and a list of its hypernyms (values)
    */
	public static void saveMap(Map<String, List<String>> mapOfHypernyms){
		try{
			File fileOne=new File("outputsyns_youtube_old.map");
			FileOutputStream fos=new FileOutputStream(fileOne);
			ObjectOutputStream oos=new ObjectOutputStream(fos);

			oos.writeObject(mapOfHypernyms);
			oos.flush();
			oos.close();
			fos.close();
		}catch(Exception e){}
    }
}
