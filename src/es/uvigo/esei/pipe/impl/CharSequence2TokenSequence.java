/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.Token;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.ia.util.CharSequenceLexer;
import es.uvigo.esei.pipe.Pipe;
import es.uvigo.esei.pipe.SerialPipes;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;


/**
 * Pipe that tokenizes a character sequence.  Expects a CharSequence
 * in the Instance data, and converts the sequence into a token
 * sequence using the given regex or CharSequenceLexer.
 * (The regex / lexer should specify what counts as a token.)
 */

public class CharSequence2TokenSequence extends Pipe implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -6937425821568778842L;

    /**
     * Lexer
     */
    CharSequenceLexer lexer;

    public CharSequence2TokenSequence(CharSequenceLexer lexer) {
        this.lexer = lexer;
    }

    public CharSequence2TokenSequence(String regex) {
        this.lexer = new CharSequenceLexer(regex);
    }

    public CharSequence2TokenSequence(Pattern regex) {
        this.lexer = new CharSequenceLexer(regex);
    }

    public CharSequence2TokenSequence() {
        this(new CharSequenceLexer());
    }

    public static void main(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {

                Instance carrier = new Instance(new File(args[i]), null, null,
                        null);
                Pipe p = new SerialPipes(
                        new Pipe[]{
                                new Input2CharSequence(),
                                new CharSequence2TokenSequence(new CharSequenceLexer())
                        });
                carrier = p.pipe(carrier);

                TokenSequence ts = (TokenSequence) carrier.getData();
                System.out.println("===");
                System.out.println(args[i]);
                System.out.println(ts.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * Set the regular expresión
     *
     * @param regex regular expression
     */

    public void setRegex(String regex) {
        this.lexer = new CharSequenceLexer(regex);
    }

    /**
     * Devuelve el Pipe
     *
     * @return pipe actual
     */

    public CharSequence2TokenSequence getPipe() {
        return this;
    }

    public Instance pipe(Instance carrier) {
        //CharSequence string = (CharSequence)carrier.getData();
        String str = ((CharSequence) carrier.getData()).toString();

        //Tema de hacer el lesing sacando URLs y s�mbolos de puntuaci�n
        lexer.setCharSequence(str
                        //Sacar las URL HTTP
                        .replaceAll("([Hh][Tt][Tt][Pp]|[Ff][Tt][Pp]|[Hh][Tt][Tt][Pp][Ss])://[\\p{Graph}&&[^ ]]+", "")
                //Sacar los s�mbolos de puntuaci�n
                //.replaceAll("[.,;:?�!�]"," ")
        );

        TokenSequence ts = new TokenSequence();

        while (lexer.hasNext()) {
            String a = (String) lexer.next();
            if (!a.trim().equals("")) ts.add(new Token(a));
        }

		/*
		if (carrier.getName().toString().equals("e2833")){
			System.out.println("CharSequence2TokenSequence-Palabras e2833 "+ts.size());
			//System.exit(0);
		}
		*/

        carrier.setData(ts);

        return carrier;
    }

}
