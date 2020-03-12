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
public class ComputePolarityTBWSFromStringBufferTest {

    String data = "December is hre :-) , ho ho ho! Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private ComputePolarityTBWSFromStringBuffer instance;

    public ComputePolarityTBWSFromStringBufferTest() {
    }

    @Before
    public void setUp() {
        instance = new ComputePolarityTBWSFromStringBuffer();
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty("language", "EN");
    }

    /**
     * Test of getPolarityProperty method, of class
     * ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testGetPolarityProperty() {
        String expResult = "PolarityTBWS";
        String result = instance.getPolarityProperty();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPolarityProperty method, of class
     * ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testSetPolarityProperty() {
        String polProp = "PolarityTBWS";
        instance.setPolarityProperty(polProp);
    }

    /**
     * Test of getUri method, of class ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testGetUri() {
        String expResult = "http://textblob-ws/postjson";
        String result = instance.getUri();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUri method, of class ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testSetUri() {
        String polProp = "http://textblob-ws/postjson";
        instance.setUri(polProp);
    }

    /**
     * Test of getInputType method, of class
     * ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class
     * ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class ComputePolarityTBWSFromStringBuffer.
     */
    @Test
    public void testPipe() {
       /* Instance expResult = new Instance(new StringBuffer(data),null, name, source);
        expResult.setProperty("language", "EN");
        expResult.setProperty(instance.getPolarityProperty(), 2);

        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));*/
    }
}
