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
public class LeetSpeakFromStringBufferTest {

    String data = "|#บ11 |`37@|`(|";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private LeetSpeakFromStringBuffer instance;

    public LeetSpeakFromStringBufferTest() {
    }

    @Before
    public void setUp() {
        instance = new LeetSpeakFromStringBuffer();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class LeetSpeakFromStringBuffer.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class LeetSpeakFromStringBuffer.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class LeetSpeakFromStringBuffer.
     */
    @Test
    public void testPipe() {
        String expectedData = "full retard";
        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);

        Instance result = instance.pipe(carrier);
        
        assertEquals(expResult,result);
    }

}
