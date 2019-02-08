package org.ski4spam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ml.DatasetFromFile;
import org.bdp4j.types.Instance;
import org.bdp4j.util.InstanceListUtils;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.transformers.Date2MillisTransformer;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.types.Transformer;
import org.ski4spam.pipe.impl.*;
import org.ski4spam.util.textextractor.EMLTextExtractor;
//import weka.core.converters.ConverterUtils.DataSource;

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
        //System.out.println("Program started.");
        if (args.length == 0) {
            generateInstances("tests/");
        } else {
            generateInstances(args[0]);
        }

        //Configurations
        EMLTextExtractor.setCfgPartSelectedOnAlternative("text/plain");

        for (Instance i : instances) {
            logger.info("Instance data before pipe: " + i.getData().toString());
        }

        /* Create an example to identify methods which have ParameterPipe annotations.*/
 /*        Method[] methods = SynsetVector2SynsetFeatureVectorPipe.class.getMethods();
        PipeParameter parameterPipe;
        for (Method method : methods) {
            parameterPipe = method.getAnnotation(PipeParameter.class);//Obtienes los métodos que tengan alguna anotación de tipo ParameterPipe
           if (parameterPipe != null) {
                String parameterName = parameterPipe.name();
                String parameterDescription = parameterPipe.description();
				String defaultValue = parameterPipe.defaultValue();
                Class<?>[] types = method.getParameterTypes(); // Obtienes los tipos de los parámetros para un método
                //System.out.println(parameterName + " --> " + parameterDescription);
            }
        }
         */
 /*
          // Parámetro para el transformador Enum2IntTransformer de la propiedad target
        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer<Object>> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target",  new Enum2IntTransformer(transformList));
        
        //TeeCSVDatasetFromSynsetFeatureVectorPipe teeCSVDatasetFSV = new TeeCSVDatasetFromSynsetFeatureVectorPipe();
        //teeCSVDatasetFSV.setTransformersList(transformersList);
  
        String filePath = "outputsyns.csv";//Main.class.getResource("/outputsyns.csv").getPath();
        DatasetFromFile jml = new DatasetFromFile(filePath, transformersList);
        jml.loadFile();
*/
        /*create a example of pipe*/
        Pipe p = new SerialPipes(new Pipe[]{
            new TargetAssigningFromPathPipe(),
            new StoreFileExtensionPipe(),
            new GuessDateFromFilePipe(),
            new File2StringBufferPipe(),
            new MeasureLengthFromStringBufferPipe(),
            new StripHTMLFromStringBufferPipe(),
            new MeasureLengthFromStringBufferPipe("length_after_html_drop"),
            new FindUserNameInStringBufferPipe(),
            new FindHashtagInStringBufferPipe(),
            new FindUrlInStringBufferPipe(),
            new FindEmoticonInStringBufferPipe(),
            new FindEmojiInStringBufferPipe(),
            new MeasureLengthFromStringBufferPipe("length_after_cleaning_text"),
            new GuessLanguageFromStringBufferPipe(),
            new ContractionsFromStringBuffer(),
            new AbbreviationFromStringBufferPipe(),
            new SlangFromStringBufferPipe(),
            new StringBufferToLowerCasePipe(),
            new InterjectionFromStringBufferPipe(),
            new StopWordFromStringBufferPipe(),
            //new ComputePolarityFromStringBufferPipe(),
            //new NERFromStringBufferPipe(),
            //sudo ssh -L 80:textblob_ws:80 moncho@ski.4spam.group
            //new ComputePolarityTBWSFromStringBuffer("http://localhost/postjson"),
            new TeeCSVFromStringBufferPipe("output.csv", true),
            new StringBuffer2SynsetVectorPipe(),
            new SynsetVector2SynsetFeatureVectorPipe(SynsetVectorGroupingStrategy.COUNT),
            new TeeCSVFromSynsetFeatureVectorPipe("outputsyns.csv"), 
            //teeCSVDatasetFSV
        });

        if (!p.checkDependencies()){
          System.out.println("Pipe dependencies are not satisfied");
          System.out.println(Pipe.getErrorMesage());
          System.exit(1);
        }else
          System.out.println("Pipe dependencies are satisfied");

        instances = InstanceListUtils.dropInvalid(instances);

        /*Pipe all instances*/
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
