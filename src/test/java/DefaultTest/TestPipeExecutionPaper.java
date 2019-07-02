package DefaultTest;

import org.nlpa.pipe.impl.GuessDateFromFilePipe;
import org.nlpa.pipe.impl.StoreFileExtensionPipe;
import org.nlpa.pipe.impl.TargetAssigningFromPathPipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.bdp4j.util.InstanceListUtils;
import org.nlpa.util.textextractor.EMLTextExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author María Novo
 */
public class TestPipeExecutionPaper {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TestPipeExecutionPaper.class);

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

        /* Create the processing pipe */
        AbstractPipe p = new SerialPipes(new AbstractPipe[]{
            new TargetAssigningFromPathPipe(),
            new StoreFileExtensionPipe(),
            new GuessDateFromFilePipe()
        });

        logger.info("orchest:" + p.toString() + "\n");

        /*Check orchestration dependencyes*/
        if (!p.checkDependencies()) {
            logger.fatal(
                    "[CHECK DEPENDENCIES] "
                    + AbstractPipe.getErrorMessage()
            );
            System.exit(-1);
        }

        /*Load and pipe the current burst*/
        ArrayList<Instance> burst = new ArrayList<>();
        p.pipeAll(burst);

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
