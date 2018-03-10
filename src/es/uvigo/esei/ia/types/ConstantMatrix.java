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

public interface ConstantMatrix {
    int getNumDimensions();

    int getDimensions(int[] sizes);

    double value(int[] indices);

    // Access using a single index, efficient for dense matrices, but not sparse
    // Move to DenseMatrix?
    int singleIndex(int[] indices);

    void singleToIndices(int i, int[] indices);

    double singleValue(int i);

    int singleSize();

    // Access by index into sparse array, efficient for sparse and dense matrices
    int numLocations();

    int location(int index);

    double valueAtLocation(int location);

    // Returns a "singleIndex"
    int indexAtLocation(int location);

    double dotProduct(ConstantMatrix m);

    double absNorm();

    double oneNorm();

    double twoNorm();

    double infinityNorm();

    void print();

    boolean isNaN();

    ConstantMatrix cloneMatrix();
}
