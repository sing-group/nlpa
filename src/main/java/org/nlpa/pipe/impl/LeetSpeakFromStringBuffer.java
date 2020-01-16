package org.nlpa.pipe.impl;

import java.nio.channels.Pipe;

import com.google.auto.service.AutoService;

import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;
import org.nlpa.util.LeetSpeak;

/**
 * Leet Speak decoder for instances
 * 
 * @author José Ramón Méndez Reboredo
 */
@AutoService(Pipe.class)
@TransformationPipe
public class LeetSpeakFromStringBuffer extends AbstractPipe {

    /**
     * Default constructor for leet speak decoder pipe
     */
    public LeetSpeakFromStringBuffer() {
        super(new Class<?>[0], new Class<?>[0]);
    }

    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    @Override
    public Instance pipe(Instance carrier) {
        carrier.setData(LeetSpeak.decode((StringBuffer)carrier.getData()));
        return carrier;
    }

}