package org.nlpa.util.CurrencyPackage;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DateEntity {
    private static String [] eraDesignator = {"AC","DC"}; //Representa a G
    private static String fullYear = "\\> -1 \\< 10000"; //Fecha de 4 dígitos de 0 a 9999 YYYY (hay que hacer un metodo para meter los ceros donde convenga)
    private static String [] firstTenDigits = {"00","01","02","03","04","05","06","07","08","09"};
    private static String partialYear = "\\> -1 \\< 100"; //firstTenDigits + Fecha de 2 dígitos de 0 a 99 yy
    private static String [] monthNameEs = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private static String [] shortMonthNameEs = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
    private static String [] getMonthNameEn = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    private static String [] shortMonthNameEn = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private static String [] romanMonth = {"I","II","III","IV","V","VI","VII","VIII","IX","X","XI","XII"};
    private static String month = "\\> -1 \\< 13"; //Detecta tanto los numeros del 0 al 12 como de 00 a 12 (¿es necesaria la distincion?)
    private static String weekInAYear = "\\> 9 \\< 53"; //firstTenDigits + 52 semanas tiene un año representa a w
    private static String weekInAMonth = "\\> 9 \\< 53"; //firstTenDigits + 4 semanas tiene un mes representa a W
    private static String daysInAYear = "\\> 9 \\< 367"; //firstTenDigits + 365 dias en un año, 366 en un año bisiesto (¿es necesaria la distincion?)
    private static String daysInAMonth1 = "\\> 9 \\< 29"; //firstTenDigits + daysInAMonth1 Febrero normal representa a D
    private static String daysInAMonth2 = "\\> 9 \\< 30"; //firstTenDigits + daysInAMonth2 Febrero bisiesto representa a D
    private static String daysInAMonth3 = "\\> 9 \\< 31"; //firstTenDigits + daysInAMonth3 Abril,Junio,Septiembre,Noviembre representa a D
    private static String daysInAMonth4 = "\\> 9 \\< 32"; //firstTenDigits + daysInAMonth4 Enero,Marzo,Mayo,Julio,Agosto,Octubre,Diciembre representa a D
    private static String [] daysInAWeekEs = {"Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"};
    private static String [] shortDaysInAWeekEs = {"Lun","Mar","Mie","Jue","Vie","Sab","Dom"};
    private static String [] daysInAWeekEn = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private static String [] shortDaysInAWeekEn = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    private static String [] amPmMarker = {"am","pm"}; //Representado por a
    private static String hours = "\\> 9 \\< 24"; //firstTenDigits + hours Representado por HH (0-23) (hacer de 00 a 09?)
    private static String hourInADay = "\\> 0 \\< 25"; //Representado por k (1-24)
    private static String hourInADayWithAmMarkerK = "\\> -1 \\< 12 am"; //K	Hora en am (0-11)
    private static String hourInADayWithPmMarkerK = "\\> -1 \\< 12 pm"; //K	Hora en pm (0-11)
    private static String hourInADayWithAmMarker = "\\> 9 \\< 13 am"; //h	firstTenDigits + hourInADayWithAmMarker Hora en am (0-12)
    private static String hourInADayWithPmMarker = "\\> 9 \\< 13 pm"; //h	firstTenDigits + hourInADayWithPmMarker Hora en pm (0-12)
    private static String minuteInAHour = "\\> 9 \\< 60"; //firstTenDigits + minuteInAHour mm
    private static String secondsInAMinute = "\\> 9 \\< 60"; //firstTenDigits + secondsInAMinute ss
    private static String [] timeZoneWithDigits = {"+0000","+0100","+0200","+0300","+0400","+0430","+0500","+0530","+0545","+0600",
            "+0630","+0700","+0800","+0900","+0930","+1000","+1030","+1100","+1130","+1200","+1245","+1300","+1345","+1400",
            "−0100","−0200","−0230","−0300","−0330","−0400","−0500","−0600","−0700","−0800","−0900","−0930","−1000","−1100","−1200"};

    public DateEntity() {
    }
    public HashMap<Integer, List<String>> getTimeZone(){
        HashMap <Integer, List<String>> mapOfCurrencyElements = new HashMap<>();
        List<String> listOfAbrr = new ArrayList<>();
        List<String> listOfNames = new ArrayList<>();
        List<String> listOfGmt = new ArrayList<>();
        try {
            InputStream is = Currency.class.getResourceAsStream("/timezone-json/timeZone.json");
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();
            for(String timeZone : jsonObject.keySet()) {
                listOfAbrr.add(timeZone);
                listOfNames.add(jsonObject.getJsonObject(timeZone).getString("Name"));
                String gmt = jsonObject.getJsonObject(timeZone).getString("GMT");
                if (!gmt.isEmpty() && !listOfGmt.contains(gmt)){
                    listOfGmt.add(gmt);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if (!listOfAbrr.isEmpty() && !listOfNames.isEmpty() && !listOfGmt.isEmpty()){
            mapOfCurrencyElements.put(0, listOfAbrr);
            mapOfCurrencyElements.put(1, listOfNames);
            mapOfCurrencyElements.put(2, listOfGmt);
        }

        return mapOfCurrencyElements;

    }

}
