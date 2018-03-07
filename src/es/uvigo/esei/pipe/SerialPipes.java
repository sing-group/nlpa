/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
package es.uvigo.esei.pipe;

import es.uvigo.esei.ia.types.Instance;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Convert an instance through a sequence of pipes.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */


public class SerialPipes extends Pipe implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2530269803030314901L;

    /**
     * Pipes
     */
    ArrayList<Pipe> pipes;

    public SerialPipes() {
        this.pipes = new ArrayList<Pipe>();
    }

    public SerialPipes(Object[] pipes) {
        this((Pipe[]) pipes);
    }

    public SerialPipes(Pipe[] pipes) {
        this.pipes = new ArrayList<Pipe>(pipes.length);

        //System.out.println ("SerialPipes init this = "+this);
        for (int i = 0; i < pipes.length; i++)
            this.add(pipes[i]);
    }


    public SerialPipes(ArrayList<Pipe> pipeList //added by Fuchun
    ) {
        this.pipes = new ArrayList<Pipe>(pipeList.size());

        for (int i = 0; i < pipeList.size(); i++) {
            this.add(pipeList.get(i));
        }
    }

    public Pipe[] getPipes() {
        if (pipes == null) return new Pipe[0];
        Pipe[] returnValue = new Pipe[pipes.size()];
        return pipes.toArray(returnValue);
    }

    /**
     * Set the pipes used
     *
     * @param pipes pipes used
     */

    public void setPipes(Pipe[] pipes) {
        this.pipes = new ArrayList<Pipe>(pipes.length);

        //System.out.println ("SerialPipes init this = "+this);
        for (int i = 0; i < pipes.length; i++)
            this.add(pipes[i]);
    }

    /**
     * Returns the current Pipe
     *
     * @return current Pipe
     */

    public SerialPipes getPipe() {
        return this;
    }

    //public ArrayList<Pipe> getPipes() { //added by Fuchun
    //    return pipes;
    //}

    public void setTargetProcessing(boolean lookForAndProcessTarget) {
        super.setTargetProcessing(lookForAndProcessTarget);

        for (int i = 0; i < pipes.size(); i++)
            (pipes.get(i)).setTargetProcessing(lookForAndProcessTarget);
    }

    protected void add(Pipe pipe) {
        pipe.setParent(this);
        pipes.add(pipe);
    }

    public Instance pipe(Instance carrier, int startingIndex) {
        for (int i = startingIndex; i < pipes.size(); i++) {

            Pipe p = pipes.get(i);

            if (p == null) {
                System.err.println("Pipe is null");
            } else {

                try {
                    carrier = p.pipe(carrier);
                } catch (Exception e) {
                    System.err.println("Exception on pipe " + i + ". " + e);
                    e.printStackTrace(System.err);
                }
            }
        }

        return carrier;
    }

    // Call this version when you are not training and don't want conjunctions to mess up the decoding.
    public Instance pipe(Instance carrier, int startingIndex,
                         boolean growAlphabet) {
        System.out.print("*");
        for (int i = startingIndex; i < pipes.size(); i++) {
            Pipe p = pipes.get(i);

            if (p == null) {
                System.err.println("Pipe is null");
                System.exit(0);
            } else {
                try {
                    carrier = p.pipe(carrier);
                } catch (Exception e) {
                    System.err.println("Exception on pipe " + i + ". " + e);
                }
            }
        }
        return carrier;
    }

    public void removePipe(int index) {

        try {
            pipes.remove(index);
        } catch (Exception e) {
            System.err.println(
                    "Error removing pipe. Index = " + index + ".  " +
                            e.getMessage());
        }
    }

    //added by Fuchun Jan.30, 2004
    public void replacePipe(int index, Pipe p) {

        try {
            pipes.set(index, p);
        } catch (Exception e) {
            System.err.println(
                    "Error replacing pipe. Index = " + index + ".  " +
                            e.getMessage());
        }
    }

    public int size() {

        return pipes.size();
    }

    public Pipe getPipe(int index) {
        Pipe retPipe = null;

        try {
            retPipe = pipes.get(index);
        } catch (Exception e) {
            System.err.println(
                    "Error getting pipe. Index = " + index + ".  " +
                            e.getMessage());
        }

        return retPipe;
    }

    public Instance pipe(Instance carrier) {
        return pipe(carrier, 0);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < pipes.size(); i++)
            sb.append(pipes.get(i).toString() + ",");

        return sb.toString();
    }

}
