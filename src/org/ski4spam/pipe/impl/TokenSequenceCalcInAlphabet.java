/**
 *
 */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Alphabet;
import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.Token;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.pipe.Pipe;

import java.io.Serializable;

/**
 * Calcular el alfabeto de entrada
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk1.5
 */

public class TokenSequenceCalcInAlphabet extends Pipe implements Serializable {
    /**
     * Serial versionUID
     */
    private static final long serialVersionUID = -5770363610164873256L;

    /**
     * Alphabeto de entrada
     */
    private Alphabet alphabet = null;

    /**
     * Para que no se pueda usar
     */
    public TokenSequenceCalcInAlphabet() {

    }

    /**
     * Construcor con la referencia al alphabeto de entrada
     *
     * @param alphabet referencia al alphabeto de entrada
     */
    public TokenSequenceCalcInAlphabet(Alphabet alphabet) {
        if (alphabet == null) this.alphabet = new Alphabet();
        else this.alphabet = alphabet;
    }

    /**
     * Return this pipe
     *
     * @return this pipe
     */

    public TokenSequenceCalcInAlphabet getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {
        TokenSequence ts = (TokenSequence) carrier.getData();

        for (int i = 0; i < ts.size(); i++) {
            //recuperar el token
            Token t = ts.getToken(i);
            //A�adir el token al alphabeto
            alphabet.lookupIndex(t.getText(), true);
        }

        return carrier;
    }

    /**
     * @return Retorna el alfabeto de entrada.
     */

    public Alphabet getAlphabet() {
        return alphabet;
    }


    /**
     * @param alphabet El alfabeto de entrada.
     */

    public void setAlphabet(Alphabet alphabet) {
        if (alphabet == null) this.alphabet = new Alphabet();
        else this.alphabet = alphabet;
    }


}
