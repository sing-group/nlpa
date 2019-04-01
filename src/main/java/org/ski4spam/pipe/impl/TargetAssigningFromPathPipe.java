package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.TargetAssigningPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This pipe assign a target to an instance keeping in mind the file path of the instance
 *
 * @author José Ramón Méndez
 */
@TargetAssigningPipe()
public class TargetAssigningFromPathPipe extends AbstractPipe{
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TargetAssigningPipe.class);

    /**
     * Return the input type included the data attribute of a Instance
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return File.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after processing
     * @return the datatype expected in the data attribute of a Instance after processing
     */
    @Override
    public Class<?> getOutputType() {
        return File.class;
    }

    /**
     * A map where the key is the substring found in the file path and the value the label for the object
     */
    Map<String, String> targets = null;

    public void setTargets(Map<String, String> targets){
        this.targets = targets;
    }
    
    public Map<String, String> getTargets(){
        return this.targets;
    }
    
    /**
     * Create a TargetAssigningPipe using the default mapping ("_spam_" for target "spam" and "_ham_" for target "ham")
     */

    public TargetAssigningFromPathPipe() {
        this(new HashMap<String,String>());

        targets.put("_ham_", "ham");
        targets.put("_spam_", "spam");
        
    }

    /**
     * Create a TargetAssigningPipe using the map for mapping from filepath to targets
     *
     * @param targets Map of targets. The key represents the substring of the path and the value the specific target.
     */
    public TargetAssigningFromPathPipe(Map<String, String> targets) {
        super(new Class<?>[0],new Class<?>[0]);

        this.targets = targets;
    }

    /**
    * Process an Instance.  This method takes an input Instance,
    * destructively modifies it in some way, and returns it.
    * This is the method by which all pipes are eventually run.
    *
    * @param carrier Instance to be processed.
    * @return Instancia procesada
    */
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