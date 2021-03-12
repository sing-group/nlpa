/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bdp4j.types.Dataset;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.nlpa.matchers.IsEqualToInstance.containsInstancesInOrder;
import weka.core.Attribute;
import weka.core.Instance;

/**
 *
 * @author María Novo
 */
public class DatasetFeatureRepresentationTest {

    private DatasetFeatureRepresentation dfr;
    Dataset originalDataset;
    Dataset generalizatedTrainingDataset;
    Dataset testingDataset;
    Dataset trainingDataset;

    public DatasetFeatureRepresentationTest() {
    }

    @Before
    public void setUp() throws FileNotFoundException, IOException {
        List<String> target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00093430v"));
        attributes.add(new Attribute("bn:00049156n"));
        attributes.add(new Attribute("bn:00054316n"));
        attributes.add(new Attribute("bn:00018748n"));
        attributes.add(new Attribute("bn:00007731n"));
        attributes.add(new Attribute("bn:00052061n"));
        attributes.add(new Attribute("bn:00053079n"));
        attributes.add(new Attribute("bn:00029424n"));
        attributes.add(new Attribute("bn:00036129n"));
        attributes.add(new Attribute("bn:00090408v"));
        attributes.add(new Attribute("bn:00056068n"));
        attributes.add(new Attribute("bn:00033987n"));
        attributes.add(new Attribute("bn:00004222n"));
        attributes.add(new Attribute("bn:00114778r"));
        attributes.add(new Attribute("bn:00084231v"));
        attributes.add(new Attribute("bn:00023542n"));
        attributes.add(new Attribute("bn:00036188n"));
        attributes.add(new Attribute("bn:00016606n"));
        attributes.add(new Attribute("bn:00110606a"));
        attributes.add(new Attribute("bn:00031965n"));
        attributes.add(new Attribute("bn:00015258n"));
        attributes.add(new Attribute("bn:13674300v"));
        attributes.add(new Attribute("bn:00015267n"));
        attributes.add(new Attribute("bn:00114491r"));
        attributes.add(new Attribute("bn:00081469n"));
        attributes.add(new Attribute("bn:00100895a"));
        attributes.add(new Attribute("bn:00084331v"));
        attributes.add(new Attribute("bn:00030950n"));
        attributes.add(new Attribute("bn:00047172n"));
        attributes.add(new Attribute("bn:00114535r"));
        attributes.add(new Attribute("bn:00022996n"));
        attributes.add(new Attribute("bn:00003095n"));
        attributes.add(new Attribute("bn:00099558a"));
        attributes.add(new Attribute("bn:00058006n"));
        attributes.add(new Attribute("bn:03149538n"));
        attributes.add(new Attribute("bn:00050884n"));
        attributes.add(new Attribute("bn:00097846a"));
        attributes.add(new Attribute("bn:00102850a"));
        attributes.add(new Attribute("bn:00004605n"));
        attributes.add(new Attribute("bn:00098274a"));
        attributes.add(new Attribute("bn:00026191n"));
        attributes.add(new Attribute("bn:00054126n"));
        attributes.add(new Attribute("bn:00107825a"));
        attributes.add(new Attribute("bn:00059945n"));
        attributes.add(new Attribute("bn:00071570n"));
        attributes.add(new Attribute("target", target_values));
        originalDataset = new Dataset("Dataset to represent", attributes, 0);

        Instance instance = originalDataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 1d);
        instance.setValue(4, 1d);
        instance.setValue(5, 0d);
        instance.setValue(6, 0d);
        instance.setValue(7, 0d);
        instance.setValue(8, 0d);
        instance.setValue(9, 0d);
        instance.setValue(10, 0d);
        instance.setValue(11, 0d);
        instance.setValue(12, 0d);
        instance.setValue(13, 0d);
        instance.setValue(14, 0d);
        instance.setValue(15, 0d);
        instance.setValue(16, 0d);
        instance.setValue(17, 0d);
        instance.setValue(18, 0d);
        instance.setValue(19, 0d);
        instance.setValue(20, 0d);
        instance.setValue(21, 0d);
        instance.setValue(22, 0d);
        instance.setValue(23, 0d);
        instance.setValue(24, 0d);
        instance.setValue(25, 0d);
        instance.setValue(26, 0d);
        instance.setValue(27, 0d);
        instance.setValue(28, 0d);
        instance.setValue(29, 0d);
        instance.setValue(30, 0d);
        instance.setValue(31, 0d);
        instance.setValue(32, 0d);
        instance.setValue(33, 0d);
        instance.setValue(34, 0d);
        instance.setValue(35, 0d);
        instance.setValue(36, 0d);
        instance.setValue(37, 0d);
        instance.setValue(38, 0d);
        instance.setValue(39, 0d);
        instance.setValue(40, 0d);
        instance.setValue(41, 0d);
        instance.setValue(42, 0d);
        instance.setValue(43, 0d);
        instance.setValue(44, 0d);
        instance.setValue(45, 0d);
        instance = originalDataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 1d);
        instance.setValue(4, 0d);
        instance.setValue(5, 1d);
        instance.setValue(6, 1d);
        instance.setValue(7, 1d);
        instance.setValue(8, 0d);
        instance.setValue(9, 0d);
        instance.setValue(10, 0d);
        instance.setValue(11, 0d);
        instance.setValue(12, 0d);
        instance.setValue(13, 0d);
        instance.setValue(14, 0d);
        instance.setValue(15, 0d);
        instance.setValue(16, 0d);
        instance.setValue(17, 0d);
        instance.setValue(18, 0d);
        instance.setValue(19, 0d);
        instance.setValue(20, 0d);
        instance.setValue(21, 0d);
        instance.setValue(22, 0d);
        instance.setValue(23, 0d);
        instance.setValue(24, 0d);
        instance.setValue(25, 0d);
        instance.setValue(26, 0d);
        instance.setValue(27, 0d);
        instance.setValue(28, 0d);
        instance.setValue(29, 0d);
        instance.setValue(30, 0d);
        instance.setValue(31, 0d);
        instance.setValue(32, 0d);
        instance.setValue(33, 0d);
        instance.setValue(34, 0d);
        instance.setValue(35, 0d);
        instance.setValue(36, 0d);
        instance.setValue(37, 0d);
        instance.setValue(38, 0d);
        instance.setValue(39, 0d);
        instance.setValue(40, 0d);
        instance.setValue(41, 0d);
        instance.setValue(42, 0d);
        instance.setValue(43, 0d);
        instance.setValue(44, 0d);
        instance.setValue(45, 0d);
        Dataset[] stratifiedDataset = originalDataset.split(true, 20, 80);
        testingDataset = stratifiedDataset[0];
        trainingDataset = stratifiedDataset[1];

        eSDRS gESDRS = new eSDRS();
        //Generalizated training dataset
        generalizatedTrainingDataset = gESDRS.transform(trainingDataset);
        dfr = new DatasetFeatureRepresentation(generalizatedTrainingDataset);
    }

    /**
     * Test of getFeaturesDataset method, of class DatasetFeatureRepresentation.
     */
    @Test
    public void testGetFeaturesDataset() {
        Dataset expResult = generalizatedTrainingDataset;
        Dataset result = dfr.getFeaturesDataset();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFeaturesDataset method, of class DatasetFeatureRepresentation.
     */
    @Test
    public void testSetFeaturesDataset() {
        dfr.setFeaturesDataset(generalizatedTrainingDataset);
    }

    /**
     * Test of getCombineOperator method, of class DatasetFeatureRepresentation.
     */
    @Test
    public void testGetCombineOperator() {
        Dataset.CombineOperator expResult = Dataset.COMBINE_SUM;
        Dataset.CombineOperator result = dfr.getCombineOperator();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCombineOperator method, of class DatasetFeatureRepresentation.
     */
    @Test
    public void testSetCombineOperator() {
        Dataset.CombineOperator combineOperator = Dataset.COMBINE_SUM;
        dfr.setCombineOperator(combineOperator);
    }

    /**
     * Test of transformTemplate method, of class DatasetFeatureRepresentation.
     *
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testTransformTemplate() throws FileNotFoundException, IOException {

        List<String> target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("bn:00093430v"));
        attributes.add(new Attribute("bn:00049156n"));
        attributes.add(new Attribute("bn:00054316n"));
        attributes.add(new Attribute("bn:00007731n"));
        attributes.add(new Attribute("bn:00062164n"));
        attributes.add(new Attribute("bn:00036129n"));
        attributes.add(new Attribute("bn:00090408v"));
        attributes.add(new Attribute("bn:00056068n"));
        attributes.add(new Attribute("bn:00033987n"));
        attributes.add(new Attribute("bn:00004222n"));
        attributes.add(new Attribute("bn:00114778r"));
        attributes.add(new Attribute("bn:00084231v"));
        attributes.add(new Attribute("bn:00023542n"));
        attributes.add(new Attribute("bn:00036188n"));
        attributes.add(new Attribute("bn:00016606n"));
        attributes.add(new Attribute("bn:00110606a"));
        attributes.add(new Attribute("bn:00031965n"));
        attributes.add(new Attribute("bn:00015258n"));
        attributes.add(new Attribute("bn:13674300v"));
        attributes.add(new Attribute("bn:00015267n"));
        attributes.add(new Attribute("bn:00114491r"));
        attributes.add(new Attribute("bn:00081469n"));
        attributes.add(new Attribute("bn:00100895a")); 
        attributes.add(new Attribute("bn:00084331v"));
        attributes.add(new Attribute("bn:00030950n"));
        attributes.add(new Attribute("bn:00047172n"));
        attributes.add(new Attribute("bn:00114535r"));
        attributes.add(new Attribute("bn:00022996n"));
        attributes.add(new Attribute("bn:00003095n"));
        attributes.add(new Attribute("bn:00099558a"));
        attributes.add(new Attribute("bn:00058006n"));
        attributes.add(new Attribute("bn:03149538n"));
        attributes.add(new Attribute("bn:00050884n"));
        attributes.add(new Attribute("bn:00097846a"));
        attributes.add(new Attribute("bn:00102850a"));
        attributes.add(new Attribute("bn:00004605n"));
        attributes.add(new Attribute("bn:00098274a"));
        attributes.add(new Attribute("bn:00026191n"));
        attributes.add(new Attribute("bn:00054126n"));
        attributes.add(new Attribute("bn:00107825a"));
        attributes.add(new Attribute("bn:00059945n"));
        attributes.add(new Attribute("bn:00071570n"));
        attributes.add(new Attribute("target", target_values));
        Dataset expInstancesDataset = new Dataset("result ", attributes, 0);
        

        Instance instance = expInstancesDataset.createDenseInstance();
        instance.setValue(0, 0d);
        instance.setValue(1, 0d);
        instance.setValue(2, 0d);
        instance.setValue(3, 1d);
        instance.setValue(4, 0d);
        instance.setValue(5, 0d);
        instance.setValue(6, 0d);
        instance.setValue(7, 0d);
        instance.setValue(8, 0d);
        instance.setValue(9, 1d);
        instance.setValue(10, 0d);
        instance.setValue(11, 0d);
        instance.setValue(12, 0d);
        instance.setValue(13, 0d);
        instance.setValue(14, 0d);
        instance.setValue(15, 0d);
        instance.setValue(16, 0d);
        instance.setValue(17, 0d);
        instance.setValue(18, 0d);
        instance.setValue(19, 0d);
        instance.setValue(20, 0d);
        instance.setValue(21, 0d);
        instance.setValue(22, 0d);
        instance.setValue(23, 0d);
        instance.setValue(24, 0d);
        instance.setValue(25, 0d);
        instance.setValue(26, 0d);
        instance.setValue(27, 0d);
        instance.setValue(28, 0d);
        instance.setValue(29, 0d);
        instance.setValue(30, 0d);
        instance.setValue(31, 0d);
        instance.setValue(32, 0d);
        instance.setValue(33, 0d);
        instance.setValue(34, 0d);
        instance.setValue(35, 0d);
        instance.setValue(36, 0d);
        instance.setValue(37, 0d);
        instance.setValue(38, 0d);
        instance.setValue(39, 0d);
        instance.setValue(40, 0d);
        instance.setValue(41, 0d);
        instance.setValue(42, 0d);

        Dataset result = dfr.transformTemplate(testingDataset);
        assertEquals(expInstancesDataset.getAttributes(), result.getAttributes());
        assertThat(expInstancesDataset.getInstances(), containsInstancesInOrder(result.getInstances()));
    }

}
