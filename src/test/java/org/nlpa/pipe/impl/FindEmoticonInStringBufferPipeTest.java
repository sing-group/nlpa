package org.nlpa.pipe.impl;

import static org.junit.Assert.*;

import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rodrigo Currás
 *
 */
public class FindEmoticonInStringBufferPipeTest {

    String data = "December is hre :), ho ho ho! Santa_Claus Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on #xx or @xx";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static String SUPPORTED_LANGUAGE = "EN";
    private static String DEFAULT_EMOTICON_PROP = "emoticon";
    private static String DEFAULT_POLARITY_PROP = "emoticonPolarity";
    private static String DEFAULT_LANG_PROP = "language";

    private static Instance carrier = null;
    private FindEmoticonInStringBufferPipe instance;
    private FindEmoticonInStringBufferPipe instanceRemove;

    public FindEmoticonInStringBufferPipeTest() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new FindEmoticonInStringBufferPipe();
        instanceRemove = new FindEmoticonInStringBufferPipe(DEFAULT_EMOTICON_PROP, true, DEFAULT_LANG_PROP, false, false);
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty(instance.getLangProp(), SUPPORTED_LANGUAGE);
    }

    /**
     * Test of getInputType method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEmoticonProp of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testSetEmoticonProp() {
        String emoticonProp = "";
        instance.setEmoticonProp(emoticonProp);
    }

    /**
     * Test of getEmoticonProp of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testGetEmoticonProp() {
        String expResult = "emoticon";
        String result = instance.getEmoticonProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRemoveEmoticon method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testSetRemoveEmoticon_String() {
        String removeEmoticon = "yes";
        instance.setRemoveEmoticon(removeEmoticon);
    }

    /**
     * Test of setRemoveEmoticon method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testSetRemoveEmoticon_boolean() {
        Boolean removeEmoticon = false;
        instance.setRemoveEmoticon(removeEmoticon);
    }

    /**
     * Test of getRemoveEmoticon method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testGetRemoveEmoticon() {
        boolean expResult = false;
        boolean result = instance.getRemoveEmoticon();
        assertEquals(expResult, result);

    }

    /**
     * Test of setReplaceEmoticon method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testSetReplaceEmoticon_String() {
        String replaceEmoticon = "no";
        instance.setReplaceEmoticon(replaceEmoticon);
    }

    /**
     * Test of setReplaceEmoticon method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testSetReplaceEmoticon_boolean() {
        Boolean replaceEmoticon = false;
        instance.setReplaceEmoticon(replaceEmoticon);
    }

    /**
     * Test of getReplaceEmoticon method of FindEmoticonInStringBufferPipe class
     */
    @Test
    public void testGetReplaceEmoticon() {
        boolean expResult = true;
        boolean result = instance.getReplaceEmoticon();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCalculatePolarity method of FindEmoticonInStringBufferPipe
     * class
     */
    @Test
    public void testSetCalculatePolarity_String() {
        String calculatePolarity = "no";
        instance.setCalculatePolarity(calculatePolarity);
    }

    /**
     * Test of setCalculatePolarity method of FindEmoticonInStringBufferPipe
     * class
     */
    @Test
    public void testSetCalculatePolarity_boolean() {
        boolean calculatePolarity = false;
        instance.setCalculatePolarity(calculatePolarity);
    }

    /**
     * Test of getCalculatePolarity method of FindEmoticonInStringBufferPipe
     * class
     */
    @Test
    public void testGetCalculatePolarity() {
        boolean expResult = true;
        boolean result = instance.getReplaceEmoticon();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method of FindEmoticonInStringBufferPipe class This test
     * checks if emoticons were replaced by its meaning Also checks if polarity
     * is calculated correctly
     */
    @Test
    public void testPipe() {
        String expData = "December is hre happy, ho ho ho! Santa_Claus Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on #xx or @xx";
        Double expDataPolarity = 0.657;
        Instance expResult = new Instance(new StringBuffer(expData), null, name, source);
        expResult.setProperty(instance.getLangProp(), SUPPORTED_LANGUAGE);
        expResult.setProperty(instance.getEmoticonProp(), DEFAULT_EMOTICON_PROP);

        Instance result = instance.pipe(carrier);
        System.out.println("exp: " + expResult.getData().toString());
        System.out.println("res: " + result.getData().toString());
        assertTrue(expResult.getData().toString().equals(result.getData().toString()));
        assertTrue(result.getProperty("emoticonPolarity").equals(expDataPolarity));
    }

    /**
     * Test of pipe method of FindEmoticonInStringBufferPipe class This test
     * checks if emoticons were removed from the text
     */
    @Test
    public void testPipeRemoveEmoticon() {
        String expectedData = "December is hre , ho ho ho! Santa_Claus Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on #xx or @xx";
        Instance expResult = new Instance(new StringBuffer(expectedData), null, name, source);
        expResult.setProperty(instanceRemove.getLangProp(), SUPPORTED_LANGUAGE);
        expResult.setProperty(instanceRemove.getEmoticonProp(), DEFAULT_EMOTICON_PROP);

        Instance result = instanceRemove.pipe(carrier);
        System.out.println("exp: " + expResult.getData().toString());
        System.out.println("res: " + result.getData().toString());
        assertTrue(expResult.getData().toString().equals(result.getData().toString()));
    }

    /**
     * Test of pipe method of FindEmoticonInStringBufferPipe class In case of
     * not supported language, pipe shouldn't do any change to the text and
     * polarity should be 0
     */
    @Test
    public void testPipeNotSupportedLanguage() {
        String noSuppLanguageData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua :).";
        String noSuppLanguageName = "invalidLanguageEmoticon.tsms";
        String noSuppLanguageSource = "invalidLanguageEmoticon.tsms";
        String noSuppLanguage = "LA";

        Instance noSuppLanguageCarrier = new Instance(new StringBuffer(noSuppLanguageData), null, noSuppLanguageName, noSuppLanguageSource);
        noSuppLanguageCarrier.setProperty(instance.getLangProp(), noSuppLanguage);
        noSuppLanguageCarrier.setProperty(instance.getEmoticonProp(), DEFAULT_EMOTICON_PROP);

        String expData = noSuppLanguageData;

        Instance expResult = new Instance(new StringBuffer(expData), null, noSuppLanguageName, noSuppLanguageSource);
        expResult.setProperty(instance.getLangProp(), noSuppLanguage);
        expResult.setProperty(instance.getEmoticonProp(), DEFAULT_EMOTICON_PROP);

        Instance result = instance.pipe(noSuppLanguageCarrier);
        assertTrue(expResult.getData().toString().equals(result.getData().toString()));
        assertTrue(result.getProperty(DEFAULT_POLARITY_PROP).equals(0.0));

    }

}
