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
public class TokenSequencePorterStemmerPipeTest {
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private TokenSequencePorterStemmerPipe instance;

    public TokenSequencePorterStemmerPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new TokenSequencePorterStemmerPipe();

        TokenSequence inputTokenSequence = new TokenSequence();
        inputTokenSequence.add("tk:RGVjZW1iZXI=");
        inputTokenSequence.add("tk:aXM=");
        inputTokenSequence.add("tk:aHJl");
        inputTokenSequence.add("tk:aG8=");
        inputTokenSequence.add("tk:aG8=");
        inputTokenSequence.add("tk:aG8=");
        inputTokenSequence.add("tk:8J+OhQ==");
        inputTokenSequence.add("tk:QmVhdA==");
        inputTokenSequence.add("tk:dGhl");
        inputTokenSequence.add("tk:Q2hyaXN0bWFz");
        inputTokenSequence.add("tk:ZGF5cw==");
        inputTokenSequence.add("tk:d2l0aA==");
        inputTokenSequence.add("tk:dXM=");
        inputTokenSequence.add("tk:YW5k");
        inputTokenSequence.add("tk:d2U=");
        inputTokenSequence.add("tk:bGw=");
        inputTokenSequence.add("tk:ZXZlbg==");
        inputTokenSequence.add("tk:Z2l2ZQ==");
        inputTokenSequence.add("tk:eW91");
        inputTokenSequence.add("tk:MTk=");
        inputTokenSequence.add("tk:b2Zm");
        inputTokenSequence.add("tk:b25saW5l");
        inputTokenSequence.add("tk:dW50aWw=");
        inputTokenSequence.add("tk:MzE=");
        inputTokenSequence.add("tk:RGVj");
        inputTokenSequence.add("tk:VmlzaXQ=");
        inputTokenSequence.add("tk:dXM=");
        inputTokenSequence.add("tk:b24=");
        inputTokenSequence.add("tk:aGVyZQ==");
        inputTokenSequence.add("tk:eHg=");
        inputTokenSequence.add("tk:b3I=");
        inputTokenSequence.add("tk:eHg=");

        carrier = new Instance(inputTokenSequence, null, name, source);
        carrier.setProperty("language", "EN");
    }

    /**
     * Test of getInputType method, of class TokenSequencePorterStemmerPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = TokenSequence.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class TokenSequencePorterStemmerPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = TokenSequence.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class TokenSequencePorterStemmerPipe.
     */
    @Test
    public void testPipe() {
        TokenSequence expectedTokenSequence = new TokenSequence();

        expectedTokenSequence.add("tk:ZGVjZW1i");
        expectedTokenSequence.add("tk:aQ==");
        expectedTokenSequence.add("tk:aHJl");
        expectedTokenSequence.add("tk:aG8=");
        expectedTokenSequence.add("tk:aG8=");
        expectedTokenSequence.add("tk:aG8=");
        expectedTokenSequence.add("tk:8J+OhQ==");
        expectedTokenSequence.add("tk:YmVhdGU=");
        expectedTokenSequence.add("tk:dGhl");
        expectedTokenSequence.add("tk:Y2hyaXN0bWE=");
        expectedTokenSequence.add("tk:ZGF5");
        expectedTokenSequence.add("tk:d2l0aA==");
        expectedTokenSequence.add("tk:dQ==");
        expectedTokenSequence.add("tk:YW5k");
        expectedTokenSequence.add("tk:d2U=");
        expectedTokenSequence.add("tk:bGw=");
        expectedTokenSequence.add("tk:ZXZlbg==");
        expectedTokenSequence.add("tk:Z2l2ZQ==");
        expectedTokenSequence.add("tk:eW91");
        expectedTokenSequence.add("tk:MTk=");
        expectedTokenSequence.add("tk:b2Y=");
        expectedTokenSequence.add("tk:b25saW4=");
        expectedTokenSequence.add("tk:dW50aWw=");
        expectedTokenSequence.add("tk:MzE=");
        expectedTokenSequence.add("tk:ZGVj");
        expectedTokenSequence.add("tk:dmlzaXQ=");
        expectedTokenSequence.add("tk:dQ==");
        expectedTokenSequence.add("tk:b24=");
        expectedTokenSequence.add("tk:aGVyZQ==");
        expectedTokenSequence.add("tk:eA==");
        expectedTokenSequence.add("tk:b3I=");
        expectedTokenSequence.add("tk:eA==");

        Instance expResult = new Instance(expectedTokenSequence, null, name, source);
        expResult.setProperty("language", "EN");

        Instance result = instance.pipe(carrier);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of extractRoot method, of class TokenSequencePorterStemmerPipe.
     */
    @Test
    public void testExtractRoot() {
        String word = "days";
        String lang = "EN";
        String expResult = "day";
        String result = instance.extractRoot(word, lang);
        assertEquals(expResult, result);
    }

}
