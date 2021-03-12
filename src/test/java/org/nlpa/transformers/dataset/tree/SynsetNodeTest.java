/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.io.Serializable;
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
public class SynsetNodeTest {
    
    public SynsetNodeTest() {
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
     * Test of getParent method, of class SynsetNode.
     */
    @Test
    public void testGetParent() {
        System.out.println("getParent");
        SynsetNode instance = null;
        SynsetNode expResult = null;
        SynsetNode result = instance.getParent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasParent method, of class SynsetNode.
     */
    @Test
    public void testHasParent() {
        System.out.println("hasParent");
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasParent();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setParent method, of class SynsetNode.
     */
    @Test
    public void testSetParent() {
        System.out.println("setParent");
        SynsetNode parent = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.setParent(parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isParentOf method, of class SynsetNode.
     */
    @Test
    public void testIsParentOf() {
        System.out.println("isParentOf");
        SynsetNode child = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.isParentOf(child);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChildren method, of class SynsetNode.
     */
    @Test
    public void testGetChildren() {
        System.out.println("getChildren");
        SynsetNode instance = null;
        List<SynsetNode> expResult = null;
        List<SynsetNode> result = instance.getChildren();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addChild method, of class SynsetNode.
     */
    @Test
    public void testAddChild() {
        System.out.println("addChild");
        SynsetNode child = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.addChild(child);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeChild method, of class SynsetNode.
     */
    @Test
    public void testRemoveChild() {
        System.out.println("removeChild");
        SynsetNode child = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.removeChild(child);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRoot method, of class SynsetNode.
     */
    @Test
    public void testGetRoot() {
        System.out.println("getRoot");
        SynsetNode instance = null;
        SynsetNode expResult = null;
        SynsetNode result = instance.getRoot();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLeafs method, of class SynsetNode.
     */
    @Test
    public void testGetLeafs() {
        System.out.println("getLeafs");
        SynsetNode instance = null;
        List<SynsetNode> expResult = null;
        List<SynsetNode> result = instance.getLeafs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescendants method, of class SynsetNode.
     */
    @Test
    public void testGetDescendants() {
        System.out.println("getDescendants");
        SynsetNode instance = null;
        List<SynsetNode> expResult = null;
        List<SynsetNode> result = instance.getDescendants();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescendantAndSelf method, of class SynsetNode.
     */
    @Test
    public void testGetDescendantAndSelf() {
        System.out.println("getDescendantAndSelf");
        SynsetNode instance = null;
        List<SynsetNode> expResult = null;
        List<SynsetNode> result = instance.getDescendantAndSelf();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSelfOrDescendantBySynset method, of class SynsetNode.
     */
    @Test
    public void testGetSelfOrDescendantBySynset() {
        System.out.println("getSelfOrDescendantBySynset");
        String synsetId = "";
        SynsetNode instance = null;
        SynsetNode expResult = null;
        SynsetNode result = instance.getSelfOrDescendantBySynset(synsetId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isAncestorOf method, of class SynsetNode.
     */
    @Test
    public void testIsAncestorOf() {
        System.out.println("isAncestorOf");
        SynsetNode descendant = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.isAncestorOf(descendant);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeDescendant method, of class SynsetNode.
     */
    @Test
    public void testRemoveDescendant() {
        System.out.println("removeDescendant");
        SynsetNode descendant = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.removeDescendant(descendant);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getReferenceSynset method, of class SynsetNode.
     */
    @Test
    public void testGetReferenceSynset() {
        System.out.println("getReferenceSynset");
        SynsetNode instance = null;
        String expResult = "";
        String result = instance.getReferenceSynset();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSynsets method, of class SynsetNode.
     */
    @Test
    public void testGetSynsets() {
        System.out.println("getSynsets");
        SynsetNode instance = null;
        List<String> expResult = null;
        List<String> result = instance.getSynsets();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasSynsets method, of class SynsetNode.
     */
    @Test
    public void testHasSynsets() {
        System.out.println("hasSynsets");
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasSynsets();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasSynsetsDeep method, of class SynsetNode.
     */
    @Test
    public void testHasSynsetsDeep() {
        System.out.println("hasSynsetsDeep");
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasSynsetsDeep();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasSynset method, of class SynsetNode.
     */
    @Test
    public void testHasSynset() {
        System.out.println("hasSynset");
        String synsetId = "";
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasSynset(synsetId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasDescendantSynset method, of class SynsetNode.
     */
    @Test
    public void testHasDescendantSynset() {
        System.out.println("hasDescendantSynset");
        String synsetId = "";
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasDescendantSynset(synsetId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countSynsets method, of class SynsetNode.
     */
    @Test
    public void testCountSynsets() {
        System.out.println("countSynsets");
        SynsetNode instance = null;
        int expResult = 0;
        int result = instance.countSynsets();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstances method, of class SynsetNode.
     */
    @Test
    public void testGetInstances() {
        System.out.println("getInstances");
        SynsetNode instance = null;
        Set<SynsetInstance> expResult = null;
        Set<SynsetInstance> result = instance.getInstances();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasInstances method, of class SynsetNode.
     */
    @Test
    public void testHasInstances() {
        System.out.println("hasInstances");
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasInstances();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasInstancesDeep method, of class SynsetNode.
     */
    @Test
    public void testHasInstancesDeep() {
        System.out.println("hasInstancesDeep");
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.hasInstancesDeep();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addInstance method, of class SynsetNode.
     */
    @Test
    public void testAddInstance() {
        System.out.println("addInstance");
        SynsetInstance instance_2 = null;
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.addInstance(instance_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTargetFrequency method, of class SynsetNode.
     */
    @Test
    public void testGetTargetFrequency() {
        System.out.println("getTargetFrequency");
        Serializable target = null;
        SynsetNode instance = null;
        double expResult = 0.0;
        double result = instance.getTargetFrequency(target);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCombinedTargetFrequency method, of class SynsetNode.
     */
    @Test
    public void testGetCombinedTargetFrequency() {
        System.out.println("getCombinedTargetFrequency");
        Serializable target = null;
        SynsetNode node = null;
        SynsetNode instance = null;
        double expResult = 0.0;
        double result = instance.getCombinedTargetFrequency(target, node);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generalize method, of class SynsetNode.
     */
    @Test
    public void testGeneralize() {
        System.out.println("generalize");
        SynsetNode[] nodes = null;
        SynsetNode instance = null;
        instance.generalize(nodes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPathTo method, of class SynsetNode.
     */
    @Test
    public void testGetPathTo() {
        /*System.out.println("getPathTo");
        SynsetNode targetNode = null;
        SynsetNode instance = null;
        SynsetNode[] expResult = null;
        SynsetNode[] result = instance.getPathTo(targetNode);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of getDegree method, of class SynsetNode.
     */
    @Test
    public void testGetDegree() {
        System.out.println("getDegree");
        SynsetNode instance = null;
        int expResult = 0;
        int result = instance.getDegree();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxDegree method, of class SynsetNode.
     */
    @Test
    public void testGetMaxDegree() {
        System.out.println("getMaxDegree");
        SynsetNode instance = null;
        int expResult = 0;
        int result = instance.getMaxDegree();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prune method, of class SynsetNode.
     */
    @Test
    public void testPrune() {
        System.out.println("prune");
        SynsetNode instance = null;
        boolean expResult = false;
        boolean result = instance.prune();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class SynsetNode.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        SynsetNode instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toStringDeep method, of class SynsetNode.
     */
    @Test
    public void testToStringDeep_0args() {
        System.out.println("toStringDeep");
        SynsetNode instance = null;
        String expResult = "";
        String result = instance.toStringDeep();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toStringDeep method, of class SynsetNode.
     */
    @Test
    public void testToStringDeep_String() {
        /*System.out.println("toStringDeep");
        String prefix = "";
        SynsetNode instance = null;
        String expResult = "";
        String result = instance.toStringDeep(prefix);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }
    
}
