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

public interface Matrix extends ConstantMatrix {
    void setValue(int[] indices, double value);

    void setSingleValue(int i, double value);

    void incrementSingleValue(int i, double delta);

    void setAll(double v);

    void set(ConstantMatrix m);

    void setWithAddend(ConstantMatrix m, double addend);

    void setWithFactor(ConstantMatrix m, double factor);

    void plusEquals(ConstantMatrix m);

    void plusEquals(ConstantMatrix m, double factor);

    void equalsPlus(double factor, ConstantMatrix m);

    void timesEquals(double factor);

    void elementwiseTimesEquals(ConstantMatrix m);

    void elementwiseTimesEquals(ConstantMatrix m, double factor);

    void divideEquals(double factor);

    void elementwiseDivideEquals(ConstantMatrix m);

    void elementwiseDivideEquals(ConstantMatrix m, double factor);

    double oneNormalize();

    double twoNormalize();

    double absNormalize();

    double infinityNormalize();
}
