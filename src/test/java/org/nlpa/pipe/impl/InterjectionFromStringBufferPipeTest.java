/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.bdp4j.types.Instance;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class InterjectionFromStringBufferPipeTest {

    String data = "December is hre :-) , ho ho ho! Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;
    private InterjectionFromStringBufferPipe instance;

    public InterjectionFromStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new InterjectionFromStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty("language","EN");
    }

    /**
     * Test of getInputType method, of class InterjectionFromStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class InterjectionFromStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveInterjection method, of class
     * InterjectionFromStringBufferPipe.
     */
    @Test
    public void testSetRemoveInterjection_String() {
        String removeInterjection = "";
        instance.setRemoveInterjection(removeInterjection);
    }

    /**
     * Test of setLangProp method, of class InterjectionFromStringBufferPipe.
     */
    @Test
    public void testSetLangProp() {
        String langProp = "language";
        instance.setLangProp(langProp);
    }

    /**
     * Test of setRemoveInterjection method, of class
     * InterjectionFromStringBufferPipe.
     */
    @Test
    public void testSetRemoveInterjection_boolean() {
        boolean removeInterjection = false;
        instance.setRemoveInterjection(removeInterjection);
    }

    /**
     * Test of getRemoveInterjection method, of class
     * InterjectionFromStringBufferPipe.
     */
    @Test
    public void testGetRemoveInterjection() {
        boolean expResult = false;
        boolean result = instance.getRemoveInterjection();
        assertEquals(expResult, result);
    }

    /**
     * Test of setInterjectionProp method, of class
     * InterjectionFromStringBufferPipe.
     */
    @Test
    public void testSetInterjectionProp() {
        String interjectionProp = "";
        instance.setInterjectionProp(interjectionProp);
    }

    /**
     * Test of getInterjectionProp method, of class
     * InterjectionFromStringBufferPipe.
     */
    @Test
    public void testGetInterjectionProp() {
        String expResult = "interjection";
        String result = instance.getInterjectionProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class InterjectionFromStringBufferPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(new StringBuffer(data), null, name, source);
        expResult.setProperty("language", "EN");
        expResult.setProperty("interjection", "ho ho ho! -- ho -- here -- ");

        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }
}
