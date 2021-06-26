package org.nlpa.util;

public class Currency {
    private String name;
    private String namePlural;
    private String iso;
    private String symbol;

    public Currency(String name, String namePlural, String iso, String symbol) {
        this.name = name;
        this.namePlural = namePlural;
        this.iso = iso;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePlural() {
        return namePlural;
    }

    public void setNamePlural(String namePlural) {
        this.namePlural = namePlural;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
