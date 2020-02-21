/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.pipe.impl;

import java.util.ArrayList;
import java.util.List;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.nlpa.types.SynsetSequence;

/**
 *
 * @author María Novo
 */
public class StringBuffer2SynsetSequencePipeTest {

    String data = "December is hre :), ho ho ho! 🎅 Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on <a href=\"www.xx.com\">here</a>, #xx or @xx.";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private StringBuffer2SynsetSequencePipe instance;

    public StringBuffer2SynsetSequencePipeTest() {
    }

    @Before
    public void setUp() {
        instance = new StringBuffer2SynsetSequencePipe();
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty(instance.getLangProp(), "EN");
    }

    /**
     * Test of getInputType method, of class StringBuffer2SynsetSequencePipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class StringBuffer2SynsetSequencePipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = SynsetSequence.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLangProp method, of class StringBuffer2SynsetSequencePipe.
     */
    @Test
    public void testSetLangProp() {
        String langProp = "language";
        instance.setLangProp(langProp);
    }

    /**
     * Test of getLangProp method, of class StringBuffer2SynsetSequencePipe.
     */
    @Test
    public void testGetLangProp() {
        String expResult = "language";
        String result = instance.getLangProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class StringBuffer2SynsetSequencePipe.
     */
    @Test
    public void testPipe() {
        SynsetSequence expectedSynsetSequence = new SynsetSequence(data);
        List<Pair<String, String>> synsets = new ArrayList<>();
        synsets.add(new Pair("bn:00025645n", "December"));
        synsets.add(new Pair("bn:00006898n", "ho"));
        synsets.add(new Pair("bn:03100869n", "ho ho"));
        synsets.add(new Pair("bn:00006898n", "ho"));
        synsets.add(new Pair("bn:03100869n", "ho ho"));
        synsets.add(new Pair("bn:00006898n", "ho"));
        synsets.add(new Pair("bn:00009394n", "Beat"));
        synsets.add(new Pair("bn:00018836n", "Christmas"));
        synsets.add(new Pair("bn:00018836n", "Christmas days"));
        synsets.add(new Pair("bn:00000086n", "days"));
        synsets.add(new Pair("bn:03149538n", "online"));
        synsets.add(new Pair("bn:00025645n", "Dec"));
        synsets.add(new Pair("bn:00080111n", "Visit"));
        expectedSynsetSequence.setSynsets(synsets);
        expectedSynsetSequence.setFixedText(data);

        Instance expResult = new Instance(expectedSynsetSequence, null, name, source);
        expResult.setProperty(instance.getLangProp(), "EN");
        Instance result = instance.pipe(carrier);

        if (result.getData() instanceof SynsetSequence) {
            SynsetSequence ss = (SynsetSequence) carrier.getData();
            for (Pair<String, String> synset : ss.getSynsets()) {
                System.out.println(synset.getObj1() + "," + synset.getObj2());
            }
        }
        assertEquals(expResult, result);
    }

    /**
     * Test of writeToDisk method, of class StringBuffer2SynsetSequencePipe.
     */
    @Test
    public void testWriteToDisk() {

        String dir = "output/testWriteToDisk.txt";
        instance.writeToDisk(dir);
    }

}
