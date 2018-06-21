/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.FeatureSequence;
import org.ski4spam.ia.types.FeatureVector;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import java.io.Serializable;


// This class does not insist on getting its own Alphabet because it can rely on getting
// it from the FeatureSequence input.


/**
 * Convert the data field from a feature sequence to a feature vector.
 */

public class FeatureSequence2FeatureVector extends Pipe implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -469265506491936763L;

    /**
     * Indica si ser� binario o no el vector de caracter�sticas
     */
    boolean binary;

    public FeatureSequence2FeatureVector(boolean binary) {
        this.binary = binary;
    }

    public FeatureSequence2FeatureVector() {
        this(false);
    }

    /**
     * Indica si se usan características binarias o de recuento
     */

    public void setIsbinary(boolean binary) {
        this.binary = binary;
    }

    /**
     * Devuelve el Pipe
     *
     * @return pipe actual
     */

    public FeatureSequence2FeatureVector getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {

        FeatureSequence fs = (FeatureSequence) carrier.getData();
        carrier.setData(new FeatureVector(fs, binary));

        return carrier;
    }
}
