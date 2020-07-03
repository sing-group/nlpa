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
public class FindEmojiInStringBufferPipeTest {

    String data = "December is hre :-), ho ho ho!🎅Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on #xx or @xx";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    private static String SUPPORTED_LANGUAGE = "EN";
    private static String DEFAULT_EMOJI_PROP = "emoji";
    private static String DEFAULT_POLARITY_PROP = "emojiPolarity";
    private static String DEFAULT_LANG_PROP = "language";

    private static Instance carrier = null;

    private FindEmojiInStringBufferPipe instance;
    private FindEmojiInStringBufferPipe instanceRemove;

    public FindEmojiInStringBufferPipeTest() {
    }

    @Before
    public void setUp() throws Exception {
        instance = new FindEmojiInStringBufferPipe();
        instanceRemove = new FindEmojiInStringBufferPipe(DEFAULT_EMOJI_PROP, true, DEFAULT_LANG_PROP, false, false);
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty(instance.getLangProp(), SUPPORTED_LANGUAGE);
    }

    /**
     * Test of getInputType method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testGetInputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getInputType method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testGetOutputType() {
        Class<?> expResult = StringBuffer.class;
        Class<?> result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEmojiProp method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetEmojiProp() {
        String emojiProp = "";
        instance.setEmojiProp(emojiProp);
    }

    /**
     * test of getEmojiProp method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testGetEmojiProp() {
        String expResult = "emoji";
        String result = instance.getEmojiProp();
        assertEquals(expResult, result);
    }

    /**
     * test of setRemoveEmoji method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetRemoveEmoji_String() {
        String removeEmoji = "yes";
        instance.setRemoveEmoji(removeEmoji);
    }

    /**
     * test of setRemoveEmoji method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetRemoveEmoji_boolean() {
        boolean removeEmoji = true;
        instance.setRemoveEmoji(removeEmoji);
    }

    /**
     * test of getRemoveEmoji method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testGetRemoveEmoji() {
        boolean expResult = false;
        boolean result = instance.getRemoveEmoji();
        assertEquals(expResult, result);
    }

    /**
     * test of setReplaceEmoji method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetReplaceEmoji_String() {
        String replaceEmoji = "no";
        instance.setReplaceEmoji(replaceEmoji);
    }

    /**
     * test of setReplaceEmoji method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetReplaceEmoji_boolean() {
        boolean replaceEmoji = false;
        instance.setReplaceEmoji(replaceEmoji);
    }

    /**
     * test of getReplaceEmoji method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testGetReplaceEmoji() {
        boolean expResult = true;
        boolean result = instance.getReplaceEmoji();
        assertEquals(expResult, result);
    }

    /**
     * test of setCalculatePolarity method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetCalculatePolarity_String() {
        String calculatePolarity = "no";
        instance.setCalculatePolarity(calculatePolarity);
    }

    /**
     * test of setCalculatePolarity method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testSetCalculatePolarity_boolean() {
        boolean calculatePolarity = false;
        instance.setCalculatePolarity(calculatePolarity);
    }

    /**
     * test of getCalculatePolarity method of FindEmojiInStringBufferPipe class
     */
    @Test
    public void testGetCalculatePolarity() {
        boolean expResult = true;
        boolean result = instance.getCalculatePolarity();
        assertEquals(expResult, result);
    }

    /**
     * test of pipe method of FindEmojiInStringBufferPipe class This test checks
     * if emojis were replaced by its meaning Also checks if polarity is
     * calculated correctly
     */
    @Test
    public void testPipe() {
        String expData = "December is hre :-), ho ho ho!Santa_ClausBeat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on #xx or @xx";
        Double expDataPolarity = 0.318;
        Instance expResult = new Instance(new StringBuffer(expData), null, name, source);
        expResult.setProperty(instance.getLangProp(), SUPPORTED_LANGUAGE);
        expResult.setProperty(instance.getEmojiProp(), "🎅");
        expResult.setProperty(DEFAULT_POLARITY_PROP, expDataPolarity);

        Instance result = instance.pipe(carrier);
        assertTrue(expResult.equals(result));
        assertTrue(result.getProperty("emojiPolarity").equals(expDataPolarity));
    }

    /**
     * Test of pipe method of FindEmoticonInStringBufferPipe class This test
     * checks if emojis were removed from the text
     */
    @Test
    public void testPipeRemoveEmoji() {
        String expData = "December is hre :-), ho ho ho!Beat the Christmas days with us and we'll even give you 19% off online until 31 Dec. Visit us on #xx or @xx";
        Instance expResult = new Instance(new StringBuffer(expData), null, name, source);
        expResult.setProperty(DEFAULT_LANG_PROP, "EN");
        expResult.setProperty(instanceRemove.getEmojiProp(), "🎅");

        carrier.setProperty(instanceRemove.getLangProp(), SUPPORTED_LANGUAGE);

        Instance result = instanceRemove.pipe(carrier);
        assertTrue(expResult.equals(result));
    }

    /**
     * test of pipe method of FindEmojiInStringBufferPipe class In case of not
     * supported language, pipe shouldn't do any change to the text and polarity
     * should be 0
     */
    @Test
    public void testPipeNotSupportedLanguage() {

        String noSuppLanguageData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua 🎅.";
        String noSuppLanguageName = "invalidLanguageEmoji.tsms";
        String noSuppLanguageSource = "invalidLanguageEmoji.tsms";
        String noSuppLanguage = "LA";

        Instance noSuppLanguageCarrier = new Instance(new StringBuffer(noSuppLanguageData), null, noSuppLanguageName, noSuppLanguageSource);
        noSuppLanguageCarrier.setProperty(instance.getLangProp(), noSuppLanguage);
        noSuppLanguageCarrier.setProperty(instance.getEmojiProp(), DEFAULT_EMOJI_PROP);

        String expData = noSuppLanguageData;

        Instance expResult = new Instance(new StringBuffer(expData), null, noSuppLanguageName, noSuppLanguageSource);
        expResult.setProperty(instance.getLangProp(), noSuppLanguage);
        expResult.setProperty(instance.getEmojiProp(), "");
        expResult.setProperty(DEFAULT_POLARITY_PROP, 0.0);

        Instance result = instance.pipe(noSuppLanguageCarrier);
        assertTrue(expResult.equals(result));
        assertTrue(result.getProperty("emojiPolarity").equals(0.0));
    }

}
