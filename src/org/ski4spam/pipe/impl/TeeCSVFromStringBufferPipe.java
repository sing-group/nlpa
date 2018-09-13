package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.TeePipe;

import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import org.ski4spam.util.CSVUtils;
import static org.ski4spam.util.CSVUtils.CSV_SEP;

/**
 * This pipe parses Instances to csv format.
 * It can be for showing it on terminal or exporting it to .csv file.
 * The resulting CSV could be readed in R using
 * out <- read.csv("output.csv",header = TRUE, sep=";", encoding = "UTF-8", skipNul = TRUE, stringsAsFactors = FALSE )
 *
 * @author Yeray Lage Freitas
 */
@TeePipe()
public class TeeCSVFromStringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(TeeCSVFromStringBufferPipe.class);
    private static FileWriter fw = null;
    private static Writer w = null;
    private static BufferedWriter bw = null;
    private static String filename = "output.csv";
    private static File f = null;
    private boolean isFirst = true;
    private boolean saveData = false;

    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }

    public TeeCSVFromStringBufferPipe() {
        initFile();
    }

    public TeeCSVFromStringBufferPipe(boolean saveData) {
        this.saveData = saveData;
        initFile();
    }

    public TeeCSVFromStringBufferPipe(String filePath) {
        filename = filePath;
        initFile();
    }

    public TeeCSVFromStringBufferPipe(String filePath, boolean saveData) {
        filename = filePath;
        this.saveData = saveData;
        initFile();
    }

    public TeeCSVFromStringBufferPipe(File f) {
        TeeCSVFromStringBufferPipe.f = f;
        filename = null;
        initFile();
    }

    public TeeCSVFromStringBufferPipe(File f, boolean saveData) {
        TeeCSVFromStringBufferPipe.f = f;
        filename = null;
        this.saveData = saveData;
        initFile();
    }

    public TeeCSVFromStringBufferPipe(Writer w) {
        TeeCSVFromStringBufferPipe.w = w;
        filename = null;
    }

    public TeeCSVFromStringBufferPipe(Writer w, boolean saveData) {
        TeeCSVFromStringBufferPipe.w = w;
        filename = null;
        this.saveData = saveData;
    }

    public void initFile() {
        try {
            if (filename != null) {
                fw = new FileWriter(filename, false);
                bw = new BufferedWriter(fw);
            } else if (f != null) {
                fw = new FileWriter(f, false);
                bw = new BufferedWriter(fw);
            }
            fw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the CSV header for the instance
     */
    public static String getCSVHeader(boolean withData, Hashtable<String, Object> properties) {
        String str = new String();
        
        str += "id" + CSV_SEP + (withData ? ("data" + CSV_SEP) : "");
        Enumeration<String> keys = properties.keys();
        while (keys.hasMoreElements()) {
            str += (keys.nextElement() + CSV_SEP);
        }
        str += "target";
        return str;
    }

    /**
     * Converts this instance toCSV string representation
     */
    public static String toCSV(boolean withData, Instance carrier) {
        String str = "";
        Object name =  carrier.getName();
        Object data =  carrier.getData();
        Object target = carrier.getTarget();
        str += name + CSV_SEP + (withData ? CSVUtils.escapeCsv(data.toString()) : "");
        Hashtable<String, Object> properties = carrier.getProperties();
        Collection values = properties.values();
        Iterator it = values.iterator();
        while (it.hasNext()) {
            str += (it.next() + CSV_SEP);
        }
        str += target.toString();
        return str;
    }
    
    @Override
    public Instance pipe(Instance carrier) {
        try {
            if (filename != null) {
                fw = new FileWriter(filename, true);
                bw = new BufferedWriter(fw);
            } else if (w != null) {
                bw = new BufferedWriter(w);
            } else if (f != null) {
                fw = new FileWriter(f, true);
                bw = new BufferedWriter(fw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        try {
            
            if (isFirst) {
                Hashtable<String, Object> properties = carrier.getProperties();
                bw.write(getCSVHeader(saveData, properties));
                isFirst = false;
            }
            bw.write(toCSV(saveData, carrier) + "\n");
            bw.close();
            if (fw != null) {
                fw.close();
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
