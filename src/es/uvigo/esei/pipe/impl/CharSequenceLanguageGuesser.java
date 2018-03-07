package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;
import org.apache.lucene.misc.TrigramLanguageGuesser;

import java.io.Serializable;

//Tiene que ser con una charSequence

public class CharSequenceLanguageGuesser extends Pipe implements Serializable {

    public static final String PROPERTY_NAME = "Language";
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -6160409594112988900L;

    /**
     * Return the pipe
     *
     * @return the generated pipe
     */

    public CharSequenceLanguageGuesser getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (!(carrier.getData() instanceof CharSequence))
            throw new IllegalArgumentException("La portadora debe tener una secuencia de caracteres");
        String x = ((CharSequence) carrier.getData()).toString();
        try {
            carrier.getProperties().put(PROPERTY_NAME, TrigramLanguageGuesser.detectLanguage(x));
        } catch (Exception queNuncaSeproduce) {

        }
        return carrier;
    }

}
