package org.nlpa.util;

import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastner.FastNER;
import org.apache.commons.lang3.StringUtils;

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

    //Metodo que devuelve una lista de los cardinales encontrados en la cadena de texto que se le pasa por parámetro (falta procesar el texto que se pasa por parámetro poniendo todo a minuscula, quitando espacios y separadores)
    public List<String> findAllCardinalsInTheText (String textToFindCardinals){
        List<String> listOfEntitiesFound = new ArrayList<>();
        String entityFound = "";

        //Limpiar la cadena para facilitar la busqueda
        textToFindCardinals = StringUtils.stripAccents(textToFindCardinals);
        textToFindCardinals = textToFindCardinals.toLowerCase();
        textToFindCardinals = textToFindCardinals.trim();
        textToFindCardinals = textToFindCardinals.replaceAll("\t", " ");
        textToFindCardinals = textToFindCardinals.replaceAll("\n", " ");
        textToFindCardinals = textToFindCardinals.replaceAll(" +", " ");


        Boolean theSearchIsOver = false;
        do{
            entityFound = findCardinalInText(textToFindCardinals);
            if (!entityFound.isEmpty() && !listOfEntitiesFound.contains(entityFound)){
                listOfEntitiesFound.add(entityFound);
                textToFindCardinals = deleteAEntityWithPattern(entityFound,textToFindCardinals);
            }else theSearchIsOver = true;
        }while (!theSearchIsOver);
        return listOfEntitiesFound;
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

    public String findCardinalInText (String textToFindCardinals){
        String entityToReturn = "";
        if (textToFindCardinals.contains("trillones") || textToFindCardinals.contains("trillon")){
            entityToReturn = beforeAQuatrillion(textToFindCardinals);
        }else if (textToFindCardinals.contains("billones") || textToFindCardinals.contains("billon")){
            entityToReturn = beforeATrillion(textToFindCardinals);
        }else if (textToFindCardinals.contains("millones") || textToFindCardinals.contains("millon")){
            entityToReturn = beforeABillion(textToFindCardinals);
        }else if (textToFindCardinals.contains("mil")){
            entityToReturn = beforeAMillion(textToFindCardinals);
        }else{
            entityToReturn = beforeOneThousand(textToFindCardinals);
        }

        if (!entityToReturn.isEmpty()){
            String entityWithFractionalPart = fractionalPartOfACardinalNumber(entityToReturn,textToFindCardinals);
            if (!entityWithFractionalPart.isEmpty()){
                entityToReturn = entityWithFractionalPart;
            }
        }
        return entityToReturn;
    }

    //Revisar esto
    public String fractionalPartOfACardinalNumber (String cardinalNumberFound ,String textToFindCardinals){
        String rule = "";
        String cardinalToAdd = "";
        String toReturn = "";
        List<String> result = new ArrayList<>();

        Boolean hasTenths = false;
        Boolean hasTenthsWithAnd = false;
        Boolean hasSpecialTenths = false;
        Boolean hasUnits = false;

        for(String cardinalNumber : tenthsEs){
                rule = cardinalNumberFound + " con " + cardinalNumber.concat(" y ") + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    hasTenthsWithAnd = true;
                    result.add(cardinalToAdd);
                    break;
                }else{
                    rule = cardinalNumberFound + " con " + cardinalNumber + "\t Cardinal \n";
                    findWithFastNERToken(rule,textToFindCardinals);
                    if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                        result.add(cardinalToAdd);
                        hasTenths = true;
                        break;
                    }

                }
        }

        //Special Tenths (solo si no tiene tenthsWithAnd y tenths)
        if (!hasTenthsWithAnd && !hasTenths){
            for(String cardinalNumber : specialTenthsEs) {
                rule = cardinalNumberFound + " con " + cardinalNumber + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    result.add(cardinalToAdd);
                    hasSpecialTenths = true;
                    break;
                }
            }
        }

        //Units (solo si no tiene tenths y specialTenths)
        if (hasTenthsWithAnd && !hasTenths && !hasSpecialTenths){
            for(String cardinalNumber : unitsEs) {
                String fractionalFirstPart = printList(result);
                rule = fractionalFirstPart + cardinalNumber + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    result.clear();
                    hasUnits = true;
                    result.add(cardinalToAdd);
                    break;
                }
            }
        }else{
            for(String cardinalNumber : unitsEs) {
                rule = cardinalNumber + "\t Cardinal \n";
                findWithFastNERToken(rule,textToFindCardinals);
                cardinalToAdd = findWithFastNERToken(rule,textToFindCardinals);
                if (!cardinalToAdd.isEmpty() && !result.contains(cardinalToAdd)){
                    result.add(cardinalToAdd);
                    break;
                }
            }
        }
        if (hasTenthsWithAnd && !hasUnits){
            result.clear();
        }

        toReturn = printList(result);
        return toReturn;
    }

    //Metodo que hace búsqueda de numeros cardinales entre 0 y 999, devuelve el cardinal encontrado o una cadena vacia (todo clean code)
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

    //Metodo que busca en una cadena un numero cardinal entre 0 y 999 999, devuelve el cardinal encontrado o una cadena vacia (todo clean code)
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

    //Metodo que busca en una cadena un numero cardinal entre 0 y 999 999 999 999, devuelve el cardinal encontrado o una cadena vacia (todo clean code)
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
                String rule = "";
                if (!stringAfterAMillion.isEmpty()){
                    String cardinalAfterAMillion = beforeATrillion(stringAfterAMillion);
                    rule = "un millon " + cardinalAfterAMillion +  "\t Cardinal \n";
                }else {
                    rule = "un millon" + "\t Cardinal \n";
                }
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }
        return beforeAMillion(textToFindCardinals);
    }

    //Metodo que busca en una cadena un numero cardinal entre 0 y 999 999 999 999 999 999, devuelve el cardinal encontrado o una cadena vacia (todo clean code)
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
                String rule = "";
                if (!stringAfterABillion.isEmpty()){
                    String cardinalAfterABillion = beforeATrillion(stringAfterABillion);
                    rule = "un billon " + cardinalAfterABillion +  "\t Cardinal \n";
                }else {
                    rule = "un billon" + "\t Cardinal \n";
                }
                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }
        return beforeABillion(textToFindCardinals);
    }

    //Metodo que busca en una cadena un numero cardinal entre 0 y 999 999 999 999 999 999 999 999, devuelve el cardinal encontrado o una cadena vacia (todo clean code)
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
                String rule = "";
                if (!stringAfterATrillion.isEmpty()){
                    String cardinalAfterATrillion = beforeATrillion(stringAfterATrillion);
                    rule = "un trillon " + cardinalAfterATrillion +  "\t Cardinal \n";
                }else {
                    rule = "un trillon" + "\t Cardinal \n";
                }

                toReturn = findWithFastNERToken(rule,textToFindCardinals);
                return toReturn;
            }
        }
        return beforeATrillion(textToFindCardinals);
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

    public String printList (List<String> listOfCardinals){
        StringBuilder sb = new StringBuilder();
        for (String string : listOfCardinals){
            sb.append(string + " ");
        }
        return sb.toString();
    }
}