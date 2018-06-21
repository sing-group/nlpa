/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.Token;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.pipe.Pipe;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Remove tokens from the token sequence in the data field whose text is in the
 * stopword list.
 *
 * @author Andrew McCallum <a
 * href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
public abstract class TokenSequenceRemoveStopwords extends Pipe implements Serializable {

    protected HashSet<String> stoplist = new HashSet<String>();

    protected boolean caseSensitive = false;

    public TokenSequenceRemoveStopwords(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        loadData();
    }

    public TokenSequenceRemoveStopwords() {
        this(false);
    }

    @Override
    public Instance pipe(Instance carrier) {

        TokenSequence ts = (TokenSequence) carrier.getData();

        // xxx This doesn't seem so efficient. Perhaps have TokenSequence
        // use a LinkedList, and remove Tokens from it?
        TokenSequence ret = new TokenSequence();

        /*
         * if (carrier.getName().toString().equals("e2833")){
         * System.out.println("TokenSequenceRemoveStopWords(Antes)-Palabras
         * e2833 "+ts.size()); //System.exit(0); }
         */

        for (int i = 0; i < ts.size(); i++) {

            Token t = ts.getToken(i);

            if (!stoplist.contains(caseSensitive ? t.getText().toLowerCase()
                    : t.getText()))

                // xxx Should we instead make and add a copy of the Token?
                ret.add(t);
        }

        /*
         * if (carrier.getName().toString().equals("e2833")){
         * System.out.println("TokenSequenceRemoveStopWords(Despu�s)-Palabras
         * e2833 "+ret.size()); try { FileWriter f=new FileWriter(new
         * File("palabas.txt")); for(Object i:ret) f.write(i.toString()+" ");
         * f.close(); } catch (IOException e) { e.printStackTrace(); } }
         */

        carrier.setData(ret);

        return carrier;
    }

    protected abstract void loadData();
}
