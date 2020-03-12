/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.nlpa.types.SynsetSequence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author María Novo
 */
public class SynsetCounterFromSynsetSequencePipeTest {
    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private SynsetCounterFromSynsetSequencePipe instance;

    public SynsetCounterFromSynsetSequencePipeTest() {
    }
    
    
    @Before
    public void setUp() {
        SynsetSequence synsetSequence = new SynsetSequence(data);
        List<Pair<String, String>> synsets = new ArrayList<>();
        synsets.add(new Pair<>("bn:00025645n", "December"));
        synsets.add(new Pair<>("bn:03100869n", "ho ho"));
        synsets.add(new Pair<>("bn:03100869n", "ho ho"));
        synsets.add(new Pair<>("bn:00009394n", "Beat"));
        synsets.add(new Pair<>("bn:00018836n", "Christmas days"));
        synsets.add(new Pair<>("bn:03149538n", "online"));
        synsets.add(new Pair<>("bn:00025645n", "Dec"));
        synsets.add(new Pair<>("bn:00080111n", "Visit"));
        synsetSequence.setSynsets(synsets);
        synsetSequence.setFixedText(data);
        instance = new SynsetCounterFromSynsetSequencePipe();
        carrier = new Instance(synsetSequence, null, name, source);
    }

    /**
     * Test of getInputType method, of class SynsetCounterFromSynsetSequencePipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = SynsetSequence.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class SynsetCounterFromSynsetSequencePipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = SynsetSequence.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSynsetCounterProp method, of class SynsetCounterFromSynsetSequencePipe.
     */
    @Test
    public void testSetSynsetCounterProp() {
        String synsetCounterProp = "synset-counter";
        instance.setSynsetCounterProp(synsetCounterProp);
    }

    /**
     * Test of getSynsetCounterProp method, of class SynsetCounterFromSynsetSequencePipe.
     */
    @Test
    public void testGetSynsetCounterProp() {
        String expResult = "synset-counter";
        String result = instance.getSynsetCounterProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class SynsetCounterFromSynsetSequencePipe.
     */
    @Test
    public void testPipe() {
        SynsetSequence expectedSynsetSequence = new SynsetSequence(data);
        List<Pair<String, String>> synsets = new ArrayList<>();
        synsets.add(new Pair<>("bn:00025645n", "December"));
        synsets.add(new Pair<>("bn:03100869n", "ho ho"));
        synsets.add(new Pair<>("bn:03100869n", "ho ho"));
        synsets.add(new Pair<>("bn:00009394n", "Beat"));
        synsets.add(new Pair<>("bn:00018836n", "Christmas days"));
        synsets.add(new Pair<>("bn:03149538n", "online"));
        synsets.add(new Pair<>("bn:00025645n", "Dec"));
        synsets.add(new Pair<>("bn:00080111n", "Visit"));
        expectedSynsetSequence.setSynsets(synsets);
        expectedSynsetSequence.setFixedText(data);

        Instance expResult = new Instance(expectedSynsetSequence, null, name, source);
        expResult.setProperty(instance.getSynsetCounterProp(), 8);
        Instance result = instance.pipe(carrier);

        assertEquals(expResult, result);
    }
    
}
