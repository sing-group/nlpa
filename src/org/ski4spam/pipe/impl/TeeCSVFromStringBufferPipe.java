package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.PropertyComputingPipe;
import org.ski4spam.pipe.TeePipe;

import java.io.*;

/**
 * This pipe parses Instances to csv format.
 * It can be for showing it on terminal or exporting it to .csv file.
 * The resulting CSV could be readed in R using 
 * out <- read.csv("output.csv",header = TRUE, sep=";", encoding = "UTF-8", skipNul = TRUE, stringsAsFactors = FALSE )
 *
 * @author Yeray Lage Freitas
 */
@TeePipe(inputType = "StringBuffer")
public class TeeCSVFromStringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(TeeCSVFromStringBufferPipe.class);
    private static FileWriter fw = null;
    private static Writer w = null;
    private static BufferedWriter bw = null;
    private static String filename = "output.csv";
    private static File f = null;
    private boolean isFirst = true;
    private boolean saveData = false;

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
        

		
		/*
        Hashtable<String, Object> properties = carrier.getProperties();
        System.out.println(carrier.getName());
        System.out.println("\t" + properties.entrySet());

        String props = "";
        for (Map.Entry<String, Object> p : carrier.getProperties().entrySet()) {
            if ((p.getValue()) instanceof Double) {
                p.setValue(String.valueOf(p.getValue()));
            }

            props += "\"" + p.getValue() + "\",";
        }
        */
        try {
            if (isFirst) {
                bw.write(carrier.getCSVHeader(saveData) + "\n");
                isFirst = false;
            }
            bw.write(carrier.toCSV(saveData) + "\n");

            //System.out.println(props.substring(0, props.length() - 1) + "\n");
            //bw.write(props.substring(0, props.length() - 1) + "\n");

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
