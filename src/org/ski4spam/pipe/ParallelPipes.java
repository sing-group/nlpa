package org.ski4spam.pipe;

import org.ski4spam.ia.types.Instance;

import java.io.Serializable;
import java.util.ArrayList;

public class ParallelPipes extends Pipe implements Serializable {

    private ArrayList<Pipe> pipes;

    public ParallelPipes() {
        this.pipes = new ArrayList<>();
    }

    @Override
    public Instance pipe(Instance carrier) {
        return null;
    }

    public void add(ParallelPipes parallelPipes) {
        pipes.add(parallelPipes);
    }

    public void add(SerialPipes serialPipes) {
        pipes.add(serialPipes);
    }

    public void add(Pipe pipe) {
        pipes.add(pipe);
    }


}
