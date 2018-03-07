package es.uvigo.esei.pipe.impl;

import es.uvigo.esei.ia.types.Instance;
import es.uvigo.esei.ia.types.Token;
import es.uvigo.esei.ia.types.TokenSequence;
import es.uvigo.esei.ia.util.Pair;
import es.uvigo.esei.pipe.Pipe;

import java.io.Serializable;
import java.util.Vector;

/**
 * Stemmer seg�n el algoritmo de Porter
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since JDK 1.5
 */
public abstract class TokenSequencePorterStemmer extends Pipe implements Serializable {

    /**
     * Reglas de stemming
     */
    protected Vector<Pair<String, Vector<Regla>>> allRules =
            new Vector<Pair<String, Vector<Regla>>>();

    /**
     * Constructor por defecto
     */
    protected TokenSequencePorterStemmer() {
        loadRules();
    }

    /**
     * Realiza el filtrado correspondiente
     *
     * @see es.uvigo.esei.pipe.Pipe#pipe(es.uvigo.esei.ia.types.Instance)
     */
    @Override
    public Instance pipe(Instance carrier) {
        //Recuperamos los datos del carrier
        TokenSequence ts = (TokenSequence) carrier.getData();
        TokenSequence ret = new TokenSequence();

        //Aplicamos stemmer a cada palabra
        for (int i = 0; i < ts.size(); i++) {
            Token t = ts.getToken(i);
            t.setText(extraerRaiz(t.getText()));
            if (!t.getText().trim().equals("")) ret.add(t);
        }

        //Cambiamos los tokens
        carrier.setData(ret);

        //Devolver el resultado
        return carrier;
    }

    /**
     * Carga las reglas espec�ficas para el stemmer
     */
    protected abstract void loadRules();

    /**
     * Indica si una letra es vocal
     *
     * @param c caracter a comprobar si es vocal
     * @return Verdadero si el caracter es una vocal
     */
    private boolean esVocal(char c) {
        return (c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u') || (c == '�') || (c == '�') || (c == '�') || (c == '�') || (c == '�');
    }

    /**
     * Calcula el n�mero de s�labas de una palabra
     *
     * @param word Palaba a calcular el n�mero de s�labas
     * @return n�mero de s�labas de una palabra
     */
    private int wordSize(String word) {
        int result = 0;
        int state = 0;
        int i = 0;
        state = 0;
        i = 0;
        while (i < word.length()) {
            switch (state) {
                case 0:
                    state = (esVocal(word.charAt(i))) ? 1 : 2;
                    break;
                case 1:
                    state = (esVocal(word.charAt(i))) ? 1 : 2;
                    if (state == 2)
                        result++;
                    break;
                case 2:
                    state = (esVocal(word.charAt(i)) || ('y' == word.charAt(i))) ? 1 : 2;
                    break;
            }
            i++;
        }
        return result;
    }

    /**
     * Devuelve Veradero si la palabra tiene una vocal
     *
     * @param word palabra apara ver si tiene una vocal
     * @return Verdadero si la palabra tiene una vocal
     */
    private boolean contieneVocal(String word) {
        if (word.equals("")) return false;
        int i;
        boolean cond1, cond2;
        String vocales = "aeiou�����y";

        if (word == "")
            return false;

        cond1 = esVocal(word.charAt(0));
        i = 1;
        cond2 = false;
        while ((i < word.length()) && (!cond2)) {
            if (vocales.indexOf(word.charAt(i)) != -1)
                cond2 = true;
            else
                i++;
        }
        return (cond1 || cond2);
    }

    /**
     * Devuelve Verdadero si la palabra acaba en <Consonante><Vocal><Consonante>
     *
     * @param word Palabra para ver si termina en CVC
     * @return verdadero si la palabra acaba en CVC y falso en otro caso
     */
    private boolean endsWithCVC(String word) {

        String C1 = "aeiou�����wxy";
        String C2 = "aeiou�����y";
        String C3 = "aeiou�����";

        int l = word.length();

        if (l < 3)
            return false;

        l--;
        return ((C1.indexOf(word.charAt(l)) == -1) &&
                (C2.indexOf(word.charAt(l - 1)) != -1) &&
                (C3.indexOf(word.charAt(l - 2)) == -1));
    }

    /**
     * Devuelve Verdadero si se debe eliminar una e para aplicar una regla
     *
     * @param word Palabra para eliminar una e
     * @return Verdadero si se debe eliminar una e para aplicar la regla, falso en otro caso
     */
    private boolean removeAnE(String word) {
        return ((wordSize(word) == 1) && !endsWithCVC(word));
    }

    /**
     * Aplica una pasada sobre las cadenas con un conjunto de reglas
     *
     * @param reglas Conjunto de reglas que se aplican en la pasada
     * @return Cadena de resultado parcial
     */
    private String replaceEnd(Vector<Regla> reglas, String processWord) {
        String ending;
        String tmp_ch = "";

        int i = 0;
        i = 0;
        tmp_ch = "";
        String returnValue = processWord;

        while (i < reglas.size()) {
            // hay que ver si coincide la ra�z de la palabra con la ra�z de una regla
            if (returnValue.length() - reglas.elementAt(i).getFinAnt().length() < 0)
                ending = "";
            else
                ending = returnValue.substring(returnValue.length() - reglas.elementAt(i).getFinAnt().length());

            if (ending != "")
                if (reglas.elementAt(i).getFinAnt().equals(ending)) {
                    if (returnValue.endsWith(reglas.elementAt(i).getFinAnt())) { // si la palabra acaba con un sufijo
                        tmp_ch = ending; // tmp_ch contiene el sifijo actual
                        ending = "";
                        returnValue = returnValue.substring(0, returnValue.length() - reglas.elementAt(i).getFinAnt().length()); // elimina el sufijo a la palabra
                        if (reglas.elementAt(i).getMinRootSize() < wordSize(returnValue)) {   // si el tama�o de la ra�z permite el cambio
                            if ((reglas.elementAt(i).getCondicion() == -1) || ((reglas.elementAt(i).getCondicion() == 1) && (contieneVocal(returnValue)))
                                    || ((reglas.elementAt(i).getCondicion() == 2) && (removeAnE(returnValue)))) { // si hay que aplicar alguna condici�n
                                returnValue += reglas.elementAt(i).getFinNuevo();
                                break;
                            }
                        }
                        ending = tmp_ch; // en caso de que no valiese el sufijo se restaura
                        returnValue += ending;
                    }
                }
            i++;
        }

        return returnValue;
    }

    /**
     * Elimina todos los caracteres que no son vocales o consonantes
     *
     * @param str cadena de Origen
     * @return cadena despu�s de haber limpiado toda la basura
     */
    private String limpiarCadena(String str) {
        int last = str.length();

        //Character ch = new Character( str.charAt(0) );
        String temp = "";

        for (int i = 0; i < last; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) || esVocal(str.charAt(i)))
                temp += str.charAt(i);
        }
        return temp;
    }

    /**
     * Realiza el stemming de la palabra, mediante el algoritmo
     * de porter usando 4 pasos de stemming
     *
     * @param word Palabra para procesar
     * @return Cadena despu�s del stemming
     */
    private String stem(String word) {
        String tmp = word;

        //Procesar todas las reglas en orden
        for (int i = 0; i < allRules.size(); i++) {
            tmp = replaceEnd(allRules.elementAt(i).getObj2(), tmp);
        }

        //tmp = replaceEnd(/*reglasPaso1a*/this.allRules[0].getObj2(), tmp);
        //tmp = replaceEnd(/*reglasPaso1b*/this.allRules[1].getObj2(), tmp);
        //tmp = replaceEnd(/*reglasPaso2*/, tmp);
        //tmp = replaceEnd(/*reglasPaso3*/, tmp);
        //tmp = replaceEnd(/*reglasPaso4*/, tmp);
        //tmp = replaceEnd(/*reglasPaso5*/, tmp);

        return tmp;
    }

    /**
     * Elimina los acentos de la palabra actual
     *
     * @param word Palabra a eliminar los acentos
     * @return Cadena con los acentos eliminados
     */
    private String eliminarAcentos(String word) {
        String tmp = word;

        tmp = tmp.replace('�', 'a');
        tmp = tmp.replace('�', 'e');
        tmp = tmp.replace('�', 'i');
        tmp = tmp.replace('�', 'o');
        tmp = tmp.replace('�', 'u');
        return tmp;
    }

    /**
     * Dada una palabra w extrae su raiz
     *
     * @param word Palabra de la cual se extrae la raiz
     * @return Cadena conteniendo la raiz extraida
     */
    public String extraerRaiz(String word) {
        //Salvaguardo la palabra en returnValue
        String returnValue = word;

        returnValue = returnValue.toLowerCase(); // Paso la palabra a min�culas
        returnValue = limpiarCadena(returnValue); // Elimino s�mbolos que no pertenezcan al lenguaje
        returnValue = stem(returnValue); //Hago el stemming
        eliminarAcentos(returnValue); // Elimino los acentos de la cadena

        return returnValue;
    }

    /**
     * Ordenar los pasos.
     */
    protected void sortPasos() {
        boolean cambios = true;
        //Mientras no des todas las vueltas y haya cambios
        for (int i = 0; i < allRules.size() && cambios; i++) {
            cambios = false;
            for (int j = 0; j < allRules.size() - 1; j++) {
                //Si el elemento j es mayor que el elemento j+1
                if (allRules.elementAt(j).getObj1().compareTo(allRules.elementAt(j + 1).getObj1()) > 0) {
                    //Intercambiar los elementos de la posici�n j y j+1
                    Pair<String, Vector<Regla>> aux;
                    aux = allRules.elementAt(j);
                    allRules.setElementAt(allRules.elementAt(j + 1), j);
                    allRules.setElementAt(aux, j + 1);
                    //Marcar que hay cambios
                    //System.out.println(".");
                    cambios = true;
                }
            }
        }
    }
}

/**
 * Regla de sustituci�n seg�n el algoritmo de Porter
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since JDK 1.5
 */
class Regla {
    /**
     * Sufijo anterior
     */
    String finAnt;

    /**
     * Sufijo de reemplazo
     */
    String finNuevo;

    /**
     * Tama�o m�nimo de la raiz en s�labas (-1 no aplicable)
     */
    int minRootSize;

    /**
     * Condici�n de cambio.
     * Si vale 1 es que contiene una vocal, si vale 2 borrar una e, si vale -1 no hace falta condici�n
     */
    int condicion; // si vale 1 --> contieneVocal,  si vale 2 ---> removeAnE   -1 --> no hace falta condici�n

    public Regla(String fa, String fn, int mrs, int c) {
        finAnt = fa;
        finNuevo = fn;
        minRootSize = mrs;
        condicion = c;
    }

    /**
     * @return Returns the condicion.
     */
    public int getCondicion() {
        return condicion;
    }


    /**
     * @param condicion The condicion to set.
     */
    public void setCondicion(int condicion) {
        this.condicion = condicion;
    }


    /**
     * @return Returns the finAnt.
     */
    public String getFinAnt() {
        return finAnt;
    }


    /**
     * @param finAnt The finAnt to set.
     */
    public void setFinAnt(String finAnt) {
        this.finAnt = finAnt;
    }


    /**
     * @return Returns the finNuevo.
     */
    public String getFinNuevo() {
        return finNuevo;
    }


    /**
     * @param finNuevo The finNuevo to set.
     */
    public void setFinNuevo(String finNuevo) {
        this.finNuevo = finNuevo;
    }


    /**
     * @return Returns the minRootSize.
     */
    public int getMinRootSize() {
        return minRootSize;
    }


    /**
     * @param minRootSize The minRootSize to set.
     */
    public void setMinRootSize(int minRootSize) {
        this.minRootSize = minRootSize;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.finAnt + " " + finNuevo + " " + minRootSize + " " + condicion;
    }
}
