package org.nlpa.iu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.bdp4j.util.InstanceListUtils;
import org.nlpa.Main;
import org.nlpa.pipe.impl.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppCore {
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * List of instances that are being processed
     */
    private static List<Instance> instances = new ArrayList<>();

    static AbstractPipe p = new SerialPipes(new AbstractPipe[]{
            new TargetAssigningFromPathPipe(),
            new StoreFileExtensionPipe(),
            new GuessDateFromFilePipe(),
            new File2StringBufferPipe(),
            new StripHTMLFromStringBufferPipe(),
            new GuessLanguageFromStringBufferPipe(),
            new NewNERFromStringBufferPipe(GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY,true, true, true, true),
    });

    private static void checkDependencies() {
        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }
    }
    private static void checkDependencies(AbstractPipe p) {
        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }
    }

    public static Collection<Instance> findEntitiesInString(StringBuffer str) {
        checkDependencies();
        instances = new ArrayList<Instance>();
        Instance ins = new Instance(str, "entity", "NER", str);
        instances.add(ins);

        // Create the output directory if it doesn't exist
        File outputDirectory = new File("./output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
        return p.pipeAll(instances);
    }

    /**
     * Process the files within a folder
     * @return the collection of instances after being processed by the pipes
     */
    public static Collection<Instance> findEntitiesInFiles() {
        checkDependencies();
        List<Instance> newInstances = new ArrayList<>(InstanceListUtils.dropInvalid(instances));
        instances.clear();

        // Create the output directory if it doesn't exist
        File outputDirectory = new File("./output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdir();
        }

        return p.pipeAll(newInstances);
    }

    public static void generateInstances(String testDir) {
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

        static void visit(Path path) {
            File data = path.toFile();
            String target = null;
            String name = data.getPath();
            File source = data;

            instances.add(new Instance(data, target, name, source));
        }
    }
}
