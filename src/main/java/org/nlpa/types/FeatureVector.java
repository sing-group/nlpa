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
package org.nlpa.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.features);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeatureVector other = (FeatureVector) obj;
        if (!Objects.equals(this.features, other.features)) {
            return false;
        }
        return true;
    }

}
