/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * Sufijo anterior
     */
    String oldSuffix;

    /**
     * Sufijo de reemplazo
     */
    String newSuffix;

    /**
     * Minimum root size in syllables (-1 is not applicable)
     */
    int minRootSize;

    /**
     * Change condition. Condición de cambio. If it's worth 1 contains a vowel,
     * if it's worth 2 then removeAnE. If it's worth 3 thn condition is not
     * neccesary
     */
    int condition;

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

    public String toString() {
        return this.oldSuffix + " " + newSuffix + " " + minRootSize + " " + condition;
    }
}
