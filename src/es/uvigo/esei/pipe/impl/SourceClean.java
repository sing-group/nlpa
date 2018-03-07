/**
 *
 */
package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.pipe.Pipe;

/**
 * Limpia el source y llama al recolector de basura
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk 1.5
 */
public class SourceClean extends Pipe {


    static final int PIPES_PER_GC = 50;
    static int cuenta = 0;

    @Override
    public Instance pipe(Instance carrier) {
        carrier.setSource(null);

        cuenta = (cuenta + 1) % PIPES_PER_GC;

        if (cuenta == 0) {
            Runtime.getRuntime().runFinalization();
            Runtime.getRuntime().gc();
        }

        return carrier;
    }

}
