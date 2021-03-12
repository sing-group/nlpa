/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import java.util.ArrayList;
import java.util.List;
import org.bdp4j.types.Dataset;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.nlpa.matchers.IsEqualToInstance.containsInstancesInOrder;
import org.nlpa.transformers.dataset.tree.SynsetNode;
import org.nlpa.transformers.dataset.tree.SynsetNodeBuilder;
import org.nlpa.transformers.dataset.tree.SynsetNodeTest;
import weka.core.Attribute;
import weka.core.Instance;

/**
 *
 * @author María Novo
 */
public class eSDRSTest {

    private eSDRS esdrs;

    public eSDRSTest() {
    }

    @Before
    public void setUp() {
        esdrs = new eSDRS(2, Dataset.COMBINE_SUM, 0.90);
    }

    /**
     * Test of getMaxDegree method, of class eSDRS.
     */
    @Test
    public void testGetMaxDegree() {
        int expResult = 2;
        int result = esdrs.getMaxDegree();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMaxDegree method, of class eSDRS.
     */
    @Test
    public void testSetMaxDegree() {
        int maxDegree = 3;
        esdrs.setMaxDegree(maxDegree);
    }

    /**
     * Test of getRequiredSimilarity method, of class eSDRS.
     */
    @Test
    public void testGetRequiredSimilarity() {
        double expResult = 0.90;
        double result = esdrs.getRequiredSimilarity();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setRequiredSimilarity method, of class eSDRS.
     */
    @Test
    public void testSetRequiredSimilarity() {
        double matchRate = 0.95;
        esdrs.setRequiredSimilarity(matchRate);
    }

    /**
     * Test of getCombineOperator method, of class eSDRS.
     */
    @Test
    public void testGetCombineOperator() {
        Dataset.CombineOperator expResult = Dataset.COMBINE_SUM;
        Dataset.CombineOperator result = esdrs.getCombineOperator();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCombineOperator method, of class eSDRS.
     */
    @Test
    public void testSetCombineOperator() {
        Dataset.CombineOperator combineOperator = Dataset.COMBINE_OR;
        esdrs.setCombineOperator(combineOperator);
    }

    @Test
    public void test() {
        List<SynsetNode> trees = SynsetNodeBuilder.buildTrees(SynsetNodeTest.class.getResourceAsStream("test.tree"));

        for (SynsetNode tree : trees) {
            System.out.println("BEFORE");
            System.out.println(tree.toStringDeep());
            System.out.println("AFTER");
            this.esdrs.generalizeVertically(tree, "s");
            System.out.println(tree.toStringDeep());
        }
    }

    /**
     * Test of transformTemplate method, of class eSDRS.
     */
    @Test
    public void testTransformTemplate() {
        List<String> target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");

        /* Expected dataset */
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00007731n"));
        attributes.add(new Attribute("bn:00010309n"));
        attributes.add(new Attribute("bn:00036129n"));
        attributes.add(new Attribute("target", target_values));
        Dataset expDataset = new Dataset("Expected dataset", attributes, 0);

        Instance instance = expDataset.createDenseInstance();

        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 2d);
        instance.setValue(3, 0d);
        instance.setValue(4, 0d);

        instance = expDataset.createDenseInstance();
        instance.setValue(0, 2d);
        instance.setValue(1, 0d);
        instance.setValue(2, 2d);
        instance.setValue(3, 0d);
        instance.setValue(4, 0d);

        instance = expDataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 0d);
        instance.setValue(4, 1d);
        /* End expected dataset */

 /* Dataset to test */
        attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00049156n"));
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00007731n"));
        attributes.add(new Attribute("bn:00010309n"));
        attributes.add(new Attribute("bn:00053079n"));
        attributes.add(new Attribute("bn:00036129n"));
        attributes.add(new Attribute("target", target_values));
        Dataset dataset = new Dataset("Dataset to represent", attributes, 0);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 1d);
        instance.setValue(3, 1d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);
        instance.setValue(6, 0d);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 0d);
        instance.setValue(3, 1d);
        instance.setValue(4, 1d);
        instance.setValue(5, 0d);
        instance.setValue(6, 0d);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 0d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);
        instance.setValue(6, 1d);

        Dataset result = esdrs.transformTemplate(dataset);

        assertEquals(expDataset.getAttributes(), result.getAttributes());
        assertThat(expDataset.getInstances(), containsInstancesInOrder(result.getInstances()));

    }

    @Test
    public void testTransformTemplateWithDifferentClases() {
        List<String> target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");

        /* Expected dataset */
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00049156n"));
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00007731n"));
        attributes.add(new Attribute("bn:00010309n"));
        attributes.add(new Attribute("bn:00036129n"));
        attributes.add(new Attribute("target", target_values));
        Dataset expDataset = new Dataset("Expected dataset", attributes, 0);

        Instance instance = expDataset.createDenseInstance();

        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 1d);
        instance.setValue(3, 1d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);

        instance = expDataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 2d);
        instance.setValue(2, 0d);
        instance.setValue(3, 1d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);

        instance = expDataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 0d);
        instance.setValue(4, 0d);
        instance.setValue(5, 1d);

        instance = expDataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 0d);
        instance.setValue(4, 1d);
        instance.setValue(5, 1d);
        /* End expected dataset */

 /* Dataset to test */
        attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00049156n"));
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00007731n"));
        attributes.add(new Attribute("bn:00010309n"));
        attributes.add(new Attribute("bn:00053079n"));
        attributes.add(new Attribute("bn:00036129n"));
        attributes.add(new Attribute("target", target_values));
        Dataset dataset = new Dataset("Dataset to represent", attributes, 0);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 1d);
        instance.setValue(3, 1d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);
        instance.setValue(6, 0d);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 0d);
        instance.setValue(3, 1d);
        instance.setValue(4, 1d);
        instance.setValue(5, 0d);
        instance.setValue(6, 0d);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 0d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);
        instance.setValue(6, 1d);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 0d);
        instance.setValue(4, 0d);
        instance.setValue(5, 1d);
        instance.setValue(6, 1d);

        Dataset result = esdrs.transformTemplate(dataset);

        assertEquals(expDataset.getAttributes(), result.getAttributes());
        assertThat(expDataset.getInstances(), containsInstancesInOrder(result.getInstances()));

    }

    @Test
    public void testTransformTemplateHypernymWithDifferentClases() {
        List<String> target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");

        /* Expected dataset */
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00009677n"));
        attributes.add(new Attribute("target", target_values));
        Dataset expDataset = new Dataset("Dataset to represent", attributes, 0);

        Instance instance = expDataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 1d);

        instance = expDataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 0d);
        /* End expected dataset */

 /* Dataset to test */
        attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00009677n"));
        attributes.add(new Attribute("target", target_values));
        Dataset dataset = new Dataset("Dataset to represent", attributes, 0);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 1d);

        instance = dataset.createDenseInstance();
        instance.setValue(0, 1d);
        instance.setValue(1, 1d);
        instance.setValue(2, 0d);

        Dataset result = esdrs.transformTemplate(dataset);

        assertEquals(expDataset.getAttributes(), result.getAttributes());
        assertThat(expDataset.getInstances(), containsInstancesInOrder(result.getInstances()));

    }
}
