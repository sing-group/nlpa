package org.ski4spam.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * This pipe parses Instances to csv format.
 * It can be for showing it on terminal or exporting it to .csv file.
 *
 * @author Yeray Lage Freitas
 */
public class File2CsvPipe extends Pipe {
    private static final Logger logger = LogManager.getLogger(File2CsvPipe.class);
    private static FileWriter fw;
    private static BufferedWriter bw;

    @Override
    public Instance pipe(Instance carrier) {
        try {
            fw = new FileWriter("output.csv", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);

        Hashtable<String, Object> properties = carrier.getProperties();
        System.out.println(carrier.getName());
        System.out.println("\t" + properties.entrySet());

        String props = "";
        for (Map.Entry<String, Object> p : carrier.getProperties().entrySet()) {
            if ((p.getValue()) instanceof Double) {
                p.setValue(String.valueOf(p.getValue()));
            }

            props += "\"" + p.getValue() + "\",";
        }

        try {
            System.out.println(props.substring(0, props.length() - 1) + "\n");
            bw.write(props.substring(0, props.length() - 1) + "\n");
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
