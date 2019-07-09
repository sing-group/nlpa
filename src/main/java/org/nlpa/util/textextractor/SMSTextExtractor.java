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
  * A TextExtractor used to extract text from SMSs 
  * Files using this TextExtractor should contain the original 
  * representation of SMS
  * @author José Ramón Méndez
  */
public class SMSTextExtractor extends TextExtractor{
	/**
	  * A static instance of the TexTextractor to implement a singleton pattern
	  */	
	static TextExtractor instance=null;

	/**
	  * Private default constructor
	  */	
	private SMSTextExtractor(){
		
	}

   /**
	* Retrieve the extensions that can process this TextExtractor
	* @return An array of Strings containing the extensions of files that this TextExtractor can handle
	*/	
	public static String[] getExtensions(){
		return new String[]{"sms"};
	}

   /**
	* Return an instance of this TextExtractor
	* @return an instance of this TextExtractor
	*/	
	public static TextExtractor getInstance(){
		if (instance==null) {
			instance=new SMSTextExtractor();
		}
		return instance;
	}
	
   /**
	* Extracts text from a given file
	* @param f The file where the text is included
	* @return A StringBuffer with the extracted text		
	*/
 @Override	
	public StringBuffer extractText(File f){
		return new StringBuffer("This is an example of SMS text");
	}
}