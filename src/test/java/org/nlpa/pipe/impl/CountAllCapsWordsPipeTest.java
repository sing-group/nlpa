/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Patricia Martin Perez
 */
public class CountAllCapsWordsPipeTest {

	String data = "DECÉMBER is hre :), HO HO HO! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 DEC.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private CountAllCapsWordsPipe instance;

    public CountAllCapsWordsPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new CountAllCapsWordsPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test getInputType method of class CountAllCapsWordsPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test getOutputType method of class CountAllCapsWordsPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    
    /**
     * Test getAllCapsProp method of class CountAllCapsWordsPipe.
     */
    @Test
    public void testGetAllCapsProp() {
        String expResult = "allCaps";
        String result = instance.getAllCapsProperty();
        assertEquals(expResult, result);
    }


    /**
     * Test setAllCapsProp method of class CountAllCapsWordsPipe.
     */
    @Test
    public void testSetAllCapsProp() {
        String allCapsProperty = "allCaps";
        instance.setAllCapsProperty(allCapsProperty);
    }


    /**
     * Test pipe method of class CountAllCapsWordsPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(new StringBuffer(data), null, name, source);
        expResult.setProperty("allCaps", 5);

        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
