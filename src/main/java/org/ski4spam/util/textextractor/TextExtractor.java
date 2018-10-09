package org.ski4spam.util.textextractor;

import java.io.File;

/**
  * An abstract class for defining the behaviour of TextExtractors.
  * A text extractor is able to extract all text from a specific kind of files
  * that are identified by their extensions
  */
public abstract class TextExtractor {

   /**
	* Extracts text from a given file
	* @param f The file where the text is included
	* @return A StringBuffer with the extracted text		
	*/	 
	public abstract StringBuffer extractText(File f);
	 
}