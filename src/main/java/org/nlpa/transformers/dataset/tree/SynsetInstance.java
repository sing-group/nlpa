/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.io.Serializable;

/**
 *
 * @author María Novo
 */
public interface SynsetInstance {
    public Object getName();
    
    public Serializable getTarget();
}
