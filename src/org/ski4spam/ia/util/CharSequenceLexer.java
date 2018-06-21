/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/**
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */
package org.ski4spam.ia.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CharSequenceLexer implements Lexer {

    public static final String LEX_ALPHA_STR = "\\p{Alpha}+";
    public static final String LEX_WORDS_STR = "\\w+";
    public static final String LEX_NONWHITESPACE_TOGETHER_STR = "\\S+";
    public static final String LEX_WORD_CLASSES_STR = "\\p{Alpha}+|\\p{Digit}+";
    public static final String LEX_ALNUM_CLASSES_STR = "\\p{Alnum}+";
    public static final String LEX_NONWHITESPACE_CLASSES_STR = "\\p{Alpha}+|\\p{Digit}+|\\p{Punct}";
    // Some predefined lexing rules
    public static final Pattern LEX_ALPHA = Pattern.compile(LEX_ALPHA_STR);
    public static final Pattern LEX_WORDS = Pattern.compile(LEX_WORDS_STR);
    public static final Pattern LEX_NONWHITESPACE_TOGETHER =
            Pattern.compile(LEX_NONWHITESPACE_TOGETHER_STR);
    public static final Pattern LEX_WORD_CLASSES = Pattern.compile(LEX_WORD_CLASSES_STR);
    //A�adido por mi
    public static final Pattern LEX_ALNUM_CLASSES = Pattern.compile(LEX_ALNUM_CLASSES_STR);

    public static final Pattern LEX_NONWHITESPACE_CLASSES =
            Pattern.compile(LEX_NONWHITESPACE_CLASSES_STR);
    Pattern regex;
    Matcher matcher = null;
    CharSequence input;
    String matchText;
    boolean matchTextFresh;

    public CharSequenceLexer() {
        //this(LEX_ALPHA);
        //this(LEX_ALNUM_CLASSES);
        //Cambiado por mi
        this(LEX_NONWHITESPACE_TOGETHER);
    }

    public CharSequenceLexer(Pattern regex) {
        this.regex = regex;
        setCharSequence(null);
    }

    public CharSequenceLexer(String regex) {
        this(Pattern.compile(regex));
    }

    public CharSequenceLexer(CharSequence input, Pattern regex) {
        this(regex);
        setCharSequence(input);
    }

    public CharSequenceLexer(CharSequence input, String regex) {
        this(input, Pattern.compile(regex));
    }

    public static void main(String[] args) {

        try {

            BufferedReader in = new BufferedReader(new FileReader(args[0]));

            for (String line = in.readLine();
                 line != null;
                 line = in.readLine()) {

                CharSequenceLexer csl = new CharSequenceLexer(line,
                        LEX_NONWHITESPACE_CLASSES);

                while (csl.hasNext())
                    System.out.println(csl.next());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public CharSequence getCharSequence() {

        return input;
    }

    public void setCharSequence(CharSequence input) {
        this.input = input;
        this.matchText = null;
        this.matchTextFresh = false;

        if (input != null)
            this.matcher = regex.matcher(input);
    }

    public String getPattern() {

        return regex.pattern();
    }

    public void setPattern(String reg // added by Fuchun
    ) {

        if (!regex.equals(getPattern())) {
            this.regex = Pattern.compile(reg);

            //         this.matcher = regex.matcher(input);
        }
    }

    public int getStartOffset() {

        if (matchText == null)

            return -1;

        return matcher.start();
    }

    public int getEndOffset() {

        if (matchText == null)

            return -1;

        return matcher.end();
    }

    public String getTokenString() {

        return matchText;
    }

    // Iterator interface methods
    private void updateMatchText() {

        if (matcher != null && matcher.find()) {
            matchText = matcher.group();

            if (matchText.length() == 0) {

                // xxx Why would this happen?
                // It is happening to me when I use the regex ".*" in an attempt to make
                // Token's out of entire lines of text. -akm.
                updateMatchText();

                //System.err.println ("Match text is empty!");
            }

            //matchText = input.subSequence (matcher.start(), matcher.end()).toString ();
        } else
            matchText = null;

        matchTextFresh = true;
    }

    public boolean hasNext() {

        if (!matchTextFresh)
            updateMatchText();

        return (matchText != null);
    }

    public Object next() {

        if (!matchTextFresh)
            updateMatchText();

        matchTextFresh = false;

        return matchText;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
