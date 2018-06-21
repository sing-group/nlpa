/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
 This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
 http://www.cs.umass.edu/~mccallum/mallet
 This software is provided under the terms of the Common Public License,
 version 1.0, as published by http://www.opensource.org.  For further
 information, see the file `LICENSE' included with this distribution. */
package org.ski4spam.ia.types;

import java.util.ArrayList;


/**
 * A mapping from arbitrary objects (usually String's) to integers
 * (and corresponding Label objects) and back.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

/**
 * @author Administrador
 */
public class LabelAlphabet extends Alphabet {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 3258130241326231858L;
    ArrayList<Label> labels;

    /**
     * Constructor por defecto
     */
    public LabelAlphabet() {
        super();
        this.labels = new ArrayList<Label>();
    }

    public int lookupIndex(Object entry, boolean addIfNotPresent) {
        int index = super.lookupIndex(entry, addIfNotPresent);

        if (index >= labels.size() && addIfNotPresent)
            labels.add(new Label(entry, this, index));

        return index;
    }

    public Label lookupLabel(Object entry, boolean addIfNotPresent) {

        int index = lookupIndex(entry, addIfNotPresent);

        if (index >= 0)

            return labels.get(index);
        else

            return null;
    }

    public Label lookupLabel(Object entry) {
        return this.lookupLabel(entry, true);
    }

    public Label lookupLabel(int labelIndex) {
        return labels.get(labelIndex);
    }
}
