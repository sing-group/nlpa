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
import org.nlpa.iu.MainUI;
import org.nlpa.pipe.impl.*;
import org.nlpa.util.textextractor.EMLTextExtractor;

import javax.swing.*;
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

    public static void normalCode (String[] args){
        System.out.println("Program started");
        if (args.length == 0) {
            System.out.println("Selecciona el texto para crear las instancias");
            File file = showFileSelector();
            generateInstances(file.getAbsolutePath());
        } else {
            generateInstances(args[0]);
        }

        //Configurations
        EMLTextExtractor.setCfgPartSelectedOnAlternative("text/plain");

        for (Instance i : instances){
            logger.info("Instance data before pipe: " + i.getData().toString());
        }

        //Then load the dataset to use it with Weka TM
        Map<String, Integer> targetValues = new HashMap<>();
        targetValues.put("ham", 0);
        targetValues.put("spam", 1);

        System.out.println("Default encoding: "+System.getProperty("file.encoding"));
        System.setProperty("file.encoding", "UTF-8");

        AbstractPipe p = new SerialPipes(new AbstractPipe[]{
                new TargetAssigningFromPathPipe(),
                new StoreFileExtensionPipe(),
                new GuessDateFromFilePipe(),
                new File2StringBufferPipe(),
                new StripHTMLFromStringBufferPipe(),
                new GuessLanguageFromStringBufferPipe(),
                new NewNERFromStringBufferPipe(GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY,true, true, true, true),
        });

        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
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
    public static void main(String[] args) {
        MainUI.initUI();
    }

    public static void readList (List<String> list){
        for (String i : list){
            System.out.println(i);

        }
    }

    public static File showFileSelector() {
        JFileChooser fileSelector = new JFileChooser(".");
        fileSelector.setDialogTitle("Select a folder");
        fileSelector.setMultiSelectionEnabled(true);
        fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int toRet = fileSelector.showOpenDialog(null);
        if (toRet == JFileChooser.APPROVE_OPTION) {
            return fileSelector.getSelectedFile();
        } else {
            return null;
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
