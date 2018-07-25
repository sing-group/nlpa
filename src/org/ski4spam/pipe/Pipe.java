package org.ski4spam.pipe;

import org.ski4spam.ia.types.Instance;
// import org.ski4spam.ia.util.PropertyList;


/**
 * The abstract superclass of all Pipes, which transform one data type to another.
 * Pipes are most often used for feature extraction.
 * <p>
 * A pipe operates on an {@link org.ski4spam.ia.types.Instance}, which is a carrier of data.
 * A pipe reads from and writes to fields in the Instance when it is requested
 * to process the instance. It is up to the pipe which fields in the Instance it
 * reads from and writes to, but usually a pipe will read its input from and write
 * its output to the "data" field of an instance.
 * <p>
 * Pipes can be hierachically composed. In a typical usage, a SerialPipe is created which
 * holds instances of other pipes in an ordered list. Piping
 * in instance through a SerialPipe means piping the instance through the child pipes
 * in sequence.
 * <p>
 * A pipe holds onto two separate Alphabets: one for the symbols (feature names)
 * encountered in the data fields of the instances processed through the pipe,
 * and one for the symbols encountered in the target fields.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @modified Jos� Ram�n M�ndez Reboredo
 */
public abstract class Pipe {
    Pipe parent = null;
    boolean targetProcessing = true;

    /**
     * Construct a pipe with no data and target dictionaries
     */
    public Pipe() {
        //this((Class)null, (Class)null);
    }

    /**
     * Process an Instance.  This method takes an input Instance,
     * destructively modifies it in some way, and returns it.
     * This is the method by which all pipes are eventually run.
     * <p>
     * One can create a new concrete subclass of Pipe simply by
     * implementing this method.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    public abstract Instance pipe(Instance carrier);


    /**
     * Create and process an Instance. An instance is created from
     * the given arguments and then the pipe is run on the instance.
     *
     * @param data       Object used to initialize data field of new instance.
     * @param target     Object used to initialize target field of new instance.
     * @param name       Object used to initialize name field of new instance.
     * @param source     Object used to initialize source field of new instance.
     * @param parent     Unused
     * @param props      Unused
     */
    /*
    public Instance pipe(Object data, Object target, Object name,
                         Object source, Instance parent,
                         PropertyList props) {

        return pipe(new Instance(data, target, name, source));
    }
    */

    /**
     * Return true iff this pipe expects and processes information in
     * the <tt>target</tt> slot.
     */

    public boolean isTargetProcessing() {

        return targetProcessing;
    }

    /**
     * Set whether input is taken from target field of instance during processing.
     * If argument is false, don't expect to find input material for the target.
     * By default, this is true.
     */
    public void setTargetProcessing(boolean lookForAndProcessTarget) {
        targetProcessing = lookForAndProcessTarget;
    }

    public Pipe getParent() {
        return parent;
    }

    // Note: This must be called *before* this Pipe has been added to
    // the parent's collection of pipes, otherwise in
    // DictionariedPipe.setParent() we will simply get back this Pipe's
    // Alphabet information.
    public void setParent(Pipe p) {
        if (parent != null)
            throw new IllegalStateException("Parent already set.");

        parent = p;
    }

    public Pipe getParentRoot() {

        if (parent == null)

            return this;

        Pipe p = parent;

        while (p.parent != null)
            p = p.parent;

        return p;
    }
}
