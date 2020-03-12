/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class File2StringBufferPipeTest {
    private final static URL EXAMPLE_FILE = File2StringBufferPipeTest.class.getResource("/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms");
   
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private File2StringBufferPipe instance;

    public File2StringBufferPipeTest() {
    }

    @Before
    public void setUp() throws URISyntaxException {
        File inputData = new File(EXAMPLE_FILE.toURI());
        instance = new File2StringBufferPipe();
        carrier = new Instance(inputData, null, name, source);
    }

    /**
     * Test of getInputType method, of class File2StringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = File.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class File2StringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class File2StringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData="December is hre :) , ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.\n";
        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);

        Instance result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }

}
