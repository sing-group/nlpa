/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DefaultTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bdp4j.types.Dataset;
import weka.core.Instance;
import weka.core.Attribute;
import weka.core.converters.CSVLoader;
import weka.core.Instances;

/**
 *
 * @author María Novo
 */
public class TestLoadCSVFromWeka {

    /*
    * The main method for the running application
     */
    public static void main(String[] args) {

        try {
            CSVLoader loader = new CSVLoader();

//            String csvFilePath = "output/outputsyns_smsTest.csv";
            //String csvFilePath = "output/outputsyns_20200324.csv";
            String csvFilePath = "output/outputsyns_spamassassin.csv";
            // loader.setFieldSeparator(";");
            loader.setNominalAttributes("0-1");

            //loader.setEnclosureCharacters("\";\'");
            //loader.setMissingValue("");
            loader.setSource(new File(csvFilePath));

            // loader.setSource(new File(csvFilePath));
            Instances data = loader.getDataSet();

            int numAtt = 0;

            Enumeration<Attribute> attributeEnum = data.enumerateAttributes();
            while (attributeEnum.hasMoreElements()) {
                attributeEnum.nextElement();
                numAtt++;
            }
            
            int numInst= 0;
             Enumeration<Instance> instanceEnum = data.enumerateInstances();
            while (instanceEnum.hasMoreElements()) {
                instanceEnum.nextElement();
                numInst++;
            }
            
            System.out.println("numAtt: " + numAtt);
            System.out.println("numInst: " + numInst);
//            System.out.println(data.get(0).attribute(0).name());
//            System.out.println(data.get(0).attribute(1).name());
            
        } catch (IOException ex) {
            Logger.getLogger(TestLoadCSVFromWeka.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

}
