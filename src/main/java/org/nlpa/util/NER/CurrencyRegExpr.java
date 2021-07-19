package org.nlpa.util.NER;

import javax.json.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyRegExpr {
    private static HashMap<String,String> regularExpresionForCurrencyMap = new HashMap<>();
    private static List<String> listOfRegExpressionToMatchEs = new ArrayList<>();
    public CurrencyRegExpr(){
    }
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionforcurrency-json/regExprForDates.json");
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
    static {
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionforcurrency-json/matchingRegExpression.es.json");;
            JsonReader rdr = Json.createReader(is);
            JsonArray array = rdr.readArray();
            rdr.close();
            array.forEach((v) -> {
                listOfRegExpressionToMatchEs.add(((JsonString)v).getString());
            });
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
                System.out.println(numberFound);
                numberEntitiesFound.add(numberFound);
            }
        }
        return numberEntitiesFound;
    }

    public List<String> findAllCardinalNumberEntities (String textToFindEntities){
        List<String> numberEntitiesFound = new ArrayList<>();
        Pattern pattern = Pattern.compile(regularExpresionForCurrencyMap.get("CardinalNumberEs"));
        Matcher matcher = pattern.matcher(textToFindEntities);
        while (matcher.find()){
            String numberFound = matcher.group();
            if(!numberEntitiesFound.contains(numberFound)){
                System.out.println(numberFound);
                numberEntitiesFound.add(numberFound);
            }
        }
        return numberEntitiesFound;
    }
    //To Test (falla CurrencyISOandSymbols)
    private List<String> currencyPatternsEs (){
        List<String> listCurrencyPatterns = new ArrayList<>();
        if (!listOfRegExpressionToMatchEs.isEmpty() && !regularExpresionForCurrencyMap.isEmpty()){
            for (String regExprToMatch : listOfRegExpressionToMatchEs){
                String stringToChange = regExprToMatch;
                for (String key : regularExpresionForCurrencyMap.keySet()){
                    if (stringToChange.contains(key)){
                        String regExp = regularExpresionForCurrencyMap.get(key);
                        stringToChange = stringToChange.replaceAll(key, regExp);
                    }
                }
                if (!regExprToMatch.equals(stringToChange) && !listCurrencyPatterns.contains(stringToChange)){
                    listCurrencyPatterns.add(stringToChange);
                }
            }
        }

        return listCurrencyPatterns;
    }
    public List<String> findAllCurrencyEntities (String textToFindEntities){
        List<String> currencyEntity = new ArrayList<>();
        if (!regularExpresionForCurrencyMap.isEmpty()){
            textToFindEntities = formatString(textToFindEntities);
            List<String> currencyPatterns = currencyPatternsEs();
            Pattern pattern;
            Matcher matcher;
            for (String currency : currencyPatterns){
                pattern = Pattern.compile(currency);
                matcher = pattern.matcher(textToFindEntities);
                while (matcher.find()){
                    System.out.println(matcher.group());
                    currencyEntity.add(matcher.group());
                }
            }
        }
        return currencyEntity;
    }

    private String formatString (String stringToFormat){
        //stringToFormat = StringUtils.stripAccents(stringToFormat);
        //stringToFormat = stringToFormat.toLowerCase();
        stringToFormat = stringToFormat.trim();
        stringToFormat = stringToFormat.replaceAll("\r", " ");
        stringToFormat = stringToFormat.replaceAll("\t", " ");
        //stringToFormat = stringToFormat.replaceAll("\n", " ");
        stringToFormat = stringToFormat.replaceAll(" +", " ");
        return stringToFormat;
    }

}
