/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.ski4spam.ia.types.SynsetFeatureVector;

/**
 *
 * @author María Novo
 */
public class TeeCSVFromSynsetFeatureVector extends Pipe {

    private static final Logger logger = LogManager.getLogger(TeeCSVFromSynsetFeatureVector.class);
    private Writer output;
    private boolean saveData;
    private boolean isFirst;
    
//    private static FileWriter fw = null;
//    private static Writer w = null;
//    private static BufferedWriter bw = null;
//    private static String filename = "output.csv";
//    private static File f = null;
//
//    private boolean isFirst = true;
//    private boolean saveData = false;
    private static SynsetFeatureVector synsetFeatureVector = null;

    public TeeCSVFromSynsetFeatureVector() {
        this(null,null,false);
    }

    public TeeCSVFromSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector) {
        this(synsetFeatureVector, null, false);
    }

    public TeeCSVFromSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector, Writer output) {
        this(synsetFeatureVector, output, false);
    }
    
    public TeeCSVFromSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector, Writer output, boolean saveData) {
        this.synsetFeatureVector = synsetFeatureVector;
        this.output = output;
        this.saveData = saveData;
    }
    
    public void setSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector) {
        this.synsetFeatureVector = synsetFeatureVector;
    }

    public void setOutput(Writer output) {
        this.output = output;
    }

    public Writer getOutput() {
        return this.output;
    }
    
    public void setSaveData(boolean saveData) {
        this.saveData = saveData;
    }

    public boolean getSaveData() {
        return this.saveData;
    }
    
    public SynsetFeatureVector getSynsetFeatureVector() {
        return this.synsetFeatureVector;
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
     */
    public static String getCSVHeader(boolean withData, Set<String> propertyList) {
        String str = new String();
//        
//        str += "id" + CSV_SEP + (withData ? ("data" + CSV_SEP) : "");
//        Enumeration<String> keys = properties.keys();
//        while (keys.hasMoreElements()) {
//            str += (keys.nextElement() + CSV_SEP);
//        }
//        str += "target";
        return str;
    }

    /**
     * Converts this instance toCSV string representation
     */
    public static String toCSV(boolean withData, Instance carrier) {
        String str = "";
//        Object name =  carrier.getName();
//        Object data =  carrier.getData();
//        Object target = carrier.getTarget();
//        str += name + CSV_SEP + (withData ? (StringEscapeUtils.escapeCsv(data.toString().replaceAll(";", "\\;")) + CSV_SEP) : "");
//        Hashtable<String, Object> properties = carrier.getProperties();
//        Collection values = properties.values();
//        Iterator it = values.iterator();
//        while (it.hasNext()) {
//            str += (it.next() + CSV_SEP);
//        }
//        str += target.toString();
        return str;
    }

    @Override
    public Instance pipe(Instance carrier) {
        try {
            if (isFirst) {
                //Map<String, Object> properties = carrier.getProperties();
                this.output.write(getCSVHeader(saveData, carrier.getPropertyList()));
                isFirst = false;
            }
            output.write(toCSV(saveData, carrier) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
