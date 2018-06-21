/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Alphabet;
import es.uvigo.esei.ia.types.FeatureSequence;
import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.pipe.Pipe;

import java.io.Serializable;


/**
 * Convert the token sequence in the data field each instance to a
 * feature sequence.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class TokenSequence2FeatureSequence extends Pipe implements Serializable {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -7057342383542475902L;

    /**
     * Input alphabet
     */
    Alphabet alphabet;

    public TokenSequence2FeatureSequence() {
        //super(Alphabet.class, null);
    }

    public TokenSequence2FeatureSequence(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * Return this pipe
     *
     * @return this pipe
     */

    public TokenSequence2FeatureSequence getPipe() {
        return this;
    }

    /**
     * Set the alphabet
     *
     * @param alphabet the input alphabet
     */

    public void setAlphabet(Alphabet alphabet) {
        this.alphabet = alphabet;
    }

    public Instance pipe(Instance carrier) {
        TokenSequence ts = (TokenSequence) carrier.getData();

        //Creamos una Secuencia de caracter�sticas
        FeatureSequence ret = new FeatureSequence(
                alphabet,
                //(Alphabet)getDataAlphabet(),
                ts.size()
        );

        //A�adimos todos los tokens a la secuencia de caracter�sticas
        for (int i = 0; i < ts.size(); i++) {
            ret.add(ts.getToken(i).getText());
        }

        //Actualizamos el data
        carrier.setData(ret);

        return carrier;
    }
}
