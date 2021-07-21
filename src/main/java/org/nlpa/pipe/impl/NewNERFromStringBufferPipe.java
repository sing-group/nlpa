package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.checkerframework.checker.units.qual.C;
import org.nlpa.util.NER.CurrencyFastNER;
import org.nlpa.util.NER.CurrencyRegExpr;
import org.nlpa.util.NER.DateFastNER;
import org.nlpa.util.NER.DateRegExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

@AutoService(Pipe.class)
@PropertyComputingPipe()
public class NewNERFromStringBufferPipe extends AbstractPipe {

    private String langProp = DEFAULT_LANG_PROPERTY;

    List<String> identifiedEntitiesProperty = null;

    public static final String DEFAULT_IDENTIFIED_ENTITIES_PROPERTY = "FASTNERDATE,FASTNERCURRENCY,REGEXPNERDATE,REGEXPNERCURRENCY";

    private void init() {
        setIdentifiedEntitiesProperty(DEFAULT_IDENTIFIED_ENTITIES_PROPERTY);
    }

    private String identifiedEntitiesProp = DEFAULT_IDENTIFIED_ENTITIES_PROPERTY;

    private static final Logger logger = LogManager.getLogger();

    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    public String getIdentifiedEntitiesProp() {
        return identifiedEntitiesProp;
    }

    public void setLangProp(String langProp) {
        this.langProp = langProp;
    }

    public String getLangProp() {
        return this.langProp;
    }

    public void setIdentifiedEntitiesProperty(String identifiedEntitiesProperty) {
        this.identifiedEntitiesProp = identifiedEntitiesProperty;
        this.identifiedEntitiesProperty = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(identifiedEntitiesProperty, ", ");
        while (st.hasMoreTokens()) {
            this.identifiedEntitiesProperty.add(st.nextToken());
        }
    }

    public NewNERFromStringBufferPipe() {
        super(new Class<?>[0], new Class<?>[0]);

        init();
    }

    public NewNERFromStringBufferPipe(String identifiedEntitiesProp, String langProp) {
        super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class }, new Class<?>[0]);
        this.langProp = langProp;
        this.identifiedEntitiesProp = identifiedEntitiesProp;
    }

    @Override
    public Instance pipe (Instance carrier){
        if (carrier.getData() instanceof StringBuffer){
            String data = carrier.getData().toString();
            String value = "";
            String lang = (String) carrier.getProperty(langProp);

            DateFastNER dateEntity = new DateFastNER();
            value = dateEntity.testingFastNERTime(data);
            carrier.setProperty("FASTNERDATE",value);
            DateRegExpr regExpressionForDates = new DateRegExpr();
            value = regExpressionForDates.testingRegExpressionTime(lang, data);
            carrier.setProperty("REGEXPNERDATE",value);

            CurrencyFastNER currency = new CurrencyFastNER();
            value = currency.findAllCurrenciesAsociatedToANumber(lang,data);
            carrier.setProperty("FASTNERCURRENCY",value);
            CurrencyRegExpr currencyRegExpr = new CurrencyRegExpr();
            value = currencyRegExpr.findAllCurrencyAsociatedToANumberEntities(lang, data);
            carrier.setProperty("REGEXPNERCURRENCY",value);
        }else{
            logger.error("Data it's not a Stringbuffer " + carrier.getName() + " it's a " + carrier.getData().getClass().getName());
        }
        carrier.setProperty("NERDate", 0);
        return carrier;
    }

}
