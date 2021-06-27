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

public class CardinalNumbersTest {
    private static final String zeroEs = "cero";
    private static final String[] unitsEs = {"uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};
    private static final String[] specialTenthsEs = {"diez","once","doce","trece","catorce","quince","dieciséis","dieciseis",
                                      "diecisiete","dieciocho","diecinueve","veinte","veintiuno","veintidos","veintitres",
                                      "veinticuatro","veinticinco","veintiseis","veintisiete","veintiocho","veintinueve"};
    private static final String[] tenthsEs = {"treinta","cuarenta","cincuenta","sesenta","setenta","ochenta","noventa"};
    private static final String specialHundredEs = "cien";
    private static final String [] hundredsEs = {"novecientos","ochocientos","setecientos","seiscientos","quinientos","cuatrocientos","trescientos","doscientos","ciento"};

    public CardinalNumbersTest() {
    }

    public String beforeOneThousand (String textToFindCardinals){
        String rule = "";
        String cardinalToAdd = "";
        List<String> result = new ArrayList<>();

        //First step: Hundreds
        for(String cardinalNumber : hundredsEs){
            rule = cardinalNumber + "\t Cardinal \n";
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                result.add(cardinalToAdd);
                break;
            }
        }

        //Second step: Special Hundred
        rule = specialHundredEs + "\t Cardinal \n";
        findWithFastNERToken(rule,textToFindCardinals);
        cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
        if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
            result.add(cardinalToAdd);
        }

        //Third step: Tenths
        for(String cardinalNumber : tenthsEs){
            rule = cardinalNumber.concat(" y ") + "\t Cardinal \n";
            findWithFastNERToken(rule,textToFindCardinals);
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                result.add(cardinalToAdd);
                break;
            }else{
                rule = cardinalNumber + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    result.add(cardinalToAdd);
                    break;
                }
            }
        }
        //Fourth step: Special Tenths
        for(String cardinalNumber : specialTenthsEs) {
            rule = cardinalNumber + "\t Cardinal \n";
            findWithFastNERToken(rule,textToFindCardinals);
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                result.add(cardinalToAdd);
                break;
            }
        }
        //Fifth step : Units
        for(String cardinalNumber : unitsEs) {
            rule = cardinalNumber + "\t Cardinal \n";
            findWithFastNERToken(rule,textToFindCardinals);
            cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
            if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                result.add(cardinalToAdd);
                break;
            }
        }
        //Last step: zero
        rule = zeroEs + "\t Cardinal \n";
        findWithFastNERToken(rule,textToFindCardinals);
        cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
        if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
            result.add(cardinalToAdd);
        }

        System.out.println(printList(result));
        return printList(result);

    }

    public String beforeAMillion (String textToFindCardinals){
            Pattern pattern = Pattern.compile("mil");
            Matcher matcher = pattern.matcher(textToFindCardinals);
            Boolean isFound = matcher.find();
            if (isFound){
                //Cambiar para que localice según el número de instancias de mil hay en el String a analizar
                String[] beforeAndAfterAThousand = pattern.split(textToFindCardinals,2);
                String stringBeforeAThousand = beforeAndAfterAThousand[0].replaceAll(" +", " ").trim();
                String stringAfterAThousand = beforeAndAfterAThousand[1].replaceAll(" +", " ").trim();
                if (!stringBeforeAThousand.isEmpty() && !stringAfterAThousand.isEmpty()){
                    String cardinalBeforeThousand = this.beforeOneThousand(stringBeforeAThousand);
                    String cardinalAfterThousand = this.beforeOneThousand(stringAfterAThousand);
                    String rule = cardinalBeforeThousand + " mil " + cardinalAfterThousand + "\t Cardinal \n";
                    System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                    return findWithFastNERToken(rule,textToFindCardinals);
                }else if (!stringBeforeAThousand.isEmpty()){
                    String cardinalBeforeThousand = this.beforeOneThousand(stringBeforeAThousand);
                    String rule = cardinalBeforeThousand + " mil" + "\t Cardinal \n";
                    System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                    return findWithFastNERToken(rule,textToFindCardinals);
                }else if (!stringAfterAThousand.isEmpty()){
                    String cardinalAfterThousand = this.beforeOneThousand(stringAfterAThousand);
                    String rule = "mil " + cardinalAfterThousand +  "\t Cardinal \n";
                    System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                    return findWithFastNERToken(rule,textToFindCardinals);

                }else{
                    String rule = "mil\t Cardinal \n";
                    System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                    return findWithFastNERToken(rule,textToFindCardinals);
                }

            }
            return beforeOneThousand(textToFindCardinals);

    }

    public String beforeABillion (String textToFindCardinals){
        Pattern pattern = Pattern.compile("millones");
        Matcher matcher = pattern.matcher(textToFindCardinals);
        Boolean isFound = matcher.find();
        if (isFound){
            String[] beforeAndAfterAMillion = pattern.split(textToFindCardinals, 2);
            String stringBeforeAMillion = beforeAndAfterAMillion[0].replaceAll(" +", " ").trim();
            String stringAfterAMillion = beforeAndAfterAMillion[1].replaceAll(" +", " ").trim();
            if (!stringBeforeAMillion.isEmpty() && !stringAfterAMillion.isEmpty()){
                String cardinalBeforeAMillion = beforeAMillion(stringBeforeAMillion);
                String cardinalAfterAMillion = beforeAMillion(stringAfterAMillion);
                String rule = cardinalBeforeAMillion + " millones " + cardinalAfterAMillion +  "\t Cardinal \n";
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
            }else if (!stringBeforeAMillion.isEmpty() && stringAfterAMillion.isEmpty()){
                String cardinalBeforeAMillion = beforeAMillion(stringBeforeAMillion);
                String rule = cardinalBeforeAMillion + " millones" + "\t Cardinal \n";
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
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
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
            }
        }
        return beforeAMillion(textToFindCardinals);
    }
    public String beforeATrillion (String textToFindCardinals){
        Pattern pattern = Pattern.compile("billones");
        Matcher matcher = pattern.matcher(textToFindCardinals);
        Boolean isFound = matcher.find();
        if (isFound){
            String[] beforeAndAfterABillion = pattern.split(textToFindCardinals, 2);
            String stringBeforeABillion = beforeAndAfterABillion[0].replaceAll(" +", " ").trim();
            String stringAfterABillion = beforeAndAfterABillion[1].replaceAll(" +", " ").trim();
            if (!stringBeforeABillion.isEmpty() && !stringAfterABillion.isEmpty()){
                String cardinalBeforeABillion = beforeABillion(stringBeforeABillion);
                String cardinalAfterABillion = beforeABillion(stringAfterABillion);
                String rule = cardinalBeforeABillion + " billones " + cardinalAfterABillion +  "\t Cardinal \n";
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
            }else if (!stringBeforeABillion.isEmpty() && stringAfterABillion.isEmpty()){
                String cardinalBeforeABillion = beforeABillion(stringBeforeABillion);
                String rule = cardinalBeforeABillion + " billones" + "\t Cardinal \n";
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
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
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
            }
        }
        return beforeABillion(textToFindCardinals);
    }

    public String beforeAQuatrillion (String textToFindCardinals){
        Pattern pattern = Pattern.compile("trillones");
        Matcher matcher = pattern.matcher(textToFindCardinals);
        Boolean isFound = matcher.find();
        if (isFound){
            String[] beforeAndAfterATrillion = pattern.split(textToFindCardinals, 2);
            String stringBeforeATrillion = beforeAndAfterATrillion[0].replaceAll(" +", " ").trim();
            String stringAfterATrillion = beforeAndAfterATrillion[1].replaceAll(" +", " ").trim();
            if (!stringBeforeATrillion.isEmpty() && !stringAfterATrillion.isEmpty()){
                String cardinalBeforeATrillion = beforeATrillion(stringBeforeATrillion);
                String cardinalAfterATrillion = beforeATrillion(stringAfterATrillion);
                String rule = cardinalBeforeATrillion + " trillones " + cardinalAfterATrillion +  "\t Cardinal \n";
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
            }else if (!stringBeforeATrillion.isEmpty() && stringAfterATrillion.isEmpty()){
                String cardinalBeforeATrillion = beforeATrillion(stringBeforeATrillion);
                String rule = cardinalBeforeATrillion + " trillones" + "\t Cardinal \n";
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
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
                System.out.println(findWithFastNERToken(rule,textToFindCardinals));
                return findWithFastNERToken(rule,textToFindCardinals);
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


