/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 *
 * @author María Novo
 */
public class Dictionary {
    private Set<Object> hs;
    private static Dictionary dict = null;
    private Dictionary(){};
    
    public Dictionary getDictionary() {
        if (dict == null) {
            dict = new Dictionary();
        }
        return dict;
    }

    public void publish(Object obj) {
        //TODO method not implemented yet.
        hs =  new HashSet<>();
        hs.add(obj);
    }

    public Object retrieve() {
        //TODO method not implemented yet.
        return hs;
    }

}
