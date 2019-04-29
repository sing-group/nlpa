package org.ski4spam.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * Load configurations from config file
 *
 * @author Jose Ramon Mendez
 */
public class Configuration {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Configuration.class);

    /**
     * The configuration file
     */
    public static final String DEFAULT_CONFIG_FILE = "config/configurations.ini";

    /**
     * a Wini instance to load properties from .ini files (Similar to properties
     * but with sections)
     */
    private Wini ini;

    /**
     * The default configuration
     */
    private static Configuration cfg = null;

    /**
     * Retrieve the system configuration
     *
     * @return The system configuration
     */
    public static Configuration getSystemConfig() {
        if (cfg == null) {
            cfg = new Configuration();
        }
        return cfg;
    }

    /**
     * Changes the path for the current configuration
     *
     * @param newPath for the configuration
     */
    public static void changeConfigPath(String newPath) {
        cfg = new Configuration(newPath);
    }

    /**
     * Default constructor for the class
     */
    private Configuration() {
        ini = null;
        try {
            ini = new Wini(new File(DEFAULT_CONFIG_FILE));
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage());
        }
    }

    /**
     * Init the configuration using another configuration path
     *
     * @param configPath The new configuration path to load the configuration
     */
    private Configuration(String configPath) {
        ini = null;
        try {
            ini = new Wini(new File(configPath));
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage());
        }
    }

    public String getConfigOption(String group, String option) {
        String retValue;

        retValue = ini.get(group, option);
        if (retValue != null) {
            retValue = retValue.replaceAll("<space>", " ");
        }

        return retValue;
    }

}
