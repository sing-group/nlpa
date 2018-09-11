package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.TargetAssigningPipe;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This pipe assign a target to an instance keeping in mind the file path of the instance
 *
 * @author José Ramón Méndez Reboredo
 */
@TargetAssigningPipe()
public class TargetAssigningFromPathPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(TargetAssigningPipe.class);

    @Override
    public Class getInputType() {
        //TODO what data type goes here?
        return File.class;
    }

    @Override
    public Class getOutputType() {
        //TODO what data type goes here?
        return File.class;
    }

    /**
     * A map where the key is the substring found in the file path and the value the label for the object
     */
    Map<String, String> targets = null;

    /**
     * Create a TargetAssigningPipe using the default mapping ("_spam_" for target "spam" and "_ham_" for target "ham")
     */
    public TargetAssigningFromPathPipe() {
        targets = new HashMap();
        targets.put("_ham_", "ham");
        targets.put("_spam_", "spam");
    }

    /**
     * Create a TargetAssigningPipe using the map for mapping from filepath to targets
     *
     * @targets Map of targets. The key represents the substring of the path and the value the specific target.
     */
    public TargetAssigningFromPathPipe(Map<String, String> targets) {
        this.targets = targets;
    }

    @Override
    public Instance pipe(Instance carrier) {
        Set<String> keys = targets.keySet();
        for (String path : keys) {
            if (((String) carrier.getName()).contains(path)) {
                String target = targets.get(path);
                logger.info("Assigning \"" + target + "\" target for instance " + carrier.toString());
                carrier.setTarget(target);
                break;
            }
        }
        return carrier;
    }
}