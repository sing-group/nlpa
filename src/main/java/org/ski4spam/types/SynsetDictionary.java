/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.types;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A dictionary of Synsets
 * @author María Novo
 */
public class SynsetDictionary implements Iterable<String> {

	/**
	  * The information storage for the dictionary. Only a Hashset of synsetsId is 
	  * required 
	  */
    private Set<String> synsetIdsHashSet;

	/**
	  * A instance of the Dictionary to implement a singleton pattern
	  */
    private static SynsetDictionary dictionary = null;

	/**
	  * The default constructor 
	  */	
    private SynsetDictionary() {
        synsetIdsHashSet = new LinkedHashSet<>();
    }

    /**
		* Retrieve the System Dictionary
		* @return The default dictionary for the system
		*/
    public static SynsetDictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new SynsetDictionary();
        }
        return dictionary;
    }

    /**
		* Add a synset to dictionarly
		* @param synsetId the new id of synset to add to the dictionary
		*/
    public void add(String synsetId) {
        //TODO method not implemented yet.
        synsetIdsHashSet.add(synsetId);
    }

    /**
		* Determines if a synsetId is included in the dictionary
		* @param synsetId the identifier of the synset to check
		* @return a boolean indicating whether the synsetId is included 
		*         in the dictionary or not
		*/
    public boolean isIncluded(String synsetId) {
        return synsetIdsHashSet.contains(synsetId);

    }
    
	 /**
		* Achieves an iterator to iterate through the stored synsetsIDs
		* @return an iterator
		*/
    @Override
    public Iterator<String> iterator() {
        return this.synsetIdsHashSet.iterator();
    }
}
