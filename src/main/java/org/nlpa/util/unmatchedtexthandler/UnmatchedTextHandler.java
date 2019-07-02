package org.nlpa.util.unmatchedtexthandler;

import org.bdp4j.util.Pair;

public abstract class UnmatchedTextHandler{
	public abstract void handle(Pair<String,String> text, String lang);
}