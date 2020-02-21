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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class StoreTweetLangPipeTest {

    private final static URL EXAMPLE_FILE = StoreTweetLangPipeTest.class.getResource("/basic_example/_ham_/329850912749256704.twtid");
   
    String name = "/basic_example/_ham_/329850912749256704.twtid";
    String source = "/basic_example/_ham_/329850912749256704.twtid";
    
    File inputData;
    

    private static Instance carrier = null;

    private StoreTweetLangPipe instance;

    public StoreTweetLangPipeTest() {
    }

    @Before
    public void setUp() throws URISyntaxException {
        inputData = new File(EXAMPLE_FILE.toURI());
        
        instance = new StoreTweetLangPipe();
        carrier = new Instance(inputData, null, name, source);
        carrier.setProperty("extension", "twtid");
    }

    /**
     * Test of getInputType method, of class StoreTweetLangPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = File.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class StoreTweetLangPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = File.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class StoreTweetLangPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(inputData, null, name, source);
        expResult.setProperty("extension", "twtid");
//        expResult.setProperty("language", "en");
//        expResult.setProperty("language-reliability", 1.0);

        Instance result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }

}
