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
import org.checkerframework.checker.units.qual.C;
import org.nlpa.pipe.impl.*;
import org.nlpa.types.SequenceGroupingStrategy;
import org.nlpa.util.CurrencyCardinalNumbers;
import org.nlpa.util.textextractor.EMLTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
        //Testing Currency Cardinal Numbers Class
        String stringIntroduced = "";
        do{
            System.out.println("Escriba la cadena a detectar");
            Scanner scanner = new Scanner(System.in);
            stringIntroduced = scanner.nextLine();
            CurrencyCardinalNumbers currencyCardinalNumbers = new CurrencyCardinalNumbers();
           //String stringToReturn = currencyCardinalNumbers.testingRegularExpressions(stringIntroduced);
           //if (stringToReturn.equals(stringIntroduced)){
           //    System.out.println("Ninguna entidad encontrada en el texto : " + stringIntroduced);
           //}else {
           //    System.out.println("Texto final : " + stringToReturn );
           //    //System.out.println("Entidades encontradas : " + currencyCardinalNumbers.getListOfEntitiesFound().toString() );

           //}
            currencyCardinalNumbers.testingCurrencyFastNER3(stringIntroduced);

        }while(!stringIntroduced.equals("0"));

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
