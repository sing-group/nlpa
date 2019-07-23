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

import org.nlpa.util.textextractor.TextExtractor;
import org.nlpa.util.textextractor.EMLTextExtractor;
import org.nlpa.util.textextractor.YTBIDTextExtractor;
import org.nlpa.util.textextractor.WARCTextExtractor;
import org.nlpa.util.textextractor.SMSTextExtractor;
import org.nlpa.util.textextractor.GenericTextExtractor;
import org.nlpa.util.textextractor.TWTIDTextExtractor;
import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import java.util.HashMap;
import org.bdp4j.pipe.Pipe;

/**
 * This pipe reads text and html contents from different file formats
 *
 * @author José Ramón Méndez Reboredo
 */
@AutoService(Pipe.class)
@TransformationPipe()
public class File2StringBufferPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(File2StringBufferPipe.class);

    /**
     * A collection of Textextractors to extract the text
     */
    private static HashMap<String, TextExtractor> htExtractors;

    static {
        htExtractors = new HashMap<>();

        //Add the extractors
        for (String ext : GenericTextExtractor.getExtensions()) {
            htExtractors.put(ext, GenericTextExtractor.getInstance());
        }
        for (String ext : EMLTextExtractor.getExtensions()) {
            htExtractors.put(ext, EMLTextExtractor.getInstance());
        }
        for (String ext : SMSTextExtractor.getExtensions()) {
            htExtractors.put(ext, SMSTextExtractor.getInstance());
        }
        for (String ext : WARCTextExtractor.getExtensions()) {
            htExtractors.put(ext, WARCTextExtractor.getInstance());
        }
        for (String ext : TWTIDTextExtractor.getExtensions()) {
            htExtractors.put(ext, TWTIDTextExtractor.getInstance());
        }
        for (String ext : YTBIDTextExtractor.getExtensions()) {
            htExtractors.put(ext, YTBIDTextExtractor.getInstance());
        }
    }

    /**
     * Determines the input type for the data attribute of the Instance
     * processed
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return File.class;
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
     * Default constructor. Construct a File2StringBufferPipe instance
     */
    public File2StringBufferPipe() {
        super(new Class<?>[0], new Class<?>[0]);
    }

    /**
     * Process an Instance. This method reads text and html contents from
     * different file formats and modify an instance, setting its data value. This
     * is the method by which all pipes are eventually run.
     *
     * @param carrier Instance to processed
     * @return Processed instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof File) {
            String[] extensions = {"eml", "tsms", "sms", "warc", "tytb", "ytbid", "twtid", "ttwt"};
            String extension = "";
            String name = (((File) carrier.getData()).getAbsolutePath()).toLowerCase();
            int i = 0;
            while (i < extensions.length && !name.endsWith(extensions[i])) {
                i++;
            }

            if (i < extensions.length) {
                extension = extensions[i];
            }

            TextExtractor te = htExtractors.get(extension);

            if (te != null) {
                StringBuffer txt = te.extractText((File) (carrier.getData()));
                if (txt == null) {
                    logger.warn("Invalidating instance " + carrier.toString() + " due to a fault in parsing.");
                    carrier.invalidate();
                } else {
                    carrier.setData(txt);
                }
            } else {
                logger.warn("No parser available for instance " + carrier.toString() + ". Invalidating instance.");
                carrier.invalidate();
            }
        }

        return carrier;
    }
}
