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
 *
 * Substitution rule according to Porter's algorithm
 *
 * @author María Novo
 * @author José Ramón Mendez
 */
public class Rule {

    /**
     * Previous suffix
     */
    String oldSuffix;

    /**
     * Replacement suffix
     */
    String newSuffix;

    /**
     * Minimum root size in syllables (-1 is not applicable)
     */
    int minRootSize;

    /**
     * Change condition. If it's worth 1 contains a vowel, if it's worth 2 then
     * removeAnE. If it's worth 3 thn condition is not neccesary
     */
    int condition;

    /**
     * Default constructor. Creates a Rule instance.
     *
     * @param fa Previous suffix
     * @param fn Replacement suffix
     * @param mrs Min root size
     * @param c Condition
     */
    public Rule(String fa, String fn, int mrs, int c) {
        oldSuffix = fa;
        newSuffix = fn;
        minRootSize = mrs;
        condition = c;
    }

    /**
     * @return Returns the condition.
     */
    public int getCondition() {
        return condition;
    }

    /**
     * @param condition The condition to set.
     */
    public void setCondition(int condition) {
        this.condition = condition;
    }

    /**
     * @return Returns the oldSuffix.
     */
    public String getOldSuffix() {
        return oldSuffix;
    }

    /**
     * @param oldSuffix The oldSuffix to set.
     */
    public void setOldSuffix(String oldSuffix) {
        this.oldSuffix = oldSuffix;
    }

    /**
     * @return Returns the newSuffix.
     */
    public String getNewSuffix() {
        return newSuffix;
    }

    /**
     * @param newSuffix The newSuffix to set.
     */
    public void setNewSuffix(String newSuffix) {
        this.newSuffix = newSuffix;
    }

    /**
     * @return Returns the minRootSize.
     */
    public int getMinRootSize() {
        return minRootSize;
    }

    /**
     * @param minRootSize The minRootSize to set.
     */
    public void setMinRootSize(int minRootSize) {
        this.minRootSize = minRootSize;
    }

    @Override
    public String toString() {
        return this.oldSuffix + " " + newSuffix + " " + minRootSize + " " + condition;
    }
}
