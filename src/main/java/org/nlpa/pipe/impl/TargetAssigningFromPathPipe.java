/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.TargetAssigningPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;

/**
 * This pipe assign a target to an instance keeping in mind the file path of the
 * instance
 *
 * @author José Ramón Méndez
 */
@AutoService(Pipe.class)
@TargetAssigningPipe()
public class TargetAssigningFromPathPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TargetAssigningPipe.class);

    /**
     * Return the input type included the data attribute of a Instance
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return File.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return File.class;
    }

    /**
     * A map where the key is the substring found in the file path and the value
     * the label for the object
     */
    Map<String, String> targets = null;

    /**
     * Changes the targets
     *
     * @param targets Map where the key is the substring found in the file path
     * and the value the label for the object
     */
    @PipeParameter(name = "targets", description = "A map where the key is the substring found in the file path and the value the label for the object", defaultValue = "")
    public void setTargets(Map<String, String> targets) {
        this.targets = targets;
    }

    /**
     * Retrieves the targets
     *
     * @return Map where the key is the substring found in the file path and the
     * value the label for the object
     */
    public Map<String, String> getTargets() {
        return this.targets;
    }

    /**
     * Default constructor. Create a TargetAssigningPipe using the default
     * mapping ("_spam_" for target "spam" and "_ham_" for target "ham")
     */
    public TargetAssigningFromPathPipe() {
        this(new HashMap<String, String>());
        targets.put("_ham_", "ham");
        targets.put("_spam_", "spam");
    }

    /**
     * Create a TargetAssigningPipe using the map for mapping from filepath to
     * targets
     *
     * @param targets Map of targets. The key represents the substring of the
     * path and the value the specific target.
     */
    public TargetAssigningFromPathPipe(Map<String, String> targets) {
        super(new Class<?>[0], new Class<?>[0]);

        this.targets = targets;
    }

    /**
     * Process an Instance. This method takes an input Instance, and
     * destructively modifies its target and returns it. This is the method by
     * which all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Processed instance
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
