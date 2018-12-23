/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Dataset;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.bdp4j.util.SubClassParameterTypeIdentificator;
import org.ski4spam.types.SynsetDictionary;
import org.ski4spam.types.SynsetFeatureVector;
import weka.core.Attribute;

/**
 * Create a Dataset from Instanced containing a SynsetFeatureVector as data
 * @author María Novo
 */
public class TeeDatasetFromSynsetFeatureVectorPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TeeDatasetFromSynsetFeatureVectorPipe.class);

    /**
     * The list of transformers. A transformer is a class used to transform a
     * non double value in double value.
     */
    Map<String, Transformer<Object>> transformersList;

    /**
     * A list of instances
     */
    List<Instance> instanceList = null;

    /**
     * The types that are currently detected
     */
    private Set<String> detectedTypes = null;

    /**
     * The types por columns
     */
    List<Pair<String, String>> columnTypes = null;

    /**
     * The attributes
     */
    ArrayList<Attribute> attributes = null;

    /**
     * A Map with integer fields mapped to its types
     */
    Map<String, Integer> indexColumnTypes = null;

    /**
     * Indicates if this the current element is the first one to be processed
     */
    private boolean isFirst = true;

    /**
     * Default constructor
     */
    public TeeDatasetFromSynsetFeatureVectorPipe() {
        super(new Class<?>[0],new Class<?>[0]);
        
        transformersList = new HashMap<>();
    }

    /**
     * Set the transformers list
     *
     * @param transformersList The list of transformers.
     */
    @PipeParameter(name = "transformersList", description = "The list of transformers", defaultValue = "")
    public void setTransformersList(Map<String, Transformer<Object>> transformersList) {
        this.transformersList = transformersList;
    }

    /**
     * Get the transformersList
     *
     * @return the transformersList
     */
    public Map<String, Transformer<Object>> getTransformersList() {
        return this.transformersList;
    }

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
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
    public Class<?> getOutputType() {
        return Dataset.class;
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
        Pair<String, String> pair;
        Pair<String, String> newPair;
        try {

            if (isFirst) {
                attributes = new ArrayList<>();
                detectedTypes = new HashSet<>();
                columnTypes = new ArrayList<>();
                instanceList = new ArrayList<>();
                indexColumnTypes = new HashMap<>();
                isFirst = false;
            }

            // Create an Instance list to save data
            instanceList.add(carrier);
            Predicate<String> isDetectedColumnType = name -> columnTypes.stream().anyMatch(header -> header.getObj1().equals(name));

            // Identified property data type
            for (String propertyName : carrierPropertyList) {
                field = carrier.getProperty(propertyName).toString();
                if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                    type = identifyType(field);
                    if (detectedTypes.contains(propertyName)) {
                        int columnTypeIndex = indexColumnTypes.get(propertyName);
                        pair = columnTypes.get(columnTypeIndex);
                        if (!pair.getObj2().equals(type)) {
                            columnTypes.remove(columnTypeIndex);
                            newPair = new Pair<>(propertyName, "String");
                            columnTypes.add(columnTypeIndex, newPair);
                        }
                    } else {
                        if (isDetectedColumnType.test("target")) {
                            // Target field always has to be the last one
                            int lastColumnTypesPosition = columnTypes.size() - 1;
                            Pair<String, String> targetPair = columnTypes.get(lastColumnTypesPosition);
                            columnTypes.remove(lastColumnTypesPosition);
                            pair = new Pair<>(propertyName, type);
                            columnTypes.add(pair);
                            indexColumnTypes.put(propertyName, columnTypes.indexOf(pair));
                            columnTypes.add(targetPair);
                            indexColumnTypes.put("target", columnTypes.indexOf(pair));
                        } else {
                            detectedTypes.add(propertyName);
                            pair = new Pair<>(propertyName, type);
                            columnTypes.add(pair);
                            indexColumnTypes.put(propertyName, columnTypes.indexOf(pair));
                        }
                    }
                }
            }

            // Create the dataset, when we reach last instance.
            if (isLast()) {
                // Get transformes which parameter type is not Double
                Set<String> noDoubleTransformers = new HashSet<>();
                if (transformersList.size() > 0) {
                    for (Map.Entry<String, Transformer<Object>> entry : transformersList.entrySet()) {
                        String key = entry.getKey();
                        Transformer<? extends Object> value = entry.getValue();
                        if (!SubClassParameterTypeIdentificator.findSubClassParameterType(value, Transformer.class, 0).getName().equals("Double")) {
                            noDoubleTransformers.add(key);
                        }
                    }
                }

                // Get attribute list to generate Dataset. This list will contain the columns to add to the dataset.
                Predicate<String> isAttribute = name -> attributes.stream()
                        .anyMatch(attribute -> attribute.name().equals(name));

                attributes.add(new Attribute("id", true));
                if (!columnTypes.isEmpty()) {
                    for (Pair<String, String> next : columnTypes) {
                        final String header = next.getObj1();
                        final String typeHeader = next.getObj2();

                        if ((typeHeader.equals("Double") || noDoubleTransformers.contains(header)) && !isAttribute.test(header)) {
                            attributes.add(new Attribute(header));
                        }
                    }
                }

                // Add synsetIds to attribute list
                SynsetDictionary synsetsDictionary = SynsetDictionary.getDictionary();
                for (String synsetId : synsetsDictionary) {
                    attributes.add(new Attribute(synsetId));
                }
                attributes.add(new Attribute("target"));

                Dataset dataset = new Dataset("dataset", attributes, 0);
                int indInstance = 0;
                SynsetFeatureVector synsetFeatureVector = null;
                weka.core.Instance instance = null;
                Transformer<Object> t;
                for (Instance entry : instanceList) {
                    instance = dataset.createDenseInstance();
                    synsetFeatureVector = (SynsetFeatureVector) entry.getData();
                    String attName = "";
                    for (int index = 0; index < attributes.size(); index++) {
                        attName = attributes.get(index).name();
                        if (attName.equals("id")) {
                            instance.setValue(indInstance, entry.getName().toString());
                            indInstance++;
                        } else {
                            if (attName.startsWith("bn:")) {
                                // Add synsetIds values
                                for (String synsetId : synsetsDictionary) {
                                    Double frequency = synsetFeatureVector.getFrequencyValue(synsetId);
                                    if (frequency > 0) {
                                        instance.setValue(indInstance, frequency);
                                    } else {
                                        instance.setValue(indInstance, 0d);
                                    }
                                    index = indInstance;
                                    indInstance++;
                                }
                            } else {
                                if (attName.equals("target")) {
                                    field = entry.getTarget().toString();
                                } else {
                                    field = entry.getProperty(attName).toString();
                                }
                                if ((t = transformersList.get(attName)) != null) {
                                    if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                        instance.setValue(indInstance, t.transform(field));
                                    } else {
                                        instance.setValue(indInstance, 0d);
                                    }
                                } else {
                                    if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                        instance.setValue(indInstance, Double.parseDouble(field));
                                    } else {
                                        instance.setValue(indInstance, 0d);
                                    }
                                }
                                indInstance++;
                            }
                        }
                    }
                    indInstance = 0;
                }
                dataset.generateCSV();
                carrier.setData(dataset);
                //---------------------------------------------------------------------------
                // Se imprime el dataset
                //---------------------------------------------------------------------------
//                System.out.println("-------------BEGIN DATASET PIPE-----------------------");
//                dataset.printLine();
//                System.out.println("-------------END DATASET PIPE-----------------------");

            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());

        }

        return carrier;
    }
}
