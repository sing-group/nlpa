/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.bdp4j.types.Instance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class GuessLanguageFromStringBufferPipeTest {

    String data = "December is hre :-) , ho ho ho! Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;
    private GuessLanguageFromStringBufferPipe instance;

    public GuessLanguageFromStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new GuessLanguageFromStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInputType method, of class GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLangProp method, of class GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testSetLangProp() {
       String langProp = "language";
        instance.setLangReliabilityProp(langProp);
    }

    /**
     * Test of getLangProp method, of class GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testGetLangProp() {
        String expResult ="language";
        String result = instance.getLangProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLangReliabilityProp method, of class
     * GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testSetLangReliabilityProp() {
        String langReliabilityProp = "language-reliability";
        instance.setLangReliabilityProp(langReliabilityProp);
    }

    /**
     * Test of getLangReliabilityProp method, of class
     * GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testGetLangReliabilityProp() {
        String expResult ="language-reliability";
        String result = instance.getLangReliabilityProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class GuessLanguageFromStringBufferPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(new StringBuffer(data),null, name, source);
        expResult.setProperty(instance.getLangProp(), "EN");
        expResult.setProperty(instance.getLangReliabilityProp(), "0.9999981494831541");

        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
