package org.nlpa.util.NER;

import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastner.FastNER;
import org.apache.commons.lang3.StringUtils;

import javax.json.*;
import java.io.InputStream;
import java.util.*;

public class DateFastNER {
    public DateFastNER() {
    }

    private static final HashMap<String, String> dateFormatDictionary = new HashMap<>();
    private static final List<String> listOfDatesToRecognize = new ArrayList<>();
    private static List<String> fastnerDictionarySortedKeys = new ArrayList<>();
    private static HashMap<String, List<String>> mapOfFastNERRules = new HashMap<>();
    private static List<String> fastNERRules = new ArrayList<>();

    static {
        try {
            InputStream is = DateFastNER.class.getResourceAsStream("/fastnerrulesfordates-json/dates.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String keyRegExpr : jsonObject.keySet()) {
                dateFormatDictionary.put(keyRegExpr, jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            InputStream is = DateFastNER.class.getResourceAsStream("/fastnerrulesfordates-json/DateFormat.json");
            JsonReader rdr = Json.createReader(is);
            JsonArray array = rdr.readArray();
            rdr.close();
            array.forEach((v) -> {
                listOfDatesToRecognize.add(((JsonString) v).getString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String testingFastNERTime(String textToTest) {
        long startTime = System.nanoTime();
        textToTest = formatString(textToTest);
        List<String> listOfEntitiesFound = new ArrayList<>();
        if (!dateFormatDictionary.isEmpty() && !listOfDatesToRecognize.isEmpty()){
            List<String> fastNERRules = matchDatesWithFastNERRules();
            for (String rule : fastNERRules) {
                List<String> dateEntities = findWithFastNERToken(rule, textToTest);
                if (!dateEntities.isEmpty()){
                    for(String dateEntity : dateEntities){
                        listOfEntitiesFound.add(dateEntity);
                    }
                }
            }
        }
        System.out.println("Número de fechas encontradas con fastNER: " + listOfEntitiesFound.size());
        long endTime = System.nanoTime();
        System.out.println("Duración búsqueda de fechas con fastNER: " + (endTime - startTime) / 1e6 + " ms");
        return printList(listOfEntitiesFound);
    }

    public List<String> matchDatesWithFastNERRules() {
        List<String> listOfFastNERRulesForDates = new ArrayList<>();
        for (String i : dateFormatDictionary.keySet()) {
            fastnerDictionarySortedKeys.add(i);
        }
        fastnerDictionarySortedKeys = sortKeys(fastnerDictionarySortedKeys);
        for (String date : listOfDatesToRecognize) {
            String dateFormated = date;
            for (String key : fastnerDictionarySortedKeys) {
                if (date.contains(key)) {
                    String fastNERRule = dateFormatDictionary.get(key);
                    dateFormated = dateFormated.replaceAll(key, fastNERRule);
                }
            }
            if (!dateFormated.equals(date)) {
                listOfFastNERRulesForDates.add(dateFormated + " \tDATE\n");
            }
        }

        return listOfFastNERRulesForDates;
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro, puede devolver una lista con los elementos encontrados o un string vacio
    public List<String> findWithFastNERToken(String rule, String textToFindTokens) {
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
        return result;
    }

    private String formatString(String stringToFormat) {
        stringToFormat = StringUtils.stripAccents(stringToFormat);
        stringToFormat = stringToFormat.toLowerCase();
        stringToFormat = stringToFormat.trim();
        stringToFormat = stringToFormat.replaceAll("\t", " ");
        stringToFormat = stringToFormat.replaceAll("\r", " ");
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
