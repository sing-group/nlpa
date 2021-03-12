/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.io.InputStream;
import java.util.List;
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
public class SynsetNodeBuilderTest {
    
    public SynsetNodeBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of buildTrees method, of class SynsetNodeBuilder.
     */
    @Test
    public void testBuildTrees() {
        System.out.println("buildTrees");
        InputStream input = null;
        List<SynsetNode> expResult = null;
        List<SynsetNode> result = SynsetNodeBuilder.buildTrees(input);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
