package org.nlpa.util;

import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastner.FastNER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardinalNumbers {
    private static final String zeroEs = "cero";
    private static final String[] unitsEs = {"uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};
    private static final String[] specialTenthsEs = {"diez","once","doce","trece","catorce","quince","dieciséis","dieciseis",
                                      "diecisiete","dieciocho","diecinueve","veinte","veintiuno","veintidos","veintitres",
                                      "veinticuatro","veinticinco","veintiseis","veintisiete","veintiocho","veintinueve"};
    private static final String[] tenthsEs = {"treinta","cuarenta","cincuenta","sesenta","setenta","ochenta","noventa"};
    private static final String specialHundredEs = "cien";
    private static final String [] hundredsEs = {"novecientos","ochocientos","setecientos","seiscientos","quinientos","cuatrocientos","trescientos","doscientos","ciento"};

    public CardinalNumbers() {
    }

    public void findCardinalsInTheText (String textToFindCardinals){
        //Falta por implementar
    }

    public String beforeOneThousand (String textToFindCardinals){
        String rule = "";
        String cardinalToAdd = "";
        String toReturn = "";
        List<String> result = new ArrayList<>();

        Boolean hasHundreds = false;
        Boolean hasSpecialHundreds = false;
        Boolean hasTenths = false;
        Boolean hasTenthsWithAnd = false;
        Boolean hasSpecialTenths = false;
        Boolean hasUnits = false;

        //First step: Hundreds
        for(String cardinalNumber : hundredsEs){
            rule = cardinalNumber + "\t Cardinal \n";
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                result.add(cardinalToAdd);
                hasHundreds = true;
                break;
            }
        }

        //Second step: Special Hundred (solo si no hay hundreds)
        if (!hasHundreds){
            rule = specialHundredEs + "\t Cardinal \n";
            findWithFastNERToken(rule,textToFindCardinals);
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                hasSpecialHundreds = true;
                result.add(cardinalToAdd);
            }
        }

        //Third step: Tenths (solo si no hay specialHundreds)
        if (!hasSpecialHundreds){
            for(String cardinalNumber : tenthsEs){
                rule = cardinalNumber.concat(" y ") + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    hasTenthsWithAnd = true;
                    result.add(cardinalToAdd);
                    break;
                }else{
                    rule = cardinalNumber + "\t Cardinal \n";
                    findWithFastNERToken(rule,textToFindCardinals);
                    if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                        result.add(cardinalToAdd);
                        hasTenths = true;
                        break;
                    }
                }
            }
        }

        //Fourth step: Special Tenths (solo si no tiene special hundreds, tenthsWithAnd y tenths)
        if (!hasSpecialHundreds && !hasTenthsWithAnd && !hasTenths){
            for(String cardinalNumber : specialTenthsEs) {
                rule = cardinalNumber + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    result.add(cardinalToAdd);
                    hasSpecialTenths = true;
                    break;
                }
            }
        }

        //Fifth step : Units (solo si no tiene special hundreds, tenths y specialTenths)
        if (!hasSpecialHundreds && !hasTenths && !hasSpecialTenths){
            for(String cardinalNumber : unitsEs) {
                rule = cardinalNumber + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    result.add(cardinalToAdd);
                    hasUnits = true;
                    break;
                }
            }
        }

        //Last step: zero (No cumple ninguna de las anteriores)
        if (!hasHundreds && !hasSpecialHundreds && !hasTenths && !hasSpecialTenths && !hasUnits){
            rule = zeroEs + "\t Cardinal \n";
            findWithFastNERToken(rule,textToFindCardinals);
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                result.add(cardinalToAdd);
            }
        }

        toReturn = printList(result);
        return toReturn;
    }

    public String beforeAMillion (String textToFindCardinals){
            Pattern pattern = Pattern.compile("mil");
            Matcher matcher = pattern.matcher(textToFindCardinals);
            Boolean isFound = matcher.find();
            String toReturn = "";
            if (isFound){
                //Cambiar para que localice según el número de instancias de mil hay en el String a analizar
                String[] beforeAndAfterAThousand = pattern.split(textToFindCardinals,2);
                String stringBeforeAThousand = beforeAndAfterAThousand[0].replaceAll(" +", " ").trim();
                String stringAfterAThousand = beforeAndAfterAThousand[1].replaceAll(" +", " ").trim();
                if (!stringBeforeAThousand.isEmpty() && !stringAfterAThousand.isEmpty()){
                    String cardinalBeforeThousand = this.beforeOneThousand(stringBeforeAThousand);
                    String cardinalAfterThousand = this.beforeOneThousand(stringAfterAThousand);
                    String rule = cardinalBeforeThousand + " mil " + cardinalAfterThousand + "\t Cardinal \n";
                    toReturn = findWithFastNERToken(rule,textToFindCardinals);
                    return toReturn;
                }else if (!stringBeforeAThousand.isEmpty()){
                    String cardinalBeforeThousand = this.beforeOneThousand(stringBeforeAThousand);
                    String rule = cardinalBeforeThousand + " mil" + "\t Cardinal \n";
                    toReturn = findWithFastNERToken(rule,textToFindCardinals);
                    return toReturn;
                }else if (!stringAfterAThousand.isEmpty()){
                    String cardinalAfterThousand = this.beforeOneThousand(stringAfterAThousand);
                    String rule = "mil " + cardinalAfterThousand +  "\t Cardinal \n";
                    toReturn = findWithFastNERToken(rule,textToFindCardinals);
                    return toReturn;
                }else{
                    String rule = "mil\t Cardinal \n";
                    toReturn = findWithFastNERToken(rule,textToFindCardinals);
                    return toReturn;
                }

            }
            return beforeOneThousand(textToFindCardinals);

    }

    public String beforeABillion (String textToFindCardinals){
        Pattern pattern = Pattern.compile("millones");
        Matcher matcher = pattern.matcher(textToFindCardinals);
        Boolean isFound = matcher.find();
        String toReturn = "";
        if (isFound){
            String[] beforeAndAfterAMillion = pattern.split(textToFindCardinals, 2);
            String stringBeforeAMillion = beforeAndAfterAMillion[0].replaceAll(" +", " ").trim();
            String stringAfterAMillion = beforeAndAfterAMillion[1].replaceAll(" +", " ").trim();
            if (!stringBeforeAMillion.isEmpty() && !stringAfterAMillion.isEmpty()){
                String cardinalBeforeAMillion = beforeAMillion(stringBeforeAMillion);
                String cardinalAfterAMillion = beforeAMillion(stringAfterAMillion);
                String rule = cardinalBeforeAMillion + " millones " + cardinalAfterAMillion +  "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }else if (!stringBeforeAMillion.isEmpty() && stringAfterAMillion.isEmpty()){
                String cardinalBeforeAMillion = beforeAMillion(stringBeforeAMillion);
                String rule = cardinalBeforeAMillion + " millones" + "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }else{
            pattern = Pattern.compile("millon");
            matcher = pattern.matcher(textToFindCardinals);
            isFound = matcher.find();
            if (isFound) {
                String[] beforeAndAfterAMillion = pattern.split(textToFindCardinals,2);
                String stringAfterAMillion = beforeAndAfterAMillion[1].replaceAll(" +", " ").trim();
                String cardinalAfterAMillion = beforeAMillion(stringAfterAMillion);
                String rule = "un millon " + cardinalAfterAMillion +  "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }
        return beforeAMillion(textToFindCardinals);
    }
    public String beforeATrillion (String textToFindCardinals){
        Pattern pattern = Pattern.compile("billones");
        Matcher matcher = pattern.matcher(textToFindCardinals);
        Boolean isFound = matcher.find();
        String toReturn = "";
        if (isFound){
            String[] beforeAndAfterABillion = pattern.split(textToFindCardinals, 2);
            String stringBeforeABillion = beforeAndAfterABillion[0].replaceAll(" +", " ").trim();
            String stringAfterABillion = beforeAndAfterABillion[1].replaceAll(" +", " ").trim();
            if (!stringBeforeABillion.isEmpty() && !stringAfterABillion.isEmpty()){
                String cardinalBeforeABillion = beforeABillion(stringBeforeABillion);
                String cardinalAfterABillion = beforeABillion(stringAfterABillion);
                String rule = cardinalBeforeABillion + " billones " + cardinalAfterABillion +  "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }else if (!stringBeforeABillion.isEmpty() && stringAfterABillion.isEmpty()){
                String cardinalBeforeABillion = beforeABillion(stringBeforeABillion);
                String rule = cardinalBeforeABillion + " billones" + "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }else{
            pattern = Pattern.compile("billon");
            matcher = pattern.matcher(textToFindCardinals);
            isFound = matcher.find();
            if (isFound) {
                String[] beforeAndAfterABillion = pattern.split(textToFindCardinals,2);
                String stringAfterABillion = beforeAndAfterABillion[1].replaceAll(" +", " ").trim();
                String cardinalAfterABillion = beforeABillion(stringAfterABillion);
                String rule = "un billon " + cardinalAfterABillion +  "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }
        return beforeABillion(textToFindCardinals);
    }

    public String beforeAQuatrillion (String textToFindCardinals){
        Pattern pattern = Pattern.compile("trillones");
        Matcher matcher = pattern.matcher(textToFindCardinals);
        Boolean isFound = matcher.find();
        String toReturn = "";
        if (isFound){
            String[] beforeAndAfterATrillion = pattern.split(textToFindCardinals, 2);
            String stringBeforeATrillion = beforeAndAfterATrillion[0].replaceAll(" +", " ").trim();
            String stringAfterATrillion = beforeAndAfterATrillion[1].replaceAll(" +", " ").trim();
            if (!stringBeforeATrillion.isEmpty() && !stringAfterATrillion.isEmpty()){
                String cardinalBeforeATrillion = beforeATrillion(stringBeforeATrillion);
                String cardinalAfterATrillion = beforeATrillion(stringAfterATrillion);
                String rule = cardinalBeforeATrillion + " trillones " + cardinalAfterATrillion +  "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }else if (!stringBeforeATrillion.isEmpty() && stringAfterATrillion.isEmpty()){
                String cardinalBeforeATrillion = beforeATrillion(stringBeforeATrillion);
                String rule = cardinalBeforeATrillion + " trillones" + "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }else{
            pattern = Pattern.compile("trillon");
            matcher = pattern.matcher(textToFindCardinals);
            isFound = matcher.find();
            if (isFound) {
                String[] beforeAndAfterATrillion = pattern.split(textToFindCardinals,2);
                String stringAfterATrillion = beforeAndAfterATrillion[1].replaceAll(" +", " ").trim();
                String cardinalAfterATrillion = beforeATrillion(stringAfterATrillion);
                String rule = "un trillon " + cardinalAfterATrillion +  "\t Cardinal \n";
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }
        return beforeATrillion(textToFindCardinals);
    }

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

    public String printList (List<String> listOfCardinals){
        StringBuilder sb = new StringBuilder();
        for (String string : listOfCardinals){
            sb.append(string + " ");
        }
        return sb.toString();
    }
}