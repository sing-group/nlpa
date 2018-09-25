/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

/**
 *
 * @author María Novo
 */
public enum SynsetVectorGroupingStrategy {
    COUNT("Count Grouping Strategy"),
    BOOLEAN("Boolean Grouping Strategy"),
    FREQUENCY("Frecuency Grouping Strategy");
    
    private String name;
    
    private SynsetVectorGroupingStrategy(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
