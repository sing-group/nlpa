/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Instance;
import org.ski4spam.types.SynsetDictionary;
import org.ski4spam.types.SynsetFeatureVector;
import org.bdp4j.util.EBoolean;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Iterator;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SharedDataConsumer;
import org.bdp4j.pipe.TeePipe;
import org.bdp4j.util.CSVDatasetWriter;
import org.bdp4j.util.Configurator;

/**
 * Create a CSV file from a SynsetFeatureVector object located in the data field
 * of an instance.
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@TeePipe()
public class TeeCSVFromSynsetFeatureVectorPipe extends AbstractPipe implements SharedDataConsumer {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TeeCSVFromSynsetFeatureVectorPipe.class);

    /**
     * The default value for the output file
     */
    public static final String DEFAULT_OUTPUT_FILE = "output.csv";

    /**
     * Default value for telling this pipe if the props will be saved or not
     */
    public static final String DEFAULT_SAVEPROPS = "yes";

    /**
     * The output filename to store the CSV information
     */
    private String output;

    /**
     * The output Bufferered reader for writting purposes
     */
    private BufferedWriter outputFile;

    /**
     * Csv DAtaset to store data
     */
    CSVDatasetWriter dataset = null;

    /**
     * Number of properties
     */
    int nprops = 0;

    /**
     * Length of the dictionary
     */
    int dictLength = 0;

    /**
     * Indicates whether if the props will be created or not
     */
    private boolean saveProps = true;

    /**
     * Indicates if this the current element is the first one to be processed
     * (this is because with the first element, the CSV header needs to be
     * generated)
     */
    private boolean isFirst = true;

    /**
     * Build a TeeCSVFromSynsetFeatureVector using the default information
     */
    public TeeCSVFromSynsetFeatureVectorPipe() {
        this(DEFAULT_OUTPUT_FILE);
    }

    /**
     * Creates a TeeCSVFromSynsetFeatureVector indicating a different filename
     * for CSV output
     *
     * @param output The filename to store the output
     */
    public TeeCSVFromSynsetFeatureVectorPipe(String output) {
        super(new Class<?>[0], new Class<?>[0]);

        this.output = Configurator.getLastUsed().getProp(Configurator.OUTPUT_FOLDER) + System.getProperty("file.separator")+ output;
        File f = new File(this.output);
        if (f.exists()) {
            f.delete();
        }
        this.dataset = new CSVDatasetWriter(this.output);
    }

    /**
     * Creates a TeeCSVFromSynsetFeatureVector indicating the output filename
     * ans selecting whether the properties will be also outputed
     *
     * @param output The output filename to store the CSV
     * @param saveProps Indicates whether the props will be also saved
     */
    public TeeCSVFromSynsetFeatureVectorPipe(String output, boolean saveProps) {
        super(new Class<?>[0], new Class<?>[0]);

        this.output = Configurator.getLastUsed().getProp(Configurator.OUTPUT_FOLDER) + System.getProperty("file.separator")+ output;
        this.saveProps = saveProps;

        File f = new File(this.output);
        if (f.exists()) {
            f.delete();
        }
        this.dataset = new CSVDatasetWriter(this.output);          
    }

    /**
     * Set the output fileName to store the CSV contents
     *
     * @param output The filename/filepath to store the CSV contents
     */
    @PipeParameter(name = "output", description = "Indicates the output filename/path for saving CSV", defaultValue = DEFAULT_OUTPUT_FILE)
    public void setOutput(String output) {
        this.output = Configurator.getLastUsed().getProp(Configurator.OUTPUT_FOLDER) + System.getProperty("file.separator")+ output;
        File f = new File(this.output);
        if (f.exists()) {
            f.delete();
        }
        this.dataset.flushAndClose();
        this.dataset = new CSVDatasetWriter(this.output);        
    }

    /**
     * Returns the filename where the CSV contents will be stored
     *
     * @return the filename/filepath where the CSV contents will be stored
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Indicates if the properties of the instance should be also saved in the
     * CSV file
     *
     * @param saveProps True if the properties should be also saved in the CSV
     */
    public void setSaveProps(boolean saveProps) {
        this.saveProps = saveProps;
    }

    /**
     * Indicates if the properties of the instance should be also saved in the
     * CSV file (but from string)
     *
     * @param saveProps "true" if the properties should be also saved in the CSV
     */
    @PipeParameter(name = "saveProps", description = "Indicates if the properties should be saved or not", defaultValue = DEFAULT_SAVEPROPS)
    public void setSaveProps(String saveProps) {
        this.saveProps = EBoolean.parseBoolean(saveProps);
    }

    /**
     * Indicates if the Instance properties should be saved in the CSV file
     *
     * @return true if the Instance properties will be saved in the CSV file
     */
    public boolean getSaveProps() {
        return this.saveProps;
    }

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return SynsetFeatureVector.class;
    }

    private static boolean contains(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue)) {
                return true;
            }
        }
        return false;
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
        return SynsetFeatureVector.class;
    }

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        SynsetFeatureVector fsv = (SynsetFeatureVector) carrier.getData();

        //Ensure the columns of the dataset fits with the instance
        if (dataset.getColumnCount() == 0) {
            nprops = carrier.getPropertyList().size();
            dictLength = SynsetDictionary.getDictionary().size();

            String columnsToAdd[] = new String[2 + nprops + dictLength];
            Object defaultValues[] = new Object[2 + nprops + dictLength];
            columnsToAdd[0] = "id";
            defaultValues[0] = "0";

            int j = 1;
            for (String i : carrier.getPropertyList()) {
                columnsToAdd[j] = i;
                defaultValues[j] = "0";
                j++;
            }

            Iterator<String> it = SynsetDictionary.getDictionary().iterator();
            while (it.hasNext()) {
                String dictEntry = it.next();
                // columnsToAdd[j] = encodeFeat(dictEntry);
                columnsToAdd[j] = dictEntry;
                defaultValues[j] = "0";
                j++;
            }

            columnsToAdd[j] = "target";
            defaultValues[j] = "";
            dataset.addColumns(columnsToAdd, defaultValues);

        } else if (dataset.getColumnCount() != SynsetDictionary.getDictionary().size() + 2 + carrier.getPropertyList().size()) {
            String currentProps[] = dataset.getColumnNames();

            String newProps[] = new String[carrier.getPropertyList().size() - nprops];
            Object newDefaultValues[] = new Object[carrier.getPropertyList().size() - nprops];
            int j = 0;
            for (String prop : carrier.getPropertyList()) {
                if (!contains(currentProps, prop)) {
                    newProps[j] = prop;
                    newDefaultValues[j] = "0";
                    j++;
                }
            }
            dataset.insertColumnsAt(newProps, newDefaultValues, nprops + 1);
            nprops += newProps.length;

            newProps = new String[SynsetDictionary.getDictionary().size() - dictLength];
            newDefaultValues = new Object[SynsetDictionary.getDictionary().size() - dictLength];
            j = 0;
            int currentEntryIdx = 0;
            Iterator<String> it = SynsetDictionary.getDictionary().iterator();
            while (it.hasNext()) {
                String dictEntry = it.next();
                if (currentEntryIdx >= dictLength) {
                    //newProps[j] = encodeFeat(dictEntry);
                    newProps[j] = dictEntry;
                    newDefaultValues[j] = "0";
                    j++;
                }
                currentEntryIdx++;
            }

            dataset.insertColumnsAt(newProps, newDefaultValues, dataset.getColumnCount() - 1);
            dictLength += newProps.length;
        }

        //Create and add the new row
        Object newRow[] = new Object[nprops + dictLength + 2];
        newRow[0] = carrier.getName();
        int i = 1;
        for (Object current : carrier.getValueList()) {
            newRow[i] = current;
            i++;
        }
        Iterator<String> it = SynsetDictionary.getDictionary().iterator();
        while (it.hasNext()) {
            newRow[i] = fsv.getValue(it.next());
            i++;
        }
        newRow[i] = carrier.getTarget();
        dataset.addRow(newRow);

        //If islast on the current burst close the dataset
        if (isLast()) {
            dataset.flushAndClose();
        }

        return carrier;
    }

    @Override
    /**
     * Retrieve data from directory
     *
     * @param dir Directory name to retrieve data
     */
    public void readFromDisk(String dir) {
        SynsetDictionary.getDictionary().readFromDisk(dir + System.getProperty("file.separator") + "synsetDictionary.ser");
    }
}
