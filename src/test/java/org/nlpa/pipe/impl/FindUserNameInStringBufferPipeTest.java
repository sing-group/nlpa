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
public class FindUserNameInStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static Instance carrier = null;

    private FindUserNameInStringBufferPipe instance;

    public FindUserNameInStringBufferPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new FindUserNameInStringBufferPipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
    }

    /**
     * Test of getInputType method, of class FindUserNameInStringBufferPipe.
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class FindUserNameInStringBufferPipe.
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveUserName method, of class
     * FindUserNameInStringBufferPipe.
     */
    @Test
    public void testSetRemoveUserName_String() {
        String removeUserName = "";
        instance.setRemoveUserName(removeUserName);
    }

    /**
     * Test of setRemoveUserName method, of class
     * FindUserNameInStringBufferPipe.
     */
    @Test
    public void testSetRemoveUserName_boolean() {
        boolean removeUserName = true;
        instance.setRemoveUserName(removeUserName);
    }

    /**
     * Test of getRemoveUserName method, of class
     * FindUserNameInStringBufferPipe.
     */
    @Test
    public void testGetRemoveUserName() {
        boolean expResult = true;
        boolean result = instance.getRemoveUserName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setUserNameProp method, of class FindUserNameInStringBufferPipe.
     */
    @Test
    public void testSetUserNameProp() {
        String userNameProp = "@userName";
        instance.setUserNameProp(userNameProp);
    }

    /**
     * Test of isUserName method, of class FindUserNameInStringBufferPipe.
     */
    @Test
    public void testIsUserName() {
        String s = "@myid";
        boolean expResult = true;
        boolean result = FindUserNameInStringBufferPipe.isUserName(s);
        assertEquals(expResult, result);
        
        s = "myid";
        expResult = false;
        result = FindUserNameInStringBufferPipe.isUserName(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class FindUserNameInStringBufferPipe.
     */
    @Test
    public void testPipe() {
        String expectedData = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or ";

        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instance.getUserNameProp(), "@xx. ");
        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

}
