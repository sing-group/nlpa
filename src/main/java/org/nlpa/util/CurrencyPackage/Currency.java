package org.nlpa.util.CurrencyPackage;

import edu.utah.bmi.nlp.core.NERRule;
import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastcner.FastCNER;
import edu.utah.bmi.nlp.fastner.FastNER;
import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.utah.bmi.nlp.core.DeterminantValueSet.Determinants.ACTUAL;

public class Currency {

    public Currency() {
    }

    public List<String> findAllCurrenciesAsociatedToANumber (String textToFindAllCurrencies){
        List<String> currencyEntitiesAsociatedToANumber = new ArrayList<>();
        Boolean currencyAsociatedToANumber = false;

        HashMap <Integer, List<String>> listOfEntities = getCurrenciesEs();
        HashMap <String, List<String>> allCurrencyEntitiesInText = findAllCurrencyEntities(listOfEntities,textToFindAllCurrencies);
        CardinalNumbersEs cardinalNumbersEs = new CardinalNumbersEs();
        List<String> cardinalNumbersFoundInText = cardinalNumbersEs.findAllCardinalsInTheText(textToFindAllCurrencies);
        List<String> numberEntitiesFoundInText = findAllNumberEntities(textToFindAllCurrencies);

        List<String> listOfNamesInPlural = allCurrencyEntitiesInText.get("NamesPlural");
        for (String currency : listOfNamesInPlural){
            for (String cardinal : cardinalNumbersFoundInText){
                String rule = cardinal + " " + currency + "\t CURRENCY\n";
                String entityFound = findWithFastNERToken(rule,textToFindAllCurrencies);
                if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)){
                    currencyEntitiesAsociatedToANumber.add(entityFound);
                    if (!currencyAsociatedToANumber){
                        currencyAsociatedToANumber = true;
                    }
                }
            }
            for (String number : numberEntitiesFoundInText){
                String rule = number + " " + currency + "\t CURRENCY\n";
                String entityFound = findWithFastNERToken(rule,textToFindAllCurrencies);
                if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)){
                    currencyEntitiesAsociatedToANumber.add(entityFound);
                    if (!currencyAsociatedToANumber){
                        currencyAsociatedToANumber = true;
                    }
                }
            }
            if (!currencyAsociatedToANumber){
                //En el caso de que no esté asociado a ningun numero interesa igualmente guardar la moneda encontrada
                currencyEntitiesAsociatedToANumber.add(currency);

            }
        }

        List<String> listOfNames = allCurrencyEntitiesInText.get("Names");
            for (String currency : listOfNames) {
                for (String cardinal : cardinalNumbersFoundInText) {
                    String rule = cardinal + " " + currency + "\t CURRENCY\n";
                    String entityFound = findWithFastNERToken(rule, textToFindAllCurrencies);
                    if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)) {
                        currencyEntitiesAsociatedToANumber.add(entityFound);
                        if (!currencyAsociatedToANumber) {
                            currencyAsociatedToANumber = true;
                        }
                    }
                }
                for (String number : numberEntitiesFoundInText) {
                    String rule = number + " " + currency + "\t CURRENCY\n";
                    String entityFound = findWithFastNERToken(rule, textToFindAllCurrencies);
                    if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)) {
                        currencyEntitiesAsociatedToANumber.add(entityFound);
                        if (!currencyAsociatedToANumber) {
                            currencyAsociatedToANumber = true;
                        }
                    }
                }
                if (!currencyAsociatedToANumber) {
                    //En el caso de que no esté asociado a ningun numero interesa igualmente guardar la moneda encontrada
                    currencyEntitiesAsociatedToANumber.add(currency);
                }
            }
        List<String> listOfIsoAndSymbols = allCurrencyEntitiesInText.get("IsoAndSymbols");
        for (String currency : listOfIsoAndSymbols) {
            for (String cardinal : cardinalNumbersFoundInText) {
                String rule = cardinal + " " + currency + "\t CURRENCY\n";
                String entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)) {
                    currencyEntitiesAsociatedToANumber.add(entityFound);
                    if (!currencyAsociatedToANumber) {
                        currencyAsociatedToANumber = true;
                    }
                }
                rule =  currency +  " " + cardinal  +  "\t CURRENCY\n";
                entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)) {
                    currencyEntitiesAsociatedToANumber.add(entityFound);
                    if (!currencyAsociatedToANumber) {
                        currencyAsociatedToANumber = true;
                    }
                }
            }
            for (String number : numberEntitiesFoundInText) {
                String rule = number + " " + currency + "\t CURRENCY\n";
                String entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)) {
                    currencyEntitiesAsociatedToANumber.add(entityFound);
                    if (!currencyAsociatedToANumber) {
                        currencyAsociatedToANumber = true;
                    }
                }
                rule =  currency +  " " + number  +  "\t CURRENCY\n";
                entityFound = findWithFastCNER(rule, textToFindAllCurrencies);
                if (!entityFound.isEmpty() && !currencyEntitiesAsociatedToANumber.contains(entityFound)) {
                    currencyEntitiesAsociatedToANumber.add(entityFound);
                    if (!currencyAsociatedToANumber) {
                        currencyAsociatedToANumber = true;
                    }
                }
            }
            if (!currencyAsociatedToANumber) {
                //En el caso de que no esté asociado a ningun numero interesa igualmente guardar la moneda encontrada
                currencyEntitiesAsociatedToANumber.add(currency);
            }
        }

        return currencyEntitiesAsociatedToANumber;
    }

    public HashMap <Integer, List<String>> getCurrenciesEs(){
        HashMap <Integer, List<String>> mapOfCurrencyElements = new HashMap<>();
        List<String> listOfNames = new ArrayList<>();
        List<String> listOfNamesInPlural = new ArrayList<>();
        List<String> listOfIsoAndSymbol = new ArrayList<>();
        try {
            InputStream is = Currency.class.getResourceAsStream("/currency-json/currency.es.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();
            for(String currency : jsonObject.keySet()) {
                listOfNames.add(formatString(currency));
                listOfNamesInPlural.add(formatString(jsonObject.getJsonObject(currency).getString("NamePlural")));
                listOfIsoAndSymbol.add(formatString(jsonObject.getJsonObject(currency).getString("ISO")));
                String symbol = jsonObject.getJsonObject(currency).getString("Symbol");
                if (!symbol.isEmpty() && !listOfIsoAndSymbol.contains(symbol)){
                    listOfIsoAndSymbol.add(symbol);
                }
            }
        }catch (Exception e) {
                e.printStackTrace();
        }

        if (!listOfNames.isEmpty() && !listOfNamesInPlural.isEmpty() && !listOfIsoAndSymbol.isEmpty()){
            mapOfCurrencyElements.put(0, listOfNames);
            mapOfCurrencyElements.put(1, listOfNamesInPlural);
            mapOfCurrencyElements.put(2, listOfIsoAndSymbol);
        }

        return mapOfCurrencyElements;

    }
    public HashMap <Integer, List<String>> getCurrenciesEn(){
        HashMap <Integer, List<String>> mapOfCurrencyElements = new HashMap<>();
        List<String> listOfNames = new ArrayList<>();
        List<String> listOfNamesInPlural = new ArrayList<>();
        List<String> listOfIsoAndSymbol = new ArrayList<>();
        try {
            InputStream is = Currency.class.getResourceAsStream("/currency-json/currency.en.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();
            for(String currency : jsonObject.keySet()) {
                listOfNames.add(formatString(currency));
                listOfNamesInPlural.add(formatString(jsonObject.getJsonObject(currency).getString("NamePlural")));
                listOfIsoAndSymbol.add(formatString(jsonObject.getJsonObject(currency).getString("ISO")));
                String symbolEs = jsonObject.getJsonObject(currency).getString("Symbol");
                if (!symbolEs.isEmpty() && !listOfIsoAndSymbol.contains(symbolEs)){
                    listOfIsoAndSymbol.add(symbolEs);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (!listOfNames.isEmpty() && !listOfNamesInPlural.isEmpty() && !listOfIsoAndSymbol.isEmpty()){
            mapOfCurrencyElements.put(0, listOfNames);
            mapOfCurrencyElements.put(1, listOfNamesInPlural);
            mapOfCurrencyElements.put(2, listOfIsoAndSymbol);
        }

        return mapOfCurrencyElements;
    }

    public HashMap<String, List<String>> findAllCurrencyEntities (HashMap <Integer, List<String>> mapOfCurrencyElements , String textToGetCurrencies){
        textToGetCurrencies = formatString(textToGetCurrencies);
        HashMap<String, List<String>> mapOfCurrencyElementsFound = new HashMap<>();
        List<String> listOfNames = mapOfCurrencyElements.get(0);
        List<String> listOfNamesInPlural = mapOfCurrencyElements.get(1);
        List<String> listOfISOandSymbols= mapOfCurrencyElements.get(2);

        if (!listOfNames.isEmpty()){
            mapOfCurrencyElementsFound.put("NamesPlural", getCurrencyEntitiesInText(listOfNamesInPlural, textToGetCurrencies));
        }
        if (!listOfNamesInPlural.isEmpty()){
            mapOfCurrencyElementsFound.put("Names",getCurrencyEntitiesInText(listOfNames, textToGetCurrencies));
        }
        if (!listOfISOandSymbols.isEmpty()){
            mapOfCurrencyElementsFound.put("IsoAndSymbols",getCurrencyEntitiesInText(listOfISOandSymbols, textToGetCurrencies));
        }
        return mapOfCurrencyElementsFound;
    }

    public List<String> findAllNumberEntities (String textToFindEntities){
        List<String> numberEntitiesFound = new ArrayList<>();
        String ruleNumberWithPoint = "\\d+ \t CURRENCY\n";
        String [] entitiesFoundNumberWithPoint = findWithFastNERToken(ruleNumberWithPoint, textToFindEntities).split(" ");
        for (String entity : entitiesFoundNumberWithPoint){
            if (!entity.isEmpty() && !numberEntitiesFound.contains(entity)){
                numberEntitiesFound.add(entity);
            }
        }
        String ruleNumberWithComma = "\\d+ , \\d+ \t CURRENCY\n";
        String [] entityFoundNumberWithComma = findWithFastNERToken(ruleNumberWithComma, textToFindEntities).split(" ");
        for (String entity : entityFoundNumberWithComma){
            if (!entity.isEmpty() && !numberEntitiesFound.contains(entity)){
                numberEntitiesFound.add(entity);
            }
        }
        String ruleNumberWithSingleQuote = "\\d+ , \\d+ \t CURRENCY\n";
        String [] entityFoundNumberWithSingleQuote = findWithFastNERToken(ruleNumberWithSingleQuote, textToFindEntities).split(" ");
        for (String entity : entityFoundNumberWithSingleQuote){
            if (!entity.isEmpty() && !numberEntitiesFound.contains(entity)){
                numberEntitiesFound.add(entity);
            }
        }

        return numberEntitiesFound;
    }

    public List<String> getCurrencyEntitiesInText (List<String> currencyEntities, String textToGetCurrencies){
        List<String> entitiesFound = new ArrayList<>();
        if (!currencyEntities.isEmpty()){
            for (String currency : currencyEntities){
                String rule = currency + "\t Currency \n";
                String entityFound = findWithFastNERToken(rule,textToGetCurrencies);
                if (!entityFound.isEmpty() && !entitiesFound.contains(entityFound)){
                    entitiesFound.add(entityFound);
                }
            }
        }
        return entitiesFound;
    }

    //Metodo privado para eliminar ciertos caracteres en una cadena a traves de un patron, devuelve la cadena resultante con un espacio en blanco donde estaban las entidades
    private String deleteAEntityWithPattern (String entity, String textToDeleteEntity){
        String textToReturn = "";
        Pattern pattern = Pattern.compile(entity);
        Matcher matcher = pattern.matcher(textToDeleteEntity);
        Boolean isFound = matcher.find();
        if (isFound){
            String[] textSplited = pattern.split(textToDeleteEntity);
            StringBuilder sb = new StringBuilder();
            for (String string : textSplited){
                sb.append(string + " ");
            }
            textToReturn = sb.toString();
            return textToReturn;
        }
        return textToReturn;
    }

    private String formatString (String stringToFormat){
        stringToFormat = StringUtils.stripAccents(stringToFormat);
        stringToFormat = stringToFormat.toLowerCase();
        stringToFormat = stringToFormat.trim();
        stringToFormat = stringToFormat.replaceAll("\t", " ");
        stringToFormat = stringToFormat.replaceAll("\n", " ");
        stringToFormat = stringToFormat.replaceAll(" +", " ");
        return stringToFormat;
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro, puede devolver una lista con los elementos encontrados o un string vacio
    public String findWithFastNERToken (String rule, String textToFindTokens){
        FastNER fastNER = new FastNER(rule);
        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textToFindTokens, true).get(0);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            entry.getValue().forEach((span) -> {
                String resultToAdd = textToFindTokens.substring(span.getBegin(), span.getEnd()).replaceAll(" +", " ").trim();
                if (!result.contains(resultToAdd)) {
                    result.add(resultToAdd);
                }
            });
        }
        return printList(result);
    }
    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro, puede devolver un String con los elementos encontrados o un string vacio
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
                if (!resultList.contains(resultToAdd)) {
                    resultList.add(resultToAdd);
                }
            });
        }
        return printList(resultList);
    }



    public String printList (List<String> listOfCardinals){
        StringBuilder sb = new StringBuilder();
        for (String string : listOfCardinals){
            sb.append(string + " ");
        }
        return sb.toString();
    }
}


