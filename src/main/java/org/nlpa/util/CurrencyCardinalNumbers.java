package org.nlpa.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyCardinalNumbers {
    private List<String> listOfEntitiesFound;
    //Cardinal numbers in spanish
    private static final String whiteSpace = "[ ]".concat("*");
    private static final String tenFirstCardinalNumberEs = "(" + "cero" + "|" + "uno" + "|" + "dos" + "|" + "tres" +
            "|" + "cuatro" + "|" + "cinco" + "|" + "seis" + "|" + "siete" + "|" + "ocho" + "|" + "nueve" + ")";
    private static final String tensCardinalNumberEs = "(" + "diez" + "|" + "once" + "|" + "doce" +
            "|" + "trece" + "|" + "catorce" + "|" + "quince" + "|" + "dieciséis" + "|" + "dieciseis" +
            "|" + "diecisiete" + "|" + "dieciocho" + "|" + "diecinueve" + ")";
    private static final String twentiesCardinalNumberEs = "(" + "veinte" + "|" + "veintiuno" + "|" + "veintidós" +
            "|" + "veintidos" + "|" +  "veintitrés" + "|" + "veintitres" + "|" + "veinticuatro" + "|" + "veinticinco" +
            "|" + "veintiséis" + "|" + "veintiseis" + "|" +  "veintisiete" + "|" + "veintiocho" + "|" + "veintinueve" + ")";
    private static final String beforeAHundredCardinalNumberEs = "(" + "treinta" + "|" + "cuarenta" + "|" + "cincuenta" + "|" + "sesenta" + "|" + "setenta" + "|" + "ochenta" + "|" + "noventa" + ")";
    private static final String hundredCardinalNumberEs = "(" + "ciento" + "|" + "cien" + ")";
    private static final String beforeAThousandCardinalNumberEs =  "(" + "doscientos" + "|" + "trescientos" + "|" + "cuatrocientos" +
            "|" + "quinientos" + "|" + "seiscientos" + "|" + "setecientos" + "|" + "ochocientos" + "|" + "novecientos" + ")";

    //0-9|(10-29)|(30-90)([ ]*y[ ]*(0-9))
    private static final String regularExpresionforCardinalNumbersBeforeAHundredEs = tenFirstCardinalNumberEs  + "|" + "(" + tensCardinalNumberEs + "|" + twentiesCardinalNumberEs + ")" +
            "|" + "(" + beforeAHundredCardinalNumberEs + whiteSpace + "(" + "(y)" + whiteSpace + tenFirstCardinalNumberEs + ")".concat("?")  + ")";

    //((100|200-900)[ ]*(0-99)?|0-99)

     private static  final String regularExpresionforCardinalNumbersBeforeAThousandEs = "(" + "(" + hundredCardinalNumberEs +
             "|" + beforeAThousandCardinalNumberEs + ")" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeAHundredEs.concat("?") + ")" + ")" + "|" + "(" + regularExpresionforCardinalNumbersBeforeAHundredEs + ")";

    //(0-999)?(1000)(0-999)?
    private static final String regularExpresionforCardinalNumbersBeforeAMillionEs = "(" + regularExpresionforCardinalNumbersBeforeAThousandEs + ")" + "".concat("?") + whiteSpace + "(mil)" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeAThousandEs + ")" + "".concat("?");

    private static final String regularExpresionforCardinalNumbersBeforeABillionEs = "(" + regularExpresionforCardinalNumbersBeforeAMillionEs + ")" + "".concat("?") + whiteSpace + "(millón|millon|millones)" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeAMillionEs + ")" + "".concat("?");

    private static final String regularExpresionforCardinalNumbersBeforeATrillionEs= "(" + regularExpresionforCardinalNumbersBeforeABillionEs + ")" + "".concat("?") + whiteSpace + "(billón|billon|billones)" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeABillionEs + ")" + "".concat("?");

    private static final String regularExpresionforCardinalNumbersBeforeAQuadrillionEs = "(" + regularExpresionforCardinalNumbersBeforeATrillionEs + ")" + "".concat("?") + whiteSpace + "(trillón|trillon|trillones)" + whiteSpace +  "(" + regularExpresionforCardinalNumbersBeforeATrillionEs + ")" + "".concat("?");

    private static final String regularExpresionforCardinalNumbersBeforeAQuintillionEs = "(" + regularExpresionforCardinalNumbersBeforeAQuadrillionEs + ")" + "".concat("?") + whiteSpace + "(cuatrillón|cuatrillon|cuatrillones)" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeAQuadrillionEs + ")" + "".concat("?");

    private static final String regularExpresionforCardinalNumbersBeforeASextillionEs = "(" + regularExpresionforCardinalNumbersBeforeAQuintillionEs+ ")" + "".concat("?") + whiteSpace + "(quintillón|quintillon|quintillones)" + whiteSpace + "(" + regularExpresionforCardinalNumbersBeforeAQuintillionEs+ ")" + "".concat("?");

    //Cardinal numbers in english
    private static final String tenFirstCardinalNumberEn = "(" + "zero" + "|" + "one" + "|" + "two" + "|" + "three" + "|" + "four" + "|" + "five" + "|" + "six" + "|" + "seven" + "|" + "eight" + "|" + "nine" + ")";
    private static final String tensCardinalNumberEn = "(" + "ten" + "|" + "eleven" + "|" + "twelve" + "|" + "thirteen" + "|" + "fourteen" + "|" + "fifteen" + "|" + "sixteen" +
            "|" + "seventeen" + "|" + "eighteen" + "|" + "nineteen" + ")";
    private static final String beforeAHundredCardinalNumberEn = "(" + "twenty" + "thirty" + "|" + "forty" + "|" + "fifty" + "|" + "sixty" + "|" + "seventy" + "|" + "eighty" + "|" + "ninety" + ")";

    private static final String regularExpresionforCardinalNumbersBeforeAHundredEn = tenFirstCardinalNumberEn  + "|" + tensCardinalNumberEn +
            "|" + "(" + beforeAHundredCardinalNumberEn + whiteSpace + tenFirstCardinalNumberEn.concat("?") + ")";
    private static final String regularExpresionforCardinalNumbersBeforeAThousandEn = "(" + tenFirstCardinalNumberEn.concat("?") + whiteSpace + ("hundred") + whiteSpace + regularExpresionforCardinalNumbersBeforeAHundredEn.concat("?");
    private static final String regularExpresionforCardinalNumbersBeforeAMillionEn = regularExpresionforCardinalNumbersBeforeAThousandEn.concat("?") + whiteSpace + "(thousand)" + whiteSpace + regularExpresionforCardinalNumbersBeforeAThousandEn.concat("?");
    private static final String regularExpresionforCardinalNumbersBeforeABillionEn = regularExpresionforCardinalNumbersBeforeAMillionEn.concat("?") + whiteSpace + "(million|milliard)" + whiteSpace + regularExpresionforCardinalNumbersBeforeAMillionEn.concat("?");
    private static final String regularExpresionforCardinalNumbersBeforeATrillionEn= regularExpresionforCardinalNumbersBeforeABillionEn.concat("?") + whiteSpace + "(billion|billiard)" + whiteSpace + regularExpresionforCardinalNumbersBeforeABillionEn.concat("?");
    private static final String regularExpresionforCardinalNumbersBeforeAQuadrillionEn = regularExpresionforCardinalNumbersBeforeATrillionEn.concat("?") + whiteSpace + "(trillion)" + whiteSpace + regularExpresionforCardinalNumbersBeforeATrillionEn.concat("?");
    private static final String regularExpresionforCardinalNumbersBeforeAQuintillionEn = regularExpresionforCardinalNumbersBeforeAQuadrillionEn.concat("?") + whiteSpace + "(quadrillion)" + whiteSpace + regularExpresionforCardinalNumbersBeforeAQuadrillionEn.concat("?");
    private static final String regularExpresionforCardinalNumbersBeforeASextillionEn = regularExpresionforCardinalNumbersBeforeAQuintillionEn.concat("?") + whiteSpace + "(quintillion)" + whiteSpace + regularExpresionforCardinalNumbersBeforeAQuintillionEn.concat("?");


    public CurrencyCardinalNumbers (){
        listOfEntitiesFound = new ArrayList<String>();
    }

    public List<String> getListOfEntitiesFound() {
        return listOfEntitiesFound;
    }

    public void setListOfEntitiesFound(List<String> listOfEntitiesFound) {
        this.listOfEntitiesFound = listOfEntitiesFound;
    }

    public String testingRegularExpressions (String string){
        Pattern pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAMillionEs);
        Matcher matcher = pattern.matcher(string);
        Boolean isFound = matcher.find();
        if (isFound){
            String[] stringToReturn = pattern.split(string,2);
            StringBuilder stringBuilder = new StringBuilder();
            for (String stringParts : stringToReturn) {
                if (!stringParts.isEmpty()) {
                    stringBuilder.append(stringParts);
                }
            }
            String toReturn = stringBuilder.toString();
            if (!toString().isEmpty()){
                return testingRegularExpressions(toReturn);
            }else return stringBuilder.toString();

        }else return string;
    }

    public String deleteCardinalNumbersFromStringEs (String string){
        Pattern pattern;
        string = string.toLowerCase();

        if (!string.isEmpty() && string != null) {
            if (string.contains("quintillón") | string.contains("quintillon") | string.contains("quintillones")) {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeASextillionEs);
            } else if (string.contains("cuatrillón") | string.contains("cuatrillon") | string.contains("cuatrillones")) {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuintillionEs);
            } else if (string.contains("trillón") | string.contains("trillon") | string.contains("trillones")) {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuadrillionEs);
            } else if (string.contains("billón") | string.contains("billon") | string.contains("billones")) {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeATrillionEs);
            } else if (string.contains("millón") | string.contains("millon") | string.contains("millones")) {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeABillionEs);
            } else if (string.contains("mil")) {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAMillionEs);
            } else {
                pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAThousandEs);
            }

            Matcher matcher = pattern.matcher(string);
            Boolean isFind = matcher.find();
            //In case that matcher finds the regular expression in the string parameter
            if (isFind) {
                String[] stringToReturn = pattern.split(string,2);
                StringBuilder stringBuilder = new StringBuilder();
                for (String stringParts : stringToReturn) {
                    if(!stringParts.isEmpty()){
                        stringBuilder.append(stringParts);
                    }
                }
                //Adds to the list the entity that have been found doing the diff between the original string and the new one

                //Returns the string without the entity that have been found and calls again the method to check if there's another entity
                return deleteCardinalNumbersFromStringEs(stringBuilder.toString());
            } else return string;
        }else return string;
    }

    public String deleteCardinalNumbersFromStringEn (String string) {
        Pattern pattern;
        string = string.toLowerCase();

        if (string.contains("quintillion")){
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeASextillionEn);
        }else if (string.contains("cuatrillion")){
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuintillionEn);
        }else if (string.contains("trillion")){
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAQuadrillionEn);
        }else if (string.contains("billion")|string.contains("billiard")){
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeATrillionEn);
        }else if (string.contains("million")|string.contains("milliard")){
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeABillionEn);
        }else if (string.contains("thousand")){
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAMillionEn);
        }else{
            pattern = Pattern.compile(regularExpresionforCardinalNumbersBeforeAThousandEn);
        }

        Matcher matcher = pattern.matcher(string);
        Boolean isFind = matcher.find();
        //In case that matcher finds the regular expression in the string parameter
        if (isFind) {
            String[] stringToReturn = pattern.split(string,2);
            StringBuilder stringBuilder = new StringBuilder();
            for (String stringParts : stringToReturn) {
                stringBuilder.append(stringParts);
            }
            //Adds to the list the entity that have been found doing the diff between the original string and the new one
            listOfEntitiesFound.add(StringUtils.difference(string, stringBuilder.toString()));
            //Returns the string without the entity that have been found and calls again the method to check if there's another entity
            return deleteCardinalNumbersFromStringEn(stringBuilder.toString());
        } else return string;
    }

}


