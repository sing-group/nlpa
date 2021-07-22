package org.nlpa.util.NER;

import org.apache.commons.lang3.StringUtils;

import javax.json.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyRegExpr {
    private static HashMap<String,String> regularExpresionForCurrencyMap = new HashMap<>();
    public CurrencyRegExpr(){
    }
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/currency-json/regExprForCurrency.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String keyRegExpr : jsonObject.keySet()) {
                regularExpresionForCurrencyMap.put(keyRegExpr,jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> findAllNumberEntities (String textToFindEntities){
        List<String> numberEntitiesFound = new ArrayList<>();
        Pattern pattern = Pattern.compile(regularExpresionForCurrencyMap.get("Numbers"));
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String numberFound = matcher.group();
            if(!numberEntitiesFound.contains(numberFound)){
                numberEntitiesFound.add(numberFound);
            }
        }
        return numberEntitiesFound;
    }

    public List<String> findAllCardinalNumberEsEntities (String textToFindEntities){
        List<String> cardinalNumberEntitiesFound = new ArrayList<>();
        Pattern pattern = Pattern.compile(regularExpresionForCurrencyMap.get("CardinalNumberEs"));
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String cardinalNumberFound = matcher.group();
            if(!cardinalNumberEntitiesFound.contains(cardinalNumberFound)){
                cardinalNumberEntitiesFound.add(cardinalNumberFound);
            }
        }
        return cardinalNumberEntitiesFound;
    }
    public List<String> findAllCardinalNumberEnEntities (String textToFindEntities){
        List<String> cardinalNumberEntitiesFound = new ArrayList<>();
        Pattern pattern = Pattern.compile(regularExpresionForCurrencyMap.get("CardinalNumberEn"));
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String cardinalNumberFound = matcher.group();
            if(!cardinalNumberEntitiesFound.contains(cardinalNumberFound)){
                cardinalNumberEntitiesFound.add(cardinalNumberFound);
            }
        }
        return cardinalNumberEntitiesFound;
    }

    public List<String> findAllCurrencyNameEsEntities (String textToFindEntities){
        List<String> currencyNameEntitiesFound = new ArrayList<>();
        String namesEs = StringUtils.stripAccents(regularExpresionForCurrencyMap.get("CurrencyNameEs"));
        Pattern pattern = Pattern.compile(namesEs);
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String currencyNameFound = matcher.group();
            if(!currencyNameEntitiesFound.contains(currencyNameFound)){
                currencyNameEntitiesFound.add(currencyNameFound);
            }
        }
        pattern = Pattern.compile(namesEs.toLowerCase());
        matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String currencyNameFound = matcher.group();
            if(!currencyNameEntitiesFound.contains(currencyNameFound)){
                currencyNameEntitiesFound.add(currencyNameFound);
            }
        }
        return currencyNameEntitiesFound;
    }

    public List<String> findAllCurrencyNameEnEntities (String textToFindEntities){
        long startTime = System.nanoTime();
        List<String> currencyNameEntitiesFound = new ArrayList<>();
        String namesEs = StringUtils.stripAccents(regularExpresionForCurrencyMap.get("CurrencyNameEn"));
        Pattern pattern = Pattern.compile(namesEs);
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String currencyNameFound = matcher.group();
            if(!currencyNameEntitiesFound.contains(currencyNameFound)){
                currencyNameEntitiesFound.add(currencyNameFound);
            }
        }
        pattern = Pattern.compile(namesEs.toLowerCase());
        matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String currencyNameFound = matcher.group();
            if(!currencyNameEntitiesFound.contains(currencyNameFound)){
                currencyNameEntitiesFound.add(currencyNameFound);
            }
        }
        System.out.println("Número de entidades de dinero encontrados con expresiones regulares: " + currencyNameEntitiesFound.size());
        long endTime = System.nanoTime();
        System.out.println("Duración búsqueda de entidades de dinero con expresiones regulares: " + (endTime-startTime)/1e6 + " ms");
        return currencyNameEntitiesFound;
    }

    public List<String> findAllCurrencyIsoAndSymbolEntities (String textToFindEntities){
        List<String> currencyISOandSymbolEntitiesFound = new ArrayList<>();
        Pattern pattern = Pattern.compile(regularExpresionForCurrencyMap.get("CurrencyISOandSymbol"));
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String currencyISOandSymbolFound = matcher.group();
            if(!currencyISOandSymbolFound.isEmpty() && !currencyISOandSymbolEntitiesFound.contains(currencyISOandSymbolFound)){
                currencyISOandSymbolEntitiesFound.add(currencyISOandSymbolFound);
            }
        }
        return currencyISOandSymbolEntitiesFound;
    }
    public String findAllCurrencyAsociatedToANumberEntities (String lang, String textToFindEntities){
        long startTime = System.nanoTime();
        List<String> currencyAssociatedToANumberEntities = new ArrayList<>();
        if (!regularExpresionForCurrencyMap.isEmpty()){
            Pattern pattern;
            Matcher matcher;
            Boolean currencyAsociatedToANumber = false;
            textToFindEntities = formatString(textToFindEntities);
            List<String> listNumberEntities  = findAllNumberEntities(textToFindEntities);
            List<String> listCardinalNumberEntities  = new ArrayList<>();
            List<String> listCurrencyNameEntities  = new ArrayList<>();
            if (lang.equals("EN")){
                listCardinalNumberEntities  = findAllCardinalNumberEnEntities(textToFindEntities);
                listCurrencyNameEntities  = findAllCurrencyNameEnEntities(textToFindEntities);
            }else if (lang.equals("ES")) {
                listCardinalNumberEntities  = findAllCardinalNumberEsEntities(textToFindEntities);
                listCurrencyNameEntities  = findAllCurrencyNameEsEntities(textToFindEntities);
            }
            List<String> listCurrencyISOandSymbolEntities  = findAllCurrencyIsoAndSymbolEntities(textToFindEntities);

            for (String currencyName : listCurrencyNameEntities){
                for (String number : listNumberEntities){
                    pattern = Pattern.compile(number + "[ ]*" + currencyName);
                    matcher = pattern.matcher(textToFindEntities);
                    while (matcher.find()){
                        currencyAsociatedToANumber = true;
                        currencyAssociatedToANumberEntities.add(matcher.group());
                    }
                }

                for (String cardinalNumber : listCardinalNumberEntities){
                    pattern = Pattern.compile(cardinalNumber + "[ ]*" + currencyName);
                    matcher = pattern.matcher(textToFindEntities);
                    while (matcher.find()){
                        currencyAsociatedToANumber = true;
                        currencyAssociatedToANumberEntities.add(matcher.group());
                    }
                }

                if (!currencyAsociatedToANumber){
                    currencyAssociatedToANumberEntities.add(currencyName);
                } else {currencyAsociatedToANumber = false;}
            }
            currencyAsociatedToANumber = false;
            for (String currencyISOandSymbol : listCurrencyISOandSymbolEntities){
                for (String number : listNumberEntities){
                    pattern = Pattern.compile(number + "[ ]*" + currencyISOandSymbol);
                    matcher = pattern.matcher(textToFindEntities);
                    while (matcher.find()){
                        currencyAsociatedToANumber = true;
                        currencyAssociatedToANumberEntities.add(matcher.group());
                    }
                    pattern = Pattern.compile(currencyISOandSymbol + "[ ]*" + number);
                    matcher = pattern.matcher(textToFindEntities);
                    while (matcher.find()){
                        currencyAsociatedToANumber = true;
                        currencyAssociatedToANumberEntities.add(matcher.group());
                    }
                }

                for (String cardinalNumber : listCardinalNumberEntities){
                    pattern = Pattern.compile(cardinalNumber + "[ ]*" + currencyISOandSymbol);
                    matcher = pattern.matcher(textToFindEntities);
                    while (matcher.find()){
                        currencyAsociatedToANumber = true;
                        currencyAssociatedToANumberEntities.add(matcher.group());
                    }
                    pattern = Pattern.compile(currencyISOandSymbol + "[ ]*" +  cardinalNumber);
                    matcher = pattern.matcher(textToFindEntities);
                    while (matcher.find()){
                        currencyAsociatedToANumber = true;
                        currencyAssociatedToANumberEntities.add(matcher.group());
                    }
                }

                if (!currencyAsociatedToANumber){
                    currencyAssociatedToANumberEntities.add(currencyISOandSymbol);
                } else {currencyAsociatedToANumber = false;}
            }
        }
        long endTime = System.nanoTime();
        System.out.println("Duración Currency Expresiones Regulares: " + (endTime - startTime) / 1e6 + " ms");
        return printList(currencyAssociatedToANumberEntities);
    }

    private String formatString (String stringToFormat){
        stringToFormat = StringUtils.stripAccents(stringToFormat);
        stringToFormat = stringToFormat.trim();
        stringToFormat = stringToFormat.replaceAll("\r", " ");
        stringToFormat = stringToFormat.replaceAll("\t", " ");
        stringToFormat = stringToFormat.replaceAll("\n", " ");
        stringToFormat = stringToFormat.replaceAll(" +", " ");
        return stringToFormat;
    }

    public String printList (List<String> listOfCardinals){
        StringBuilder sb = new StringBuilder();
        for (String string : listOfCardinals){
            sb.append(string + "\n");
        }
        return sb.toString();
    }

}
