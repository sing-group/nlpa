/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class GuessDateFromFilePipeTest {

    private final static URL EXAMPLE_FILE = GuessDateFromFilePipeTest.class.getResource("/basic_example/_ham_/z12kix1zlmz3fhmhf04cipe5qpvsxrezbpg0k.ytbid");
   
    String name = "/basic_example/_ham_/z12kix1zlmz3fhmhf04cipe5qpvsxrezbpg0k.ytbid";
    String source = "/basic_example/_ham_/z12kix1zlmz3fhmhf04cipe5qpvsxrezbpg0k.ytbid";

    private static Instance carrier = null;
    
    private GuessDateFromFilePipe instance;
    private File inputData;

    public GuessDateFromFilePipeTest() {
    }

    @Before
    public void setUp() throws URISyntaxException {
        inputData = new File(EXAMPLE_FILE.toURI());
        instance = new GuessDateFromFilePipe();
        carrier = new Instance(inputData, null, name, source);
    }

    /**
     * Test of getInputType method, of class GuessDateFromFilePipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = File.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class GuessDateFromFilePipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = File.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDateProp method, of class GuessDateFromFilePipe.
     */
    @Test
    public void testSetDateProp() {
        String dateProp = "date";
        instance.setDateProp(dateProp);
    }

    /**
     * Test of getDateProp method, of class GuessDateFromFilePipe.
     */
    @Test
    public void testGetDateProp() {
        String expResult = "date";
        String result = instance.getDateProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class GuessDateFromFilePipe.
     */
    @Test
    public void testPipe() throws ParseException {
         Instance expResult = new Instance(inputData, null, name, source);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse("2014-11-07 12:51:06");// HH:mm:ss.SSS
            expResult.setProperty(instance.getDateProp(), date);

        } catch (ParseException ex) {
            Logger.getLogger(GuessDateFromFilePipeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Instance result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }
}
