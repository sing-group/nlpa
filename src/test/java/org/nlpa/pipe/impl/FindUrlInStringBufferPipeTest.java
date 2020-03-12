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
public class FindUrlInStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private FindUrlInStringBufferPipe instance;

    public FindUrlInStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new FindUrlInStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveURL method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testSetRemoveURL_String() {
        String removeURL = "";
        instance.setRemoveURL(removeURL);
    }

    /**
     * Test of setRemoveURL method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testSetRemoveURL_boolean() {
        boolean removeURL = true;
        instance.setRemoveURL(removeURL);
    }

    /**
     * Test of getRemoveURL method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testGetRemoveURL() {
        boolean expResult = true;
        boolean result = instance.getRemoveURL();
        assertEquals(expResult, result);
    }

    /**
     * Test of setURLProp method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testSetURLProp() {
        String URLProp = "URLs";
        instance.setURLProp(URLProp);
    }
    
    /**
     * Test of getURLProp method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testGetURLProp() {
        String expResult = "URLs";
        String result = instance.getURLProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of isURL method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testIsURL() {
        StringBuffer s = new StringBuffer("http://www.uvigo.es");
        boolean expResult = true;
        boolean result = FindUrlInStringBufferPipe.isURL(s);
        assertEquals(expResult, result);
        
        s = new StringBuffer("uvigo.es");
        expResult = false;
        result = FindUrlInStringBufferPipe.isURL(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class FindUrlInStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"\">here</a>, #xx or @xx.";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instance.getURLProp(),"www.xx.com ");
        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
