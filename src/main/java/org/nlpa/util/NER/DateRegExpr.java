package org.nlpa.util.NER;

import javax.json.*;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRegExpr {
    private static final HashMap<String, String> keysToMatchRegExprEs = new HashMap<>();
    private static final HashMap<String, String> keysToMatchRegExprEn = new HashMap<>();
    private static final List<String> listDatesToMatch = new ArrayList<>();
    public DateRegExpr() {
    }
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionfordates-json/regExpMatcher.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String keyRegExpr : jsonObject.keySet()) {
                keysToMatchRegExprEs.put(keyRegExpr,jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionfordates-json/datesToMatch.json");;
            JsonReader rdr = Json.createReader(is);
            JsonArray array = rdr.readArray();
            rdr.close();
            array.forEach((v) -> {
                listDatesToMatch.add(((JsonString)v).getString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/regexpressionfordates-json/regExpMatcherEn.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String keyRegExpr : jsonObject.keySet()) {
                keysToMatchRegExprEn.put(keyRegExpr,jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<String> getKeysSorted (){
        List<String> listOfKeys = new ArrayList<>();
        if(!keysToMatchRegExprEs.isEmpty()){
            for(String key : keysToMatchRegExprEs.keySet()){
                listOfKeys.add(key);
            }
        }
        return sortKeys(listOfKeys);
    }

    public String testingRegExpressionTime (String lang, String textToTest){
        long startTime = System.nanoTime();
        List<String> dateEntitiesFound = new ArrayList<>();
        List<String> dateFormated = matchDatesWithRegExpressionKey(getKeysSorted(), lang);
        if (!dateFormated.isEmpty()){
            List<String> dateEntities= testPatternRegExp(dateFormated,textToTest);
            if (!dateEntities.isEmpty()){
                for (String dateEntity : dateEntities){
                    System.out.println(dateEntity);
                    dateEntitiesFound.add(dateEntity);
                }
            }
        }

        System.out.println(dateEntitiesFound.size());
        long endTime = System.nanoTime();

        System.out.println("Duración Expresión Regular: " + (endTime-startTime)/1e6 + " ms");
        return printList(dateEntitiesFound);
    }

    public List<String> matchDatesWithRegExpressionKey(List<String> keysSorted, String lang){
        List<String> listOfDatesMatchedWithRegExp = new ArrayList<>();
        if (!keysSorted.isEmpty() && (!keysToMatchRegExprEs.isEmpty() || !keysToMatchRegExprEn.isEmpty()) && !listDatesToMatch.isEmpty()){
            for(String date : listDatesToMatch){
                String dateFormated = date;
                for(String key : keysSorted){
                    if (date.contains(key)){
                        String regExp = "" ;
                        if (lang.equals("EN")){
                            regExp = keysToMatchRegExprEn.get(key);
                        }else if (lang.equals("ES")){
                            regExp = keysToMatchRegExprEs.get(key);
                        }
                        dateFormated = dateFormated.replaceAll(key,regExp);
                    }
                }
                if(!dateFormated.equals(date)){
                    listOfDatesMatchedWithRegExp.add(dateFormated);
                }
            }
        }
        return listOfDatesMatchedWithRegExp;
    }

    public List<String> testPatternRegExp (List<String> listRegExp, String textToTry){
        List<String> listOfEntitiesFound = new ArrayList<>();
        if (!listRegExp.isEmpty()){
            for(String patternRegExp : listRegExp){
                Pattern pattern = Pattern.compile(patternRegExp);
                Matcher mat = pattern.matcher(textToTry);
                while (mat.find()){
                    //System.out.println(patternRegExp);
                    //System.out.println(mat.group());
                    listOfEntitiesFound.add(mat.group());
                }
            }
        }
        return listOfEntitiesFound;
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
