package org.nlpa.util.NER;

import edu.utah.bmi.nlp.core.NERRule;
import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastcner.FastCNER;
import edu.utah.bmi.nlp.fastner.FastNER;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.utah.bmi.nlp.core.DeterminantValueSet.Determinants.ACTUAL;

public class CurrencyFastNER {
    private static final HashMap<String, List<String>> currencyEntities = new HashMap<>();
    public CurrencyFastNER() {
    }

    //Diccionario con las claves y valores que se van a utilizar para el reconocimiento de las monedas
    static {
        try {

            InputStream is = DateFastNER.class.getResourceAsStream("/currency-json/currencyFastNER.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String currencyKeyName : jsonObject.keySet()) {
                List<String> list = new ArrayList<>();
                JsonArray jsonArray = jsonObject.getJsonObject(currencyKeyName).getJsonArray("Array");
                for (int i = 0; i < jsonArray.size(); i++) {
                    list.add(jsonArray.getString(i));
                }
                currencyEntities.put(currencyKeyName, list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Método al que se le pasa un idioma y un texto por parámetro por lo que dependiendo del idioma buscará las entidades de monedas y
    //monedas asociadas a un número o a un número cardinal, devolviendo una lista con las entidades que ha encontrado en el texto.
    //Utiliza FastNER para las monedas y expresiones regulares para los números y números cardinales
    public String findAllCurrenciesAsociatedToANumber (String lang ,String textToFindAllCurrencies){
        long startTime = System.nanoTime();
        List<String> currencyEntitiesAsociatedToANumber = new ArrayList<>();
        textToFindAllCurrencies = formatString(textToFindAllCurrencies);
        CurrencyRegExpr currencyRegExpr = new CurrencyRegExpr();
        Boolean currencyAsociatedToANumber = false;

        HashMap <String, List<String>> allCurrencyEntitiesInText = findAllCurrencyEntities(textToFindAllCurrencies);
        List<String> numberEntitiesFoundInText = currencyRegExpr.findAllNumberEntities(textToFindAllCurrencies);
        List<String> cardinalNumberEntitiesFoundInText = new ArrayList<>();
        List<String> listOfNames = new ArrayList<>();
        if (lang.equals("EN")){
            listOfNames = allCurrencyEntitiesInText.get("CurrencyNameEn");
            cardinalNumberEntitiesFoundInText = currencyRegExpr.findAllCardinalNumberEnEntities(textToFindAllCurrencies);
        }else if (lang.equals("ES")){
            listOfNames = allCurrencyEntitiesInText.get("CurrencyNameEs");
            cardinalNumberEntitiesFoundInText = currencyRegExpr.findAllCardinalNumberEsEntities(textToFindAllCurrencies);
        }
        if (!listOfNames.isEmpty()){
            for (String currency : listOfNames){
                if (!numberEntitiesFoundInText.isEmpty()){
                    for (String number : numberEntitiesFoundInText){
                        String rule = number + " " + currency;
                        String entityFound = findWithFastCNER(rule,textToFindAllCurrencies);
                        if (!entityFound.isEmpty()){
                            currencyEntitiesAsociatedToANumber.add(entityFound);
                            if (!currencyAsociatedToANumber){
                                currencyAsociatedToANumber = true;
                            }
                        }
                    }
                }
                if (!cardinalNumberEntitiesFoundInText.isEmpty()){
                    for (String cardinal : cardinalNumberEntitiesFoundInText){
                        String rule = cardinal + " " + currency;
                        String entityFound = findWithFastCNER(rule,textToFindAllCurrencies);
                        if (!entityFound.isEmpty()){
                            currencyEntitiesAsociatedToANumber.add(entityFound);
                            if (!currencyAsociatedToANumber){
                                currencyAsociatedToANumber = true;
                            }
                        }
                    }
                }

                if (!currencyAsociatedToANumber){
                    //En el caso de que no esté asociado a ningun numero interesa igualmente guardar la moneda encontrada
                    currencyEntitiesAsociatedToANumber.add(currency);
                }else {currencyAsociatedToANumber = false;}
            }
        }

        currencyAsociatedToANumber = false;
        List<String> listOfIsoAndSymbols = allCurrencyEntitiesInText.get("IsoAndSymbols");
        if (!listOfIsoAndSymbols.isEmpty()){
            for (String currency : listOfIsoAndSymbols) {
                if (!numberEntitiesFoundInText.isEmpty()){
                    for (String number : numberEntitiesFoundInText) {
                        String rule = number + " " + currency;
                        String entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                        if (!entityFound.isEmpty()) {
                            currencyEntitiesAsociatedToANumber.add(entityFound);
                            if (!currencyAsociatedToANumber) {
                                currencyAsociatedToANumber = true;
                            }
                        }
                        rule =  currency + " " + number;
                        entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                        if (!entityFound.isEmpty()) {
                            currencyEntitiesAsociatedToANumber.add(entityFound);
                            if (!currencyAsociatedToANumber) {
                                currencyAsociatedToANumber = true;
                            }
                        }
                    }
                }
                if (!cardinalNumberEntitiesFoundInText.isEmpty()){
                    for (String cardinal : cardinalNumberEntitiesFoundInText) {
                        String rule = cardinal + currency;
                        String entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                        if (!entityFound.isEmpty()) {
                            currencyEntitiesAsociatedToANumber.add(entityFound);
                            if (!currencyAsociatedToANumber) {
                                currencyAsociatedToANumber = true;
                            }
                        }
                        rule =  currency + " " + cardinal;
                        entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                        if (!entityFound.isEmpty()) {
                            currencyEntitiesAsociatedToANumber.add(entityFound);
                            if (!currencyAsociatedToANumber) {
                                currencyAsociatedToANumber = true;
                            }
                        }
                    }
                }
                if (!currencyAsociatedToANumber) {
                    //En el caso de que no esté asociado a ningun numero interesa igualmente guardar la moneda encontrada
                    currencyEntitiesAsociatedToANumber.add(currency);
                }else {currencyAsociatedToANumber = false;}
            }
        }

        System.out.println("Número de entidades de dinero encontrados con fastNER: " + currencyEntitiesAsociatedToANumber.size());
        long endTime = System.nanoTime();
        System.out.println("Duración búsqueda de entidades de dinero con fastNER: " + (endTime - startTime) / 1e6 + " ms");

        return printList(currencyEntitiesAsociatedToANumber);
    }

    //Método que se encarga de encontrar las entidades de monedas en un texto que se le pasa por parámetro
    public HashMap<String, List<String>> findAllCurrencyEntities (String textToGetCurrencies){
        HashMap<String, List<String>> mapOfCurrencyElementsFound = new HashMap<>();
        List<String> listOfCurrencyNamesEs = currencyEntities.get("CurrencyNameEs");
        List<String> listOfCurrencyNamesEn = currencyEntities.get("CurrencyNameEn");
        List<String> listOfISOandSymbols= currencyEntities.get("ISOandSymbol");

        if (!listOfCurrencyNamesEs.isEmpty()){
            mapOfCurrencyElementsFound.put("CurrencyNameEs", getCurrencyEntitiesInText(listOfCurrencyNamesEs, textToGetCurrencies));
        }
        if (!listOfCurrencyNamesEn.isEmpty()){
            mapOfCurrencyElementsFound.put("CurrencyNameEn",getCurrencyEntitiesInText(listOfCurrencyNamesEn, textToGetCurrencies));
        }
        if (!listOfISOandSymbols.isEmpty()){
            mapOfCurrencyElementsFound.put("IsoAndSymbols",getCurrencyEntitiesInTextWithFastCNER(listOfISOandSymbols, textToGetCurrencies));
        }

        return mapOfCurrencyElementsFound;

    }

    //Método que se encarga de crear las reglas de monedas, también hace la llamada al método de FastNER token que devolverá
    //la entidad en caso de que se encuentre en el texto que se le pasa por parámetro. Por último devuelve una lista con las
    //entidades reconocidas por FastNER
    public List<String> getCurrencyEntitiesInText (List<String> currencyEntities, String textToGetCurrencies){
        List<String> entitiesFound = new ArrayList<>();
        if (!currencyEntities.isEmpty()){
            for (String currency : currencyEntities){
                String rule = currency + "\t Currency \n";
                List<String> entityFound = findWithFastNERTokenList(rule,textToGetCurrencies);
                if (!entityFound.isEmpty()){
                    for (String entity : entityFound){
                        if(!entitiesFound.contains(entity)){
                            entitiesFound.add(entity);
                        }
                    }
                }
            }
        }
        return entitiesFound;
    }

    //Método que se encarga de crear las reglas de monedas, también hace la llamada al método de FastCNER que devolverá
    //la entidad en caso de que se encuentre en el texto que se le pasa por parámetro. Por último devuelve una lista con las
    //entidades reconocidas por FastNER
    public List<String> getCurrencyEntitiesInTextWithFastCNER (List<String> currencyEntities, String textToGetCurrencies){
        List<String> entitiesFound = new ArrayList<>();
        if (!currencyEntities.isEmpty()){
            for (String currency : currencyEntities){
                List<String> entityFound = findWithFastCNERList(currency,textToGetCurrencies);
                if (!entityFound.isEmpty()){
                    for (String entity : entityFound){
                        if(!entitiesFound.contains(entity)){
                            entitiesFound.add(entity);
                        }
                    }
                }
            }
        }
        return entitiesFound;
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro con FastNER Token y devuelve una cadena
    //de texto con las entidades encontradas separadas por saltos de línea a partir de una regla en el texto que se le pasa por parámetro
    public String findWithFastNERToken (String rule, String textToFindTokens){
        FastNER fastNER = new FastNER(rule);
        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textToFindTokens, true).get(0);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            entry.getValue().forEach((span) -> {
                String resultToAdd = textToFindTokens.substring(span.getBegin(), span.getEnd()).replaceAll(" +", " ").trim();
                    result.add(resultToAdd);
            });
        }
        return printList(result);
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro con FastNER Token y devuelve una lista
    //de las entidades que ha encontrado a partir de una regla en el texto que se le pasa por parámetro
    public List<String> findWithFastNERTokenList (String rule, String textToFindTokens){
        FastNER fastNER = new FastNER(rule);
        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textToFindTokens, true).get(0);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            entry.getValue().forEach((span) -> {
                String resultToAdd = textToFindTokens.substring(span.getBegin(), span.getEnd()).replaceAll(" +", " ").trim();
                if (!resultToAdd.isEmpty() && !result.contains(resultToAdd)){
                    result.add(resultToAdd);
                }
            });
        }
        return result;
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro con FastCNER y devuelve una cadena
    //de texto con las entidades encontradas separadas por saltos de línea a partir de una regla en el texto que se le pasa por parámetro
    public String findWithFastCNER(String rule, String textToFindTokens){
        HashMap<Integer, NERRule> rules = new HashMap<>();
        rules.put(0, new NERRule(0, rule, "Currency", 0.1, ACTUAL));
        FastCNER fcrp = new FastCNER(rules);
        fcrp.setReplicationSupport(true);
        HashMap<String, ArrayList<Span>> result = fcrp.processString(textToFindTokens);
        List<String> resultList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Span>> entry : result.entrySet()) {
            entry.getValue().forEach((span) -> {
                String resultToAdd = textToFindTokens.substring(span.getBegin(), span.getEnd()).replaceAll(" +", " ").trim();
                    resultList.add(resultToAdd);
            });
        }
        return printList(resultList);
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro con FastCNER y devuelve una lista
    //de las entidades que ha encontrado a partir de una regla en el texto que se le pasa por parámetro
    public List<String> findWithFastCNERList (String rule, String textToFindTokens){
        HashMap<Integer, NERRule> rules = new HashMap<>();
        rules.put(0, new NERRule(0, rule, "Currency", 0.1, ACTUAL));
        FastCNER fcrp = new FastCNER(rules);
        fcrp.setReplicationSupport(true);
        HashMap<String, ArrayList<Span>> result = fcrp.processString(textToFindTokens);
        List<String> resultList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Span>> entry : result.entrySet()) {
            entry.getValue().forEach((span) -> {
                String resultToAdd = textToFindTokens.substring(span.getBegin(), span.getEnd()).replaceAll(" +", " ").trim();
                if (!resultToAdd.isEmpty() && !resultList.contains(resultToAdd)){
                    resultList.add(resultToAdd);
                }
            });
        }
        return resultList;
    }

    //Método que se encarga de formatear la cadena de texto que se pasa por parámetro
    private String formatString (String stringToFormat){
        stringToFormat = stringToFormat.trim();
        stringToFormat = stringToFormat.replaceAll("\r", " ");
        stringToFormat = stringToFormat.replaceAll("\t", " ");
        stringToFormat = stringToFormat.replaceAll("\n", " ");
        stringToFormat = stringToFormat.replaceAll(" +", " ");
        return stringToFormat;
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
}


