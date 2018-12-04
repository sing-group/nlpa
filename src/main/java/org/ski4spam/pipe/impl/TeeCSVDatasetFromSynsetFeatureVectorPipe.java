/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.javaml.core.DenseInstance;

import org.apache.logging.log4j.LogManager;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.CSVDataset;
import org.bdp4j.types.Instance;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;
import org.bdp4j.util.Pair;
import org.bdp4j.util.SubClassParameterTypeIdentificator;
import org.ski4spam.types.SynsetDictionary;
import org.ski4spam.types.SynsetFeatureVector;

/**
 *
 * @author María Novo
 */
public class TeeCSVDatasetFromSynsetFeatureVectorPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(TeeCSVDatasetFromSynsetFeatureVectorPipe.class);
    /**
     * The list of transformers. A transformer is a class used to transform a
     * non double value in double value.
     *
     */
    Map<String, Transformer> transformersList;
    List<Instance> instanceList = null;
    private Set<String> detectedTypes = null;
    List<Pair<String, String>> columnTypes = null;
    List<String> attributes = null;
    List<String> instanceIds = null;
    Map<String, Integer> indexColumnTypes = null;

    /**
     * Indicates if this the current element is the first one to be processed
     */
    private boolean isFirst = true;

    /**
     * Default constructor
     */
    public TeeCSVDatasetFromSynsetFeatureVectorPipe() {
        transformersList = new HashMap<>();
    }

    /**
     * Set the transformers list
     *
     * @param transformersList The list of transformers.
     */
    @PipeParameter(name = "transformersList", description = "The list of transformers", defaultValue = "")
    public void setTransformersList(Map<String, Transformer> transformersList) {
        this.transformersList = transformersList;
    }

    /**
     * Get the transformersList
     *
     * @return the transformersList
     */
    public Map<String, Transformer> getTransformersList() {
        return this.transformersList;
    }

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class getInputType() {
        return SynsetFeatureVector.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class getOutputType() {
        return CSVDataset.class;
    }

    private String identifyType(String value) {
        // Check if the field is Double                            
        try {
            Double.parseDouble(value);
            return "Double";
        } catch (Exception ex) {
            if (ex.getClass().getName().equals("java.lang.NumberFormatException")) {
            }
        }
        // Check if the field is Date                            
        try {
            if (DateIdentifier.getDefault().checkDate(value) != null) {
                return "Date";
            }
        } catch (Exception ex) {
            if (ex.getClass().getName().equals("java.text.ParseException")) {
            }
        }
        return "String";
    }

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Processed Instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        Set<String> carrierPropertyList = carrier.getPropertyList();

        String field;
        String type;
        Pair pair;
        Pair newPair;
        try {

            if (isFirst) {
                attributes = new ArrayList<>();
                detectedTypes = new HashSet<>();
                columnTypes = new ArrayList<>();
                instanceList = new ArrayList<>();
                instanceIds = new ArrayList<>();
                indexColumnTypes = new HashMap<>();
                isFirst = false;
            }

            // Create an Instance list to save data
            instanceList.add(carrier);
            // Identified property data type
            for (String propertyName : carrierPropertyList) {
                field = carrier.getProperty(propertyName).toString();
                if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                    type = identifyType(field);
                    if (detectedTypes.contains(propertyName)) {
                        int columnTypeIndex = indexColumnTypes.get(propertyName);
                        pair = columnTypes.get(columnTypeIndex);
                        if (pair.getObj2() != type) {
                            columnTypes.remove(columnTypeIndex);
                            newPair = new Pair(propertyName, "String");
                            columnTypes.add(columnTypeIndex, newPair);
                        }
                    } else {
                        detectedTypes.add(propertyName);
                        pair = new Pair(propertyName, type);
                        columnTypes.add(pair);
                        indexColumnTypes.put(propertyName, columnTypes.indexOf(pair));
                    }
                }
            }

            // Create the dataset, when we reach last instance.
            if (isLast()) {
                // Get transformes which parameter type is not Double
                Set<String> noDoubleTransformers = new HashSet<>();
                if (transformersList.size() > 0) {
                    for (Map.Entry<String, Transformer> entry : transformersList.entrySet()) {
                        String key = entry.getKey();
                        Transformer value = entry.getValue();
                        if (!SubClassParameterTypeIdentificator.findSubClassParameterType(value, Transformer.class, 0).getName().equals("Double")) {
                            noDoubleTransformers.add(key);
                        }
                    }
                }

                // Get attribute list to generate CSVDataset. This list will contain the columns to add to the dataset.
                if (!columnTypes.isEmpty()) {
                    for (Iterator<Pair<String, String>> iterator = columnTypes.iterator(); iterator.hasNext();) {
                        Pair next = iterator.next();
                        if ((next.getObj2().equals("Double") || noDoubleTransformers.contains(next.getObj1().toString())) && !attributes.contains(next.getObj1().toString())) {
                            attributes.add(next.getObj1().toString());
                        }
                    }
                }

                // Add synsetIds to attribute list
                SynsetDictionary synsetsDictionary = SynsetDictionary.getDictionary();

                for (String synsetId : synsetsDictionary) {
                    attributes.add(synsetId);
                }
                attributes.add("target");

                CSVDataset dataset = new CSVDataset(attributes);

                double[] instanceValues = new double[attributes.size()];
                int indInstance = 0;
                SynsetFeatureVector synsetFeatureVector = null;

                Transformer t;
                for (Instance entry : instanceList) {
                    instanceIds.add(entry.getName().toString());
                    synsetFeatureVector = (SynsetFeatureVector) entry.getData();
                    for (String attribute : attributes) {
                        if (attribute.startsWith("bn:")) {
                            // Add synsetIds values
                            for (String synsetId : synsetsDictionary) {
                                Double frequency = synsetFeatureVector.getFrequencyValue(synsetId);
                                if (frequency > 0) {
                                    instanceValues[indInstance] = frequency;
                                } else {
                                    instanceValues[indInstance] = 0d;
                                }
                                indInstance++;
                            }
                            break;
                        } else {
                            field = entry.getProperty(attribute).toString();
                            if ((t = transformersList.get(attribute)) != null) {
                                if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                    instanceValues[indInstance] = t.transform(field);
                                } else {
                                    instanceValues[indInstance] = 0d;
                                }
                            } else {
                                if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                    instanceValues[indInstance] = Double.parseDouble(field);
                                } else {
                                    instanceValues[indInstance] = 0d;
                                }
                            }
                            indInstance++;
                        }
                    }
                    // Asignar el valor de target, solo en caso de que hay un transformador definido para ese campo.
                    if ((t = transformersList.get("target")) != null) {
                        instanceValues[indInstance] = t.transform(entry.getTarget().toString());
                    } else {
                        instanceValues[indInstance] = 0d;
                    }
                    net.sf.javaml.core.Instance instance = new DenseInstance(instanceValues);
                    indInstance = 0;
                    dataset.add(instance);
                }

                //---------------------------------------------------------------------------
                // Se imprime el dataset
                //---------------------------------------------------------------------------
                System.out.println("-------------BEGIN DATASET PIPE-----------------------");
                dataset.stream().forEach(System.out::println);
                System.out.println("-------------END DATASET PIPE-----------------------");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return carrier;
    }
}
