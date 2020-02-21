/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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
public class StoreFileExtensionPipeTest {

    private final static URL EXAMPLE_FILE = StoreFileExtensionPipeTest.class.getResource("/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms");
   
    String name = "/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "/basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    
    File inputData;
    private static Instance carrier = null;

    private StoreFileExtensionPipe instance;

    public StoreFileExtensionPipeTest() {
    }

    @Before
    public void setUp() throws URISyntaxException {
        inputData = new File(EXAMPLE_FILE.toURI());
        instance = new StoreFileExtensionPipe();
        carrier = new Instance(inputData, null, name, source);
    }

    /**
     * Test of getInputType method, of class StoreFileExtensionPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = File.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class StoreFileExtensionPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = File.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExtensionProp method, of class StoreFileExtensionPipe.
     */
    @Test
    public void testSetExtensionProp() {
        String extProp = "extension";
        instance.setExtensionProp(extProp);
    }

    /**
     * Test of getExtensionProp method, of class StoreFileExtensionPipe.
     */
    @Test
    public void testGetExtenstionProp() {
        String expResult = "extension";
        String result = instance.getExtensionProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class StoreFileExtensionPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(inputData, null, name, source);
        expResult.setProperty(instance.getExtensionProp(), "tsms");

        Instance result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }

}
