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
package org.nlpa.util.textextractor;

import java.io.File;

/**
  * An abstract class for defining the behaviour of TextExtractors.
  * A text extractor is able to extract all text from a specific kind of files
  * that are identified by their extensions
  * @author José Ramón Méndez
  */
public abstract class TextExtractor {

   /**
	* Extracts text from a given file
	* @param f The file where the text is included
	* @return A StringBuffer with the extracted text		
	*/	 
	public abstract StringBuffer extractText(File f);
	 
}