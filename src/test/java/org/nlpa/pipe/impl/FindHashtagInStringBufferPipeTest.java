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
public class FindHashtagInStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private FindHashtagInStringBufferPipe instance;

    public FindHashtagInStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new FindHashtagInStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveHashtag method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testSetRemoveHashtag_String() {
        String removeHashtag = "";
        instance.setRemoveHashtag(removeHashtag);
    }

    /**
     * Test of setRemoveHashtag method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testSetRemoveHashtag_boolean() {
        boolean removeHashtag = false;
        instance.setRemoveHashtag(removeHashtag);
    }

    /**
     * Test of getRemoveHashtag method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testGetRemoveHashtag() {
        boolean expResult = true;
        boolean result = instance.getRemoveHashtag();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHashtagProp method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testSetHashtagProp() {
        String hashtagProp = "";
        instance.setHashtagProp(hashtagProp);
    }

    /**
     * Test of getHashtagProp method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testGetHashtagProp() {
        String expResult = "hashtag";
        String result = instance.getHashtagProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of isHashtag method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testIsHashtag() {
        StringBuffer s = new StringBuffer("#find");
        boolean expResult = true;
        boolean result = FindHashtagInStringBufferPipe.isHashtag(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class FindHashtagInStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here,  or @xx.";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instance.getHashtagProp(), "#xx ");
        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
