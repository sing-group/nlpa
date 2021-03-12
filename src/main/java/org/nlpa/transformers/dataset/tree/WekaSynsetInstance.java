/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.io.Serializable;
import weka.core.Instance;


/**
 *
 * @author María Novo
 */
public class WekaSynsetInstance implements SynsetInstance {
    private Instance instance;
    
    public WekaSynsetInstance(Instance instance) {
        this.instance = instance;
    }

    @Override
    public Object getName() {
        return this.instance.stringValue(0);
    }

    @Override
    public Serializable getTarget() {
        return this.instance.value(instance.numAttributes()-1);
    }
    
}
