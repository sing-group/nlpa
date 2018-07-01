
/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package org.ski4spam.ia.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;


public class PropertyList {

    protected PropertyList next;
    protected String key;

    protected PropertyList() {
        throw new IllegalArgumentException("Zero args constructor not allowed.");
    }

    protected PropertyList(String key, PropertyList rest) {
        this.key = key;
        this.next = rest;
    }

    public static PropertyList add(String key, Object value, PropertyList rest) {
        return new ObjectProperty(key, value, rest);
    }

    public static PropertyList add(String key, String value, PropertyList rest) {
        return new ObjectProperty(key, value, rest);
    }

    public static PropertyList add(String key, double value, PropertyList rest) {
        return new NumericProperty(key, value, rest);
    }

    public static PropertyList remove(String key, PropertyList rest) {
        return new ObjectProperty(key, null, rest);
    }

    // culotta 2/02/04: to increment counts of properties values.
    public static PropertyList sumDuplicateKeyValues(PropertyList pl) {
        if (!(pl instanceof NumericProperty))
            throw new IllegalArgumentException("PropertyList must be Numeric to sum values");

        HashMap<String, Double> key2value = new HashMap<String, Double>();
        Iterator iter = pl.numericIterator();

        while (iter.hasNext()) {
            iter.nextProperty();

            String key = iter.getKey();
            double val = iter.getNumericValue();
            Double storedValue = key2value.get(key);

            if (storedValue == null)
                key2value.put(key, new Double(val));
            else // sum stored value with current value
                key2value.put(key, new Double(storedValue.doubleValue() + val));
        }

        PropertyList ret = null;
        java.util.Iterator<String> hashIter = key2value.keySet().iterator();

        while (hashIter.hasNext()) { // create new property list
            String key = hashIter.next();
            double val = key2value.get(key).doubleValue();
            ret = PropertyList.add(key, val, ret);
        }

        return ret;
    }

    public Object lookupObject(String key) {

        if (this.key.equals(key)) {

            if (this instanceof ObjectProperty)

                return ((ObjectProperty) this).value;
            else if (this instanceof NumericProperty)

                return new Double(((NumericProperty) this).value);
            else
                throw new IllegalStateException("Unrecognitized PropertyList entry.");
        } else if (this.next == null) {

            return null;
        } else {

            return next.lookupObject(key);
        }
    }

    public double lookupNumber(String key) {

        if (this.key.equals(key)) {

            if (this instanceof NumericProperty)

                return ((NumericProperty) this).value;
            else if (this instanceof ObjectProperty) {

                Object obj = ((ObjectProperty) this).value;

                if (obj == null)
                    return 0;

                // xxx Remove these?  Use might ask for numericIterator expecting to get these (and not!)
                if (obj instanceof Double)
                    return ((Double) obj).doubleValue();

                if (obj instanceof Integer)
                    return ((Double) obj).intValue();

                if (obj instanceof Float)
                    return ((Double) obj).floatValue();

                if (obj instanceof Short)
                    return ((Double) obj).shortValue();

                if (obj instanceof Long)
                    return ((Double) obj).longValue();

                // xxx? throw new IllegalStateException ("Property is not numeric.");
                return 0;
            } else
                throw new IllegalStateException("Unrecognitized PropertyList entry.");
        } else if (this.next == null) {

            return 0;
        } else {

            return next.lookupNumber(key);
        }
    }

    public boolean hasProperty(String key) {
        if (this.key.equals(key)) {
            return !(this instanceof ObjectProperty) ||
                    ((ObjectProperty) this).value != null;
        } else if (this.next == null) {
            return false;
        } else {
            return next.hasProperty(key);
        }
    }

    public Iterator iterator() {
        return new Iterator(this);
    }

    public Iterator numericIterator() {

        return new NumericIterator(this);
    }

    public Iterator objectIterator() {

        return new ObjectIterator(this);
    }

    public void print() {

        if (this instanceof NumericProperty)
            System.out.println(
                    this.key + "=" +
                            ((NumericProperty) this).value);
        else if (this instanceof ObjectProperty)
            System.out.println(
                    this.key + "=" +
                            ((ObjectProperty) this).value);
        else
            throw new IllegalArgumentException("Unrecognized PropertyList type");

        if (this.next != null)
            this.next.print();
    }

    private static class NumericProperty extends PropertyList {

        // Serialization
        // NumericProperty
        private static final long serialVersionUID = 1;
        private static final int CURRENT_SERIAL_VERSION = 0;
        protected double value;

        public NumericProperty(String key, double value, PropertyList rest) {
            super(key, rest);
            this.value = value;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(CURRENT_SERIAL_VERSION);
            out.writeDouble(value);
        }

        private void readObject(ObjectInputStream in) throws IOException {

            value = in.readDouble();
        }
    }

    private static class ObjectProperty extends PropertyList {

        // Serialization
        // ObjectProperty
        private static final long serialVersionUID = 1;
        private static final int CURRENT_SERIAL_VERSION = 0;
        protected Object value;

        public ObjectProperty(String key, Object value, PropertyList rest) {
            super(key, rest);
            this.value = value;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(CURRENT_SERIAL_VERSION);
            out.writeObject(value);
        }

        private void readObject(ObjectInputStream in) throws IOException,
                ClassNotFoundException {

            value = in.readObject();
        }
    }

    public class Iterator implements java.util.Iterator {

        // Serialization
        // Iterator
        private static final long serialVersionUID = 1;
        private static final int CURRENT_SERIAL_VERSION = 0;
        PropertyList property;
        PropertyList nextProperty;
        HashSet<String> deletedKeys = null;
        boolean nextCalled = false;
        boolean returnNumeric = true;
        boolean returnObject = true;

        public Iterator(PropertyList pl) {
            property = findReturnablePropertyAtOrAfter(pl);

            if (property == null)
                nextProperty = null;
            else
                nextProperty = findReturnablePropertyAtOrAfter(property.next);
        }

        private PropertyList findReturnablePropertyAtOrAfter(PropertyList property) {

            while (property != null) {

                if (property instanceof NumericProperty && returnNumeric) {

                    if (((NumericProperty) property).value == 0.0) {

                        if (deletedKeys == null)
                            deletedKeys = new HashSet<String>();

                        deletedKeys.add(property.key);
                        property = property.next;
                    } else

                        break;
                } else if (property instanceof ObjectProperty &&
                        returnObject) {

                    if (((ObjectProperty) property).value == null) {

                        if (deletedKeys == null)
                            deletedKeys = new HashSet<String>();

                        deletedKeys.add(property.key);
                        property = property.next;
                    } else

                        break;
                } else
                    throw new IllegalStateException("Unrecognized property type " +
                            property.getClass().getName());
            }

            return property;
        }

        public boolean hasNext() {

            return ((nextCalled && nextProperty != null) ||
                    (!nextCalled && property != null));
        }

        public boolean isNumeric() {

            return (property instanceof NumericProperty);
        }

        public double getNumericValue() {

            return ((NumericProperty) property).value;
        }

        public Object getObjectValue() {

            return ((ObjectProperty) property).value;
        }

        public String getKey() {

            return property.key;
        }

        public PropertyList nextProperty() {

            if (nextCalled) {
                property = nextProperty;
                nextProperty = findReturnablePropertyAtOrAfter(property.next);
            } else
                nextCalled = true;

            return property;
        }

        public Object next() {

            return nextProperty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(CURRENT_SERIAL_VERSION);
            out.writeObject(property);
            out.writeObject(nextProperty);
            out.writeObject(deletedKeys);
            out.writeBoolean(nextCalled);
            out.writeBoolean(returnNumeric);
            out.writeBoolean(returnObject);
        }

        private void readObject(ObjectInputStream in) throws IOException,
                ClassNotFoundException {

            property = (PropertyList) in.readObject();
            nextProperty = (PropertyList) in.readObject();
            HashSet deletedKeysLoaded = (HashSet) in.readObject();
            deletedKeys = new HashSet<String>();
            for (Object i : deletedKeysLoaded)
                deletedKeys.add((String) i);
            //deletedKeys = (HashSet)in.readObject();
            nextCalled = in.readBoolean();
            returnNumeric = in.readBoolean();
            returnObject = in.readBoolean();
        }
    }

    public class NumericIterator extends Iterator {
        private static final long serialVersionUID = 1;

        public NumericIterator(PropertyList pl) {
            super(pl);
            this.returnObject = false;
        }
    }

    public class ObjectIterator extends Iterator {
        private static final long serialVersionUID = 1;

        public ObjectIterator(PropertyList pl) {
            super(pl);
            this.returnNumeric = false;
        }
    }
}
