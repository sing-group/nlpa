/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;
import org.bdp4j.util.EBoolean;

import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bdp4j.pipe.Pipe;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;


/**
 * This pipe finds and eventually drops emoticons The data of the instance
 * should contain a StringBuffer
 *
 * References: https://es.wikipedia.org/wiki/Anexo:Emoticonos. https://en.wikipedia.org/wiki/List_of_emoticons 
 *              Thanks to Wikipedia 
 * UTF16 encoder: https://convertcodes.com/utf16-encode-decode-convert-string/. Thanks to Convert Codes
 * 
 * @author Reyes Pavón
 * @author Rosalía Laza
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class FindEmoticonInStringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(FindEmoticonInStringBufferPipe.class);

   /* private static HashSet<String> emoticons=new HashSet<>(Arrays.asList(new String[]{

        //Sideways Latin-only emoticons
        "\u003a\u2011\u0029", ":)", ":-]", ":]", ":-3", ":3", ":->", ":>","8-)", "8)", ":-}", ":}", "\u003a\u006f\u0029",
        ":c)", ":^)", "=]", "=)", //Smiley or happy face.

        "\u003a\u2011\u0044", ":D", "\u0038\u2011\u0044", "8D", "\u0078\u2011\u0044", "xD", "\u0058\u2011\u0044", "XD", "=D", "=3", "B^D", //Laughing, big grin, laugh with glasses, or wide-eyed surprise
        ":-))", //Very happy or double chin
        "\u003a\u2011\u0028", ":(", "\u003a\u2011\u0063", ":c", "\u003a\u2011\u003c", ":<", "\u003a\u2011\u005b", ":[", ":-||", ">:[", ":{", ":@", ">:(", //Frown, sad, angry, pouting
        "\u003a\u0027\u2011\u0028", ":'(", //Crying
        ":-))", //Tears of happiness
        "\u0044\u2011\u0027\u003a", "D:<", "D:", "D8", "D;", "D=", "DX", //Horror, disgust, sadness, great dismay (right to left)
        "\u003a\u2011\u004f", ":O", "\u003a\u2011\u006f", "\u003a\u006f", ":-0", "\u0038\u2011\u0030", ">:O", //Surprise, shock, yawn
        ":-*", ":*", ":×", //Kiss
        "\u003b\u2011\u0029", ";)", "*-)", "*)", "\u003b\u2011\u005d", ";]", ";^)", "\u003a\u2011\u002c", ";D", //Wink, smirk
        "\u003a\u2011\u0050", ":P", "\u0058\u2011\u0050", "XP", "\u0078\u2011\u0070", "xp", "\u003a\u2011\u0070", ":p", "\u003a\u2011\u00de", "\u003a\u00de", "\u003a\u2011\u00fe", "\u003a\u00fe", "\u003a\u2011\u0062", ":b", "d:", "=p", ">:P", //Tongue sticking out, cheeky/playful, blowing a raspberry
        "\u003a\u2011\u002f", ":/", "\u003a\u2011\u002e", ">:\\", ">:/", ":\\", "=/", "=\\", ":L", "=L", ":S", //Skeptical, annoyed, undecided, uneasy, hesitant
        "\u003a\u2011\u007c", ":|",  //Straight face no expression, indecision
        ":$", "://)", "://3", //Embarrassed, blushing
        "\u003a\u2011\u0058", ":X", "\u003a\u2011\u0023", ":#", "\u003a\u2011\u0026", ":&", //Sealed lips or wearing braces, tongue-tied
        "\u004f\u003a\u2011\u0029", "O:)", "\u0030\u003a\u2011\u0033", "0:3", "\u0030\u003a\u2011\u0029", "0:)", "0;^)", //Angel, saint, innocent
        "\u003e\u003a\u2011\u0029", ">:)", "\u007d\u003a\u2011\u0029", "}:)", "\u0033\u003a\u2011\u0029", "3:)", ">;)", ">:3", ">;3", //Evil, devilish
        "\u007c\u003b\u2011\u0029", "\u007c\u2011\u004f", //Cool, bored/yawning
        "\u003a\u2011\u004a", //Tongue-in-cheek
        "\u0023\u2011\u0029", //Partied all night
        "\u0025\u2011\u0029","\u0025\u0029", //Drunk, confused
        "\u003a\u2011\u0023\u0023\u0023\u002e\u002e", ":###..", //Being sick
        "\u003c\u003a\u2011\u007c", //Dumb, dunce-like
        "',:-|", "',:-l", //Scepticism, disbelief, or disapproval

        //Sideways Latin-only single-line art and portraits
        "@};-", "@}->--", "\u0040\u007d\u2011\u003b\u2011\u0027\u2011\u2011\u2011", "\u0040\u003e\u2011\u2011\u003e\u2011\u2011", //Rose
        "\u0035\u003a\u2011\u0029","\u007e\u003a\u2011\u005c\u005c", //Elvis Presley
        "\u002a\u003c\u007c\u003a\u2011\u0029", //Santa Claus
        "\u007e\u0028\u005f\u0038\u005e\u0028\u0049\u0029", //Homer Simpson
        "\u003d\u003a\u006f\u005d", //Bill Clinton
        "7:^]","\u002c\u003a\u2011\u0029", //Ronald Reagan
        "</3", "<\\3", //Broken heart
        "<3", //heart

        //Upright Latin-only emoticons and single-line art and portraits
        "><>", "\u003c\u002a\u0029\u0029\u0029\u2011\u007b","><(((*>", //Fish, something's fishy, Christian fish
        "\u005c\u005c\u006f\u002f", //Cheer "Yay, yay."
        "\u002a\u005c\u005c\u0030\u002f\u002a", //Cheerleader
        "\u002f\u002f\u0030\u2011\u0030\u005c\u005c", //John Lennon
        "v.v", //Horror, disgust, sadness, great dismay
        "\u004f\u005f\u004f", "\u006f\u2011\u006f", "\u004f\u005f\u006f", "\u006f\u005f\u004f", "\u006f\u005f\u006f", "\u004f\u002d\u004f", //Surprise, shock, yawn
        ">.<", //Skeptical, annoyed, undecided, uneasy, hesitant
        "^5", "\u006f\u002f\u005c\u005c\u006f", ">_>^", "^<_<", //High five

        //Upright Unicode-incorporating emoticons and single-line art
        "\u04fd\u0064\u0332\u0305\u0061\u0332\u0305\u0072\u0332\u0305\u0077\u0332\u0305\u0069\u0332\u0305\u0273\u0332\u0305\u1557", 
        "\u04fc\u0064\u0332\u0305\u0061\u0332\u0305\u0072\u0332\u0305\u0077\u0332\u0305\u0069\u0332\u0305\u0273\u0332\u0305\u1557", 
        "\u04fd\u0065\u0332\u0305\u0076\u0332\u0305\u006f\u0332\u0305\u006c\u0332\u0305\u0075\u0332\u0305\u0074\u0332\u0305\u0069\u0332\u0305\u006f\u0332\u0305\u0273\u0332\u0305\u1557", 
        "\u04fc\u0065\u0332\u0305\u0076\u0332\u0305\u006f\u0332\u0305\u006c\u0332\u0305\u0075\u0332\u0305\u0074\u0332\u0305\u0069\u0332\u0305\u006f\u0332\u0305\u0273\u0332\u0305\u1557", //Darwin fish / Evolution fish
        
        "(\u0020\u0361\u00b0\u0020\u035c\u0296\u0020\u0361\u00b0)", //The "Lenny Face", named and popularized on 4chan. Used mostly to suggest 
                    //mischief, imply sexual innuendo or a second hidden meaning behind a sentence, 
                    //or is pasted over and over to spam online discussions

        //Kaomoji faces
        "(>_<)", "(>_<)>", //Troubled
        "(';')", //Baby
		"\u0028\u005e\u005e\u309e", "(^_^;)","(-_-;)", "(~_~;)(・.・;)", "(・_・;)", "(・・;)^^; ", "^_^;","(#^.^#)","(^ ^;)", //Nervous, embrarrassed, trouble, shy, swat drop
		"\u0028\u005e\u002e\u005e\u0029\u0079\u002d\u002e\u006f\u25cb","\u0028\u002d\u002e\u002d\u0029\u0079\u002d\u00b0\u00b0\u00b0", // Smoking
		"(-_-)zzz", //Sleeping
		"(^_-)","(^_-)-☆", //Wink
		"((+_+))","(+o+)", "\u0028\u00b0\u00b0\u0029", "\u0028\u00b0\u002d\u00b0\u0029\u0028\u00b0\u002e\u00b0\u0029", "\u0028\u00b0\u005f\u00b0\u0029","\u0028\u00b0\u005f\u00b0\u003e\u0029","\u0028\u00b0\u30ec\u00b0\u0029", //Confused
		"(o|o)", //Ultraman
		"<(｀^´)>",
		"^_^", "\u0028\u00b0\u006f\u00b0\u0029", "(^_^)/", "\u0028\u005e\u004f\u005e\u0029\uff0f", "\u0028\u005e\u006f\u005e\u0029\uff0f", "\u0028\u005e\u005e\u0029\u002f\u0028\u2267\u2207\u2266\u0029\u002f", "\u0028\u002f\u25d5\u30ee\u25d5\u0029\u002f","\u0028\u005e\u006f\u005e\u0029\u4e3f", "\u2229\u0028\u00b7\u03c9\u00b7\u0029\u2229", "\u0028\u00b7\u03c9\u00b7\u0029", "\u005e\u03c9\u005e", // Joyful
		"(__)", "_(._.)_", "_(_^_)_", "<(_ _)><m(__)m>", "m(__)m", "m(_ _)m", //Kowtow as a sign of respect, or dogeza for apology
		"\uff3c\u0028\u00b0\u30ed\uff3c\u0029", "\u0028\uff0f\u30ed\u00b0\u0029\uff0f", //Questioning
		"('_')","(/_;)","(T_T)(;_;)","(;_;", "(;_:)", "\u0028\u003b\u004f\u003b\u0029", "(:_;)", "(ToT)","\u0028\uff34\u25bd\uff34\u0029\u003b\u005f\u003b", ";-;", ";n;", ";;", "Q.Q", "T.T", "TnT", "QQ", "Q_Q",
        "\u0028\u30fc\u005f\u30fc\u0029\u0021\u0021", "(-.-)", "(-_-)", "\u0028\u4e00\u4e00\u0029", "\u0028\uff1b\u4e00\u005f\u4e00\u0029", //Shame
		"(=_=)", //Tired
		"(=^・^=)", "(=^・・^=)", "=^_^=", //Cat
		"(..)", "(._.)", //Looking down
		"^m^", //Giggling with hand covering mouth
		"(・・?", "(?_?)", //Confusion
		"\u0028\uff0d\u2038\u10da\u0029", //Facepalm
		">^_^<", "<^!^>", "^/^", "（*^_^*）", "\u00a7\u005e\u002e\u005e\u00a7", "(^<^)(^.^)", "\u0028\u005e\u30e0\u005e\u0029", "(^·^)", "(^.^)", "(^_^.)", "(^_^)", "(^^)", "(^J^)", "(*^.^*)", "^_^", "(#^.^#)", "（^—^）", //Normal laugh
		"\u0028\u005e\u005e\u0029\u002f\u007e\u007e\u007e", "\u0028\u005e\u005f\u005e\u0029\u002f\u007e", "\u0028\u003b\u005f\u003b\u0029\u002f\u007e\u007e\u007e", "\u0028\u005e\u002e\u005e\u0029\u002f\u007e\u007e\u007e", "\u0028\u002d\u005f\u002d\u0029\u002f\u007e\u007e\u007e\u0028\u0024\u00b7\u00b7\u0029\u002f\u007e\u007e\u007e", "\u0028\u0040\u005e\u005e\u0029\u002f\u007e\u007e\u007e", "\u0028\u0054\u005f\u0054\u0029\u002f\u007e\u007e\u007e", "\u0028\u0054\u006f\u0054\u0029\u002f\u007e\u007e\u007e", //Waving
		"\u0028\u0056\u0029\u006f\uffe5\u006f\u0028\u0056\u0029", //Alien Baltan
		"\uff3c\u0028\u007e\u006f\u007e\u0029\uff0f", "\uff3c\u0028\u005e\u006f\u005e\u0029\uff0f", "\uff3c\u0028\u002d\u006f\u002d\u0029\uff0f\u30fd\u0028\u005e\u3002\u005e\u0029\u30ce", "\u30fd\u0028\u005e\u006f\u005e\u0029\u4e3f", "(*^0^*)", //Excited
		"(*_*)", "(*_*;", "(+_+)(@_@)", "\u0028\u0040\u005f\u0040\u3002", "\u0028\uff20\u005f\uff20\u003b\u0029", "\uff3c\u0028\u25ce\u006f\u25ce\u0029\uff0f\uff01", //Amazed
		"!(^^)!",
		"(*^^)v", "(^^)v", "(^_^)v", "（’-’*)(＾ｖ＾)", "\u0028\uff3e\u25bd\uff3e\u0029", "\u0028\u30fb\u2200\u30fb\u0029", "\u0028\u00b4\u2200\u0060\u0029", "\u0028\u2312\u25bd\u2312\uff09", //Laughing, cheerful
		"\u0028\u007e\u006f\u007e\u0029","\u0028\u007e\u005f\u007e\u0029",
		"\u0028\u005e\u005e\u309e",
		"(p_-)",
		"((d[-_-]b))", //Headphones, listening to music
		"(-\"-)", "\u0028\u30fc\u30fc\u309b\u0029", "\u0028\u005e\u005f\u005e\u30e1\u0029", "\u0028\u002d\u005f\u002d\u30e1\u0029", "\u0028\u007e\u005f\u007e\u30e1\u0029", "\u0028\uff0d\uff0d\u3006\u0029\u0028\u30fb\u3078\u30fb\u0029", "\u0028\uff40\u00b4\u0029", "\u003c\u0060\uff5e\u00b4\u003e", "\u003c\u0060\u30d8\u00b4\u003e", "\u0028\u30fc\u30fc\u003b\u0029", //worried
		"(^0_0^)", //Eyeglasses
		"\u0028\u0020\u002e\u002e\u0029\u03c6","\u03c6\u0028\u002e\u002e\u0029", //Jotting note
		"\u0028\u25cf\uff3e\u006f\uff3e\u25cf\u0029", "(＾ｖ＾)", "(＾ｕ＾)", "\u0028\uff3e\u25c7\uff3e\u0029", "\u0028\u005e\u0029\u006f\u0028\u005e\u0029", "\u0028\u005e\u004f\u005e\u0029", "\u0028\u005e\u006f\u005e\u0029", "\u0028\u005e\u25cb\u005e\u0029", "\u0029\u005e\u006f\u005e\u0028","\u0028\u002a\u005e\u25bd\u005e\u002a\u0029","\u0028\u273f\u25e0\u203f\u25e0\u0029", //happy
		"\u0028\uffe3\u30fc\uffe3\u0029", //Grinning
		"\u0028\uffe3\u25a1\uffe3\u003b\u0029", "\u00b0\u006f\u00b0", "\u00b0\u004f\u00b0", "\u003a\u004f\u006f\u005f\u004f", "\u006f\u005f\u0030", "\u006f\u002e\u004f", "\u0028\u006f\u002e\u006f\u0029", "\u006f\u004f", //Surprised
		"\u0028\u002a\u00b4\u25bd\uff40\u002a\u0029","\u0028\u002a\u00b0\u2200\u00b0\u0029\u003d\u0033",//Infatuation
		"\uff08\uff9f\u0414\uff9f\u0029","\u0028\u00b0\u25c7\u00b0\u0029", //Shocked, surprised
		"\u0028\u002a\uffe3\u006d\uffe3\u0029", //Dissatisfied
		"\u30fd\u0028\u00b4\u30fc\uff40\u0029\u250c", "\u00af\\\u005f\u0028\u30c4\u0029\u005f\u002f\u00af", //Mellow, shrug
		"\u0028\u00b4\uff65\u03c9\uff65\u0060\u0029","\u0028\u2018\u0041\u0060\u0029", //Snubbed or deflated
		"\u0028\u002a\u005e\u0033\u005e\u0029\u002f\u007e\u2606", //Blowing a kiss
		"\u002e\u002e\u002e\u002e\u002e\u03c6\u0028\u30fb\u2200\u30fb\uff0a\u0029", //Studying is good
		"uwu", "UwU", //Joy
		"\u004f\u0057\u004f", "\u004f\u0077\u004f", //Cute, inquisitive or perplexed, sometimes associated with the furry fandom

		//Other Easterm Emoticons
		"\u002e\u006f\u25cb", "\u0010\u25cb\u006f\u002e", //Bubbles
		"(^^)", "_U~~","\u005f\u65e6\u007e\u007e", //Cup of tea
		"\u2606\u5f61", "\u2606\u30df", //Shooting star
		"\u003e\u00b0\u0029\u0029\u0029\u5f61", "(Q", "))", "\u003e\u003c\u30e8\u30e8\u0028\u00b0\u0029\u0029\u003c\u003c", "\u003e\u00b0\u0029\u0029\u0029\u0029\u5f61", "\u003c\u00b0\u0029\u0029\u0029\u5f61", "\u003e\u00b0\u0029\u0029\u5f61\u0020\u003c\u002b", "))><<", "<*))", ">=<", //Fish
		"\u003c\u30b3\u003a\u5f61\u0020\uff23\u003a\u002e\u30df", //Octopus, squid
		"\u007e\u003e\u00b0\u0029\uff5e\uff5e\uff5e", //Snake
		"\uff5e\u00b0\u00b7\u005f\u00b7\u00b0\uff5e", //Bat
		"\u0028\u00b0\u00b0\u0029\uff5e", //Tadpole
		"\u25cf\uff5e\u002a", //Bomb
		"\uffe3\u007c\u25cb", "STO", "OTZ", "OTL", "orz", //Despair. The "O"s represent head on the ground, "T" or "r" forms the torso, and "S" or "z" the legs
        "\u0028\u256f\u00b0\u25a1\u00b0\uff09\u256f\ufe35\u0020\u253b\u2501\u253b", "\u252c\u2500\u2500\u252c\u0020\u00af\\\u005f\u0028\u30c4\u0029\u0020\u253b\u2501\u253b\ufe35\u30fd\u0028\u0060\u0414\u00b4\u0029\uff89\ufe35\u0020\u253b\u2501\u253b", 
        "\u252c\u2500\u252c\u30ce\u0028\u0020\u00ba\u0020\u005f\u0020\u00ba\u30ce\u0029", "\u0028\u30ce\u0ca0\u76ca\u0ca0\u0029\u30ce\u5f61\u253b\u2501\u253b",//Table flip
		"\u003a\u0033\u30df",
		
		//2channel emoticons
		"m(_ _)m", //Kowtow as a sign of respect, or dogeza for apology
		"\u0028\u00b4\uff65\u03c9\uff65\u0060\u0029", //Snubbed or deflated
		"\u0028\u0060\uff65\u03c9\uff65\u00b4\u0029", //Feel perky
		"\u0028\uff40\u002d\u00b4\u0029\u003e", //Salute
		"\u0028\u00b4\uff1b\u03c9\uff1b\u0060\u0029", //Terribly sad
		"\u30fd\u0028\u00b4\u30fc\uff40\u0029\uff89", //Peace of mind
		"\u30fd\u0028\u0060\u0414\u00b4\u0029\uff89", //Be irritable
		"\u0028\uff03\uff9f\u0414\uff9f\u0029", //Angry
		"\uff08\u0020\u00b4\u0414\uff40\uff09", //Yelling, or panting
		"\uff08\u3000\uff9f\u0414\uff9f\uff09", //Surprised, or loudmouthed
		"\u2510\u0028\u0027\uff5e\u0060\uff1b\u0029\u250c", //Don't know the answer
		"\uff08\u00b4\u2200\uff40\uff09", //Carefree
		"\uff08\u3000\u00b4\u005f\u309d\u0060\uff09", //Indifferent
		"\u03a3\u0028\u309c\u0434\u309c\u003b\u0029", //Shocked
		"\u0028\u0020\uff9f\u30ee\uff9f\u0029", //Happy, upbeat
		"\u2282\u4e8c\u4e8c\u4e8c\uff08\u3000\uff3e\u03c9\uff3e\uff09\u4e8c\u2283", //"Bu-n", being carefree and above, with arms stretched out while running/soaring
		"\u0028\u0028\u0028\u0028\u0020\uff1b\uff9f\u0414\uff9f\u0029\u0029\u0029", //Spook
		"\u03a3\u0028\uff9f\u0414\uff9f\u0029", //Huge surprise
		"\u0028\u0020\u00b4\u2200\uff40\u0029\u03c3\u0029\u2200\u0060\u0029", //Jog someone's cheek
		"\u0028\u0020\uff9f\u0434\uff9f\u0029", //Amazed
		"\u0028\u00b4\u30fc\u0060\u0029\u0079\u002d\u007e\u007e", //Smoking
		"\uff08\u0020\u005e\u005f\u005e\uff09\u006f\u81ea\u81ea\u006f\uff08\u005e\u005f\u005e\u0020\uff09", //Toast "Cheers"
		"\u006d\u0039\u0028\u30fb\u2200\u30fb\u0029", //Flash of intuition
		"\u30fd\u0028\u00b4\u30fc\u0060\u0029\u4eba\u0028\u00b4\u2207\uff40\u0029\u4eba\u0028\u0060\u0414\u00b4\u0029\u30ce", //Friendly
		"\u0028\u0027\u0041\u0060\u0029", //Lonely
		"\uff08\u0020\u00b4\u002c\u005f\u309d\u0060\u0029", //Depressed, unsatisfied (based on indifferent)
		"\uff08\u00b4\u002d\u0060\uff09\u002e\uff61\u006f\u004f\u0028\u0020\u002e\u002e\u002e\u0020\u0029", //Thinking
		"\u0028\uff9f\u0414\uff9f\u003b\u2261\u003b\uff9f\u0414\uff9f\u0029", //Impatience
		"\u0028\u0020\u00b4\u0434\u0029\uff8b\uff7f\u0028\u00b4\u0414\uff40\u0029\uff8b\uff7f\u0028\u0414\uff40\u0020\u0029", //Whispers
		"\uff08\uff65\u2200\uff65\u0029\u3064\u2469", //Carrying money
		"\u2282\uff08\uff9f\u0414\uff9f\u2282\u2312\uff40\u3064\u2261\u2261\u2261\u0028\u00b4\u2312\u003b\u003b\u003b\u2261\u2261\u2261", //Sliding on belly, "whooaaa!!!
		"\u0028\uff9f\u0434\uff9f\u0029", //Unforeseen
		"\u0028\uff9f\u22bf\uff9f\u0029", //I don't need it
		"\u0449\u0028\uff9f\u0414\uff9f\u0449\u0029\u0020\u0028\u5c6e\uff9f\u0414\uff9f\u0029\u5c6e", //Come on
		"\uff08\u30fb\u2200\u30fb\uff09", //Mocking, "good"
		"\uff08\u30fb\uff21\u30fb\uff09", //That's bad
		"\u0028\uff9f\u2200\uff9f\u0029", //Discharged drug-in-brain, goofing around, "A-HYA!
		"\uff08\u0020\u3064\u0020\u0414\u0020\uff40\uff09", //Sad
		"\u30a8\u30a7\u30a7\u0028\u00b4\u0434\uff40\u0029\u30a7\u30a7\u30a8", //Not convincing
		"\u0028\uffe3\u30fc\uffe3\u0029", //Simper, Snorlax
		"\u005b\uff9f\u0434\uff9f\u005d", //Deflagged
		"\u266a\u250f\u0028\u30fb\u006f\uff65\u0029\u251b\u266a\u2517\u0020\u0028\u0020\uff65\u006f\uff65\u0029\u0020\u2513", //Happy expressions, dancing to the music
		"\u0064\u0028\u002a\u2312\u25bd\u2312\u002a\u0029\u0062", //Happy expression
		"\uff3f\u007c\uffe3\u007c\u25cb", //Given up. Despair. The "O"s represent head on the ground, "T" or "r" forms the torso, and "S" or "z" the legs
		"\u0028\u256c\u0020\u0ca0\u76ca\u0ca0\u0029", //Extreme Distaste, meant to appear as an exaggerated grimace
		"\u0028\u2267\u30ed\u2266\u0029", //Shouting
		"\u0028\u0398\u03b5\u0398\u003b\u0029", //Pretending not to notice, asleep because of boredom
		"\uff3c\u007c\u0020\uffe3\u30d8\uffe3\u007c\uff0f\uff3f\uff3f\uff3f\uff3f\uff3f\uff3f\uff3f\u03b8\u2606\u0028\u0020\u002a\u006f\u002a\u0029\u002f", //Kick
		"\u250c\u0028\uff1b\u0060\uff5e\u002c\u0029\u2510", //Discombobulated
		"\u03b5\u003d\u03b5\u003d\u03b5\u003d\u250c\u0028\u003b\u002a\u00b4\u0414\u0060\u0029\uff89", //Running
		"\u30fd\u0028\u00b4\u25bd\u0060\u0029\u002f", //Happy
		"\u005e\u3142\u005e", //Happy
		"\u0028\u006c\u0027\u006f\u0027\u006c\u0029", //Shocked
		"\u30fd\u0028\uff4f\u0060\u76bf\u2032\uff4f\u0029\uff89", //Really angry
		"\u0028\u261e\uff9f\u30ee\uff9f\u0029\u261e", //Do it
		"\u261c\u0028\uff9f\u30ee\uff9f\u261c\u0029", //Do it
		"\u261c\u0028\u2312\u25bd\u2312\u0029\u261e", //Angel

		//2channel emoticons containing Japanese phrases
		"\u30ad\u30bf\u2501\u2501\u2501\u0028\u309c\u2200\u309c\u0029\u2501\u2501\u2501\u0021\u0021\u0021\u0021\u0021\u0020", //It's here", Kitaa!, excitement that something has appeared or happened or "I came
		"\uff77\uff80\uff9c\u30a1\u002a\uff65\u309c\uff9f\uff65\u002a\u003a\u002e\uff61\u002e\u002e\uff61\u002e\u003a\u002a\uff65\u309c\u0028\u006e\u2018\u2200\u2018\u0029\u03b7\uff9f\uff65\u002a\u003a\u002e\uff61\u002e\u0020\u002e\uff61\u002e\u003a\u002a\uff65\u309c\uff9f\uff65\u002a\u0020\u0021\u0021\u0021\u0021\u0021", //Girlish version of "It's here
		"\u0028\u002a\u00b4\u0414\u0060\u0029\uff8a\uff67\uff8a\uff67", //Erotic stirring, haa haa
		"\u0028\u3000\u00b4\u0414\uff40\u0029\uff89\u0028\u00b4\uff65\u03c9\uff65\u0060\u0029\u3000\uff85\uff83\uff9e\uff85\uff83\uff9e", //Patting, nade nade
		"\u0028\u002a\uff9f\uff89\u004f\uff9f\u0029\u003c\uff75\uff75\uff75\uff75\uff6b\uff6b\uff6b\uff6b\uff6b\uff6b\uff6b\uff70\uff70\uff70\uff70\uff70\uff72\u0021", //Calling out, "Ooooi!
		"\u0028\u0020\uff9f\u2200\uff9f\u0029\uff71\uff8a\uff8a\u516b\u516b\uff89\u30fd\uff89\u30fd\uff89\u30fd\uff89\u0020\uff3c\u0020\u002f\u0020\uff3c\u002f\u0020\uff3c", //Evil laugh (literally ahahaHAHA...)
		"\uff08\u30fb\u2200\u30fb\u0020\uff09\u30fe\u0028\u002d\u0020\u002d\uff1b\u0029\u30b3\u30e9\u30b3\u30e9", //Blaming "now now"
		"\u304a\u0028\u005e\u006f\u005e\u0029\u3084\u0028\u005e\u004f\u005e\u0029\u3059\u0028\u005e\uff61\u005e\u0029\u307f\u3043\u0028\u005e\u002d\u005e\u0029\uff89\uff9e", //Kana reading "O ya su mi" meaning "Good night" or "Night"
		
		//Multi-line 2channel emoticons
		"\u003c\u0060\u2200\u00b4\u003e", "\u003c\u4e36\uff40\u2200\u00b4\u003e" //Stereotypical Korean character (Nidā)
    }));
    */

    /**
     * A hashmap of emoticons in different languages. NOTE: All JSON files (listed
     * below) containing emoticons
     *
     */

    private static final HashMap<String, HashMap<String, Pair<Pattern, String>>> htEmoticons = new HashMap<>();

    static {
        for (String i : new String[]{"/emoticons-json/emoticons.en.json"}) {
            
            String lang = i.substring(26, 28).toUpperCase();
            
            try {
                InputStream is = FindEmoticonInStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, Pair<Pattern, String>> dict = new HashMap<>();
                jsonObject.keySet().forEach((emoticon) -> {
                    dict.put(emoticon, new Pair<>(Character.isLetter(emoticon.charAt(0))? 
                        Pattern.compile("(?<=[\\p{Space}]|^)" + Pattern.quote(emoticon)):
                        Pattern.compile(Pattern.quote(emoticon) + "(?=(?:[\\p{Space}]|$))") ,
                    jsonObject.getString(emoticon)));
                });
                htEmoticons.put(lang, dict);
            } catch (Exception e) {
                logger.error("Exception processing: " + i + " message " + e.getMessage());
            }
        }
    }


    /**
     * The default value for removed emoticons
     */
    public static final String DEFAULT_REMOVE_EMOTICON = "yes";

    /**
     * The default property name to store emoticons
     */
    public static final String DEFAULT_EMOTICON_PROPERTY = "emoticon";

    /**
     * Indicates if emoticons should be removed from data
     */
    private boolean removeEmoticon = EBoolean.getBoolean(DEFAULT_REMOVE_EMOTICON);

     /**
     * The name of the property where the language is stored
     */
    private String langProp = DEFAULT_LANG_PROPERTY;

    /**
     * The property name to store emoticons
     */
    private String emoticonProp = DEFAULT_EMOTICON_PROPERTY;

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }
    
    /**
     * Indicates if emoticon should be removed from data
     *
     * @param removeEmoticon True if emoticons should be removed
     */
    @PipeParameter(name = "removeEmoticon", description = "Indicates if the emoticons should be removed or not", defaultValue = DEFAULT_REMOVE_EMOTICON)
    public void setRemoveEmoticon(final String removeEmoticon) {
        this.removeEmoticon = EBoolean.parseBoolean(removeEmoticon);
    }

    /**
     * Indicates if emoticons should be removed
     *
     * @param removeEmoticon True if emoticons should be removed
     */
    public void setRemoveEmoticon(final boolean removeEmoticon) {
        this.removeEmoticon = removeEmoticon;
    }

    /**
     * Checks whether emoticons should be removed
     *
     * @return True if emoticons should be removed
     */
    public boolean getRemoveEmoticon() {
        return this.removeEmoticon;
    }

    /**
     * Sets the property where emoticons will be stored
     *
     * @param emoticonProp the name of the property for emoticons
     */
    @PipeParameter(name = "emoticonpropname", description = "Indicates the property name to store emoticons", defaultValue = DEFAULT_EMOTICON_PROPERTY)
    public void setEmoticonProp(final String emoticonProp) {
        this.emoticonProp = emoticonProp;
    }

    /**
     * Retrieves the property name for storing emoticons
     *
     * @return String containing the property name for storing emoticons
     */
    public String getEmoticonProp() {
        return this.emoticonProp;
    }

    
    /**
     * Establish the name of the property where the language will be stored
     *
     * @param langProp The name of the property where the language is stored
     */
   
     @PipeParameter(name = "langpropname", description = "Indicates the property name to store the language", defaultValue = DEFAULT_LANG_PROPERTY)
    public void setLangProp(String langProp) {
        this.langProp = langProp;
    }

    /**
     * Returns the name of the property in which the language is stored
     *
     * @return the name of the property where the language is stored
     */
    
    public String getLangProp() {
        return this.langProp;
    }


    /**
     * Default constructor. Construct a FindEmoticonInStringBufferPipe instance with
     * the default configuration value
     */
    public FindEmoticonInStringBufferPipe() {
        this(DEFAULT_EMOTICON_PROPERTY, EBoolean.getBoolean(DEFAULT_REMOVE_EMOTICON), DEFAULT_LANG_PROPERTY);
    }

    /**
     * Build a FindEmoticonInStringBufferPipe that stores emoticons of the
     * StringBuffer in the property emoticonProp
     * 
     * @param langProp   The name of the property that stores the language of text
     * @param emoticonProp   The name of the property to store emoticons
     * @param removeEmoticon tells if emoticons should be removed
     */
    public FindEmoticonInStringBufferPipe(String emoticonProp,  boolean removeEmoticon, String langProp) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class}, new Class<?>[] {FindHashtagInStringBufferPipe.class });

        this.langProp = langProp;
        this.emoticonProp = emoticonProp;
        this.removeEmoticon = removeEmoticon;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing emoticons, adds a property and returns it. This is the method by
     * which all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(final Instance carrier) {
        StringBuffer sb = (StringBuffer) carrier.getData();
        String data = carrier.getData().toString();
        final Stack<Pair<Integer, Integer>> replacements = new Stack<>();
        String lang = (String) carrier.getProperty(langProp);
        

        HashMap<String,Pair<Pattern,String>> dict = htEmoticons.get(lang);
        if (dict==null){
            carrier.setProperty(emoticonProp, " ");
            return carrier; //When there is not a dictionary for the language
        } 
        
        String value = "";

        for(String emoti: dict.keySet()){
            Pattern p=dict.get(emoti).getObj1();
            Matcher m = p.matcher(sb);
            while (m.find()) {
                value += m.group() + " ";
                if (removeEmoticon) {
                    replacements.push(new Pair<>(m.start(), m.end()));
                }
            }   
            
            while (!replacements.empty()) {
                final Pair<Integer, Integer> current = replacements.pop();
                data = (current.getObj1() > 0 ? data.substring(0, current.getObj1()) : "")
                    + //if startindex is 0 do not concatenate
                    (current.getObj2() < (data.length() - 1) ? data.substring(current.getObj2()) : ""); //if endindex=newSb.length()-1 do not concatenate
            }

            if (removeEmoticon) {
                carrier.setData(new StringBuffer(data));
            }
        }
        
        carrier.setProperty(emoticonProp, value);
            
        return carrier;
    }

}
