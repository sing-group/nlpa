/**
 * This is part of spamHunting system
 * Please don't remove this banner
 */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.Token;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.pipe.Pipe;

import java.io.Serializable;

/**
 * @author José Ramón Méndez Reboredo
 * @version 1.0
 * @since jdk1.5
 */

public class TokenSequenceRemoveShortWords extends Pipe implements Serializable {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7236346125783022021L;

    /**
     * Mínima longitud de un token para que no sea borrado
     */
    int minLength = 3;

    /**
     * Constructor por defecto
     * minlength=3
     */
    public TokenSequenceRemoveShortWords() {

    }


    /**
     * Constructor especificando una longitud mínima de las palabras
     *
     * @param minLength Longitud mínima que debe tener un token para no ser
     *                  eliminado
     */
    public TokenSequenceRemoveShortWords(int minLength) {

    }

    public TokenSequenceRemoveShortWords getPipe() {
        return this;
    }

    /* (non-Javadoc)
     * @see es.uvigo.esei.pipe.Pipe#pipe(es.uvigo.esei.ia.types.Instance)
     */
    @Override
    public Instance pipe(Instance carrier) {
        TokenSequence ts = (TokenSequence) carrier.getData();

        // xxx This doesn't seem so efficient. Perhaps have TokenSequence
        // use a LinkedList, and remove Tokens from it?
        TokenSequence ret = new TokenSequence();

        for (int i = 0; i < ts.size(); i++) {

            Token t = ts.getToken(i);

            if (t.getText().length() >= minLength)
                ret.add(t);
        }

        carrier.setData(ret);
        return carrier;
    }

}
