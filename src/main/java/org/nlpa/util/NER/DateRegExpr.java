package org.nlpa.util.NER;

import javax.json.*;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRegExpr {
    private static final HashMap<String, String> keysToMatchRegExpr = new HashMap<>();
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
                keysToMatchRegExpr.put(keyRegExpr,jsonObject.getString(keyRegExpr));
            }
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
    static{
        try{
            InputStream is = DateFastNER.class.getResourceAsStream("/testdatesfnandre/DateFormat.json");;
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
    public List<String> getKeysSorted (){
        List<String> listOfKeys = new ArrayList<>();
        if(!keysToMatchRegExpr.isEmpty()){
            for(String key : keysToMatchRegExpr.keySet()){
                listOfKeys.add(key);
            }
        }
        return sortKeys(listOfKeys);
    }

    public String testingRegExpressionTime (String textToTest){
        long startTime = System.nanoTime();
        List<String> dateFormated = matchDatesWithRegExpressionKey(getKeysSorted());
        dateFormated = testPatternRegExp(dateFormated,textToTest);
        System.out.println(dateFormated.size());
        long endTime = System.nanoTime();

        return "Duración Expresión Regular: " + (endTime-startTime)/1e6 + " ms";
    }

    public List<String> matchDatesWithRegExpressionKey(List<String> keysSorted){
        List<String> listOfDatesMatchedWithRegExp = new ArrayList<>();
        if (!keysSorted.isEmpty() && !keysToMatchRegExpr.isEmpty() && !listDatesToMatch.isEmpty()){
            for(String date : listDatesToMatch){
                String dateFormated = date;
                for(String key : keysSorted){
                    if (date.contains(key)){
                        String regExp = keysToMatchRegExpr.get(key);
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
