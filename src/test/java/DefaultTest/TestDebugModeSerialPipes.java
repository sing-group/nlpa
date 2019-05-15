/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DefaultTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.ResumableSerialPipes;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.InstanceListUtils;
import org.ski4spam.Main;
import org.ski4spam.pipe.impl.File2StringBufferPipe;
import org.ski4spam.pipe.impl.GuessDateFromFilePipe;
import org.ski4spam.pipe.impl.MeasureLengthFromStringBufferPipe;
import org.ski4spam.pipe.impl.StoreFileExtensionPipe;
import org.ski4spam.pipe.impl.TargetAssigningFromPathPipe;
import org.ski4spam.util.textextractor.EMLTextExtractor;

/**
 *
 * @author María Novo
 */
public class TestDebugModeSerialPipes {
    
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

        /*create a example of debug mode in Serial Pipes*/
        Configurator configurator = Configurator.getInstance("./config/configuration.xml");
        configurator.setProp(Configurator.RESUMABLE_MODE, "yes");
        configurator.setProp(Configurator.DEBUG_MODE, "yes");
        configurator.setProp(Configurator.TEMP_FOLDER, "./tmp/");
        
        StoreFileExtensionPipe sfep = new StoreFileExtensionPipe();
        //sfep.setDebugging(true);
         GuessDateFromFilePipe gdff = new GuessDateFromFilePipe();
        gdff.setDebugging(true);
        AbstractPipe p0 = new ResumableSerialPipes(new AbstractPipe[]{
            sfep,
            gdff
        });

        File2StringBufferPipe f2sb = new File2StringBufferPipe();
        AbstractPipe p1 = new ResumableSerialPipes(new AbstractPipe[]{
            f2sb,
            new MeasureLengthFromStringBufferPipe()
        });
        
        AbstractPipe p = new ResumableSerialPipes(new AbstractPipe[]{
            new TargetAssigningFromPathPipe(),
            p0,
            p1
        });
              
        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }

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
