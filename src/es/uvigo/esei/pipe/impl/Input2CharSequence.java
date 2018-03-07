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
import es.uvigo.esei.pipe.Pipe;
import es.uvigo.esei.types.TransformableToText;

import java.io.*;
import java.net.URI;

/**
 * Pipe that can read from various kinds of text sources (either URI, File, or
 * Reader) into a CharSequence
 *
 * @version $Id: Input2CharSequence.java,v 1.7 2007-03-15 10:00:50 moncho Exp $
 */

public class Input2CharSequence extends Pipe implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1597256291310164197L;

    public Input2CharSequence() {
    }

    /**
     * Devuelve el Pipe
     *
     * @return pipe actual
     */

    public Input2CharSequence getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {
        try {
            if (carrier.getData() instanceof URI)
                carrier.setData(pipe((URI) carrier.getData()));
            else if (carrier.getData() instanceof File)
                carrier.setData(pipe((File) carrier.getData()));
            else if (carrier.getData() instanceof Reader)
                carrier.setData(pipe((Reader) carrier.getData()));
            else if (carrier.getData() instanceof CharSequence)
                ; // No conversion necessary
            else if (carrier.getData() instanceof TransformableToText) // Adaptaci�n
                // Moncho
                carrier.setData(((TransformableToText) carrier.getData())
                        .extractText()); // No conversion necessary
            else
                throw new IllegalArgumentException("Does not handle class "
                        + carrier.getData().getClass());
        } catch (java.io.IOException e) {
            throw new IllegalArgumentException("IOException " + e);
        }

        return carrier;
    }

    public CharSequence pipe(URI uri) throws
            java.io.IOException {
        if (!uri.getScheme().equals("file"))
            throw new UnsupportedOperationException(
                    "Only file: scheme implemented.");

        return pipe(new File(uri.getPath()));
    }

    public CharSequence pipe(File file) throws
            java.io.IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        CharSequence cs = pipe(br);
        br.close();

        return cs;
    }

    public CharSequence pipe(Reader reader) throws java.io.IOException {

        final int BUFSIZE = 2048;
        char[] buf = new char[BUFSIZE];
        int count;
        StringBuffer sb = new StringBuffer(BUFSIZE);

        do {
            count = reader.read(buf, 0, BUFSIZE);
            if (count == -1)
                break;

            // System.out.println ("count="+count);
            sb.append(buf, 0, count);
        } while (count == BUFSIZE);

        return sb;
    }

    public CharSequence pipe(CharSequence cs) {
        return cs;
    }
}
