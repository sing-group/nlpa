package org.ski4spam.pipe.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TeePipe;
import static org.ski4spam.util.CSVUtils.getCSVSep;
import static org.ski4spam.util.CSVUtils.escapeCSV;

import org.bdp4j.pipe.PipeParameter;

import org.ski4spam.util.EBoolean;

/**
 * This pipe parses Instances to csv format. It can be for showing it on
 * terminal or exporting it to .csv file. The resulting CSV could be readed in R
 * using out &lt;- read.csv("output.csv",header = TRUE, sep=";", encoding =
 * "UTF-8", skipNul = TRUE, stringsAsFactors = FALSE )
 *
 * @author Yeray Lage Freitas
 * @author María Novo
 */
@TeePipe()
public class TeeCSVFromStringBufferPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TeeCSVFromStringBufferPipe.class);

    /**
     * Indicates the output filename/path for CSV storing
     */
    private String output;

    /**
     * A writer for the output file
     */
    private FileWriter outputFile;

    /**
     * Indicates if the data should be saved
     */
    private boolean saveData;

    /**
     * Indicates if the current instance will be the first one to save
     */
    private boolean isFirst;

    /**
     * The default value for save Data
     */
    public static final String DEFAULT_SAVEDATA = "yes";

    /**
     * The default value for the output file
     */
    public static final String DEFAULT_OUTPUT_FILE = "output.csv";

    /**
     * Build a TeeCSVFromStringBufferPipe pipe with the default configuration
     * values
     */
    public TeeCSVFromStringBufferPipe() {
        this(DEFAULT_OUTPUT_FILE, true);
    }

    /**
     * Build a TeeCSVFromStringBufferPipe using the specified output directory
     * and the default value for saveData
     *
     * @param output The filename/path for the output file
     */
    public TeeCSVFromStringBufferPipe(String output) {
        this(output, true);
    }

    /**
     * Build a TeeCSVFromStringBufferPipe using the specified output directory
     * and the value for saveData
     *
     * @param output The filename/path for the output file
     * @param saveData tells if the data should be also saved in CSV
     */
    public TeeCSVFromStringBufferPipe(String output, boolean saveData) {
        this.setOutput(output);
        this.setSaveData(saveData);
        this.isFirst = true;
    }

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
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
     * Set the output fileName to store the CSV contents
     *
     * @param output The filename/filepath to store the CSV contents
     */
    @PipeParameter(name = "output", description = "Indicates the output filename/path for saving CSV", defaultValue = DEFAULT_OUTPUT_FILE)
    public void setOutput(String output) {
        this.output = output;
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
     * Indicates if the data of the instance should be also saved in the CSV
     * file
     *
     * @param saveData True if the data should be also saved in the CSV
     */
    public void setSaveData(boolean saveData) {
        this.saveData = saveData;
    }

    /**
     * Indicates if the data of the instance should be also saved in the CSV
     * file (but from string)
     *
     * @param saveData "true" if the data should be also saved in the CSV
     */
    @PipeParameter(name = "saveData", description = "Indicates if the data should be saved or not", defaultValue = DEFAULT_SAVEDATA)
    public void setSaveData(String saveData) {
        this.saveData = EBoolean.parseBoolean(saveData);
    }

    /**
     * Checks whether the data should be saved to the CSV file or not
     *
     * @return true if the data should be also saved in CSV
     */
    public boolean getSaveData() {
        return this.saveData;
    }

    /**
     * Computes the CSV header for the instance
     *
     * @param withData Indicates whether include or not the data of the instance
     * in the output
     * @param propertyList a list of properties to be included in the CSV header
     * @return The CSV header
     */
    public static String getCSVHeader(boolean withData, Set<String> propertyList) {
        StringBuilder builder = new StringBuilder();

        builder.append("id").append(getCSVSep());

        if (withData) {
            builder.append("data").append(getCSVSep());
        }

        for (String key : propertyList) {
            builder.append(key).append(getCSVSep());
        }

        builder.append("target");

        return builder.toString();
    }

    /**
     * Converts this instance toCSV string representation
     *
     * @param withData Indicates whether include or not the data of the instance
     * in the output
     * @param carrier Indicates the instance to be represented in the CSV line
     * @return The string representation o the instance carrier
     */
    public static String toCSV(boolean withData, Instance carrier) {
        StringBuilder builder = new StringBuilder();
        Object name = carrier.getName();
        Object data = carrier.getData();
        Object target = carrier.getTarget();

        builder.append(name).append(getCSVSep());
        if (withData) {
            builder.append(escapeCSV(data.toString()/*.replaceAll("[" + getCSVSep() + getStrQuote() + "]", "")*/)).append(getCSVSep());
        }

        for (Object value : carrier.getValueList()) {
            builder.append(escapeCSV(value.toString())).append(getCSVSep());
        }
        builder.append(target.toString());

        return builder.toString();
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
        try {
            if (isFirst) {
                outputFile = new FileWriter(output);
                this.outputFile.write(getCSVHeader(saveData, carrier.getPropertyList()) + "\n");
                isFirst = false;
            }
            outputFile.write(toCSV(saveData, carrier) + (!isLast() ? "\n" : ""));
            outputFile.flush();
            if (isLast()) {
                outputFile.close();
            }
        } catch (IOException e) {
            logger.error("Exception cauth when processing instance "+carrier.getName()+" Message: "+e);
        }
        return carrier;
    }
}
