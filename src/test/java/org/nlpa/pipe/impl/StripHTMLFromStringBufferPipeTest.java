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
public class StripHTMLFromStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private StripHTMLFromStringBufferPipe instance;

    public StripHTMLFromStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new StripHTMLFromStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class StripHTMLFromStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class StripHTMLFromStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
       Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of isHtml method, of class StripHTMLFromStringBufferPipe.
     */
    @Test
    public void testIsHtml() {
        String s = "<strong>This is a strong text</strong>";
        boolean expResult = true;
        boolean result = StripHTMLFromStringBufferPipe.isHtml(s);
        assertEquals(expResult, result);
    
        s = "This is a strong text";
        expResult = false;
        result = StripHTMLFromStringBufferPipe.isHtml(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class StripHTMLFromStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        Instance result = instance.pipe(carrier);
        
        assertEquals(expResult,result);
    }
}
