package org.nlpa.util;

import org.apache.commons.lang3.StringUtils;
import org.bdp4j.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.nlpa.pipe.impl.NewNERFromStringBufferPipe;

import javax.json.*;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Currency {
    private List<String> nameEs;
    private List<String> namePluralEs;
    private List<String> nameEn;
    private List<String> namePluralEn;
    private List<String> iso;
    private List<String> symbol;

    public Currency() {
        nameEs = new ArrayList<>();
        namePluralEs = new ArrayList<>();
        nameEn = new ArrayList<>();
        namePluralEn = new ArrayList<>();
        iso = new ArrayList<>();
        symbol = new ArrayList<>();
    }

    public List<String> getNameEs() {
        return nameEs;
    }

    public void setNameEs(List<String> nameEs) {
        this.nameEs = nameEs;
    }

    public List<String> getNamePluralEs() {
        return namePluralEs;
    }

    public void setNamePluralEs(List<String> namePluralEs) {
        this.namePluralEs = namePluralEs;
    }

    public List<String> getNameEn() {
        return nameEn;
    }

    public void setNameEn(List<String> nameEn) {
        this.nameEn = nameEn;
    }

    public List<String> getNamePluralEn() {
        return namePluralEn;
    }

    public void setNamePluralEn(List<String> namePluralEn) {
        this.namePluralEn = namePluralEn;
    }

    public List<String> getIso() {
        return iso;
    }

    public void setIso(List<String> iso) {
        this.iso = iso;
    }

    public List<String> getSymbol() {
        return symbol;
    }

    public void setSymbol(List<String> symbol) {
        this.symbol = symbol;
    }

    public void generateCurrenciesListEs(){
        try {

            InputStream is = Currency.class.getResourceAsStream("/currency-json/currency.es.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();
            for(String currency : jsonObject.keySet()) {
                nameEs.add(formatString(currency));
                namePluralEs.add(formatString(jsonObject.getJsonObject(currency).getString("NamePlural")));
                iso.add(formatString(jsonObject.getJsonObject(currency).getString("ISO")));
                String symbolEs = jsonObject.getJsonObject(currency).getString("Symbol");
                if (!symbolEs.isEmpty() && !symbol.contains(symbolEs)){
                    symbol.add(symbolEs);
                }
            }
        }catch (Exception e) {
                e.printStackTrace();
        }
    }
    public void generateCurrenciesListEn(){
        try {
            InputStream is = Currency.class.getResourceAsStream("/currency-json/currency.en.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();
            for(String currency : jsonObject.keySet()) {
                nameEn.add(formatString(currency));
                namePluralEn.add(formatString(jsonObject.getJsonObject(currency).getString("NamePlural")));
                iso.add(formatString(jsonObject.getJsonObject(currency).getString("ISO")));
                String symbolEn = jsonObject.getJsonObject(currency).getString("Symbol");
                if (!symbolEn.isEmpty() && !symbol.contains(symbolEn)){
                    symbol.add(symbolEn);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatString (String stringToFormat){
        stringToFormat = StringUtils.stripAccents(stringToFormat);
        stringToFormat = stringToFormat.toLowerCase();
        stringToFormat = stringToFormat.trim();
        stringToFormat = stringToFormat.replaceAll("\t", " ");
        stringToFormat = stringToFormat.replaceAll("\n", " ");
        stringToFormat = stringToFormat.replaceAll(" +", " ");
        return stringToFormat;
    }
}


