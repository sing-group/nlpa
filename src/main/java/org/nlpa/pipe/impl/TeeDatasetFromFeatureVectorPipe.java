/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Instance;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.Pair;
import org.nlpa.types.Dictionary;
import org.nlpa.types.FeatureVector;

import java.util.*;
import java.util.function.Predicate;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SharedDataConsumer;
import org.bdp4j.pipe.TeePipe;
import org.bdp4j.types.DatasetStore;
import org.bdp4j.util.DateTimeIdentifier;
import weka.core.Attribute;

/**
 * Create a Dataset from Instanced containing a FeatureVector as data
 *
 * @author María Novo
 */
@AutoService(Pipe.class)
@TeePipe()
public class TeeDatasetFromFeatureVectorPipe extends AbstractPipe implements SharedDataConsumer {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TeeDatasetFromFeatureVectorPipe.class);

    /**
     * The list of transformers. A transformer is a class used to transform a
     * non double value in double value.
     */
    Map<String, Transformer> transformersList;

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
    // ArrayList<Attribute> attributes = null;
    DatasetStore dataset = null;
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
    public TeeDatasetFromFeatureVectorPipe() {
        super(new Class<?>[0], new Class<?>[0]);

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
    public Class<?> getInputType() {
        return FeatureVector.class;
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
        return FeatureVector.class;
    }

    private String identifyType(String value) {
        // Check if the field is Double                            
        try {
            Double.parseDouble(value);
            return "Double";
        } catch (NumberFormatException nfex) {
            // Check if the field is Date                            
            try {
                if (DateTimeIdentifier.getDefault().checkDateTime(value) != null) {
                    return "Date";
                }
            } catch (Exception ex) {
                return "String";
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
                dataset = DatasetStore.getDatasetStore();
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
                    for (Map.Entry<String, Transformer> entry : transformersList.entrySet()) {
                        String key = entry.getKey();
                        Transformer value = entry.getValue();
                        noDoubleTransformers.add(key);
                    }
                }

                // Get attribute list to generate Dataset. This list will contain the columns to add to the dataset.
                dataset.addColumn("id", String.class, "");
                if (!columnTypes.isEmpty()) {
                    for (Pair<String, String> next : columnTypes) {
                        final String header = next.getObj1();
                        final String typeHeader = next.getObj2();
                        if ((typeHeader.equals("Double") || noDoubleTransformers.contains(header))) {
                            dataset.addColumn(header, Double.class, 0);
                        }
                    }
                }

                // Add text to attribute list
                Dictionary dictionary = Dictionary.getDictionary();

                for (String text : dictionary) {
                    if (dictionary.getEncode()) {
                        dataset.addColumn(dictionary.decodeBase64(text), Double.class, 0);
                    } else {
                        dataset.addColumn(text, Double.class, 0);
                    }
                }
                List<String> target_values = new ArrayList<>();
                Transformer transformer = transformersList.get("target");
                if (transformer != null) {
                    for (Object value : transformer.getListValues()) {
                        target_values.add(value.toString());
                    }
                }

                dataset.addColumn("target", Enum.class, target_values);
                int indInstance = 0;
                FeatureVector featureVector;
                Transformer t;

                List<String> attributes = dataset.getDataset().getAttributes();
                Object[] values = new Object[attributes.size()];

                for (Instance entry : instanceList) {

                    featureVector = (FeatureVector) entry.getData();
                    String attName = "";
                    for (int index = 0; index < attributes.size(); index++) {
                        attName = attributes.get(index);
                        if (attName.equals("id")) {
                            values[indInstance] = entry.getName().toString();
                            indInstance++;
                        } else {
                            if (dictionary.isIncluded(attName)) {
                                Double frequency = featureVector.getFrequencyValue(attName);
                                if (frequency > 0) {
                                    values[indInstance] = frequency;
                                } else {
                                    values[indInstance] = 0d;
                                }
                                index = indInstance;
                                indInstance++;
                            } else {
                                if (attName.equals("target")) {
                                    field = entry.getTarget().toString();
                                } else {
                                    field = entry.getProperty(attName).toString();
                                }
                                if ((t = transformersList.get(attName)) != null) {
                                    if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                        values[indInstance] = t.transform(field);
                                    } else {
                                        values[indInstance] = 0d;
                                    }
                                } else {
                                    if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                        try {
                                            values[indInstance] = Double.parseDouble(field);
                                        } catch (NumberFormatException nfex) {
                                            values[indInstance] = 0;
                                            logger.warn("The value for field " + field + " is 0, because parse double is not possible. To change this, use a transformer." + nfex.getMessage());
                                        }
                                    } else {
                                        values[indInstance] = 0d;
                                    }
                                }
                                if (attName.equals("target")) {
                                    Double doubleValue = Double.parseDouble(values[indInstance].toString());
                                    String target_value = Integer.toString(doubleValue.intValue());
                                    if (target_values.contains(target_value)) {
                                        values[indInstance] = target_value+"";
                                    }
                                }
                                indInstance++;
                            }
                        }
                    }
                    indInstance = 0;
                    dataset.addRow(values);
                }
            }
        } catch (Exception ex) {
            logger.error("[PIPE] " + ex.getMessage());
        }
        return carrier;
    }

    @Override
    /**
     * Retrieve data from directory
     *
     * @param dir Directory name to retrieve data
     */
    public void readFromDisk(String dir) {
        Dictionary.getDictionary().readFromDisk(dir + System.getProperty("file.separator") + "Dictionary.ser");
    }
}
