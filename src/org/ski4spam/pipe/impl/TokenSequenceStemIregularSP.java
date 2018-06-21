/**
 *
 */
package org.ski4spam.pipe.impl;

import java.io.*;
import java.net.URISyntaxException;
import java.util.StringTokenizer;


/**
 * Stemming de t�rminos irregulares en espa�ol
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since JDK 1.5
 */

public class TokenSequenceStemIregularSP extends TokenSequenceStemIregular implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -2524895573537132886L;

    /**
     * Return this pipe
     *
     * @return this pipe
     */

    public TokenSequenceStemIregularSP getPipe() {
        return this;
    }

    protected void loadData() {
        FileReader fReader;
        BufferedReader bReader;
        try {
            //Abrir el fichero
            fReader = new FileReader(new File(TokenSequenceStemIregularSP.class.getResource("irregular_sp.stm").toURI()));
            bReader = new BufferedReader(fReader);

            //Procesar el fichero
            String var = new String();
            while ((var = bReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(var, " \n\t");
                irregularWords.put(st.nextToken(), st.nextToken());
            }
            System.out.println("Loaded: " + irregularWords.size() + " spanish irregular words.");
        } catch (IOException e) {

        } catch (URISyntaxException e) {

        } catch (Exception e) {

        }
    }
}
