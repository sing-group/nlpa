package es.uvigo.esei.types;

/**
 * Interfaz que indica que un objeto de una determinada
 * clase se puede transformar o extraer el texto. Por
 * ejemplo un email
 */

public interface TransformableToText {

    /**
     * Extrae todo el texto y lo devuelve en una cadena
     *
     * @return Cadena con todo el texto del objeto
     */
    CharSequence extractText();
}
