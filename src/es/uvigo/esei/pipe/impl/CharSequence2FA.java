package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CharSequence2FA extends Pipe {
    int sequencenum = 0;
    private File f;

    public CharSequence2FA(String filename) {
        f = new File(filename);
    }


    @Override
    public Instance pipe(Instance carrier) {
        try {
            FileWriter fw = new FileWriter(f, true);
            sequencenum++;

            //Visualizar las frecuencias.
            fw.write(">sequence " + sequencenum + "\n"
                    + carrier.getData().toString().replaceAll("\\s", " ")
                    + "\n\n");

            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return null;
    }

}
