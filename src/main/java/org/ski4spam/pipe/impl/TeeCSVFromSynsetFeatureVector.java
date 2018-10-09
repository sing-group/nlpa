/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.ski4spam.ia.types.SynsetFeatureVector;
import org.ski4spam.util.SynsetDictionary;
import static org.ski4spam.util.CSVUtils.getCSVSep;

import org.bdp4j.pipe.PipeParameter;

import org.ski4spam.util.EBoolean;

/**
 * Create a CSV file from a SynsetFeatureVector object located in the 
 * data field of an instance.
 * @author María Novo
 * @author José Ramón Méndez 
 */
public class TeeCSVFromSynsetFeatureVector extends Pipe {

	/**
	  * For logging purposes
	  */
    private static final Logger logger = LogManager.getLogger(TeeCSVFromSynsetFeatureVector.class);
	 
	 /**
		* The default value for the output file
		*/
	 public static final String DEFAULT_OUTPUT_FILE="output.csv";
	 
	 /**
		* Default value for telling this pipe if the props will be saved or not
		*/
	 public static final String DEFAULT_SAVEPROPS="yes";
	 
	 /**
		* The output filename to store the CSV information
		*/
    private String output;
	 
	 /**
		* The output Bufferered reader for writting purposes
		*/
    private BufferedWriter outputFile;
	 
	 /**
		* Indicates whether if the props will be created or not
		*/
    private boolean saveProps=true;
	 
	 /**
		* Indicates if this the current element is the first one to be processed 
		* (this is because with the first element, the CSV header needs to be generated)
		*/
    private boolean isFirst=true;

    /**
		* Build a TeeCSVFromSynsetFeatureVector using the default information
		*/
    public TeeCSVFromSynsetFeatureVector() {
        this(DEFAULT_OUTPUT_FILE);
    }

    /**
		* Creates a TeeCSVFromSynsetFeatureVector indicating a different filename for 
		* CSV output
		* @param output The filename to store the output
		*/
    public TeeCSVFromSynsetFeatureVector(String output) {
        this(output, true);
    }

    /**
		* Creates a TeeCSVFromSynsetFeatureVector indicating the output filename 
		* ans selecting whether the properties will be also outputed
		* @param output The output filename to store the CSV
		* @param saveProps Indicates whether the props will be also saved 
		*/
    public TeeCSVFromSynsetFeatureVector(String output, boolean saveProps) {
        this.output = output;
        this.saveProps = saveProps;
    }

    /**
		* Set the output fileName to store the CSV contents
		* @param output The filename/filepath to store the CSV contents
		*/
	 @PipeParameter(name = "output", description = "Indicates the output filename/path for saving CSV", defaultValue=DEFAULT_OUTPUT_FILE)		 
    public void setOutput(String output) {
        this.output = output;
    }

    /**
		* Returns the filename where the CSV contents will be stored
		* @return the filename/filepath where the CSV contents will be stored
		*/
    public String getOutput() {
        return this.output;
    }

    /**
		* Indicates if the properties of the instance should be also saved in the
		* CSV file
		* @param saveProps True if the properties should be also saved in the CSV
		*/
    public void setSaveProps(boolean saveProps) {
        this.saveProps = saveProps;
    }

    /**
		* Indicates if the properties of the instance should be also saved in the
		* CSV file (but from string)
		* @param saveProps "true" if the properties should be also saved in the CSV
		*/
	 @PipeParameter(name = "saveProps", description = "Indicates if the properties should be saved or not", defaultValue=DEFAULT_SAVEPROPS)
    public void setSaveProps(String saveProps) {
        this.saveProps = EBoolean.parseBoolean(saveProps);
    }
    
	 /**
		* Indicates if the Instance properties should be saved in the CSV file
		* @return true if the Instance properties will be saved in the CSV file
		*/
    public boolean getSaveProps() {
        return this.saveProps;
    }

    @Override
    public Class getInputType() {
        return SynsetFeatureVector.class;
    }

    @Override
    public Class getOutputType() {
        return SynsetFeatureVector.class;
    }

    /**
     * Computes the CSV header for the instance
	  * @param carrier A example of instance to extract the properties that will be represented
	  * @return the CSV header for representing all instances
     */
    public String getCSVHeader(Instance carrier) {
        SynsetDictionary synsetsDictionary = SynsetDictionary.getDictionary();

        StringBuilder csvHeader = new StringBuilder();
        csvHeader.append("id").append(getCSVSep());

		  if(saveProps){ //Generate the props if required
	        for (String key : carrier.getPropertyList()) {
	            csvHeader.append(key).append(getCSVSep());
	        }
		  }
		  
        for (String synsetId : synsetsDictionary) {
            csvHeader.append(synsetId).append(getCSVSep());
        }
		  
        csvHeader.append("target").append(getCSVSep());
        return csvHeader.toString();
    }

    /**
     * Converts this instance to a CSV string representation (a single line)
	  * @param carrier The instance to be represented
	  * @return The string representation of the instance 
     */
    public String toCSV(Instance carrier) {
        SynsetDictionary synsetsDictionary = SynsetDictionary.getDictionary();
		  SynsetFeatureVector synsetFeatureVector=(SynsetFeatureVector)carrier.getData();

        StringBuilder csvBody = new StringBuilder();

        Object name = carrier.getName();
        Object target = carrier.getTarget();

        csvBody.append(name).append(getCSVSep());
		  
		  if(saveProps){ //Generate the props if required
	        for (Object value: carrier.getValueList()){
	            csvBody.append(value).append(getCSVSep());
	        }
		  }		  
		  
        for (String synsetId : synsetsDictionary) {
            csvBody.append(synsetId).append(getCSVSep());
							
            Double frequency = synsetFeatureVector.getFrequencyValue(synsetId);
            if (frequency > 0) {
                csvBody.append(frequency.toString()).append(getCSVSep());
            } else {
                csvBody.append("0").append(getCSVSep());
            }
            csvBody.append(target.toString()).append(getCSVSep());
        }
        return csvBody.toString();
    }

    @Override
    public Instance pipe(Instance carrier) {
        try {
            if (isFirst) {
                outputFile = new BufferedWriter(new FileWriter(output));
                outputFile.write(getCSVHeader(carrier) + "\n");
                isFirst = false;
            }
				
            outputFile.write(toCSV(carrier) + "\n");
            
            if (isLast()) {
                outputFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carrier;
    }
}
