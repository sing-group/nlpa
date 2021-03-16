package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Pair;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.HashMap;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

@AutoService(Pipe.class)
@PropertyComputingPipe()
public class NewNERFromStringBufferPipe extends AbstractPipe {

    /**
     * Regular expression to locate numbers associated with currencies
     */

    private static final String regularExpressionNumberCurrency = "[0-9]+[.,']?[0-9]*";

    /**
     * The name of the property where the language is stored
     */

    private String langProp = DEFAULT_LANG_PROPERTY;

    /**
     * Logger
     */

    private static final Logger logger = LogManager.getLogger();

    /**
     * Hashset of currencies in Spanish and English (JSON files loaded)
     */

    private static final HashMap<String, HashMap<String,Pair<String,String>>> currencyDictionary = new HashMap<>();

    static {
        for (String i : new String[] { "/currency-json/currency.en.json", "/currency-json/currency.es.json"}) {
            String lang = i.substring(25, 27).toUpperCase();

            try {
                InputStream is = NewNERFromStringBufferPipe.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                HashMap<String, Pair<String, String>> dict = new HashMap<>();
                for(String currencyName : jsonObject.keySet()){
                    dict.put(currencyName, new Pair<>(jsonObject.getJsonObject(currencyName).getString("ISO"),
                            jsonObject.getJsonObject(currencyName).getString("Symbol")));
                }

                currencyDictionary.put(lang, dict);
            } catch (Exception e) {
                logger.error("Exception processing: " + i + " message " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }
    /**
     * Default constructor.
     */
    public NewNERFromStringBufferPipe(){
        this(DEFAULT_LANG_PROPERTY);

    }
    /**
     * Construct a ContractionsFromStringBuffer instance given a language
     * property
     *
     * @param langProp The property that stores the language of text
     */
    public NewNERFromStringBufferPipe(String langProp){
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},new Class<?>[0]);

    }

    //Proper pipe
    @Override
    public Instance pipe (Instance carrier){
        return carrier;
    }

}
