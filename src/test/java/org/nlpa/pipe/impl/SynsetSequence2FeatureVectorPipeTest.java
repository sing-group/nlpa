/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.nlpa.types.FeatureVector;
import org.nlpa.types.SequenceGroupingStrategy;
import org.nlpa.types.SynsetSequence;

/**
 *
 * @author María Novo
 */
public class SynsetSequence2FeatureVectorPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private SynsetSequence2FeatureVectorPipe instance;

    public SynsetSequence2FeatureVectorPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new SynsetSequence2FeatureVectorPipe();

        SynsetSequence synsetSequence = new SynsetSequence(data);
        List<Pair<String, String>> synsets = new ArrayList<>();
        synsets.add(new Pair("bn:00025645n", "December"));
        synsets.add(new Pair("bn:00006898n", "ho"));
        synsets.add(new Pair("bn:03100869n", "ho ho"));
        synsets.add(new Pair("bn:00006898n", "ho"));
        synsets.add(new Pair("bn:03100869n", "ho ho"));
        synsets.add(new Pair("bn:00006898n", "ho"));
        synsets.add(new Pair("bn:00009394n", "Beat"));
        synsets.add(new Pair("bn:00018836n", "Christmas"));
        synsets.add(new Pair("bn:00018836n", "Christmas days"));
        synsets.add(new Pair("bn:00000086n", "days"));
        synsets.add(new Pair("bn:03149538n", "online"));
        synsets.add(new Pair("bn:00025645n", "Dec"));
        synsets.add(new Pair("bn:00080111n", "Visit"));
        synsetSequence.setSynsets(synsets);
        synsetSequence.setFixedText(data);

        carrier = new Instance(synsetSequence, null, name, source);
    }

    /**
     * Test of getInputType method, of class SynsetSequence2FeatureVectorPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = SynsetSequence.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class SynsetSequence2FeatureVectorPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = FeatureVector.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setGroupStrategy method, of class
     * SynsetSequence2FeatureVectorPipe.
     */
    @Test
    public void testSetGroupStrategy() {
        String groupStrategy = SequenceGroupingStrategy.COUNT.toString();                
        instance.setGroupStrategy(groupStrategy);
    }

    /**
     * Test of getGroupStrategy method, of class
     * SynsetSequence2FeatureVectorPipe.
     */
    @Test
    public void testGetGroupStrategy() {
        SequenceGroupingStrategy expResult = SequenceGroupingStrategy.COUNT;
        SequenceGroupingStrategy result = instance.getGroupStrategy();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class SynsetSequence2FeatureVectorPipe.
     */
    @Test
    public void testPipe() {
        Map<String, Double> features = new HashMap<>();
        features.put("bn:03100869n", 2.0);
        features.put("bn:00006898n", 3.0);
        features.put("bn:00018836n", 2.0);
        features.put("bn:00025645n", 2.0);
        features.put("bn:00000086n", 1.0);
        features.put("bn:03149538n", 1.0);
        features.put("bn:00080111n", 1.0);
        features.put("bn:00009394n", 1.0);
        FeatureVector expectedFeatureVector = new FeatureVector(features);

        Instance expResult = new Instance(expectedFeatureVector, null, name, source);
        Instance result = instance.pipe(carrier);

        assertEquals(expResult, result);
    }

}
