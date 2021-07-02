package org.nlpa.util;

import edu.utah.bmi.nlp.core.NERRule;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.fastcner.FastCNER;
import edu.utah.bmi.nlp.fastner.FastNER;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.utah.bmi.nlp.core.DeterminantValueSet.Determinants.ACTUAL;

public class CurrencyCardinalNumbers {
    private List<String> listOfEntitiesFound;
    //Cardinal numbers in spanish
    private static final String whiteSpace = "[ ]".concat("*");
    private static final String tenFirstCardinalNumberEs = "(cero|uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)";
    private static final String tensCardinalNumberEs = "(diez|once|doce|trece|catorce|quince|dieciséis|dieciseis|diecisiete|dieciocho|diecinueve)";
    private static final String twentiesCardinalNumberEs = "(veinte|veintiuno|veintidos|veintitres|veinticuatro|veinticinco|veintiseis|veintisiete|veintiocho|veintinueve)";
    private static final String beforeAHundredCardinalNumberEs = "(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)";
    private static final String hundredCardinalNumberEs = "(ciento|cien)";
    private static final String beforeAThousandCardinalNumberEs =  "(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|setecientos|ochocientos|novecientos)";

    //0-9|(10-29)|(30-90)([ ]*y[ ]*(0-9))
    private static final String regularExpresionforCardinalNumbersBeforeAHundredEs = tenFirstCardinalNumberEs  + "|" + "(" + tensCardinalNumberEs + "|" + twentiesCardinalNumberEs + ")" +
            "|" + "(" + beforeAHundredCardinalNumberEs + whiteSpace + "(" + "(y)" + whiteSpace + tenFirstCardinalNumberEs + ")".concat("?")  + ")";

    //((100|200-900)[ ]*(0-99)?|0-99)
    private static final String regularExpresionforCardinalNumbersBeforeAThousandEs = "(" + "(" + hundredCardinalNumberEs +
                                    "|" + beforeAThousandCardinalNumberEs + ")" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeAHundredEs.concat("?") + ")" +
                                    ")" + "|" + "(" + regularExpresionforCardinalNumbersBeforeAHundredEs + ")";

    //The rest cardinals names without accent

    private static final String restOfCardinalsToFindEs = "(gugolquatruplex|gugoltriplex|gugolduplex|gugolplex|gugol|centillones|centillon|" +
                                                            "vigintillones|vigintillon|tredecillones|tredecillon|duodecillones|duodecillon|undecillones|undecillon|decillones|" +
                                                            "decillon|nonillones|nonillon|octillones|octillon|septillones|septillon|sextillones|sextillon|quintillones|quintillon|" +
                                                            "cuatrillones|cuatrillon|trillones|trillon|billones|billon|millones|millon|mil)";

    //Cardinal numbers in english
    private static final String tenFirstCardinalNumberEn = "(zero|one|two|three|four|five|six|seven|eight|nine)";
    private static final String tensCardinalNumberEn = "(ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen)";
    private static final String beforeAHundredCardinalNumberEn = "(twenty|thirty|forty|fifty|sixty|seventy|eighty|ninety)";

    //0-9|(10-29)|(30-90)([ ]*y[ ]*(0-9))
    private static final String regularExpresionforCardinalNumbersBeforeAHundredEn = tenFirstCardinalNumberEn  + "|" + tensCardinalNumberEn +
            "|" + "(" + beforeAHundredCardinalNumberEn + whiteSpace + tenFirstCardinalNumberEn.concat("?") + ")";

    //The rest cardinals names in english
    private static final String restOfCardinalsToFindEn = "(googolplex|googol|centillion|vigintillion|novemdecillion|octodecillion|" +
                                                                "septendecillion|sexdecillion|quindecillion|quattuordecillion|tredecillion|duodecillion|undecillion|decillion|nonillion|octillion|" +
                                                                "septillion|sextillion|quintillion|quadrillion|trillion|billiard|billion|milliard|million|thousand|hundred)";

    public CurrencyCardinalNumbers (){
        listOfEntitiesFound = new ArrayList<String>();
    }

    public List<String> getListOfEntitiesFound() {
        return listOfEntitiesFound;
    }

    public void setListOfEntitiesFound(List<String> listOfEntitiesFound) {
        this.listOfEntitiesFound = listOfEntitiesFound;
    }

    public String testingRegularExpressions (String toModify){
        if (toModify!=null && !toModify.isEmpty()){
            //Modify the entering string to have a proper use of it
            toModify = StringUtils.stripAccents(toModify);
            toModify = toModify.toLowerCase();
            toModify = toModify.trim();

            //Use all the patterns for the Cardinals in Spanish
            toModify = deleteFromAPattern(Pattern.compile(regularExpresionforCardinalNumbersBeforeAHundredEs), toModify);
            toModify = deleteFromAPattern(Pattern.compile(regularExpresionforCardinalNumbersBeforeAThousandEs), toModify);
            toModify = deleteFromAPattern(Pattern.compile(restOfCardinalsToFindEs), toModify);

            //If the string didnt suffered a modification, it means that it doesnt have any cardinal matches so it will return the string as it entered into the method
            return toModify;
        }else return toModify;

    }

    public String deleteFromAPattern (Pattern pattern, String stringToDeleteMatches){
        String toReturn = "";
        Matcher matcher = pattern.matcher(stringToDeleteMatches);
        Boolean isFound = matcher.find();
        if (isFound){
            String[] stringSplited = pattern.split(stringToDeleteMatches);
            StringBuilder stringBuilder = new StringBuilder();
            for (String stringParts : stringSplited) {
                if (!stringParts.isEmpty()) {
                    stringBuilder.append(stringParts);
                }
            }
            toReturn = stringBuilder.toString();
            toReturn = toReturn.trim();
            toReturn = toReturn.replaceAll("\\s{2,}", " ");
            return toReturn;
        }
        return stringToDeleteMatches;
    }

    public void testingDatesFastNER1 (String textoPrueba){

        String rule = "@fastner\n" + "\\> 0 \\< 32 / \\> 0  \\< 13 / \\> 0 \\< 3000 \\> -1 \\< 24 : \\> -1 \\< 60 \t DATE\n";

        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textoPrueba, true).get(0);
        FastNER fastNER = new FastNER(rule);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()));
            });
        }
    }
    public void testingDatesFastNER2 (String textoPrueba){

        String rule = "@fastner\n" + "\\> 0 \\< 32 - \\> 0  \\< 13 - \\> 0 \\< 3000 \\> -1 \\< 24 : \\> -1 \\< 60 \t DATE\n";

        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textoPrueba, true).get(0);
        FastNER fastNER = new FastNER(rule);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()));
            });
        }
    }

    public void testingCurrencyFastNER1 (String textoPrueba){
        String rule = "@fastner\n" + "peso argentino \t CURRENCY\n";

        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textoPrueba, true).get(0);
        FastNER fastNER = new FastNER(rule);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()));
            });
        }
    }

    public void testingCurrencyFastNER2 (String textoPrueba){
        String rule = "@fastner\n" + "\\d+ \t CURRENCY\n";

        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textoPrueba, true).get(0);
        FastNER fastNER = new FastNER(rule);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()).replaceAll(" +"," "));
            });
        }
    }

    public void testingCurrencyFastNER3 (String textoPrueba){
        String rule = "@fastner\n" + "mil novecientos treinta $\t CURRENCY\n";

        ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textoPrueba, true).get(0);
        FastNER fastNER = new FastNER(rule);
        HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        for (Map.Entry<String, ArrayList<Span>> entry : res.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()).replaceAll(" +"," "));
            });
        }
    }
    public void testingCurrencyFastNER4 (String textoPrueba){
        HashMap<Integer, NERRule> rules = new HashMap<>();
        rules.put(0, new NERRule(0, "20.5 $", "TEST", 0.1, ACTUAL));

        FastCNER fcrp = new FastCNER(rules);
        fcrp.setReplicationSupport(true);
        HashMap<String, ArrayList<Span>> result;
        result = fcrp.processString(textoPrueba);
        for (Map.Entry<String, ArrayList<Span>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()).replaceAll(" +"," "));
            });
        }

    }

    public void testingCurrencyFastNER5 (String textoPrueba){
        HashMap<Integer, NERRule> rules = new HashMap<>();
        rules.put(0, new NERRule(0, "$", "TEST", 0.1, ACTUAL));

        FastCNER fcrp = new FastCNER(rules);
        fcrp.setReplicationSupport(true);
        HashMap<String, ArrayList<Span>> result;
        result = fcrp.processString(textoPrueba);
        if (!result.isEmpty()){
            String rule = "@fastner\n" + "\\d+\t CURRENCY\n";

            ArrayList<Span> tokens = SimpleParser.tokenizeDecimalSmartWSentences(textoPrueba, true).get(0);
            FastNER fastNER = new FastNER(rule);
            HashMap<String, ArrayList<Span>> res = fastNER.processSpanList(tokens);
        }

        for (Map.Entry<String, ArrayList<Span>> entry : result.entrySet()) {
            System.out.println(entry.getKey() + ":\t");
            entry.getValue().forEach((span) -> {
                System.out.println("\t" + textoPrueba.substring(span.getBegin(), span.getEnd()).replaceAll(" +"," "));
            });
        }

    }



    //public String deleteCardinalNumbersFromStringEs (String string){
    //    Pattern pattern;
    //    string = string.toLowerCase();
//
    //    if (!string.isEmpty() && string != null) {
    //       // if (string.contains("quintillón") | string.contains("quintillon") | string.contains("quintillones")) {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeASextillionEs);
    //       // } else if (string.contains("cuatrillón") | string.contains("cuatrillon") | string.contains("cuatrillones")) {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuintillionEs);
    //       // } else if (string.contains("trillón") | string.contains("trillon") | string.contains("trillones")) {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuadrillionEs);
    //       // } else if (string.contains("billón") | string.contains("billon") | string.contains("billones")) {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeATrillionEs);
    //       // } else if (string.contains("millón") | string.contains("millon") | string.contains("millones")) {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeABillionEs);
    //       // } else if (string.contains("mil")) {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAMillionEs);
    //       // } else {
    //       //     pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAThousandEs);
    //       // }
////
    //        Matcher matcher = pattern.matcher(string);
    //        Boolean isFind = matcher.find();
    //        //In case that matcher finds the regular expression in the string parameter
    //        if (isFind) {
    //            String[] stringToReturn = pattern.split(string,2);
    //            StringBuilder stringBuilder = new StringBuilder();
    //            for (String stringParts : stringToReturn) {
    //                if(!stringParts.isEmpty()){
    //                    stringBuilder.append(stringParts);
    //                }
    //            }
    //            //Adds to the list the entity that have been found doing the diff between the original string and the new one
//
    //            //Returns the string without the entity that have been found and calls again the method to check if there's another entity
    //            return deleteCardinalNumbersFromStringEs(stringBuilder.toString());
    //        } else return string;
    //    }else return string;
    //}

    //public String deleteCardinalNumbersFromStringEn (String string) {
    //    Pattern pattern;
    //    string = string.toLowerCase();
//
    //    if (string.contains("quintillion")){
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeASextillionEn);
    //    }else if (string.contains("cuatrillion")){
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuintillionEn);
    //    }else if (string.contains("trillion")){
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuadrillionEn);
    //    }else if (string.contains("billion")|string.contains("billiard")){
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeATrillionEn);
    //    }else if (string.contains("million")|string.contains("milliard")){
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeABillionEn);
    //    }else if (string.contains("thousand")){
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAMillionEn);
    //    }else{
    //        pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAThousandEn);
    //    }
//
    //    Matcher matcher = pattern.matcher(string);
    //    Boolean isFind = matcher.find();
    //    //In case that matcher finds the regular expression in the string parameter
    //    if (isFind) {
    //        String[] stringToReturn = pattern.split(string,2);
    //        StringBuilder stringBuilder = new StringBuilder();
    //        for (String stringParts : stringToReturn) {
    //            stringBuilder.append(stringParts);
    //        }
    //        //Adds to the list the entity that have been found doing the diff between the original string and the new one
    //        listOfEntitiesFound.add(StringUtils.difference(string, stringBuilder.toString()));
    //        //Returns the string without the entity that have been found and calls again the method to check if there's another entity
    //        return deleteCardinalNumbersFromStringEn(stringBuilder.toString());
    //    } else return string;
    //}

}

