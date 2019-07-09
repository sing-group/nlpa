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
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;

/**
 * This pipe modifies the data of an Instance to pass it to lowercase
 * The data should be a StringBuffer
 * 
 * @author Rosalía Laza 
 * @author Reyes Pavón
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class StringBufferToLowerCasePipe extends AbstractPipe {
   /**
    * Return the input type included the data attribute of an Instance
    * @return the input type for the data attribute of the Instance processed
    */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Build a StringBufferToLowerCasePipe
     */
    public StringBufferToLowerCasePipe() {
        super(new Class<?>[0], new Class<?>[]{AbbreviationFromStringBufferPipe.class, SlangFromStringBufferPipe.class});
    }

    /**
    * Process an Instance.  This method takes an input Instance,
    * modifies it to pass it to lowercase, and returns it.
    * This is the method by which all pipes are eventually run.
    *
    * @param carrier Instance to be processed.
    * @return Instance processed
    */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
            StringBuffer newSb = new StringBuffer();
            newSb.append(((StringBuffer) carrier.getData()).toString().toLowerCase());
            carrier.setData(newSb);
        }

        return carrier;
    }
}
