import org.ski4spam.pipe.impl.File2StringBufferPipe;
import org.ski4spam.pipe.SerialPipes;
import org.ski4spam.ia.types.Instance;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

import org.ski4spam.util.EMLTextExtractor;

public class Main {
    private static ArrayList<Instance> instances = new ArrayList<>();

    public static void main(String[] args) {
		
        System.out.println("Program started.");
		
		if (args.length==0) generateInstances("tests/");
		else generateInstances(args[0]);


        //Configurations
		EMLTextExtractor.setCfgPartSelectedOnAlternative("text/plain");


        for (Instance i : instances) {
            System.out.println(((File) i.getData()).getPath());
        }
		
		/*create a example of pipe*/
		SerialPipes p=new SerialPipes();
		
		p.add(new File2StringBufferPipe());	

        /*Pipe all instances*/
		for (Instance i:instances){
			p.pipe(i);
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
                Properties props = new Properties();

                instances.add(new Instance(data, target, name, source));
            }
        }
    }

}
