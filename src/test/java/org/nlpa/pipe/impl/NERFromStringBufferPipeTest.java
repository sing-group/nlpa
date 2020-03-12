/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class NERFromStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private NERFromStringBufferPipe instance;

    public NERFromStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new NERFromStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class NERFromStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class NERFromStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEntityTypes method, of class NERFromStringBufferPipe.
     */
    @Test
    public void testGetEntityTypes() {
        String entityTypes = "DATE,MONEY,NUMBER,ADDRESS,LOCATION";
        Collection<String> expResult = new ArrayList<>(Arrays.asList(entityTypes.split(",")));
        Collection<String> result = instance.getEntityTypes();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEntityTypes method, of class NERFromStringBufferPipe.
     */
    @Test
    public void testSetEntityTypes_List() {
        String entityTypes = "DATE,MONEY,NUMBER,ADDRESS,LOCATION";
        List<String> entityTypesList = Arrays.asList(entityTypes.split(","));
        instance.setEntityTypes(entityTypesList);
    }

    /**
     * Test of setEntityTypes method, of class NERFromStringBufferPipe.
     */
    @Test
    public void testSetEntityTypes_String() {
        String entityTypes = "DATE,MONEY,NUMBER,ADDRESS,LOCATION";
        instance.setEntityTypes(entityTypes);
    }

    /**
     * Test of getIdentifiedEntitiesProp method, of class
     * NERFromStringBufferPipe.
     */
    @Test
    public void testGetIdentifiedEntitiesProp() {
        String expResult = "NERDATE,NERMONEY,NERNUMBER,NERADDRESS,NERLOCATION";
        String result = instance.getIdentifiedEntitiesProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIdentifiedEntitiesProp method, of class
     * NERFromStringBufferPipe.
     */
    @Test
    public void testSetIdentifiedEntitiesProp() {
        String entitiesProp = "NERDATE,NERMONEY,NERNUMBER,NERADDRESS,NERLOCATION";
        List<String> identifiedEntitiesProperty = Arrays.asList(entitiesProp.split(","));
        instance.setIdentifiedEntitiesProp(identifiedEntitiesProperty);
    }

    /**
     * Test of setIdentifiedEntitiesProperty method, of class
     * NERFromStringBufferPipe.
     */
    @Test
    public void testSetIdentifiedEntitiesProperty() {
        String identifiedEntitiesProperty = "NERDATE,NERMONEY,NERNUMBER,NERADDRESS,NERLOCATION";
        instance.setIdentifiedEntitiesProperty(identifiedEntitiesProperty);
    }

    /**
     * Test of getIdentifiedEntitiesProperty method, of class
     * NERFromStringBufferPipe.
     */
    @Test
    public void testGetIdentifiedEntitiesProperty() {
        String entitiesProp = "NERDATE,NERMONEY,NERNUMBER,NERADDRESS,NERLOCATION";
        Collection<String> expResult = new ArrayList<>(Arrays.asList(entitiesProp.split(",")));
        Collection<String> result = instance.getIdentifiedEntitiesProperty();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class NERFromStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = " is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty("NERDATE", "31 Dec.");
        expResult.setProperty("NERMONEY", "");
        expResult.setProperty("NERNUMBER", "");
        expResult.setProperty("NERADDRESS", "");
        expResult.setProperty("NERLOCATION", "");
        Instance result = instance.pipe(carrier);
        
        assertTrue(expResult.equals(result));
    }
}
