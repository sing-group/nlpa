/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author María Novo
 */
public class Dictionary implements Iterable<String> {

    private Set<String> synsetIdsHashSet;

    private static Dictionary dictionary = null;

    private Dictionary() {
        synsetIdsHashSet = new LinkedHashSet<>();
    }

    public static Dictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new Dictionary();
        }
        return dictionary;
    }

    public void add(String synsetId) {
        //TODO method not implemented yet.
        synsetIdsHashSet.add(synsetId);
    }

    public boolean isIncluded(String synsetId) {
        return synsetIdsHashSet.contains(synsetId);

    }
    
    @Override
    public Iterator<String> iterator() {
        return this.synsetIdsHashSet.iterator();
    }
}
