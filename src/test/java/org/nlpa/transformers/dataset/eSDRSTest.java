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
        esdrs = new eSDRS(2, Dataset.COMBINE_SUM, false, 0.90);
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
     * Test of getMatchRate method, of class eSDRS.
     */
    @Test
    public void testGetMatchRate() {
        double expResult = 0.90;
        double result = esdrs.getMatchRate();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setMatchRate method, of class eSDRS.
     */
    @Test
    public void testSetMatchRate() {
        double matchRate = 0.95;
        esdrs.setMatchRate(matchRate);
    }

    /**
     * Test of isGenerateFiles method, of class eSDRS.
     */
    @Test
    public void testIsGenerateFiles() {
        boolean expResult = false;
        boolean result = esdrs.isGenerateFiles();
        assertEquals(expResult, result);
    }

    /**
     * Test of setGenerateFiles method, of class eSDRS.
     */
    @Test
    public void testSetGenerateFiles() {
        boolean generateFiles = false;
        esdrs.setGenerateFiles(generateFiles);
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

    /**
     * Test of getMaxThreads method, of class eSDRS.
     */
    @Test
    public void testGetMaxThreads() {
        int expResult = 120;
        int result = esdrs.getMaxThreads();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMaxThreads method, of class eSDRS.
     */
    @Test
    public void testSetMaxThreads() {
        int maxThreads = 20;
        esdrs.setMaxThreads(maxThreads);
    }

    /**
     * Test of isLimitMaxThreads method, of class eSDRS.
     */
    @Test
    public void testIsLimitMaxThreads() {
        boolean expResult = false;
        boolean result = esdrs.isLimitMaxThreads();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLimitMaxThreads method, of class eSDRS.
     */
    @Test
    public void testSetLimitMaxThreads() {
        boolean limitMaxThreads = false;
        esdrs.setLimitMaxThreads(limitMaxThreads);
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
