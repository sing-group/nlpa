/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class ComputePolarityFromStringBufferPipeTest_ {

    String data = "December is hre :-) , ho ho ho! Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private ComputePolarityFromStringBufferPipe_ instance;

    public ComputePolarityFromStringBufferPipeTest_() {
    }

    @Before
    public void setUp() {
        instance = new ComputePolarityFromStringBufferPipe_();
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty("language", "EN");
    }

    /**
     * Test of getInputType method, of class
 ComputePolarityFromStringBufferPipe_.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class
 ComputePolarityFromStringBufferPipe_.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExtensionProp method, of class
 ComputePolarityFromStringBufferPipe_.
     */
    @Test
    public void testSetExtensionProp() {
        String popProp = "polarity";
        instance.setExtensionProp(popProp);
    }

    /**
     * Test of getPolarityProp method, of class
 ComputePolarityFromStringBufferPipe_.
     */
    @Test
    public void testGetPolarityProp() {
       String expResult = "polarity";
        String result = instance.getPolarityProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class ComputePolarityFromStringBufferPipe_.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(new StringBuffer(data),null, name, source);
        expResult.setProperty("language", "EN");
        expResult.setProperty(instance.getPolarityProp(), 2);

        Instance result = instance.pipe(carrier);
        assertEquals(expResult,result);
    }
}
