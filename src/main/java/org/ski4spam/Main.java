package org.ski4spam;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;
import org.bdp4j.ia.util.InstanceListUtils;
import org.bdp4j.pipe.ParameterPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SerialPipes;
import org.ski4spam.pipe.impl.File2StringBufferPipe;
import org.ski4spam.pipe.impl.GuessDateFromFile;
import org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe;
import org.ski4spam.pipe.impl.MeasureLengthFromStringBufferPipe;
import org.ski4spam.pipe.impl.StoreFileExtensionPipe;
import org.ski4spam.pipe.impl.StoreTweetLangPipe;
import org.ski4spam.pipe.impl.StripHTMLFromStringBufferPipe;
import org.ski4spam.pipe.impl.SynsetVector2SynsetFeatureVector;
import org.ski4spam.pipe.impl.TargetAssigningFromPathPipe;
import org.ski4spam.pipe.impl.TeeCSVFromStringBufferPipe;
import org.ski4spam.util.textextractor.EMLTextExtractor;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private static List<Instance> instances = new ArrayList<Instance>();

    public static void main(String[] args) {

        System.out.println("Program started.");

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
        Method[] methods = SynsetVector2SynsetFeatureVector.class.getMethods();
        ParameterPipe parameterPipe;
        for (Method method : methods) {
            parameterPipe = method.getAnnotation(ParameterPipe.class);//Obtienes los métodos que tengan alguna anotación de tipo ParameterPipe
           if (parameterPipe != null) {
                String parameterName = parameterPipe.name();
                String parameterDescription = parameterPipe.description();
                Class<?>[] types = method.getParameterTypes(); // Obtienes los tipos de los parámetros para un método
                System.out.println(parameterName + " --> " + parameterDescription);
            }   
        }

        
            /*create a example of pipe*/
            Pipe p = new SerialPipes(new Pipe[]{
                new TargetAssigningFromPathPipe(),
                new StoreFileExtensionPipe(),
                new StoreTweetLangPipe(),
                new StoreTweetLangPipe(),
                new GuessDateFromFile(),
                new File2StringBufferPipe(),
                new MeasureLengthFromStringBufferPipe(),
                new StripHTMLFromStringBufferPipe(),
                new MeasureLengthFromStringBufferPipe("length_after_html_drop"),
                new GuessLanguageFromStringBufferPipe(),
                new TeeCSVFromStringBufferPipe("output.csv", true)
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

    static class FileMng {

        static void visit(Path path) {
            File data = path.toFile();
            String target = null;
            String name = data.getPath();
            File source = data;

            instances.add(new Instance(data, target, name, source));
        }
    }

}
