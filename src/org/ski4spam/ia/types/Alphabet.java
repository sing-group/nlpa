/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package org.ski4spam.ia.types;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * A mapping between integers and objects where the mapping in each
 * direction is efficient.  Integers are assigned consecutively, starting
 * at zero, as objects are added to the Alphabet.  Objects can not be
 * deleted from the Alphabet and thus the integers are never reused.
 * <p>
 * The most common use of an alphabet is as a dictionary of feature names
 * associated with a {@link org.ski4spam.ia.types.FeatureVector} in an
 * {@link org.ski4spam.ia.types.Instance}. In a simple document
 * classification usage,
 * each unique word in a document would be a unique entry in the Alphabet
 * with a unique integer associated with it.   FeatureVectors rely on
 * the integer part of the mapping to efficiently represent the subset of
 * the Alphabet present in the FeatureVector.
 *
 * @see FeatureVector
 * @see Instance
 * @see es.uvigo.esei.pipe.Pipe
 * @since jdk1.5
 */
public class Alphabet implements Serializable, Iterable /*Jdk 1.5*/ {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 3977579182404350521L;

    gnu.trove.TObjectIntHashMap map;

    ArrayList<Object> entries;

    Class entryClass = null;

    public Alphabet(int capacity, Class entryClass) {
        this.map = new gnu.trove.TObjectIntHashMap(capacity);
        this.entries = new ArrayList<Object>(capacity);
        this.entryClass = entryClass;
    }

    public Alphabet(Class entryClass) {
        this(8, entryClass);
    }

    public Alphabet(int capacity) {
        this(capacity, null);
    }

    public Alphabet() {
        this(8, null);
    }

    public Object clone() {
        Alphabet ret = new Alphabet();
        ret.map = (gnu.trove.TObjectIntHashMap) map.clone();

        //Clonaci�n in-situ
        ret.entries = new ArrayList<Object>();
        for (Object i : entries) {
            ret.entries.add(i);
        }

        ret.entryClass = entryClass;

        return ret;

    }

    /**
     * Return -1 if entry isn't present.
     */
    public int lookupIndex(Object entry, boolean addIfNotPresent) {
        if (entry == null)
            throw new IllegalArgumentException("Can't lookup \"null\" in an Alphabet.");

        if (entryClass == null)
            entryClass = entry.getClass();
        else
            // Insist that all entries in the Alphabet are of the same
            // class.  This may not be strictly necessary, but will catch a
            // bunch of easily-made errors.
            if (entry.getClass() != entryClass)
                throw new IllegalArgumentException("Non-matching entry class, " +
                        entry.getClass() + ", was " +
                        entryClass);

        int retIndex = -1;

        if (map.containsKey(entry)) {
            retIndex = map.get(entry);
        } else if (/*!growthStopped &&*/ addIfNotPresent) {
            retIndex = entries.size();
            map.put(entry, retIndex);
            entries.add(entry);
        }

        return retIndex;
    }

    public int lookupIndex(Object entry) {
        return lookupIndex(entry, true);
    }

    public Object lookupObject(int index) {
        return entries.get(index);
    }

    public Object[] toArray() {
        return entries.toArray();
    }

    // xxx This should disable the iterator's remove method...
    public Iterator<Object> iterator() {
        return entries.iterator();
    }

    public Object[] lookupObjects(int[] indices) {
        Object[] ret = new Object[indices.length];

        for (int i = 0; i < indices.length; i++)
            ret[i] = entries.get(indices[i]);

        return ret;
    }

    public int[] lookupIndices(Object[] objects, boolean addIfNotPresent) {
        int[] ret = new int[objects.length];

        for (int i = 0; i < objects.length; i++)
            ret[i] = lookupIndex(objects[i], addIfNotPresent);

        return ret;
    }

    public boolean contains(Object entry) {
        return map.contains(entry);
    }

    public int size() {
        return entries.size();
    }

    public Class entryClass() {
        return entryClass;
    }

    /**
     * Return String representation of all Alphabet entries, each
     * separated by a newline.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < entries.size(); i++) {
            sb.append(entries.get(i).toString());
            sb.append('\n');
        }

        return sb.toString();
    }

    public void dump() {
        dump(System.out);
    }

    public void dump(PrintStream out) {
        for (int i = 0; i < entries.size(); i++) {
            out.println(i + " => " + entries.get(i));
        }
    }
}
