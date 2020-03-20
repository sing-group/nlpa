package org.nlpa.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.bdp4j.util.InstanceListUtils;
import org.nlpa.Main;
import org.nlpa.pipe.impl.AbbreviationFromStringBufferPipe;
import org.nlpa.pipe.impl.ComputePolarityFromLexiconPipe;
import org.nlpa.pipe.impl.ComputePolarityFromSynsetPipe;
import org.nlpa.pipe.impl.ContractionsFromStringBufferPipe;
import org.nlpa.pipe.impl.File2StringBufferPipe;
import org.nlpa.pipe.impl.FindUrlInStringBufferPipe;
import org.nlpa.pipe.impl.GuessDateFromFilePipe;
import org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe;
import org.nlpa.pipe.impl.InterjectionFromStringBufferPipe;
import org.nlpa.pipe.impl.MeasureLengthFromStringBufferPipe;
import org.nlpa.pipe.impl.SaveOriginalTextPropertyPipe;
import org.nlpa.pipe.impl.SlangFromStringBufferPipe;
import org.nlpa.pipe.impl.StopWordFromStringBufferPipe;
import org.nlpa.pipe.impl.StoreFileExtensionPipe;
import org.nlpa.pipe.impl.StringBuffer2SynsetSequencePipe;
import org.nlpa.pipe.impl.StringBufferToLowerCasePipe;
import org.nlpa.pipe.impl.StripHTMLFromStringBufferPipe;
import org.nlpa.pipe.impl.SynsetSequence2FeatureVectorPipe;
import org.nlpa.pipe.impl.TargetAssigningFromPathPipe;
import org.nlpa.pipe.impl.TeeCSVFromStringBufferPipe;
import org.nlpa.types.SequenceGroupingStrategy;

public class AppCore {
	
	/**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * List of instances that are being processed
     */
    private static List<Instance> instances = new ArrayList<Instance>();
    
    static AbstractPipe p = new SerialPipes(new AbstractPipe[] {
    		new TargetAssigningFromPathPipe(),
    		new StoreFileExtensionPipe(),
            new GuessDateFromFilePipe(), 
            new File2StringBufferPipe(),
            new MeasureLengthFromStringBufferPipe(),
            new FindUrlInStringBufferPipe(),
            new StripHTMLFromStringBufferPipe(),
            new MeasureLengthFromStringBufferPipe("length_after_html_drop"), 
            new SaveOriginalTextPropertyPipe(),
            new GuessLanguageFromStringBufferPipe(),
            new ContractionsFromStringBufferPipe(),
            new AbbreviationFromStringBufferPipe(),
            new SlangFromStringBufferPipe(),
            new StringBufferToLowerCasePipe(),
            new ComputePolarityFromLexiconPipe(),
//            new InterjectionFromStringBufferPipe(),
//            new StopWordFromStringBufferPipe(),
            new TeeCSVFromStringBufferPipe("output.csv", true), 
            new StringBuffer2SynsetSequencePipe(),
            new SynsetSequence2FeatureVectorPipe(SequenceGroupingStrategy.COUNT),
            new ComputePolarityFromSynsetPipe()
    });

	public static Instance computeStringPolarity(StringBuffer str) {
		checkDepedencies();
		
        Instance inst = new Instance(str, "polarity", "Test instance ID", str);
        inst.setProperty("language", "EN");

        return p.pipe(inst);
    }
	
	public static Collection<Instance> computeFilesPolarity() {
		checkDepedencies();
		instances = InstanceListUtils.dropInvalid(instances);
		
		// Create the output directory if it doesn't exist
		File outputDirectory = new File("./output");
	    if (!outputDirectory.exists()) {
	    	outputDirectory.mkdir();
	    }
		
        return p.pipeAll(instances);
    }
	
	private static void checkDepedencies() {
		if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
          System.out.println(AbstractPipe.getErrorMessage());
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }
	}
	
	/**
     * Generate a instance List on instances attribute by recursivelly finding
     * all files included in testDir directory
     *
     * @param testDir The directory where the instances should be loaded
     */
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
