/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bdp4j.types.Dataset;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author María Novo
 */
public class SynsetNodeTest {

    SynsetNode synsetNode;
    SynsetNode parentNode;
    SynsetInstance synsetInstance;

    public SynsetNodeTest() {

    }

    @Before
    public void setUp() {
//        String parent = "bn:00015258n"; //canid

        // child.add("bn:00015267n"); //dog
//        synsets.add("bn:00036129n"); //fox
//        synsets.add("bn:00081469n"); //wolf
        List<SynsetNode> children = new ArrayList<>();
        children.add(synsetNode);

        parentNode = new SynsetNode("bn:00015258n", null, children); // canid        
        synsetNode = new SynsetNode("bn:00015267n", parentNode); // dog

        List<String> target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("id"));
        attributes.add(new Attribute("new_att"));
        attributes.add(new Attribute("target", target_values));

        Dataset dataset = new Dataset("test", attributes, 0);

        Instance instance = dataset.createDenseInstance();
        instance.setValue(0, 25d);
        instance.setValue(1, 1d);
        instance.setValue(2, 0d);

        synsetInstance = new WekaSynsetInstance(instance);
    }

    /**
     * Test of getParent method, of class SynsetNode.
     */
    @Test
    public void testGetParent() {
        SynsetNode expResult = parentNode;
        SynsetNode result = synsetNode.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasParent method, of class SynsetNode.
     */
    @Test
    public void testHasParent() {
        boolean expResult = true;
        boolean result = synsetNode.hasParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of setParent method, of class SynsetNode.
     */
    @Test
    public void testSetParent() {
        SynsetNode parent = new SynsetNode("bn:00015258n", null);
        boolean expResult = true;
        boolean result = synsetNode.setParent(parent);
        assertEquals(expResult, result);
    }

    /**
     * Test of isParentOf method, of class SynsetNode.
     */
    @Test
    public void testIsParentOf() {
        SynsetNode parent = new SynsetNode("bn:00015258n", null);
        parent.addChild(synsetNode);
        boolean expResult = true;
        boolean result = parent.isParentOf(synsetNode);
        assertEquals(expResult, result);
    }

    /**
     * Test of getChild method, of class SynsetNode.
     */
    @Test
    public void testGetChild() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);
        SynsetNode expResult = synsetNode;
        SynsetNode result = parent.getChild();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChildren method, of class SynsetNode.
     */
    @Test
    public void testGetChildren() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);
        List<SynsetNode> expResult = new ArrayList<>();
        expResult.add(synsetNode);
        List<SynsetNode> result = parent.getChildren();
        assertEquals(expResult, result);
    }

    /**
     * Test of addChild method, of class SynsetNode.
     */
    @Test
    public void testAddChild() {
        SynsetNode child = new SynsetNode("bn:00036129n");
        boolean result = parentNode.addChild(child);
        boolean expResult = true;
        assertEquals(expResult, result);
    }

    /**
     * Test of removeChild method, of class SynsetNode.
     */
    @Test
    public void testRemoveChild() {
        boolean expResult = false;
        boolean result = parentNode.removeChild(synsetNode);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRoot method, of class SynsetNode.
     */
    @Test
    public void testGetRoot() {
        SynsetNode result = synsetNode.getRoot();
        assertEquals(parentNode, result);
    }

    /**
     * Test of getLeafs method, of class SynsetNode.
     */
    @Test
    public void testGetLeafs() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);

        List<SynsetNode> expResult = new ArrayList<>();
        expResult.add(synsetNode);

        List<SynsetNode> result = parent.getLeafs();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescendants method, of class SynsetNode.
     */
    @Test
    public void testGetDescendants_0args() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);

        SynsetNode child = new SynsetNode("bn:02964720n"); // Yorkshire Terrier
        child.setParent(synsetNode);

        List<SynsetNode> expResult = new ArrayList<>();
        expResult.add(synsetNode);
        expResult.add(child);
        List<SynsetNode> result = parent.getDescendants();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescendants method, of class SynsetNode.
     */
    @Test
    public void testGetDescendants_int() {
        int maxDistance = 1;
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);

        SynsetNode child = new SynsetNode("bn:02964720n"); // Yorkshire Terrier
        child.setParent(synsetNode);

        List<SynsetNode> expResult = new ArrayList<>();
        expResult.add(synsetNode);

        List<SynsetNode> result = parent.getDescendants(maxDistance);
        assertEquals(expResult, result);
    }

    /**
     * Test of getPathToDescendants method, of class SynsetNode.
     */
    @Test
    public void testGetPathToDescendants() {
        int maxDistance = 2;
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);

        SynsetNode node = new SynsetNode("bn:00036129n"); // fox
        node.setParent(parent);

        List<SynsetNode> synsetNodeListFirstPath = new ArrayList<>();
        synsetNodeListFirstPath.add(parent);

        List<SynsetNode> synsetNodeListSecondPath = new ArrayList<>();
        synsetNodeListSecondPath.add(parent);
        synsetNodeListSecondPath.add(synsetNode);

        List<SynsetNodePath> expResult = new ArrayList<>();
        expResult.add(new SynsetNodePath(synsetNodeListFirstPath));
        expResult.add(new SynsetNodePath(synsetNodeListSecondPath));

        List<SynsetNodePath> result = parent.getPathToDescendants(maxDistance, Collections.singleton(node));

        assertEquals(expResult.size(), result.size());

        for (int i = 0; i < expResult.size(); i++) {
            assertTrue(result.get(i).equals(expResult.get(i)));
        }
    }

    /**
     * Test of getDescendantAndSelf method, of class SynsetNode.
     */
    @Test
    public void testGetDescendantAndSelf() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);

        SynsetNode node = new SynsetNode("bn:00036129n"); // fox
        node.setParent(parent);

        List<SynsetNode> expResult = new ArrayList<>();
        expResult.add(synsetNode);
        expResult.add(node);
        expResult.add(parent);

        List<SynsetNode> result = parent.getDescendantAndSelf();
        assertEquals(expResult.size(), result.size());
        assertEquals(expResult, result);
    }

    /**
     * Test of getSelfOrDescendantBySynset method, of class SynsetNode.
     */
    @Test
    public void testGetSelfOrDescendantBySynset() {

        String synsetId = "bn:00036129n";
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);

        SynsetNode node = new SynsetNode("bn:00036129n"); // fox
        node.setParent(parent);

        SynsetNode result = parent.getSelfOrDescendantBySynset(synsetId);
        assertEquals(node, result);
    }

    /**
     * Test of getFirstAncestorWithInstancesDegree method, of class SynsetNode.
     */
    @Test
    public void testGetFirstAncestorWithInstancesDegree() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);
        parent.addInstance(synsetInstance);

        SynsetNode node = new SynsetNode("bn:00036129n"); // fox
        node.setParent(parent);

        int expResult = 1;
        int result = node.getFirstAncestorWithInstancesDegree();
        assertEquals(expResult, result);

    }

    /**
     * Test of getFirstAncestorWithInstances method, of class SynsetNode.
     */
    @Test
    public void testGetFirstAncestorWithInstances() {
        SynsetNode parent = new SynsetNode("bn:00015258n"); // canid
        synsetNode.setParent(parent);
        parent.addInstance(synsetInstance);

        SynsetNode node = new SynsetNode("bn:00036129n"); // fox
        node.setParent(parent);

        SynsetNode result = node.getFirstAncestorWithInstances();
        assertEquals(parent, result);

    }

    /**
     * // * Test of isAncestorOf method, of class SynsetNode. //
     */
//    @Test
//    public void testIsAncestorOf() {
//        System.out.println("isAncestorOf");
//        SynsetNode descendant = null;
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.isAncestorOf(descendant);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeDescendant method, of class SynsetNode.
//     */
//    @Test
//    public void testRemoveDescendant() {
//        System.out.println("removeDescendant");
//        SynsetNode descendant = null;
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.removeDescendant(descendant);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getReferenceSynset method, of class SynsetNode.
//     */
//    @Test
//    public void testGetReferenceSynset() {
//        System.out.println("getReferenceSynset");
//        SynsetNode instance = null;
//        String expResult = "";
//        String result = instance.getReferenceSynset();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSynsets method, of class SynsetNode.
//     */
//    @Test
//    public void testGetSynsets() {
//        System.out.println("getSynsets");
//        SynsetNode instance = null;
//        List<String> expResult = null;
//        List<String> result = instance.getSynsets();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasSynsets method, of class SynsetNode.
//     */
//    @Test
//    public void testHasSynsets() {
//        System.out.println("hasSynsets");
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasSynsets();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasSynsetsDeep method, of class SynsetNode.
//     */
//    @Test
//    public void testHasSynsetsDeep() {
//        System.out.println("hasSynsetsDeep");
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasSynsetsDeep();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasSynset method, of class SynsetNode.
//     */
//    @Test
//    public void testHasSynset() {
//        System.out.println("hasSynset");
//        String synsetId = "";
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasSynset(synsetId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasDescendantSynset method, of class SynsetNode.
//     */
//    @Test
//    public void testHasDescendantSynset() {
//        System.out.println("hasDescendantSynset");
//        String synsetId = "";
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasDescendantSynset(synsetId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of countSynsets method, of class SynsetNode.
//     */
//    @Test
//    public void testCountSynsets() {
//        System.out.println("countSynsets");
//        SynsetNode instance = null;
//        int expResult = 0;
//        int result = instance.countSynsets();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInstances method, of class SynsetNode.
//     */
//    @Test
//    public void testGetInstances() {
//        System.out.println("getInstances");
//        SynsetNode instance = null;
//        Set<SynsetInstance> expResult = null;
//        Set<SynsetInstance> result = instance.getInstances();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasInstances method, of class SynsetNode.
//     */
//    @Test
//    public void testHasInstances() {
//        System.out.println("hasInstances");
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasInstances();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of hasInstancesDeep method, of class SynsetNode.
//     */
//    @Test
//    public void testHasInstancesDeep() {
//        System.out.println("hasInstancesDeep");
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.hasInstancesDeep();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addInstance method, of class SynsetNode.
//     */
//    @Test
//    public void testAddInstance() {
//        System.out.println("addInstance");
//        SynsetInstance instance_2 = null;
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.addInstance(instance_2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTargetFrequency method, of class SynsetNode.
//     */
//    @Test
//    public void testGetTargetFrequency() {
//        System.out.println("getTargetFrequency");
//        Serializable target = null;
//        SynsetNode instance = null;
//        double expResult = 0.0;
//        double result = instance.getTargetFrequency(target);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCombinedTargetFrequency method, of class SynsetNode.
//     */
//    @Test
//    public void testGetCombinedTargetFrequency_Serializable_SynsetNode() {
//        System.out.println("getCombinedTargetFrequency");
//        Serializable target = null;
//        SynsetNode node = null;
//        SynsetNode instance = null;
//        double expResult = 0.0;
//        double result = instance.getCombinedTargetFrequency(target, node);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCombinedTargetFrequency method, of class SynsetNode.
//     */
//    @Test
//    public void testGetCombinedTargetFrequency_Serializable_Collection() {
//        System.out.println("getCombinedTargetFrequency");
//        Serializable target = null;
//        Collection<SynsetNode> node = null;
//        SynsetNode instance = null;
//        double expResult = 0.0;
//        double result = instance.getCombinedTargetFrequency(target, node);
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of generalize method, of class SynsetNode.
//     */
//    @Test
//    public void testGeneralize_SynsetNodeArr() {
//        System.out.println("generalize");
//        SynsetNode[] nodes = null;
//        SynsetNode instance = null;
//        instance.generalize(nodes);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of generalize method, of class SynsetNode.
//     */
//    @Test
//    public void testGeneralize_Collection() {
//        System.out.println("generalize");
//        Collection<SynsetNode> nodes = null;
//        SynsetNode instance = null;
//        instance.generalize(nodes);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPathTo method, of class SynsetNode.
//     */
//    @Test
//    public void testGetPathTo() {
//        System.out.println("getPathTo");
//        SynsetNode targetNode = null;
//        SynsetNode instance = null;
//        SynsetNodePath expResult = null;
//        SynsetNodePath result = instance.getPathTo(targetNode);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDegree method, of class SynsetNode.
//     */
//    @Test
//    public void testGetDegree() {
//        System.out.println("getDegree");
//        SynsetNode instance = null;
//        int expResult = 0;
//        int result = instance.getDegree();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMaxDegree method, of class SynsetNode.
//     */
//    @Test
//    public void testGetMaxDegree() {
//        System.out.println("getMaxDegree");
//        SynsetNode instance = null;
//        int expResult = 0;
//        int result = instance.getMaxDegree();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of prune method, of class SynsetNode.
//     */
//    @Test
//    public void testPrune() {
//        System.out.println("prune");
//        SynsetNode instance = null;
//        boolean expResult = false;
//        boolean result = instance.prune();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toString method, of class SynsetNode.
//     */
//    @Test
//    public void testToString() {
//        System.out.println("toString");
//        SynsetNode instance = null;
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toStringDeep method, of class SynsetNode.
//     */
//    @Test
//    public void testToStringDeep() {
//        System.out.println("toStringDeep");
//        SynsetNode instance = null;
//        String expResult = "";
//        String result = instance.toStringDeep();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
