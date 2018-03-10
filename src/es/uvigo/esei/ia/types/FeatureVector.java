/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package es.uvigo.esei.ia.types;

import es.uvigo.esei.ia.util.Pair;
import es.uvigo.esei.ia.util.PropertyList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;


/**
 * A subset of an {@link es.uvigo.esei.ia.types.Alphabet} in which each element of the subset has an associated value.
 * The subset is represented as a {@link es.uvigo.esei.ia.types.SparseVector}
 * <p>
 * A SparseVector represents only the non-zero locations of a vector.  In the case of a FeatureVector,
 * a location represents the index of an entry in the Alphabet that is contained in
 * the FeatureVector.
 * <p>
 * To loop over the elements of a feature vector, one loops over the consecutive integers between 0
 * and the number of locations in the feature vector. From these locations one can cheaply
 * obtain the index of the entry in the underlying Alphabet, the entry itself, and the value
 * in this feature vector associated the entry.
 * <p>
 * A SparseVector (or FeatureVector) can be sparse or dense depending on whether or not
 * an array if indices is specified at construction time.  If the FeatureVector is dense,
 * the mapping from location to index is the identity mapping.
 * <p>
 * The associated value of an element in a SparseVector (or FeatureVector) can be
 * a double or binary (0.0 or 1.0), depending on whether an array of doubles is specified at
 * contruction time.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @see SparseVector
 * @see Alphabet
 */
public class FeatureVector extends SparseVector {
    Alphabet dictionary;

    protected FeatureVector(Alphabet dict, int[] indices, double[] values,
                            int capacity, int size, boolean copy,
                            boolean checkIndicesSorted,
                            boolean removeDuplicates) {
        super(indices, values, capacity, size, copy, checkIndicesSorted,
                removeDuplicates);
        this.dictionary = dict;
    }

    /**
     * Create a dense vector
     */
    public FeatureVector(Alphabet dict, double[] values) {
        super(values);
        this.dictionary = dict;
    }

    /**
     * Create non-binary vector, possibly dense if "featureIndices" or possibly sparse, if not
     */
    public FeatureVector(Alphabet dict, int[] featureIndices, double[] values) {
        super(featureIndices, values);
        this.dictionary = dict;
    }

    /**
     * Create binary vector
     */
    public FeatureVector(Alphabet dict, int[] featureIndices) {
        super(featureIndices);
        this.dictionary = dict;
    }

    public FeatureVector(Alphabet dict, Object[] keys, double[] values) {
        this(dict, getObjectIndices(keys, dict, true), values);
    }

    public FeatureVector(FeatureSequence fs, boolean binary) {
        super(fs.toSortedFeatureIndexSequence(), false, false, true, binary);
        this.dictionary = fs.getAlphabet();
    }

    public FeatureVector(FeatureSequence fs) {
        this(fs, false);
    }

    public FeatureVector(Alphabet dict, PropertyList pl, boolean binary,
                         boolean growAlphabet) {
        super(dict, pl, binary, growAlphabet);
        this.dictionary = dict;
    }

    public FeatureVector(Alphabet dict, PropertyList pl, boolean binary) {
        this(dict, pl, binary, true);
    }

    /**
     * New feature vector containing all the features of "fv", plus new
     * features created by making conjunctions between the features in
     * "conjunctions" and all the other features.
     */
    public FeatureVector(FeatureVector fv, Alphabet newVocab,
                         int[] conjunctions) {
        this(newVocab, indicesWithConjunctions(fv, newVocab, conjunctions));
    }

    public FeatureVector(FeatureVector fv, Alphabet newVocab,
                         FeatureSelection fsNarrow, FeatureSelection fsWide) {
        this(newVocab, indicesWithConjunctions(fv, newVocab, fsNarrow, fsWide));
    }

    public static int[] getObjectIndices(Object[] entries, Alphabet dict,
                                         boolean addIfNotPresent) {
        int[] feats = new int[entries.length];

        for (int i = 0; i < entries.length; i++) {
            feats[i] = dict.lookupIndex(entries[i], addIfNotPresent);

            if (feats[i] == -1)
                throw new IllegalArgumentException("Object is not in dictionary.");
        }

        return feats;
    }

    @SuppressWarnings("unused")
    private static int[] sortedFeatureIndexSequence(FeatureSequence fs) {
        int[] feats = fs.toFeatureIndexSequence();
        java.util.Arrays.sort(feats);

        return feats;
    }

    private static int[] indicesWithConjunctions(FeatureVector fv,
                                                 Alphabet newVocab,
                                                 int[] conjunctions) {
        //assert (fv.values == null);               // Only works on binary feature vectors
        //assert (! (fv instanceof AugmentableFeatureVector));
        Alphabet v = fv.getAlphabet();

        // newVocab should be an augmented copy of v
        //assert (v.size() <= newVocab.size())
        //                              : "fv.vocab.size="+v.size()+" newVocab.size="+newVocab.size();
        int[] newIndices = new int[fv.indices.length * conjunctions.length];
        java.util.Arrays.sort(conjunctions);
        System.arraycopy(fv.indices, 0, newIndices, 0, fv.indices.length);

        int size = fv.indices.length;
        int ci = 0;

        for (int i = 0; i < fv.indices.length; i++) {

            if (ci < conjunctions.length &&
                    conjunctions[ci] < fv.indices[i])
                ci++;

            if (conjunctions[ci] == fv.indices[i]) {

                for (int j = 0; j < fv.indices.length; j++) {

                    if (conjunctions[ci] != fv.indices[j]) {

                        int index = newVocab.lookupIndex(FeatureConjunction.getName(
                                v,
                                conjunctions[ci],
                                fv.indices[j]));

                        if (index == newVocab.size() - 1 && index % 3 == 0)

                            //logger.info ("New feature "+ newVocab.lookupObject(index));
                            newIndices[size++] = index;
                    }
                }
            }
        }

        int[] ret = new int[size];
        System.arraycopy(newIndices, 0, ret, 0, size);

        return ret;
    }

    private static int[] indicesWithConjunctions(FeatureVector fv,
                                                 Alphabet newVocab,
                                                 FeatureSelection fsNarrow,
                                                 FeatureSelection fsWide) {
        //assert (fv.values == null);               // Only works on binary feature vectors
        ////assert (! (fv instanceof AugmentableFeatureVector));
        Alphabet v = fv.getAlphabet();

        // newVocab should be an augmented copy of v
        //assert (v.size() <= newVocab.size())
        //                              : "fv.vocab.size="+v.size()+" newVocab.size="+newVocab.size();
        int length;

        if (fv instanceof AugmentableFeatureVector) {
            length = ((AugmentableFeatureVector) fv).size;
            fv.sortIndices();
        } else {
            length = fv.indices.length;
        }

        int[] newIndices = new int[length * length];
        System.arraycopy(fv.indices, 0, newIndices, 0, length);

        int size = length;
        //int ci = 0;

        for (int i = 0; i < length; i++) {

            if (fsNarrow != null && !fsNarrow.contains(fv.indices[i]))
                continue;

            for (int j = 0; j < length; j++) {
                if ((fsWide == null || fsWide.contains(fv.indices[j])) &&
                        fv.indices[i] != fv.indices[j]) {

                    int index = newVocab.lookupIndex(FeatureConjunction.getName(
                            v, fv.indices[i],
                            fv.indices[j]));

                    //if (index == newVocab.size()-1 && index % 50 == 0)
                    //System.out.println ("FeatureVector: Conjunction feature "+ newVocab.lookupObject(index));
                    newIndices[size++] = index;
                }
            }
        }

        // Sort and remove duplicates
        Arrays.sort(newIndices, 0, size);

        for (int i = 1; i < size; i++) {
            if (newIndices[i - 1] == newIndices[i]) {
                for (int j = i + 1; j < size; j++)
                    newIndices[j - 1] = newIndices[j];

                size--;
            }
        }

        int[] ret = new int[size];
        System.arraycopy(newIndices, 0, ret, 0, size);

        return ret;
    }

    // xxx We need to implement this in FeatureVector subclasses
    public ConstantMatrix cloneMatrix() {
        return new FeatureVector(dictionary, indices, values);
    }

    public ConstantMatrix cloneMatrixZeroed() {
        //assert (values != null);
        if (indices == null)

            return new FeatureVector(dictionary, new double[values.length]);
        else {

            int[] newIndices = new int[indices.length];
            System.arraycopy(indices, 0, newIndices, 0, indices.length);

            return new FeatureVector(dictionary, newIndices,
                    new double[values.length], values.length,
                    values.length, false, false, false);
        }
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean onOneLine) {
        //Thread.currentThread().dumpStack();
        StringBuffer sb = new StringBuffer();

        //System.out.println ("FeatureVector toString dictionary="+dictionary);
        if (values == null) {
            //System.out.println ("FeatureVector toString values==null");
            int indicesLength = numLocations();

            for (int i = 0; i < indicesLength; i++) {

                //System.out.println ("FeatureVector toString i="+i);
                if (dictionary == null)
                    sb.append("[" + i + "]");
                else {

                    //System.out.println ("FeatureVector toString: i="+i+" index="+indices[i]);
                    sb.append(dictionary.lookupObject(indices[i]).toString());

                    //sb.append ("("+indices[i]+")");
                }

                //sb.append ("= 1.0 (forced binary)");
                if (!onOneLine)
                    sb.append('\n');
                else
                    sb.append(' ');
            }
        } else {
            //System.out.println ("FeatureVector toString values!=null");
            int valuesLength = numLocations();

            for (int i = 0; i < valuesLength; i++) {
                int idx = indices == null ? i : indices[i];

                if (dictionary == null)
                    sb.append("[" + i + "]");
                else {
                    sb.append(dictionary.lookupObject(idx).toString());
                    sb.append("(" + idx + ")");
                }

                sb.append("=");
                sb.append(values[i]);

                if (!onOneLine)
                    sb.append("\n");
                else
                    sb.append(' ');
            }
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public Pair<String, Double>[] getFrecuencyOrderVector(double minFrecuency) {
        Vector<Pair<String, Double>> pairs =
                new Vector<Pair<String, Double>>(numLocations());

        if (values != null) {
            int valuesLength = numLocations();
            for (int i = 0; i < valuesLength; i++) {
                if (values[i] >= minFrecuency) {
                    int idx = indices == null ? i : indices[i];
                    Pair<String, Double> currentPair = new Pair<String, Double>(null, null);
                    if (dictionary == null)
                        currentPair.setObj1(Integer.toString(i));
                    else {
                        currentPair.setObj1(dictionary.lookupObject(idx).toString());
                    }

                    currentPair.setObj2(new Double(values[i]));
                    pairs.addElement(currentPair);
                } else {
                    //System.out.println("Perdida");
                }
            }


            Pair<String, Double>[] returnValue = (Pair<String, Double>[]) new Pair[pairs.size()];
            //Ordenar los pares
            Arrays.sort(pairs.toArray(returnValue), new Comparator<Pair<String, Double>>() {
                public int compare(Pair<String, Double> arg1, Pair<String, Double> arg0) {
                    return arg0.getObj2().compareTo(arg1.getObj2());
                }
            });
            return returnValue;
			
			/*
			boolean cambios=true;
			//Mientras no des todas las vueltas y haya cambios
			for(int i=0; i<pairs.size() && cambios; i++){
				cambios=false;
				for (int j=0;j<pairs.size()-1;j++){
					//Si el elemento j es mayor que el elemento j+1
					if (pairs.elementAt(j).getObj2().compareTo(pairs.elementAt(j+1).getObj2())<0){
						//Intercambiar los elementos de la posici�n j y j+1
						Pair <String,Double> aux;
						aux=pairs.elementAt(j);
						pairs.setElementAt(pairs.elementAt(j+1),j);
						pairs.setElementAt(aux,j+1);
						//Marcar que hay cambios
						cambios=true;
					}
				}
			}	
			*/
        } else {
            throw new IllegalStateException("This is a binary feature vector");
        }
    }

    public String toStringFrecuencyOrder() {

        if (values == null)
            throw new IllegalStateException("This is a binary feature vector");

        StringBuilder sb = new StringBuilder();

        Pair<String, Double>[] pairs =
                getFrecuencyOrderVector(0);


        //Una vez ordenados a�adirlos al stringBuffer
        //int i=1;
        for (Pair<String, Double> p : pairs) {
            //sb.append( i + " , ");
            sb.append(p.getObj1() + " , ");
            sb.append(p.getObj2() + "\n");
            //i++;
        }

        return sb.toString();
    }

    /**
     * Cuenta las caracter�sticas que tienen un valor mayor que el dado
     *
     * @param value Valor con el que se comparan todas las caracter�sticas
     * @return n�mero de caracter�sticas con un valor mayor que el dado
     */
    public int countIfGreaterOrEqual(double value) {
        int returnValue = 0;
        for (double i : values) {
            if (i >= value) returnValue++;
        }
        return returnValue;
    }

    public Alphabet getAlphabet() {
        return dictionary;
    }

    public int location(Object entry) {
        if (dictionary == null)
            throw new IllegalStateException("This FeatureVector has no dictionary.");

        int i = dictionary.lookupIndex(entry, false);

        if (i < 0)
            return -1;
        else
            return location(i);
    }

    public boolean contains(Object entry) {
        int loc = location(entry);

        return (loc >= 0 && valueAtLocation(loc) != 0);
    }

    public double value(Object o) {
        int loc = location(o);

        if (loc >= 0)
            return valueAtLocation(loc);
        else
            throw new IllegalArgumentException("Object is not a key in the dictionary.");
    }

}
