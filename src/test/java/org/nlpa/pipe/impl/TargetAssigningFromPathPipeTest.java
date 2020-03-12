/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import java.util.Map;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class TargetAssigningFromPathPipeTest {

    private final static URL EXAMPLE_FILE = TargetAssigningFromPathPipeTest.class.getResource("/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms");
   
    String name = "/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private TargetAssigningFromPathPipe instance;
    File inputData;

    public TargetAssigningFromPathPipeTest() {
    }

    @Before
    public void setUp() throws URISyntaxException {
        inputData = new File(EXAMPLE_FILE.toURI());
        instance = new TargetAssigningFromPathPipe();
        carrier = new Instance(inputData, null, name, source);
    }

    /**
     * Test of getInputType method, of class TargetAssigningFromPathPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = File.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class TargetAssigningFromPathPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = File.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTargets method, of class TargetAssigningFromPathPipe.
     */
    @Test
    public void testSetTargets() {
        Map<String, String> targets = new HashMap<>();
        targets.put("_ham_", "ham");
        targets.put("_spam_", "spam");
        
        instance.setTargets(targets);
    }

    /**
     * Test of getTargets method, of class TargetAssigningFromPathPipe.
     */
    @Test
    public void testGetTargets() {
        Map<String, String> expResult = new HashMap<>();
        expResult.put("_ham_", "ham");
        expResult.put("_spam_", "spam");
        
        Map<String, String> result = instance.getTargets();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class TargetAssigningFromPathPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(inputData, null, name, source);
        expResult.setTarget("spam");

        Instance result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }

}
