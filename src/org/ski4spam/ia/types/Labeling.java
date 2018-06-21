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

/**
 * A distribution over possible labels for an instance.
 */
public interface Labeling {

    LabelAlphabet getLabelAlphabet();

    Label getBestLabel();

    double getBestValue();

    int getBestIndex();

    double value(Label label);

    double value(int labelIndex);

    // Zero-based
    int getRank(Label label);

    int getRank(int labelIndex);

    Label getLabelAtRank(int rank);

    double getValueAtRank(int rank);

    void addTo(double[] values);

    void addTo(double[] values, double scale);

    // The number of non-zero-weight Labels in this Labeling, not total
    // number in the Alphabet
    int numLocations();

    // xxx Use "get..."?
    int indexAtLocation(int pos);

    Label labelAtLocation(int pos);

    double valueAtLocation(int pos);

    LabelVector toLabelVector();
}
