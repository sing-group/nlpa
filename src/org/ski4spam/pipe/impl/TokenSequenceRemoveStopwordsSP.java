package es.uvigo.esei.pipe.impl;

import java.io.*;
import java.net.URISyntaxException;


/**
 * Borra la lista de parada en Castellano
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk 1.5
 */

public class TokenSequenceRemoveStopwordsSP extends TokenSequenceRemoveStopwords implements Serializable {

    /**
     * Serial verson UID
     */
    private static final long serialVersionUID = -5479679926758851507L;


    public TokenSequenceRemoveStopwordsSP getPipe() {
        return this;
    }

    protected void loadData() {
        try {
            //Cargo el fichero con bufferedReader para poder leer cada l�nea (que contiene una palabra)
            FileReader fr = new FileReader(new File(TokenSequenceRemoveStopwords.class.getResource("stop_sp.wrd").toURI()));
            BufferedReader br = new BufferedReader(fr);

            //Leo una a una las palabras y las guardo en la stoplist
            String stopWord = null;
            while ((stopWord = br.readLine()) != null)
                stoplist.add(stopWord);

            //Cierro los streams
            br.close();
            fr.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println("File Not Found Exception: " + fnfe.toString());
        } catch (URISyntaxException use) {
            System.out.println("File Not Found Exception: " + use.toString());
        } catch (Exception e) {
            System.out.println("File Not Found Exception: " + e.toString());
        }
    }
}
