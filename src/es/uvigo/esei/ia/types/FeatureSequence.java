/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */
package es.uvigo.esei.ia.types;

/**
 * An implementation of {@link Sequence} that ensures that every
 * Object in the sequence has the same class.  Feature sequences are
 * mutable, and will expand as new objects are added.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
public class FeatureSequence implements Sequence {

    Alphabet dictionary;

    int[] features;

    int length;

    /**
     * Creates a FeatureSequence given all of the objects in the
     * sequence.
     *
     * @param dictionary A dictionary that maps objects in the sequence
     *                   to numeric indices.
     * @param features   An array where features[i] gives the index
     *                   in dict of the ith element of the sequence.
     */
    public FeatureSequence(Alphabet dictionary, int[] features) {
        this(dictionary, features.length);

        for (int i = 0; i < features.length; i++)
            add(features[i]);
    }

    /**
     * Constructor a partir de un diccionario y una capacidad
     *
     * @param dictionary diccionario que se va a emplear
     * @param capacity   capacidad que se va a emplear
     */
    public FeatureSequence(Alphabet dictionary, int capacity) {
        this.dictionary = dictionary;
        features = new int[capacity > 2 ? capacity : 2];
        length = 0;
    }

    /**
     * Constructor con una capacidad determinada de caracter�sticas
     * @param capacity capacidad de almac�n de caracter�sticas
     */
    //public FeatureSequence(int capacity){
    //}

    /**
     * constructor trivial
     */
    //public FeatureSequence(){
    //	dictionary=new Alphabet();
    //}
    public FeatureSequence(Alphabet dict) {
        this(dict, 2);
    }

    /**
     * Recupera el alfabeto de la secuencia
     *
     * @return Alfabeto de la secuencia
     */
    public Alphabet getAlphabet() {
        return dictionary;
    }

    /**
     * Recupera el n�mero de caracter�sticas de la secuencia
     *
     * @return n�mero de caracter�sticas de la secuencia
     */
    public int getLength() {
        return length;
    }

    /**
     * Recupera el n�mero de caracter�sticas que tiene esta secuencias
     *
     * @return n�mero de caracter�sticas que tiene esta secuencia
     */
    public int size() {
        return length;
    }

    /**
     * Recupera el �ndice de caracter�stica que hay en una posici�n dada
     *
     * @param pos posici�n de la que se quiere recuperar el �ndice de caracter�stica
     * @return �ndice de caracter�stica del alfabeto
     */
    public int getIndexAtPosition(int pos) {
        return features[pos];
    }

    /**
     * Recupera la selecci�n de caracter�sticas que hay en Pos
     *
     * @param pos Posici�n dentro del array de caracter�sticas
     * @return Caracter�stica que se est� seleccionando
     */
    public Object getObjectAtPosition(int pos) {
        return dictionary.lookupObject(features[pos]);
    }

    // xxx This method name seems a bit ambiguous?

    /**
     * Recupera la caracter�stica almacenada en la posici�n Pos
     */
    public Object get(int pos) {
        return dictionary.lookupObject(features[pos]);
    }

    /**
     * Convierte la secuencia de caracter�sticas en una cadena
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (int fsi = 0; fsi < length; fsi++) {

            Object o = dictionary.lookupObject(features[fsi]);
            sb.append(o.toString());
            sb.append(" (");
            sb.append(features[fsi]);
            sb.append(")\n");
        }

        return sb.toString();
    }

    /**
     * Redimensiona los arrays de cuentas si no cogen m�s elementos en el.
     */
    private void growIfNecessary() {
        if (length == features.length) {
            int[] newFeatures = new int[features.length * 2];
            System.arraycopy(features, 0, newFeatures, 0, length);
            features = newFeatures;
        }
    }

    /**
     * Pide tomar cuenta de que ha aparecido una vez m�s la caracter�stica
     * que tiene �ndice featureIndex
     *
     * @param featureIndex Indice de caracter�stica
     */
    public void add(int featureIndex) {
        growIfNecessary();
        //Se a�ade una nueva caracter�stica al final
        features[length++] = featureIndex;
    }

    /**
     * A�ade una nueva caracter�stica a la secuencia
     *
     * @param feature Caracter�stica de la que se quiere anotar una
     *                nueva aparici�n
     */
    public void add(String feature) {
        //Se mira el index en el diccionario y si no est� se a�ade la
        //caracter�stica
        int fi = dictionary.lookupIndex(feature, true);
        if (fi >= 0)
            add(fi);
    }

    public void addFeatureWeightsTo(double[] weights) {
        for (int i = 0; i < length; i++)
            weights[features[i]]++;
    }

    public void addFeatureWeightsTo(double[] weights, double scale) {
        for (int i = 0; i < length; i++)
            weights[features[i]] += scale;
    }

    public int[] toFeatureIndexSequence() {
        int[] feats = new int[length];
        System.arraycopy(features, 0, feats, 0, length);

        return feats;
    }

    public int[] toSortedFeatureIndexSequence() {
        int[] feats = this.toFeatureIndexSequence();
        java.util.Arrays.sort(feats);

        return feats;
    }
}
