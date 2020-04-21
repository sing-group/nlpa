package org.nlpa.pipe.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;

import com.google.auto.service.AutoService;

/**
 * This pipe compute the compression ratio between the original text size and the compressed text size.
 * 
 * The compute is based on the next article: 
 * Ntoulas, & Alexandros, & Najork, & Marc, & Manasse, Mark & Mark, & Fetterly, & Dennis,. (2006).
 * Detecting Spam Web Pages through Content Analysis. Proceedings of the 15th International World 
 * Wide Web Conference (WWW). DOI: 10.1145/1135777.1135794. 
 *
 * @author Patricia Martin Perez
 */
@PropertyComputingPipe
@AutoService(Pipe.class)
public class ComputeCompressionRatioPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(ComputeCompressionRatioPipe.class);

    /**
     * The default property name where the compression ratio will be stored
     */
    public static final String DEFAULT_COMPRESSION_RATIO_PROPERTY = "compressionRatio";

    /**
     * The name of the property where the compression ratio is stored
     */
    private String compressionRatioProperty = DEFAULT_COMPRESSION_RATIO_PROPERTY;

    /**
     * Construct a ComputeStringCompressionRatioPipe instance
     *
     */
    public ComputeCompressionRatioPipe() {
        super(new Class<?>[0], new Class<?>[0]);

    }
    
    /**
     * Build a ComputeCompressionRatioPipe that stores the compression ratio of a text in
     * the property compressionRatioProperty
     *
     * @param compressionRatioProperty The name of the property for storing the compression ratio
     */
    public ComputeCompressionRatioPipe(String compressionRatioProperty) {
        super(new Class<?>[0], new Class<?>[0]);

        this.compressionRatioProperty = compressionRatioProperty;
    }

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance
     * after processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Establish the name of the property where the compression ratio will be stored
     *
     * @param compressionRatioProperty The name of the property for storing the compression ratio
     */
    @PipeParameter(name = "compressionRatioProperty", description = "Indicates the property name to store the compression ratio", defaultValue = DEFAULT_COMPRESSION_RATIO_PROPERTY)
    public void setCompressionRatioProperty(String compressionRatioProperty) {
        this.compressionRatioProperty = compressionRatioProperty;
    }

    /**
     * Retrieves the property name for storing the compression ratio
     *
     * @return String containing the property name for storing the compression ratio
     */
    public String getCompressionRatioProperty() {
        return this.compressionRatioProperty;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {
        	double ratio = 0;
            String text = carrier.getData().toString();
            int originalTextSize = text.getBytes().length;
            int compressedTextSize = 0;
            
            try (
            		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            		GZIPOutputStream zipOutputStream = new GZIPOutputStream(byteOutputStream);
            	){
            	
				zipOutputStream.write(text.getBytes());

				compressedTextSize = byteOutputStream.size();	            
	            
			} catch (IOException e) {
				logger.error("Exception output streams " + e.getMessage());
			}
            
            if(originalTextSize != 0) {
            	 ratio = (double) originalTextSize / (double) compressedTextSize;
            }
            
            carrier.setProperty(compressionRatioProperty, ratio);
            
        } else {
            logger.error("Data should be a StringBuffer when processing " + carrier.getName() + " but is a "
                    + carrier.getData().getClass().getName());
        }

        return carrier;
    }

}
