package es.uvigo.esei.pipe.impl;

import java.io.*;
import java.net.URISyntaxException;
import java.util.StringTokenizer;


/**
 * Stemmer para palabras irregulares en Ingl�s
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since Jdk 1.5
 */

public class TokenSequenceStemIregularEN extends TokenSequenceStemIregular implements Serializable {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -8306103630062326293L;

    /**
     * Return this pipe
     *
     * @return Return this pipe
     */

    public TokenSequenceStemIregular getPipe() {
        return this;
    }

    protected void loadData() {
        FileReader fReader;
        BufferedReader bReader;
        try {
            //Abrir el fichero
            fReader = new FileReader(new File(TokenSequenceStemIregularEN.class.getResource("irregular_en.stm").toURI()));
            bReader = new BufferedReader(fReader);

            //Procesar el fichero
            String var = new String();
            while ((var = bReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(var, " \n\t");
                irregularWords.put(st.nextToken(), st.nextToken());
            }
            System.out.println("Loaded: " + irregularWords.size() + " english irregular words.");
        } catch (IOException e) {

        } catch (URISyntaxException e) {

        } catch (Exception e) {

        }
    }
}
