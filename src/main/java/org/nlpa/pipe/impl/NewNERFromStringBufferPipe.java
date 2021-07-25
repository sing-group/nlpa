package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.util.EBoolean;
import org.nlpa.util.NER.CurrencyFastNER;
import org.nlpa.util.NER.CurrencyRegExpr;
import org.nlpa.util.NER.DateFastNER;
import org.nlpa.util.NER.DateRegExpr;

import java.util.List;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

@AutoService(Pipe.class)
@PropertyComputingPipe()
public class NewNERFromStringBufferPipe extends AbstractPipe {

    private String langProp = DEFAULT_LANG_PROPERTY;
    List<String> entityTypes = null;

    //Atributos por defecto del Pipe, en este caso todos los atributos están a true para que se haga la
    //búsqueda de entidades de todas las formas creadas
    public static final String DEFAULT_FAST_NER_DATE = "true";
    public static final String DEFAULT_FAST_NER_CURRENCY = "true";
    public static final String DEFAULT_REGEXP_NER_DATE = "true";
    public static final String DEFAULT_REGEXP_NER_CURRENCY = "true";

    private boolean fastNERDate = EBoolean.getBoolean(DEFAULT_FAST_NER_DATE);
    private boolean fastNERCurrency = EBoolean.getBoolean(DEFAULT_FAST_NER_CURRENCY);
    private boolean regExpDate = EBoolean.getBoolean(DEFAULT_REGEXP_NER_DATE);
    private boolean regExpCurrency = EBoolean.getBoolean(DEFAULT_REGEXP_NER_CURRENCY);


    private static final Logger logger = LogManager.getLogger();

    //Determina el tipo de input para el atributo de datos de las Instancias después de ser
    //procesado
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }
    //Determina el tipo de datos esperado en el atributo de datos de las instancias después del procesamiento
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    public void setLangProp(String langProp) {
        this.langProp = langProp;
    }

    public String getLangProp() {
        return this.langProp;
    }

    public boolean getFastNERDate() {
        return fastNERDate;
    }

    public void setFastNERDate(boolean fastNERDate) {
        this.fastNERDate = fastNERDate;
    }

    public boolean getFastNERCurrency() {
        return fastNERCurrency;
    }

    public void setFastNERCurrency(boolean fastNERCurrency) {
        this.fastNERCurrency = fastNERCurrency;
    }

    public boolean getRegExpDate() {
        return regExpDate;
    }

    public void setRegExpDate(boolean regExpDate) {
        this.regExpDate = regExpDate;
    }

    public boolean getRegExpCurrency() {
        return regExpCurrency;
    }

    public void setRegExpCurrency(boolean regExpCurrency) {
        this.regExpCurrency = regExpCurrency;
    }

    //Constructor que utiliza los valores por defecto en el caso de que no se le pase nada por parámetro
    public NewNERFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY ,EBoolean.getBoolean(DEFAULT_FAST_NER_DATE), EBoolean.getBoolean(DEFAULT_FAST_NER_CURRENCY),
                EBoolean.getBoolean(DEFAULT_REGEXP_NER_DATE), EBoolean.getBoolean(DEFAULT_REGEXP_NER_CURRENCY));
    }

    //Constructor que genera el Pipe con los atributos que se le pasan por parámetro
    public NewNERFromStringBufferPipe(String langProp, boolean fastNERDate, boolean fastNERCurrency, boolean regExpDate, boolean regExpCurrency) {
        super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class }, new Class<?>[0]);
        this.langProp = langProp;
        this.fastNERDate = fastNERDate;
        this.fastNERCurrency = fastNERDate;
        this.regExpDate = fastNERDate;
        this.regExpCurrency = fastNERDate;
    }

    //Pipe que se encarga de llamar a las diferentes clases creadas para detectar las diferentes entidades
    //y almacenar los resultados que se añadiran a las propiedades del Pipe, sólo en el caso de que se le pase
    //un StringBuffer como parámetro, sino devuelve un error%
    @Override
    public Instance pipe (Instance carrier){
        if (carrier.getData() instanceof StringBuffer){
            String data = carrier.getData().toString();
            String value = "";
            String lang = (String) carrier.getProperty(langProp);

            if(fastNERDate){
                DateFastNER dateEntity = new DateFastNER();
                value = dateEntity.datesWithFastNER(data);
            }
            carrier.setProperty("FASTNERDATE","Entities found with FASTNERDATE:\n" + value + "\n");
            if(regExpDate){
                DateRegExpr regExpressionForDates = new DateRegExpr();
                value = regExpressionForDates.datesWithRegularExpressions(lang, data);
            }
            carrier.setProperty("REGEXPNERDATE","Entities found with REGEXPNERDATE:\n" + value + "\n");
            if(fastNERCurrency){
                CurrencyFastNER currency = new CurrencyFastNER();
                value = currency.findAllCurrenciesAsociatedToANumber(lang, data);
            }
            carrier.setProperty("FASTNERCURRENCY","Entities found with FASTNERCURRENCY:\n" + value + "\n");;
            if(regExpCurrency){
                CurrencyRegExpr currency = new CurrencyRegExpr();
                value = currency.findAllCurrencyAsociatedToANumberEntities(lang, data);
            }
            carrier.setProperty("REGEXPNERCURRENCY","Entities found with REGEXPNERCURRENCY:\n" + value + "\n");
        }else{
            logger.error("Data it's not a Stringbuffer " + carrier.getName() + " it's a " + carrier.getData().getClass().getName());
        }
        return carrier;
    }

}
