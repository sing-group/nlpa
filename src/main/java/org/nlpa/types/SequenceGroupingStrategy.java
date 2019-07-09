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
