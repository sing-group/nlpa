package org.nlpa.util.CurrencyPackage;

import edu.utah.bmi.nlp.core.NERRule;
import edu.utah.bmi.nlp.core.SimpleParser;
import edu.utah.bmi.nlp.core.Span;
import edu.utah.bmi.nlp.fastcner.FastCNER;
import edu.utah.bmi.nlp.fastner.FastNER;

import javax.json.*;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.utah.bmi.nlp.core.DeterminantValueSet.Determinants.ACTUAL;

public class DateEntity {
    public DateEntity() {
    }

    private static final HashMap<String, List<String>> dateFormatDictionary = new HashMap<>();
    private static final List<String> listOfDatesToRecognize = new ArrayList<>();
    private static  List<String> fastnerDictionarySortedKeys = new ArrayList<>();
    private static HashMap<String, List<String>> mapOfFastNERRules = new HashMap<>();
    private static List<String> fastNERRules = new ArrayList<>();

    static {
        try {
            InputStream is = DateEntity.class.getResourceAsStream("/timezone-json/dates.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String dateEntity : jsonObject.keySet()) {
                List<String> list = new ArrayList<>();
                JsonArray jsonArray = jsonObject.getJsonObject(dateEntity).getJsonArray("Array");
                for (int i = 0; i < jsonArray.size(); i++){
                    String entity = jsonArray.getString(i);
                    list.add(entity);
                }
                dateFormatDictionary.put(dateEntity,list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static {
        try {
            InputStream is = DateEntity.class.getResourceAsStream("/testdatesfnandre/DateFormat.json");
            JsonReader rdr = Json.createReader(is);
            JsonArray array = rdr.readArray();
            rdr.close();
            array.forEach((v) -> {
                listOfDatesToRecognize.add(((JsonString)v).getString());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            InputStream is = DateEntity.class.getResourceAsStream("/fastnerrulesfordates-json/datesFastNERRulesWithKeys.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String dateEntity : jsonObject.keySet()) {
                List<String> list = new ArrayList<>();
                JsonArray jsonArray = jsonObject.getJsonObject(dateEntity).getJsonArray("Values");
                for (int i = 0; i < jsonArray.size(); i++){
                    String entity = jsonArray.getString(i);

                    list.add(entity+" \tDATE\n");
                }
                mapOfFastNERRules.put(dateEntity,list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static {
        try {
            InputStream is = DateEntity.class.getResourceAsStream("/fastnerrulesfordates-json/datesFastNERRules.json");
            JsonReader rdr = Json.createReader(is);
            JsonArray array = rdr.readArray();
            rdr.close();
            array.forEach((v) -> {
                String entity = ((JsonString)v).getString();
                entity = entity.replaceAll(" +"," ");
                fastNERRules.add(entity+" \t DATE \n");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String testingFastNERTime (String textToTest){
        long startTime = System.nanoTime();
        HashMap<String, List<String>> matchedDates = generateMapOfMatchedDates();
        List<String> listOfFastNERRules = generateListOfFastNERRules(matchedDates);
        List<String> listOfEntitiesFound = new ArrayList<>();
        if(!listOfFastNERRules.isEmpty()){
            for (String rule : listOfFastNERRules){
                List<String> entities = findWithFastNERToken(rule,textToTest);
                if(!entities.isEmpty()){
                    for (String matchedEntities : entities){
                        listOfEntitiesFound.add(matchedEntities);
                    }
                }
            }
            if(!listOfEntitiesFound.isEmpty()){
                System.out.println(printList(listOfEntitiesFound));
            }else System.out.println("No match found");
        }else {
            System.out.println("There's no FastNER rules to find entities in the text");
        }


        long endTime = System.nanoTime();
        return "Duración fastNER: " + (endTime-startTime)/1e6 + " ms";
    }

    public void processDatesToRecognize (String textToFindDateEntities){
        if(!mapOfFastNERRules.isEmpty() && !fastNERRules.isEmpty()){
            List<String> listOfEntitiesFound = new ArrayList<>();
            for(String rule : fastNERRules){
                List<String> entities = findWithFastNERToken(rule,textToFindDateEntities);
                if(!entities.isEmpty()){
                    for (String matchedEntities : entities){
                        listOfEntitiesFound.add(matchedEntities);
                    }
                }
            }
            if(!listOfEntitiesFound.isEmpty()){
                System.out.println(printList(listOfEntitiesFound));
            }else {
                System.out.println("Nothing found");
            }
        }
    }
    public void processDatesToRecognize (HashMap<String, List<String>> fastNERRules, String textToFindDateEntities){
        if(!fastNERRules.isEmpty()){
            List<String> listOfEntitiesFound = new ArrayList<>();
            for (String key : fastNERRules.keySet()){
                for(String rule : fastNERRules.get(key)){
                    List<String> entities = findWithFastNERToken(rule,textToFindDateEntities);
                    if(!entities.isEmpty()){
                        for (String matchedEntities : entities){
                            listOfEntitiesFound.add(matchedEntities);
                        }
                    }
                }
            }

            if(!listOfEntitiesFound.isEmpty()){
                System.out.println(printList(listOfEntitiesFound));
            }else {
                System.out.println("Nothing found");
            }
        }
    }


    public HashMap<String,List<String>> generateMapOfMatchedDates(){
        HashMap<String,List<String>> hashMapOfMachedKeysWithDates = new HashMap<>();

        for (String i : dateFormatDictionary.keySet()){
            fastnerDictionarySortedKeys.add(i);
        }
        fastnerDictionarySortedKeys = sortKeys(fastnerDictionarySortedKeys);

        Pattern pattern;
        Matcher matcher;
        for (String format : listOfDatesToRecognize){
            String theString = format;
            List<String> listOfRulesForFormatDates = new ArrayList<>();
            for (String key : fastnerDictionarySortedKeys){
                pattern = Pattern.compile(key);
                matcher = pattern.matcher(format);
                if (matcher.find()){
                    listOfRulesForFormatDates.add(key);
                    format = format.replaceAll(key,"0");
                }
            }
            if (!listOfRulesForFormatDates.isEmpty()){
                hashMapOfMachedKeysWithDates.put(theString,listOfRulesForFormatDates);
            }
        }
        return hashMapOfMachedKeysWithDates;
    }
    public List<String> generateListOfFastNERRules (HashMap<String, List<String>> hashMapOfMachedKeysWithDates){
        List<String> fastNERRules = new ArrayList<>();
        if(!hashMapOfMachedKeysWithDates.isEmpty()){

            for (String datesToMatch : hashMapOfMachedKeysWithDates.keySet()){
                List<String> listOfDatesMatched = new ArrayList<>();
                for (String keyToMatch : hashMapOfMachedKeysWithDates.get(datesToMatch)) {
                    if (listOfDatesMatched.isEmpty()){
                        for (String entityToMatch : dateFormatDictionary.get(keyToMatch)){
                            listOfDatesMatched.add(datesToMatch.replaceAll(keyToMatch,entityToMatch));
                        }
                    }else{
                        List<String> elementsMatched = new ArrayList<>();
                        for (String elementsOfList : listOfDatesMatched){
                            for (String entityToMatch : dateFormatDictionary.get(keyToMatch)){
                                elementsMatched.add(elementsOfList.replaceAll(keyToMatch,entityToMatch));
                            }
                        }
                        listOfDatesMatched = elementsMatched;
                    }
                }
                if (!listOfDatesMatched.isEmpty()){
                    for (String rule : listOfDatesMatched){
                        fastNERRules.add(rule+" \t DATE \n");
                    }
                }
            }
        }
        //printAllRules(fastNERRules);
        return fastNERRules;
    }

    public HashMap<String, List<String>> generateHashMapOfFastNERRules (HashMap<String, List<String>> hashMapOfMachedKeysWithDates){
        HashMap<String, List<String>> fastNERRules = new HashMap<>();
        if(!hashMapOfMachedKeysWithDates.isEmpty()){

            for (String datesToMatch : hashMapOfMachedKeysWithDates.keySet()){
                List<String> listOfDatesMatched = new ArrayList<>();
                for (String keyToMatch : hashMapOfMachedKeysWithDates.get(datesToMatch)) {
                    if (listOfDatesMatched.isEmpty()){
                        for (String entityToMatch : dateFormatDictionary.get(keyToMatch)){
                            listOfDatesMatched.add(datesToMatch.replaceAll(keyToMatch,entityToMatch));
                        }
                    }else{
                        List<String> elementsMatched = new ArrayList<>();
                        for (String elementsOfList : listOfDatesMatched){
                            for (String entityToMatch : dateFormatDictionary.get(keyToMatch)){
                                elementsMatched.add(elementsOfList.replaceAll(keyToMatch,entityToMatch));
                            }
                        }
                       listOfDatesMatched = elementsMatched;
                    }
                }
                if (!listOfDatesMatched.isEmpty()){
                    List<String> listForDateToMatch = new ArrayList<>();
                    for (String rule : listOfDatesMatched){
                        listForDateToMatch.add(rule);
                    }
                    fastNERRules.put(datesToMatch,listForDateToMatch);
                }
            }
        }

        //printRulesWithKeys(fastNERRules);
        return fastNERRules;
    }

    //Método que ejecuta la busqueda de la regla en el texto que se le pasa por parametro, puede devolver una lista con los elementos encontrados o un string vacio
    public List<String> findWithFastNERToken (String rule, String textToFindTokens){
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
        return result;
    }
    public List<String> findWithFastCNER(String rule, String textToFindTokens){
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
        return resultList;
    }
    public void printRulesWithKeys (HashMap<String,List<String>> fastNERRules){
        try(FileWriter fileWriter = new FileWriter("datesFastNERRulesWithKeys.txt") ){
            fileWriter.write("{\n");
            for (String key : fastNERRules.keySet()){
                fileWriter.write("\"" + key + "\":{" + "\"Values\":" );
                fileWriter.write("[");
                for (String rule : fastNERRules.get(key)){
                    rule = goodMatchigNERRule(rule);
                    rule = rule.concat("\",\n");
                    fileWriter.write("\"" + rule);
                }
                fileWriter.write("]},\n");
            }
            fileWriter.write("\n}");
            fileWriter.close();
        }catch (Exception e){}

    }
    public void printAllRules (HashMap<String,List<String>> fastNERRules){
        try(FileWriter fileWriter = new FileWriter("datesFastNERRules.txt") ){
            fileWriter.write("[\n");
            for (String key : fastNERRules.keySet()){
                for (String rule : fastNERRules.get(key)){
                    rule = goodMatchigNERRule(rule);
                    rule = rule.concat("\",\n");
                    fileWriter.write("\"" + rule);
                }
            }
            fileWriter.write("\n]");
            fileWriter.close();
        }catch (Exception e){}
    }

    public String goodMatchigNERRule (String rule){
        if (rule.contains("\\>")){
            rule = rule.replaceAll("\\>","\\\\>");
        }
        if (rule.contains("\\<")){
            rule = rule.replaceAll("\\<","\\\\<");
        }
        if (rule.contains("\\d")){
            rule = rule.replaceAll("\\d","\\\\d");
        }
        if (rule.contains("\\w")){
            rule = rule.replaceAll("\\w","\\\\w");
        }
        return rule;
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

    public void readList (List<String> list){
        for (String i : list){
            System.out.println(i);

        }
    }

}
