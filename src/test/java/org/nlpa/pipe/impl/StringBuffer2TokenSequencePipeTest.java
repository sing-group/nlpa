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
import org.nlpa.types.TokenSequence;

/**
 *
 * @author María Novo
 */
public class StringBuffer2TokenSequencePipeTest {

    String data = "December is hre :) , ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private StringBuffer2TokenSequencePipe instance;

    public StringBuffer2TokenSequencePipeTest() {
    }

    @Before
    public void setUp() {
        instance = new StringBuffer2TokenSequencePipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty("language", "EN");
    }

    /**
     * Test of getInputType method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = TokenSequence.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLangProp method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testSetLangProp() {
        String langProp = "language";
        instance.setLangProp(langProp);
    }

    /**
     * Test of getLangProp method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testGetLangProp() {
        String expResult = "language";
        String result = instance.getLangProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSeparators method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testSetSeparators() {
        String separators = TokenSequence.DEFAULT_SEPARATORS;
        instance.setSeparators(separators);
    }

    /**
     * Test of getSeparators method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testGetSeparators() {
        String expResult = " \t\r\n\f!\"#$%&'()*+,\\-./:;<=>?@[]^_`{|}~";
        String result = instance.getSeparators();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testPipe() {
        TokenSequence expectedTokenSequence = new TokenSequence();
        expectedTokenSequence.add("tk:RGVjZW1iZXI=");
        expectedTokenSequence.add("tk:aXM=");
        expectedTokenSequence.add("tk:aHJl");
        expectedTokenSequence.add("tk:aG8=");
        expectedTokenSequence.add("tk:aG8=");
        expectedTokenSequence.add("tk:aG8=");
        expectedTokenSequence.add("tk:8J+OhQ==");
        expectedTokenSequence.add("tk:QmVhdA==");
        expectedTokenSequence.add("tk:dGhl");
        expectedTokenSequence.add("tk:Q2hyaXN0bWFz");
        expectedTokenSequence.add("tk:ZGF5cw==");
        expectedTokenSequence.add("tk:d2l0aA==");
        expectedTokenSequence.add("tk:dXM=");
        expectedTokenSequence.add("tk:YW5k");
        expectedTokenSequence.add("tk:d2U=");
        expectedTokenSequence.add("tk:bGw=");
        expectedTokenSequence.add("tk:ZXZlbg==");
        expectedTokenSequence.add("tk:Z2l2ZQ==");
        expectedTokenSequence.add("tk:eW91");
        expectedTokenSequence.add("tk:MTk=");
        expectedTokenSequence.add("tk:b2Zm");
        expectedTokenSequence.add("tk:b25saW5l");
        expectedTokenSequence.add("tk:dW50aWw=");
        expectedTokenSequence.add("tk:MzE=");
        expectedTokenSequence.add("tk:RGVj");
        expectedTokenSequence.add("tk:VmlzaXQ=");
        expectedTokenSequence.add("tk:dXM=");
        expectedTokenSequence.add("tk:b24=");
        expectedTokenSequence.add("tk:aGVyZQ==");
        expectedTokenSequence.add("tk:eHg=");
        expectedTokenSequence.add("tk:b3I=");
        expectedTokenSequence.add("tk:eHg=");
        Instance expResult = new Instance(expectedTokenSequence, null, name, source);
        expResult.setProperty(instance.getLangProp(), "EN");
        Instance result = instance.pipe(carrier);

        assertEquals(expResult, result);
    }

    /**
     * Test of writeToDisk method, of class StringBuffer2TokenSequencePipe.
     */
    @Test
    public void testWriteToDisk() {
        String dir = "output/";
        instance.writeToDisk(dir);
    }

}
