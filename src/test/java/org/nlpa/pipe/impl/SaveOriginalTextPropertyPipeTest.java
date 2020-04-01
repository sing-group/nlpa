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
public class SaveOriginalTextPropertyPipeTest {

    SaveOriginalTextPropertyPipe instance;

    String data = "December";// is hre :-) , ho ho ho! Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on here, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    public SaveOriginalTextPropertyPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new SaveOriginalTextPropertyPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class SaveOriginalTextPropertyPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class SaveOriginalTextPropertyPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setOriginalTextProperty method, of class
     * SaveOriginalTextPropertyPipe.
     */
    @Test
    public void testSetOriginalTextProperty() {
        String originalTextProperty = "originalText";
        instance.setOriginalTextProperty(originalTextProperty);
    }

    /**
     * Test of getOriginalTextProperty method, of class
     * SaveOriginalTextPropertyPipe.
     */
    @Test
    public void testGetOriginalTextProperty() {
        String expResult = "originalText";
        String result = instance.getOriginalTextProperty();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class SaveOriginalTextPropertyPipe.
     */
    @Test
    public void testPipe() {
        Instance expResult = new Instance(new StringBuffer(data), null, name, source);
        expResult.setProperty(instance.getOriginalTextProperty(), data);
        Instance result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }

}
