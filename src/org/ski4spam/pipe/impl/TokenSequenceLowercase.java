/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.ia.types.Token;
import org.ski4spam.ia.types.TokenSequence;
import org.ski4spam.pipe.Pipe;

import java.io.Serializable;

/**
 * Convert the text in each token in the token sequence in the data field to lower case.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class TokenSequenceLowercase extends Pipe implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -6902943967188121119L;

    /**
     * Return the pipe
     *
     * @return the pipe
     */

    public TokenSequenceLowercase getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {

        TokenSequence ts = (TokenSequence) carrier.getData();

        for (int i = 0; i < ts.size(); i++) {

            Token t = ts.getToken(i);
            t.setText(t.getText().toLowerCase());
        }

        return carrier;
    }

}
