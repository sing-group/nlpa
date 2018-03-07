/**
 *
 */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.pipe.Pipe;

import java.io.Serializable;

/**
 * Cuenta el n�mero de tokens que tiene una instancia. Esto permite
 * luego calcular la frecuencia de t�rminos si se hace al principio.
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk 1.5
 */

public class TokenSequenceCountTokens extends Pipe implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5727184475425304746L;
    /**
     * Clave que se usar� para anotar en la hashtable
     */
    private String key = "TextTokenNumber";

    /**
     * Constructor trivial
     */
    public TokenSequenceCountTokens() {
    }

    /**
     * Constructor no trivial
     *
     * @param key clave que se usar� para anotar el tama�o en tokens
     *            dentro de las propiedades de la hashtable
     */
    public TokenSequenceCountTokens(String key) {
        this.key = key;
    }

    /**
     * Return the pipe
     *
     * @return the pipe
     */

    public TokenSequenceCountTokens getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {
        TokenSequence ts = (TokenSequence) carrier.getData();

        Integer i = new Integer(ts.size());

        carrier.setProperty(key, i);

        return carrier;
    }

}
