package org.nlpa.iu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.nlpa.Main;
import org.nlpa.pipe.impl.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AppCore {
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * List of instances that are being processed
     */
    private static List<Instance> instances = new ArrayList<Instance>();

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
