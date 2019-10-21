package DefaultTest;

import org.nlpa.pipe.impl.TeeDatasetFromFeatureVectorPipe;
import org.nlpa.pipe.impl.StopWordFromStringBufferPipe;
import org.nlpa.pipe.impl.FindEmojiInStringBufferPipe;
import org.nlpa.pipe.impl.FindUrlInStringBufferPipe;
import org.nlpa.pipe.impl.StripHTMLFromStringBufferPipe;
import org.nlpa.pipe.impl.TeeCSVFromStringBufferPipe;
import org.nlpa.pipe.impl.GuessDateFromFilePipe;
import org.nlpa.pipe.impl.SlangFromStringBufferPipe;
import org.nlpa.pipe.impl.FindEmoticonInStringBufferPipe;
import org.nlpa.pipe.impl.FindUserNameInStringBufferPipe;
import org.nlpa.pipe.impl.TargetAssigningFromPathPipe;
import org.nlpa.pipe.impl.StringBufferToLowerCasePipe;
import org.nlpa.pipe.impl.TeeCSVFromFeatureVectorPipe;
import org.nlpa.pipe.impl.AbbreviationFromStringBufferPipe;
import org.nlpa.pipe.impl.StringBuffer2SynsetSequencePipe;
import org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe;
import org.nlpa.pipe.impl.MeasureLengthFromStringBufferPipe;
import org.nlpa.pipe.impl.InterjectionFromStringBufferPipe;
import org.nlpa.pipe.impl.ComputePolarityFromStringBufferPipe;
import org.nlpa.pipe.impl.NERFromStringBufferPipe;
import org.nlpa.pipe.impl.StoreFileExtensionPipe;
import org.nlpa.pipe.impl.FindHashtagInStringBufferPipe;
import org.nlpa.pipe.impl.File2StringBufferPipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.transformers.Date2MillisTransformer;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.types.Instance;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.InstanceListUtils;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author María Novo
 */
public class TestPipeExecution {
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TestPipeExecution.class);

    /**
     * List of instances that are being processed
     */
    private static List<Instance> instances = new ArrayList<Instance>();

    /*
     * The main method for the running application
     */
    public static void main(String[] args) {
        String testPath = "src/test/java/resources/";
        if (args.length == 0) {
            generateInstances(testPath + "tests3");
        } else {
            generateInstances(args[0]);
        }

        //Configurations
        try {
            EMLTextExtractor.setCfgPartSelectedOnAlternative("text/plain");
        } catch (Exception x) {
            x.printStackTrace();
        }

        for (Instance i : instances) {
            logger.info("Instance data before pipe: " + i.getData().toString());
        }

        // Parámetro para el transformador Enum2IntTransformer de la propiedad target
        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target",  new Enum2IntTransformer(transformList));

        TeeDatasetFromFeatureVectorPipe teeDatasetFSV = new TeeDatasetFromFeatureVectorPipe();
        teeDatasetFSV.setTransformersList(transformersList);

        /*create a example of pipe*/
        AbstractPipe p = new SerialPipes(new AbstractPipe[]{
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
                new AbbreviationFromStringBufferPipe(),
                new StringBufferToLowerCasePipe(),
                new GuessLanguageFromStringBufferPipe(),
                new SlangFromStringBufferPipe(),
                new InterjectionFromStringBufferPipe(),
                new StopWordFromStringBufferPipe(),
                new ComputePolarityFromStringBufferPipe(),
                new NERFromStringBufferPipe(),
                new TeeCSVFromStringBufferPipe("output.csv", true),
                new StringBuffer2SynsetSequencePipe(),
                //new SynsetVector2SynsetFeatureVectorPipe(SynsetVectorGroupingStrategy.COUNT),
                new TeeCSVFromFeatureVectorPipe("outputsyns.csv"),
                teeDatasetFSV
        });

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
            Configurator.setIrrecoverableErrorInfo("IOException found " + e.getMessage());
            Configurator.getActionOnIrrecoverableError().run();
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
