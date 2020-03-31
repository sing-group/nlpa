/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.types;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
public class DictionaryTest {

    Dictionary instance;

    public DictionaryTest() {

    }

    @Before
    public void setUp() {
        instance = Dictionary.getDictionary();

    }

    /**
     * Test of getDictionary method, of class Dictionary.
     */
    /*@Test
    public void testGetDictionary() {
        System.out.println("getDictionary");
        Dictionary expResult = new Dictionary();
        Dictionary result = Dictionary.getDictionary();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
    /**
     * Test of setEncode method, of class Dictionary.
     */
    @Test
    public void testSetEncode() {
        boolean encode = true;
        instance.setEncode(encode);
    }

    /**
     * Test of getEncode method, of class Dictionary.
     */
    @Test
    public void testGetEncode() {
        boolean expResult = true;
        boolean result = instance.getEncode();
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class Dictionary.
     */
    @Test
    public void testAdd() {
        String text = "bn:12345678n";
        instance.add(text);

//        text = "";
//        instance.add(text);
    }

    /**
     * Test of isIncluded method, of class Dictionary.
     */
    @Test
    public void testIsIncluded() {
        String text = "bn:12345678n";
        boolean checkEncode = false;
        boolean expResult = true;
        boolean result = instance.isIncluded(text, checkEncode);

        assertEquals(expResult, result);
    }

    /**
     * Test of replace method, of class Dictionary.
     */
    @Test
    public void testReplace() {
        String originalText = "bn:12345678n";
        String replaceText = "bn:12345678v";
        instance.replace(originalText, replaceText);
    }

    /**
     * Test of print method, of class Dictionary.
     */
    @Test
    public void testPrint() {
        instance.print();
    }

    /**
     * Test of size method, of class Dictionary.
     */
    @Test
    public void testSize() {
        String text = "bn:12345678n";
        instance.add(text);
        
        int expResult = 1;
        int result = instance.size();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of encodeBase64 method, of class Dictionary.
     */
    @Test
    public void testEncodeBase64() {

        String feat = "bn:12345678n";
        String expResult = "Ym46MTIzNDU2Nzhu";
        String result = instance.encodeBase64(feat);
        assertEquals(expResult, result);
    }

    /**
     * Test of decodeBase64 method, of class Dictionary.
     */
    @Test
    public void testDecodeBase64() {
        String feat = "Ym46MTIzNDU2Nzhu";
        String expResult = "bn:12345678n";
        String result = instance.decodeBase64(feat);
        assertEquals(expResult, result);
    }

    /**
     * Test of iterator method, of class Dictionary.
     */
    @Test
    public void testIterator() {
        HashSet<String> synsetList = new LinkedHashSet<>();
        synsetList.add("bn:12345678v");
        Iterator<String> expResult = synsetList.iterator();
        Iterator<String> result = instance.iterator();   
        assertEquals(expResult, result);
    }

    /**
     * Test of clear method, of class Dictionary.
     */
    @Test
    public void testClear() {
        instance.clear();
    }

}
