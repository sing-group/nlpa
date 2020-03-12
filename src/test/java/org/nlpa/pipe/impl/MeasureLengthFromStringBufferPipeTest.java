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
public class MeasureLengthFromStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private MeasureLengthFromStringBufferPipe instance;
   
    public MeasureLengthFromStringBufferPipeTest() {
    }
    
    
    @Before
    public void setUp() {
        instance = new MeasureLengthFromStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }
    
    /**
     * Test of getInputType method, of class MeasureLengthFromStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class MeasureLengthFromStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLengthProp method, of class MeasureLengthFromStringBufferPipe.
     */
    @Test
    public void testSetLengthProp() {
        String lengthProp = "length";
        instance.setLengthProp(lengthProp);
    }

    /**
     * Test of getLengthProp method, of class MeasureLengthFromStringBufferPipe.
     */
    @Test
    public void testGetLengthProp() {
        String expResult = "length";
        String result = instance.getLengthProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class MeasureLengthFromStringBufferPipe.
     */
    @Test
    public void testPipe() {
         String expectedData = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instance.getLengthProp(), 172);
        Instance result = instance.pipe(carrier);

        assertEquals(expResult,result);
    }
    
}
