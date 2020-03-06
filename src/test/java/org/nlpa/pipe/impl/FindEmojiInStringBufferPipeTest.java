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
public class FindEmojiInStringBufferPipeTest {

    String data = "December is hre :-) , ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private FindEmojiInStringBufferPipe instance;

    public FindEmojiInStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new FindEmojiInStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
        //  carrier.setProperty(instance.getLangProp(), "EN");
    }

    /**
     * Test of getInputType method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveEmoji method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testSetRemoveEmoji_String() {
        String removeEmoji = "";
        instance.setRemoveEmoji(removeEmoji);        
    }

    /**
     * Test of setRemoveEmoji method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testSetRemoveEmoji_boolean() {
        boolean removeEmoji = false;
        instance.setRemoveEmoji(removeEmoji);
    }

    /**
     * Test of getRemoveEmoji method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testGetRemoveEmoji() {
        boolean expResult = true;
        boolean result = instance.getRemoveEmoji();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEmojiProp method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testSetEmojiProp() {
        String emojiProp = "";
        instance.setEmojiProp(emojiProp);
    }

    /**
     * Test of getEmojiProp method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testGetEmojiProp() {
        String expResult = "emoji";
        String result = instance.getEmojiProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class FindEmojiInStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = "December is hre :-) , ho ho ho!  Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
        
        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instance.getEmojiProp(), "🎅");
       
        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
