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
import org.nlpa.pipe.impl.File2StringBufferPipe;
import org.nlpa.pipe.impl.FindEmojiInStringBufferPipe;
import org.nlpa.pipe.impl.FindEmoticonInStringBufferPipe;
import org.nlpa.pipe.impl.FindUrlInStringBufferPipe;
import org.nlpa.pipe.impl.GuessDateFromFilePipe;
import org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe;
import org.nlpa.pipe.impl.InterjectionFromStringBufferPipe;
import org.nlpa.pipe.impl.MeasureLengthFromStringBufferPipe;
import org.nlpa.pipe.impl.StopWordFromStringBufferPipe;
import org.nlpa.pipe.impl.StoreFileExtensionPipe;
import org.nlpa.pipe.impl.StringBufferToLowerCasePipe;
import org.nlpa.pipe.impl.StripHTMLFromStringBufferPipe;
import org.nlpa.pipe.impl.TargetAssigningFromPathPipe;
import org.nlpa.pipe.impl.TeeCSVFromStringBufferPipe;

public class AppCore {
	/**
	 * A logger for logging purposes
	 */
	private static final Logger logger = LogManager.getLogger(AppCore.class);

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
			new GuessLanguageFromStringBufferPipe(),
			new FindEmojiInStringBufferPipe("emojiTest", false, GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY, true, true),
			new FindEmoticonInStringBufferPipe("emoticonTest", false, GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY, true, true),
			new StringBufferToLowerCasePipe(), 
			new TeeCSVFromStringBufferPipe("output.csv", true), 
	});

	private static void checkDependencies() {
		if (!p.checkDependencies()) {
			System.out.println("Pipe dependencies are not satisfied");
			System.exit(1);
		} else {
			System.out.println("Pipe dependencies are satisfied");
		}
	}

	/**
	 * Creates a Instance from a StringBuffer and process it
	 * 
	 * @param str text that will be processed
	 * @return the instance after being processed by the pipes
	 */
	public static Instance calculateStringPolarities(StringBuffer str) {
		checkDependencies();

		Instance carrier = new Instance(str, "polarity", "TextPolarity", str);
		// Create the output directory if it doesn't exist
		File outputDirectory = new File("./output");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}
		return p.pipe(carrier);
	}

	/**
	 * Process the files within a folder
	 * @return the collection of instances after being processed by the pipes
	 */
	public static Collection<Instance> calculateFilesPolarities() {
		checkDependencies();

		instances = InstanceListUtils.dropInvalid(instances);

		// Create the output directory if it doesn't exist
		File outputDirectory = new File("./output");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}

		return p.pipeAll(instances);
	}

	/**
	 * Generate a instance List on instances attribute by recursively finding all
	 * files included in testDir directory
	 *
	 * @param testDir The directory where the instances should be loaded
	 */
	public static void generateInstances(String testDir) {
		instances = new ArrayList<Instance>();
		try {
			Files.walk(Paths.get(testDir)).filter(Files::isRegularFile).forEach(FileMng::visit);
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
		 * Include a file in the instancelist
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
