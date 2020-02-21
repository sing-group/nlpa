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
public class FindEmoticonInStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private FindEmoticonInStringBufferPipe instance;

    public FindEmoticonInStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new FindEmoticonInStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveEmoticon method, of class
     * FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testSetRemoveEmoticon_String() {
        String removeEmoticon = "";
        instance.setRemoveEmoticon(removeEmoticon);
    }

    /**
     * Test of setRemoveEmoticon method, of class
     * FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testSetRemoveEmoticon_boolean() {
        boolean removeEmoticon = false;
        instance.setRemoveEmoticon(removeEmoticon);
    }

    /**
     * Test of getRemoveEmoticon method, of class
     * FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testGetRemoveEmoticon() {
        boolean expResult = true;
        boolean result = instance.getRemoveEmoticon();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEmoticonProp method, of class FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testSetEmoticonProp() {
        String emoticonProp = "";
        instance.setEmoticonProp(emoticonProp);
    }

    /**
     * Test of getEmoticonProp method, of class FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testGetEmoticonProp() {
        String expResult = "emoticon";
        String result = instance.getEmoticonProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class FindEmoticonInStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = "December is hre , ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instance.getEmoticonProp(), ":) ");   
        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
