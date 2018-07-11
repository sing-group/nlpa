import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.ia.util.InstanceListUtils;
import org.ski4spam.pipe.SerialPipes;
import org.ski4spam.pipe.impl.*;
import org.ski4spam.util.textextractor.EMLTextExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static List<Instance> instances = new ArrayList<Instance>();

    public static void main(String[] args) {

        System.out.println("Program started.");

        if (args.length == 0) generateInstances("tests/");
        else generateInstances(args[0]);


        //Configurations
        EMLTextExtractor.setCfgPartSelectedOnAlternative("text/plain");


        for (Instance i : instances) {
            logger.info("Instance data before pipe: " + i.getData().toString());
        }

        /*create a example of pipe*/
        SerialPipes p = new SerialPipes();

        p.add(new StoreFileExtensionPipe());
        p.add(new StoreTweetLangPipe());
        p.add(new File2StringBufferPipe());
        p.add(new StripHTMLFromStringBufferPipe());
        p.add(new GuessLanguageFromStringBufferPipe());
        p.add(new TeeCSVFromStringBufferPipe(true));

        /*Pipe all instances*/
        for (Instance i : instances) {
            p.pipe(i);
        }

        instances = InstanceListUtils.dropInvalid(instances);

        for (Instance i : instances) {
            logger.info("Instance data after pipe: " + i.getSource() + " " +
                    (((i.getData().toString().length()) > 10) ?
                            (i.getData().toString().substring(0, 10) + "...") :
                            i.getData().toString()));
        }

    }

    private static void generateInstances(String testDir) {
        String[] folders = {"hsspam14", "smsspamcollection", "spamassassin", "www", "youtube"};
        String[] targets = {"ham", "spam"};

        for (String folder : folders) {
            for (String target : targets) {
                listFilesForFolder(new File(testDir + folder + "/_" + target + "_"), target);
            }
        }
    }

    private static void listFilesForFolder(final File folder, String type) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, type);
            } else {
                File data = fileEntry;
                String target = type;
                String name = fileEntry.getPath();
                File source = fileEntry;

                instances.add(new Instance(data, target, name, source));
            }
        }
    }

}
