/**
 *
 */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.util.Pair;

import java.io.*;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Lematizador por el algoritmo de Porter. Implementaci�n en espa�ol
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since JDK 1.5
 */

public class TokenSequencePorterStemmerSP extends TokenSequencePorterStemmer implements Serializable {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -7847999477724448922L;

    /**
     * Metodo main para llamar desde linea de comando
     *
     * @param args par�metros pasados por l�nea de comando
     */
    public static void main(String args[]) {
        new TokenSequencePorterStemmerSP();
    }

    /**
     * Return this pipe
     *
     * @return this pipe
     */

    public TokenSequencePorterStemmerSP getPipe() {
        return this;
    }

    /**
     * Carga las reglas espec�ficas para Espa�ol
     *
     * @see es.uvigo.esei.pipe.impl.TokenSequencePorterStemmer#loadRules()
     */
    protected void loadRules() {
        Reader iStream;
        try {
            iStream = new FileReader(
                    new File(
                            getClass().getResource("porter_sp.stm").toURI()
                    )
            );

            BufferedReader bReader = new BufferedReader(iStream);

            String line = null;
            StringTokenizer sTokenizer = null;
            while ((line = bReader.readLine()) != null) {
                //Si tiene comentario se lo saco
                if (line.indexOf("#") >= 0) {
                    line = line.substring(0, line.indexOf("#"));
                }

                //Si no queda vac�a despu�s de borrar el comentario
                if (!line.trim().equals("")) {
                    //Leer la regla de la l�nea del fichero
                    sTokenizer = new StringTokenizer(line);
                    String tokens[] = new String[5];
                    final int PASO = 0;
                    final int TERM = 1;
                    final int TERM_CAMBIO = 2;
                    final int MIN_ROOT_SIZE = 3;
                    final int COND = 4;

                    int cuentaTokens = 0;
                    while (sTokenizer.hasMoreTokens() && cuentaTokens <= 5) {
                        //Se todos los tokens de la regla
                        tokens[cuentaTokens] = sTokenizer.nextToken();
                        cuentaTokens++;
                    }

                    if (cuentaTokens == 5) { //Si has leido 5 tokens
                        //Si el cambio es un "*" significa cadena vac�a
                        if (tokens[TERM_CAMBIO].trim().equals("*"))
                            tokens[TERM_CAMBIO] = "";
                        if (tokens[TERM].trim().equals("*"))
                            tokens[TERM] = "";

                        //Introducir en la lista de reglas
                        Regla regla = null;
                        try {
                            regla = new Regla(
                                    tokens[TERM],
                                    tokens[TERM_CAMBIO],
                                    Integer.parseInt(tokens[MIN_ROOT_SIZE]),
                                    Integer.parseInt(tokens[COND])
                            );
                        } catch (NumberFormatException nfe) {
                        }

                        //Si se han procesado bien los campos num�ricos
                        if (regla != null) {
                            //Poner la regla en la lista correspondiente.

                            //Si ya existe una lista para ese paso
                            Vector<Regla> paso = null;
                            for (int i = 0; i < allRules.size() && paso == null; i++)
                                paso = allRules.elementAt(i).getObj1().trim().equals(tokens[PASO].trim()) ?
                                        allRules.elementAt(i).getObj2() : null;

                            //A�adir un nuevo paso
                            if (paso == null) {
                                paso = new Vector<Regla>();
                                allRules.add(new Pair<String, Vector<Regla>>(tokens[PASO].trim(), paso));
                            }

                            //A�adir la regla en el paso
                            paso.add(regla);
                        }
                    }
                }
            }
            bReader.close();
            iStream.close();
        } catch (FileNotFoundException e) {
        } catch (URISyntaxException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }

        //Una vez cargados los pasos hay que ordenarlos alfab�ticamente por nombre
        //Se implementa mediante un burbuja.
        sortPasos();

        //Debug
        for (Pair<String, Vector<Regla>> p : allRules) {
            System.out.println("Rule-----------> " + p.getObj1());
            for (Regla r : p.getObj2()) {
                System.out.println(r);
            }
        }

        System.out.println(super.extraerRaiz("cant�bamos"));
        System.out.println(super.extraerRaiz("camiones"));
        System.out.println(super.extraerRaiz("pollos"));
        System.out.println(super.extraerRaiz("pat�"));
    }
}
