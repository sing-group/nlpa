package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.FeatureVector;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import java.io.Serializable;

/**
 * Calcula las frecuencias de un vector suponiendo que se han contado
 * Para aplicar esto hay que haber aplicado antes el TokenSequenceCountTokens
 * antes justo despu�s de haber aplicado CharSequence2TokenSequence
 * las palabras
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk1.5
 */

public class FeatureVectorCalculateFrecuency extends Pipe implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1584429266302810892L;

    private String propertyName = "TextTokenNumber";

    public FeatureVectorCalculateFrecuency(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Constructor por defecto
     */
    public FeatureVectorCalculateFrecuency() {

    }

    /**
     * Devuelve el Pipe
     *
     * @return pipe actual
     */

    public FeatureVectorCalculateFrecuency getPipe() {
        return this;
    }

    @Override
    public Instance pipe(Instance carrier) {
        FeatureVector fv = (FeatureVector) carrier.getData();


        Integer value = (Integer) (carrier.getProperty(propertyName));

        if (value == null) {
            throw new IllegalStateException("Debes pasar antes " +
                    "el filtro TokenSequenceCounttokens.");
        }

        int cuenta = value.intValue();

        for (int j = 0; j < fv.numLocations(); j++) {
            //fli es el �ndice en el diccionario

            // xxx Is this right?  What should we do about negative values?
            // Whatever is decided here should also go in DecisionTree.split()
            if (fv.valueAtLocation(j) > 0) {
                //Dividimos por el recuento de las frecuencias
                fv.setValueAtLocation(j, (fv.valueAtLocation(j)) / cuenta);

            } else {
                System.out.println("Valor Negativo: " + fv.valueAtLocation(j));
            }
        }

        return carrier;
    }

}
