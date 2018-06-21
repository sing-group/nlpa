package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.ia.types.Token;
import org.ski4spam.ia.types.TokenSequence;
import org.ski4spam.pipe.Pipe;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Stemmer de t�rminos irregulares gen�rico y abstracto
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since JDK 1.5
 */
public abstract class TokenSequenceStemIregular extends Pipe implements Serializable {

    protected Hashtable<String, String> irregularWords = new Hashtable<String, String>(1500);

    /**
     * Constructor por defecto de la clase abstracta
     */
    public TokenSequenceStemIregular() {
        loadData();
    }

    @Override
    public Instance pipe(Instance carrier) {

        TokenSequence ts = (TokenSequence) carrier.getData();

        TokenSequence ret = new TokenSequence();

        for (int i = 0; i < ts.size(); i++) {
            Token token = ts.getToken(i);


            //Si el token es irregular se cambia el texto
            String changeTxt = null;
            if ((changeTxt = irregularWords.get(token.getText())) != null)
                token.setText(changeTxt);

            ret.add(token);
        }

        carrier.setData(ret);

        return carrier;
    }

    protected abstract void loadData();
}
