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
 * @author Patricia Martin Perez
 */
public class ComputeCompressionRatioPipeTest {

	String data = "DECÉMBER is hre :), HO HO HO! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 DEC.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private ComputeCompressionRatioPipe instance;

    public ComputeCompressionRatioPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new ComputeCompressionRatioPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test getInputType method of class ComputeStringCompressionRatioPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test getOutputType method of class ComputeStringCompressionRatioPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    
    /**
     * Test getCompressionRatioProperty method of class ComputeStringCompressionRatioPipe.
     */
    @Test
    public void testGetCompressionRatioProp() {
        String expResult = "compressionRatio";
        String result = instance.getCompressionRatioProperty();
        assertEquals(expResult, result);
    }


    /**
     * Test setCompressionRatioProperty method of class ComputeStringCompressionRatioPipe.
     */
    @Test
    public void testSetCompressionRatioProp() {
        String compressionRatioProperty = "compressionRatio";
        instance.setCompressionRatioProperty(compressionRatioProperty);
    }


    /**
     * Test pipe method of class ComputeStringCompressionRatioPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(new StringBuffer(data), null, name, source);
        expResult.setProperty("compressionRatio", 12.0);

        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
