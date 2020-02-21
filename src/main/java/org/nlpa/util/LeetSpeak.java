package org.nlpa.util;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * LeetSpeak decoder implemented in Java. Some leet expressions has been extracted from:
 * <ul>
 * <li> https://www.codeproject.com/Tips/207582/L33t-Tr4nsl4t0r-Leet-Translator </li>
 * <li> https://www.reddit.com/r/dailyprogrammer/comments/67dxts/20170424_challenge_312_easy_l33tspeak_translator/ </li>
 * <li> http://www.gamehouse.com/blog/leet-speak-cheat-sheet/ </li>
 * </ul>
 * @author José Ramón Méndez
 */
public class LeetSpeak {
    /**
     * our leet speak dictionary
     */
    private static String translations[][]={ 
            { "|>", "p"},
            { "!3", "b" },
            { "][", "i" },
            { "|T|", "m" },
            { "]-[", "h" },
            { "|-|", "h" },
            { "'/'", "y" },
            { "[-", "e" },
            { "|)", "d" },
            { "[)", "d" },
            { "(|", "d" },
            { "/\\", "a" },
            { "|=", "f"},
            { "/=", "f"},
            { "\\=", "f"},
            { "|#", "f" },
            { "/-\\", "a"},
            { "`//", "w" },
            { ")(", "x" }, 
            { "(_)", "u" },
            { "|`", "r" },
            { "|2", "r" },
            { "\\2", "r" },
            { "/2", "r" },
            { "|_", "l"},
            { "|7", "p" },
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
            { "[\\]", "n" },
            { "\u00D0", "d" },
            { "\u0192", "f" },
            { "\u00B5", "u" },
            { "\u00A5", "y" },
            { "\uFFE5", "y" },
            { "2", "z" },
            { "(", "c" },
            { "\u00A3", "l" },
            { "\uFFE1", "l" },
            { "[z", "r" },
            { "9", "g" },
            { "$", "s" },
            { "13", "b" },
            { "0", "o" },
            { "1", "l" },
            { "3", "e" },
            { "4", "a" },
            { "6", "g" },
            { "+", "t" },
            { "|3", "b" },
            { "7", "t" },
            { "8", "b" },
            { "@", "a" },
            { "\u2020", "t" },
            { "\u00eb", "e" },
            { "\u0e1a", "u" },
            { "{", "c" },
            { "[", "c" },
            { "\u0414", "a" },
            { "\u0418", "n" },
            { "¡", "i" },
            { "\u0e17", "n" },
            { "\u00a9", "c" }
    };
    
    /**
     * Decode a leet encoded word
     * @param orig The original word
     * @return the word decoded
     */
    public static String decodeWord(String orig){
        String text=orig;
        for (String [] current:translations){
            text=text.replaceAll(Pattern.quote(current[0]), current[1]);
        }
        
        return text;
    }

    /**
     * Determines whether a word have leet expressions or not
     * @param token A word
     * @return true if leet encoding is found (false otherwise)
     */
    public static boolean isLeet(String token){
        boolean retValue=false;

        //False if it is a number
        if (token.matches("^[0-9.E]*$")) return false;

        //Otherwise, we can detect if any of the leet forms are inside
        for(int i=0;i<translations.length&&retValue==false;i++){
           retValue=token.contains(translations[i][0]);
        }

        return retValue;
    }

    /**
     * Decode a text written in leet in a new StringBuffer (it does not modify 
     * the original StringBuffer)
     * @param orig The original text to decode
     * @return The decoded text
     */
    public static StringBuffer decode(StringBuffer orig){
        StringBuffer retValue=new StringBuffer(orig);
        int startSearch=0;

        StringTokenizer st=new StringTokenizer(orig.toString());
        while(st.hasMoreTokens()){
            String newToken=st.nextToken();
            startSearch=retValue.indexOf(newToken,startSearch);
            if (isLeet(newToken)) retValue.replace(startSearch, startSearch+newToken.length(), decodeWord(newToken));
        }

        return retValue;
    }

    /**
     * Decode a text written in leet (String Version)
     * @param orig The original text to decode as String
     * @return The decoded text as String
     */
    public static String decode(String orig){
        StringBuffer retValue=new StringBuffer(orig);
        int startSearch=0;

        StringTokenizer st=new StringTokenizer(orig.toString());
        while(st.hasMoreTokens()){
            String newToken=st.nextToken();
            startSearch=retValue.indexOf(newToken,startSearch);
            if (isLeet(newToken)) retValue.replace(startSearch, startSearch+newToken.length(), decodeWord(newToken));
        }

        return retValue.toString();
    }    
    
    /*
    public static void main(String args[]){
        System.out.println(decode("|#บ11 |`37@|`(| 7337 ©@[\\] !33 ][|T||>13|T|3[\\]73(| !3'/' 7]-[3 "));
    }
    */
}