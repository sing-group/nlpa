/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.types;

/**
 * Represents a strategy of grouping different entries of the dictionary with
 * the same value
 *
 * @author María Novo
 */
public enum SequenceGroupingStrategy {
    COUNT("Count Grouping Strategy"),
    BOOLEAN("Boolean Grouping Strategy"),
    FREQUENCY("Frecuency Grouping Strategy");

    /**
     * The description of the SequenceGroupingStrategy
     */
    private final String desc;

    /**
     * Creates the enum instance
     *
     * @param desc The full description of the constant
     */
    private SequenceGroupingStrategy(final String desc) {
        this.desc = desc;
    }

    /**
     * Find the description of the constant
     *
     * @return the description of the constant
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * Builds a string representation of the constant
     *
     * @return the string representation of the constant
     */
    @Override
    public String toString() {
        return desc;
    }
}
