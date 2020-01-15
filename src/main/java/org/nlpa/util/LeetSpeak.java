package org.nlpa.util;

import java.util.regex.Pattern;

public class LeetSpeak {
    //Dictionary extracted from: https://www.codeproject.com/Tips/207582/L33t-Tr4nsl4t0r-Leet-Translator
    //and https://www.reddit.com/r/dailyprogrammer/comments/67dxts/20170424_challenge_312_easy_l33tspeak_translator/
    private static String translations[][]={ 
            { "`//", "w" },
            { ")(", "x" }, 
            { "\u00B6\u00B8", "Q" }, 
            { "\u00B6", "q" },
            { "\u204B", "p" },
            { "|\u00B0" , "p" },
            { "_|", "j" },
            { "\u00DF", "b" },
            { ")v(" , "m"},
            { "/\\/\\" , "m"},
            { "|V|" , "m"},
            { "/V\\" , "m"},
            { "(\\/)" , "m"},
            { "IVI" , "m"},
            { "[\\/]" , "m"},
            { "//\\\\//\\\\" , "m"},
            { "/|\\" , "m"},
            { "/^^\\" , "m"},
            { "|\\/|", "m" },
            { "(\\/)", "m" },
            { "\u00AE", "r" },
            { "|{", "k" },
            { "\\/", "v" },
            { "|-|", "h" },
            { "\\/\\/", "w"},
            { "(/\\)", "w"},
            { "|\\|", "n" },
            { "\u00D0", "d" },
            { "\u0192", "f" },
            { "\u00B5", "u" },
            { "\u00A5", "y" },
            { "\uFFE5", "y" },
            { "2", "z" },
            { "7", "t" },
            { "(", "c" },
            { "\u00A3", "l" },
            { "\uFFE1", "l" },
            { "9", "g" },
            { "$", "s" },
            { "0", "o" },
            { "1", "i" },
            { "3", "e" },
            { "4", "a" },
            { "9", "g" }
    };
    
    public static String decode(String orig){
        String text=orig;
        for (String [] current:translations){
            text=text.replaceAll(Pattern.quote(current[0]), current[1]);
        }
        
        return text;
    }
    
/*     public static void main(String args[]) {
        System.out.println(decode("\\/i4gr4"));
    } */
}