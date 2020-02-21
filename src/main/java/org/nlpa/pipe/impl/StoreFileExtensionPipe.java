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
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe adds the extension of a file as instance property.
 *
 * @author Rosalía Laza
 * @author Reyes Pavón
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class StoreFileExtensionPipe extends AbstractPipe {

    /**
     * The default property name to store the extension
     */
    public static final String DEFAULT_EXTENSION_PROPERTY = "extension";

    /**
     * The property name to store the extension
     */
    private String extProp = DEFAULT_EXTENSION_PROPERTY;

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return File.class;
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
        return File.class;
    }
    
    /**
     * Sets the property where the extension will be stored
     *
     * @param extProp the name of the property for the extension
     */
    @PipeParameter(name = "extpropname", description = "Indicates the property name to store the extension", defaultValue = DEFAULT_EXTENSION_PROPERTY)
    public void setExtensionProp(String extProp) {
        this.extProp = extProp;
    }

    /**
     * Retrieves the property name for storing the file extension
     *
     * @return String containing the property name for storing the file
     * extension
     */
    public String getExtensionProp() {
        return this.extProp;
    }

    /**
     * Default constructor. Creates a StoreFileExtensionPipe Pipe.
     */
    public StoreFileExtensionPipe() {
        this(DEFAULT_EXTENSION_PROPERTY);
    }

    /**
     * Build a StoreFileExtensionPipe that stores the extension of the file in
     * the property extProp
     *
     * @param extProp The name of the property to extore the file extension
     */
    public StoreFileExtensionPipe(String extProp) {
        super(new Class<?>[0], new Class<?>[0]);

        this.extProp = extProp;
    }

    /**
     * Process an Instance. This method takes an input Instance, adds extProp
     * property and returns it. This is the method by which all pipes are
     * eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Processed instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            String[] extensions = {"eml", "tsms", "sms", "warc", "ytbid", "tytb", "twtid", "ttwt"};
            String value = "";
            String name = (((File) carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while (i < extensions.length && !name.endsWith(extensions[i])) {
                i++;
            }

            if (i < extensions.length) {
                value = extensions[i];
            }

            carrier.setProperty(extProp, value);
        }
        return carrier;
    }
}
