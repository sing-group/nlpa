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
package org.nlpa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.bdp4j.util.InstanceListUtils;
import org.nlpa.pipe.impl.*;
import org.nlpa.types.SequenceGroupingStrategy;
import org.nlpa.util.textextractor.EMLTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.bdp4j.dataset.CSVDatasetReader;
//import org.bdp4j.transformers.CheckVoidTransformer;
import org.bdp4j.transformers.Date2MillisTransformer;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.transformers.Url2BinaryTransformer;
import org.bdp4j.types.Transformer;

/**
 * Main class for SKI4Spam project
 *
 * @author Yeray Lage
 * @author José Ramón Méndez
 * @author María Novo
 */
public class Main {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * List of instances that are being processed
     */
    private static List<Instance> instances = new ArrayList<Instance>();

    /*
     * The main method for the running application
     */
    public static void main(String[] args) {
        // System.out.println("Program started.");
        if (args.length == 0) {
            generateInstances("tests/");
        } else {
            generateInstances(args[0]);
        }

        // Configurations
        EMLTextExtractor.setCfgPartSelectedOnAlternative("text/plain");

        for (Instance i : instances) {
            logger.info("Instance data before pipe: " + i.getData().toString());
        }

        /*
         * Create an example to identify methods which have ParameterPipe annotations.
         */
 /*
         * Method[] methods = SynsetVector2SynsetFeatureVectorPipe.class.getMethods();
         * PipeParameter parameterPipe; for (Method method : methods) { parameterPipe =
         * method.getAnnotation(PipeParameter.class);//Obtienes los métodos que tengan
         * alguna anotación de tipo ParameterPipe if (parameterPipe != null) { String
         * parameterName = parameterPipe.name(); String parameterDescription =
         * parameterPipe.description(); String defaultValue =
         * parameterPipe.defaultValue(); Class<?>[] types = method.getParameterTypes();
         * // Obtienes los tipos de los parámetros para un método
         * //System.out.println(parameterName + " --> " + parameterDescription); } }
         */
 /*
         * // Parámetro para el transformador Enum2IntTransformer de la propiedad target
         * Map<String, Integer> transformList = new HashMap<>();
         * transformList.put("ham", 0); transformList.put("spam", 1); //Se define la
         * lista de transformadores Map<String, Transformer<Object>> transformersList =
         * new HashMap<>(); transformersList.put("date", new Date2MillisTransformer());
         * transformersList.put("target", new Enum2IntTransformer(transformList));
         * 
         * //TeeCSVDatasetFromSynsetFeatureVectorPipe teeCSVDatasetFSV = new
         * TeeCSVDatasetFromSynsetFeatureVectorPipe();
         * //teeCSVDatasetFSV.setTransformersList(transformersList);
         * 
         * String filePath =
         * "outputsyns.csv";//Main.class.getResource("/outputsyns.csv").getPath();
         * DatasetFromFile jml = new DatasetFromFile(filePath, transformersList);
         * jml.loadFile();
         */
        //Then load the dataset to use it with Weka TM
//        Map<String, Integer> targetValues = new HashMap<>();
//        targetValues.put("ham", 0);
//        targetValues.put("spam", 1);

        //Lets define transformers for the dataset
//        Map<String, Transformer> transformersList = new HashMap<>();
//        transformersList.put("target", new Enum2IntTransformer(targetValues));
//        transformersList.put("date", new Date2MillisTransformer());
//        transformersList.put("URLs", new Url2BinaryTransformer());
//        long before = System.currentTimeMillis();
//        System.out.println("before");
//        weka.core.Instances data = (new CSVDatasetReader("output_spam_ass.csv", transformersList)).loadFile().getWekaDataset();
//        System.out.println(" -- DATA --- \r\n " + data);
//        long time = System.currentTimeMillis() - before;
//        System.out.println("time: " + time);

//        TeeDatasetFromFeatureVectorPipe teeDatasetFSV = new TeeDatasetFromFeatureVectorPipe();
//        teeDatasetFSV.setTransformersList(transformersList);
        /* create a example of pipe */
        AbstractPipe p = new SerialPipes(new AbstractPipe[]{new TargetAssigningFromPathPipe(),
            new StoreFileExtensionPipe(), 
            new GuessDateFromFilePipe(), 
            new File2StringBufferPipe(),
            new MeasureLengthFromStringBufferPipe(),
            new FindUrlInStringBufferPipe(),
            new StripHTMLFromStringBufferPipe(),
            new MeasureLengthFromStringBufferPipe("length_after_html_drop"), 
            new GuessLanguageFromStringBufferPipe(),
            new ContractionsFromStringBufferPipe(),
            new AbbreviationFromStringBufferPipe(),
            new SlangFromStringBufferPipe(),
            new ComputePolarityFromLexiconPipe(),
            new StringBufferToLowerCasePipe(), 
            new InterjectionFromStringBufferPipe(),
            new StopWordFromStringBufferPipe(),
            new TeeCSVFromStringBufferPipe("output.csv", true), 
            new StringBuffer2TokenSequencePipe(),
            new TokenSequence2FeatureVectorPipe(SequenceGroupingStrategy.COUNT),
             new TeeCSVFromFeatureVectorPipe("outputsyns.csv")
             
//            teeDatasetFSV
            
        });

        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
         System.out.println(AbstractPipe.getErrorMessage()); // TODO why is this an error?
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }

        instances = InstanceListUtils.dropInvalid(instances);

        //Pipe all instances
        p.pipeAll(instances);

        for (Instance i : instances) {
            logger.info("Instance data after pipe: " + i.getSource() + " "
                    + (((i.getData().toString().length()) > 10)
                    ? (i.getData().toString().substring(0, 10) + "...")
                    : i.getData().toString()));
        }
    }

    /**
     * Generate a instance List on instances attribute by recursivelly finding
     * all files included in testDir directory
     *
     * @param testDir The directory where the instances should be loaded
     */
    private static void generateInstances(String testDir) {
        try {
            Files.walk(Paths.get(testDir))
                    .filter(Files::isRegularFile)
                    .forEach(FileMng::visit);
        } catch (IOException e) {
            logger.error("IOException found " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Used to add a new instance on instances attribute when a new file is
     * detected.
     */
    static class FileMng {

        /**
         * Include a filne in the instancelist
         *
         * @param path The path of the file
         */
        static void visit(Path path) {
            File data = path.toFile();
            String target = null;
            String name = data.getPath();
            File source = data;

            instances.add(new Instance(data, target, name, source));
        }
    }

}
