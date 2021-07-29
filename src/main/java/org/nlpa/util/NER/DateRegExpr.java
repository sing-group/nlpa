package org.nlpa.util.NER;

import javax.json.*;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRegExpr {
    private static final HashMap<String, String> keysToMatchRegExprEs = new HashMap<>();
    private static final HashMap<String, String> keysToMatchRegExprEn = new HashMap<>();
    private static final List<String> listDatesToMatch = new ArrayList<>();
    public DateRegExpr() {
    }

    //Diccionario de claves con sus correspondientes valores en español para el reconocimiento de fechas con expresiones regulares
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionfordates-json/regExpMatcher.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String keyRegExpr : jsonObject.keySet()) {
                keysToMatchRegExprEs.put(keyRegExpr,jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    //Diccionario con los formatos de fecha a reconocer con expresiones regulares
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionfordates-json/datesToMatch.json");;
            JsonReader rdr = Json.createReader(is);
            JsonArray array = rdr.readArray();
            rdr.close();
            array.forEach((v) -> {
                listDatesToMatch.add(((JsonString)v).getString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Diccionario de claves con sus correspondientes valores en inglés para el reconocimiento de fechas con expresiones regulares
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionfordates-json/regExpMatcherEn.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String keyRegExpr : jsonObject.keySet()) {
                keysToMatchRegExprEn.put(keyRegExpr,jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Método que devuelve la lista de claves del diccionario de claves ordenadas según el número de letras que contenga la clave,
    //siendo las de mayor número de letras las primeras.
    //Nótese que este método utiliza las claves del diccionario en español, en éste caso no supone un problema ya que las claves
    //del diccionario en inglés y español son las mismas.
    public List<String> getKeysSorted (){
        List<String> listOfKeys = new ArrayList<>();
        if(!keysToMatchRegExprEs.isEmpty()){
            for(String key : keysToMatchRegExprEs.keySet()){
                listOfKeys.add(key);
            }
        }
        return sortKeys(listOfKeys);
    }

    //Método que tiene como parámetro el texto y el idioma de éste, por lo que dependiendo del idioma buscará las entidades de fecha
    //en un idioma u otro, devolviendo como resultado una cadena de texto de las entidades encontradas separadas por un salto de línea
    public String datesWithRegularExpressions(String lang, String textToTest){
        long startTime = System.nanoTime();
        List<String> dateEntitiesFound = new ArrayList<>();
        if (!keysToMatchRegExprEs.isEmpty() && !keysToMatchRegExprEn.isEmpty() && !listDatesToMatch.isEmpty()){
            List<String> dateFormated = matchDatesWithRegExpressionKey(getKeysSorted(), lang);
            if (!dateFormated.isEmpty()){
                List<String> dateEntities= testPatternRegExp(dateFormated,textToTest);
                if (!dateEntities.isEmpty()){
                    for (String dateEntity : dateEntities){
                        dateEntitiesFound.add(dateEntity);
                    }
                }
            }
        }
        System.out.println("Número de fechas encontradas con expresiones regulares: " + dateEntitiesFound.size());
        long endTime = System.nanoTime();
        System.out.println("Duración búsqueda de fechas con expresiones regulares: " + (endTime-startTime)/1e6 + " ms");

        return printList(dateEntitiesFound);
    }

    //Método que se encarga de hacer matching entre el diccionario de claves de fecha y las fechas que se quieren detectar en el texto. Para ello
    //se le pasa por parámetro el idioma para que éste matching se haga con los valores correspondientes a dicho idioma
    public List<String> matchDatesWithRegExpressionKey(List<String> keysSorted, String lang){
        List<String> listOfDatesMatchedWithRegExp = new ArrayList<>();
        if (!keysSorted.isEmpty() && (!keysToMatchRegExprEs.isEmpty() || !keysToMatchRegExprEn.isEmpty()) && !listDatesToMatch.isEmpty()){
            for(String date : listDatesToMatch){
                String dateFormated = date;
                for(String key : keysSorted){
                    if (date.contains(key)){
                        String regExp = "" ;
                        if (lang.equals("EN")){
                            regExp = keysToMatchRegExprEn.get(key);
                        }else if (lang.equals("ES")){
                            regExp = keysToMatchRegExprEs.get(key);
                        }else {
                            regExp = keysToMatchRegExprEn.get(key);
                        }
                        dateFormated = dateFormated.replaceAll(key,regExp);
                    }
                }
                if(!dateFormated.equals(date)){
                    listOfDatesMatchedWithRegExp.add(dateFormated);
                }
            }
        }
        return listOfDatesMatchedWithRegExp;
    }

    //Método que recibe una lista de patrones y un texto por parámetro. Se encarga de comprobar la existencia de cada uno de los patrones
    //en el texto, devolviendo una lista con las entidades que se han encontrado que cumplen con dichos patrones
    public List<String> testPatternRegExp (List<String> listRegExp, String textToTry){
        List<String> listOfEntitiesFound = new ArrayList<>();
        if (!listRegExp.isEmpty()){
            for(String patternRegExp : listRegExp){
                Pattern pattern = Pattern.compile(patternRegExp);
                Matcher mat = pattern.matcher(textToTry);
                while (mat.find()){
                    listOfEntitiesFound.add(mat.group());
                }
            }
        }
        return listOfEntitiesFound;
    }

    //Método que a partir de una lista de cadenas de texto devuelve una cadena de texto con cada una de los componentes de dicha lista
    //separados por saltos de línea
    public String printList (List<String> listOfCardinals){
        StringBuilder sb = new StringBuilder();
        for (String string : listOfCardinals){
            sb.append(string + "\n");
        }
        return sb.toString();
    }

    //Método que se encarga de ordenar una lista de cadenas de texto por la cantidad de letras que contenga cada una de ellas
    //siendo las de mayor tamaño las primeras en introducirse en la nueva lista
    public List<String> sortKeys(List<String> keys) {
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        return keys;
    }


}
