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
import org.ski4spam.util.Dictionary;
import static org.ski4spam.util.CSVUtils.CSV_SEP;

/**
 *
 * @author María Novo
 */
public class TeeCSVFromSynsetFeatureVector extends Pipe {

    private static final Logger logger = LogManager.getLogger(TeeCSVFromSynsetFeatureVector.class);
    private String output;
    private BufferedWriter outputFile;
    private boolean saveData;
    private boolean isFirst;
    private static SynsetFeatureVector synsetFeatureVector = null;

    public TeeCSVFromSynsetFeatureVector() {
        this(null, null, false);
    }

    public TeeCSVFromSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector) {
        this(synsetFeatureVector, null, false);
    }

    public TeeCSVFromSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector, String output) {
        this(synsetFeatureVector, output, false);
    }

    public TeeCSVFromSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector, String output, boolean saveData) {
        this.synsetFeatureVector = synsetFeatureVector;
        this.output = output;
        this.saveData = saveData;
        this.isFirst = true;
    }

    public void setSynsetFeatureVector(SynsetFeatureVector synsetFeatureVector) {
        this.synsetFeatureVector = synsetFeatureVector;
    }

    public SynsetFeatureVector getSynsetFeatureVector() {
        return this.synsetFeatureVector;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return this.output;
    }

    public void setSaveData(boolean saveData) {
        this.saveData = saveData;
    }

    public boolean getSaveData() {
        return this.saveData;
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
    public static String getCSVHeader() {
        /* Datos prueba */
        Dictionary synsetsDictionary = Dictionary.getDictionary();
        synsetsDictionary.add("21565421");
        synsetsDictionary.add("54554548");
        synsetsDictionary.add("78248598");
        //********************

        StringBuilder csvHeader = new StringBuilder();
        csvHeader.append("id").append(CSV_SEP);
        for (String synsetId : synsetsDictionary) {
            csvHeader.append(synsetId).append(CSV_SEP);
        }
        csvHeader.append("target").append(CSV_SEP);
        return csvHeader.toString();
    }

    /**
     * Converts this instance toCSV string representation
     */
    public static String toCSV(boolean withData, Instance carrier) {
        /* Datos prueba */
        Dictionary synsetsDictionary = Dictionary.getDictionary();
        synsetsDictionary.add("21565421");
        synsetsDictionary.add("54554548");
        synsetsDictionary.add("78248598");
        //********************

        StringBuilder csvBody = new StringBuilder();

        Object name = carrier.getName();
        Object target = carrier.getTarget();

        csvBody.append(name).append(CSV_SEP);
        for (String synsetId : synsetsDictionary) {
            csvBody.append(synsetId).append(CSV_SEP);
            Double frequency = synsetFeatureVector.getFrequencyValue(synsetId);
            if (frequency > 0) {
                csvBody.append(frequency.toString()).append(CSV_SEP);
            } else {
                csvBody.append("0").append(CSV_SEP);
            }
            csvBody.append(target.toString()).append(CSV_SEP);
        }
        return csvBody.toString();
    }

    @Override
    public Instance pipe(Instance carrier) {
        try {
            if (isFirst) {
                outputFile = new BufferedWriter(new FileWriter(output));
                this.outputFile.write(getCSVHeader() + "\n");
                isFirst = false;
            }
            outputFile.write(toCSV(saveData, carrier) + "\n");
            outputFile.flush();
            if (isLast()) {
                outputFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carrier;
    }
}
