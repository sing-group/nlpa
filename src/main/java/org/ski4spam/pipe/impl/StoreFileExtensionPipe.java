package org.ski4spam.pipe.impl;

import java.io.File;

import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

import org.bdp4j.pipe.PipeParameter;

/**
 * This pipe adds the extension of a file as instance property.
 *
 * @author Rosalía Laza 
 * @author Reyes Pavón
 */
@PropertyComputingPipe()
public class StoreFileExtensionPipe extends Pipe {
   /**
    * Return the input type included the data attribute of a Instance
    * @return the input type for the data attribute of the Instances processed
    */
    @Override
    public Class getInputType() {
        return File.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after processing
     * @return the datatype expected in the data attribute of a Instance after processing
     */
    @Override
    public Class getOutputType() {
        return File.class;
    }

    /**
		* The default property name to store the extension
		*/
    public static final String DEFAULT_EXTENSION_PROPERTY="extension";

    /**
		* The property name to store the extension
		*/
    private String extProp=DEFAULT_EXTENSION_PROPERTY;

	 /**
		* Sets the property where the extension will be stored
		* @param extProp the name of the property for the extension
		*/
	 @PipeParameter(name = "extpropname", description = "Indicates the property name to store the extension", defaultValue=DEFAULT_EXTENSION_PROPERTY)    
    public void setExtensionProp(String extProp) {
        this.extProp = extProp;
    }
	 
	 /**
		* Retrieves the property name for storing the file extension
		* @return String containing the property name for storing the file extension
		*/
    public String getExtenstionProp(){
        return this.extProp;
    }

    /**
		* Default constructor
		*/
    public StoreFileExtensionPipe() {
    }

    /**
		* Build a StoreFileExtensionPipe that stores the extension of the file in the property extProp
		* @param extProp The name of the property to extore the file extension
		*/
    public StoreFileExtensionPipe(String extProp) {
        this.extProp = extProp;
    }

    /**
    * Process an Instance.  This method takes an input Instance,
    * destructively modifies it in some way, and returns it.
    * This is the method by which all pipes are eventually run.
    *
    * @param carrier Instance to be processed.
    * @return Instancia procesada
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
