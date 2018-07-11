package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * This pipe parses Instances to csv format.
 * It can be for showing it on terminal or exporting it to .csv file.
 *
 * @author Yeray Lage Freitas
 */
public class TeeCSVFromStringBufferPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(File2CsvPipe.class);
	private static FileWriter fw=null;
    private static Writer w=null;
    private static BufferedWriter bw=null;
	private static String filename="output.csv";
	private static File f=null;
	private boolean isFirst=true;

    public TeeCSVFromStringBufferPipe(){
		initFile();
    }
	
    public TeeCSVFromStringBufferPipe(String filePath){
		this.filename=filePath;
		initFile();
    }	
	
	public TeeCSVFromStringBufferPipe(File f){
        this.f=f;
		this.filename=null;
		initFile();
 	}
	
	public TeeCSVFromStringBufferPipe(Writer w){
		this.w=w;
		this.filename=null;
	}
	
	public void initFile(){
        try {
			if (this.filename!=null){
                fw = new FileWriter(filename, false);
				bw = new BufferedWriter(fw);
			}else if (this.f!=null){
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
			if (this.filename!=null){
                fw = new FileWriter(filename, true);
				bw = new BufferedWriter(fw);
			}else if (this.w!=null){
				bw = new BufferedWriter(w);
			}else if (this.f!=null){
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
			if (isFirst){ bw.write(carrier.getCSVHeader(true)+"\n"); isFirst=false; }
			bw.write(carrier.toCSV(true)+"\n");			
			
            //System.out.println(props.substring(0, props.length() - 1) + "\n");
            //bw.write(props.substring(0, props.length() - 1) + "\n");
			
            bw.close();
			if (this.fw!=null){
				fw.close();
			}
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
