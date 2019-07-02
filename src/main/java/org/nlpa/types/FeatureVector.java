/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a FeatureVector
 *
 * @author Maria Novo
 */
public class FeatureVector implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * A map of tokens together with its values
     */
    private Map<String, Double> features = new HashMap<>();

    /**
     * Builds a FeatureVector from a Map (String,Double) where the string
     * represents the feature and the double contains the value for such
     * feature
     *
     * @param features The features Map used to build the FeatureVector
     */
    public FeatureVector(Map<String, Double> features) {
        this.features = features;
    }

    /**
     * Retrieves the map that connects each feature found with its
     * value
     *
     * @return a Map that connects each feature with the value for it
     */
    public Map<String, Double> getFeatures() {
        return features;
    }

    /**
     * Gets the size (number of properties) of the current FeatureVector
     *
     * @return the size of the current FeatureVector
     */
    public int getSize() {
        return features.size();
    }

    /**
     * Get the value for a feature
     *
     * @param feature Returns the value for a feature
     * @return the value for a feature
     */
    public double getValue(String feature) {
        Double retVal = features.get(feature);
        if (retVal == null) {
            retVal = 0d;
        }

        return retVal;
    }

    /**
     * Checks for the value stored for the token
     *
     * @param feature The target token
     * @return The value asociated to feature, which represents the frequency
     * of appearance of the feature
     */
    public double getFrequencyValue(String feature) {
        if (features.containsKey(feature)) {
            for (Map.Entry<String, Double> entry : features.entrySet()) {
                if (entry.getKey().equals(feature)) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }

}
