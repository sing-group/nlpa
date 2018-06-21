
package es.uvigo.esei.pipe.impl;


import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.Label;
import es.uvigo.esei.ia.types.LabelAlphabet;
import es.uvigo.esei.pipe.Pipe;


/**
 * Colocar en el target el n�mero de la etiqueta del alfabeto
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk1.5
 */
public class Target2Label extends Pipe {

    /**
     * Alfabeto de las posibles etiquetas que se le pueden colocar
     * a esta instancia
     */
    LabelAlphabet lblAlphabet = null;

    private Target2Label() {
        //super(null, LabelAlphabet.class);
    }

    public Target2Label(LabelAlphabet lblAlphabet) {
        this.lblAlphabet = lblAlphabet;
        //super(null, ldict);
    }

    /**
     * @return Returns the lblAlphabet.
     */
    public LabelAlphabet getLblAlphabet() {
        return lblAlphabet;
    }


    /**
     * @param lblAlphabet The lblAlphabet to set.
     */
    public void setLblAlphabet(LabelAlphabet lblAlphabet) {
        this.lblAlphabet = lblAlphabet;
    }

    @Override
    public Instance pipe(Instance carrier) {

        if (carrier.getTarget() != null) {
            if (carrier.getTarget() instanceof Label)
                throw new IllegalArgumentException("Already a label.");

            //LabelAlphabet ldict = (LabelAlphabet)getTargetAlphabet();

            carrier.setTarget(lblAlphabet.lookupLabel(carrier.getTarget()));
        }

        return carrier;
    }
}
