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
package org.nlpa.util.dateextractor;

import java.io.File;
import java.util.Date;

/**
  * Extracts the content date from a file (maybe parsing the file to find the date in headers, etc)
  * @author José Ramón Méndez
  */
public abstract class DateExtractor {

	/**
	  * Finds the content date from a file
	  * @param f The file to use to retrieve the content date
	  * @return the date of the content
	  */
    public abstract Date extractDate(File f);

}