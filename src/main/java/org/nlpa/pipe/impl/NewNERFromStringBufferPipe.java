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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

@AutoService(Pipe.class)
@PropertyComputingPipe()
public class NewNERFromStringBufferPipe extends AbstractPipe {

    private String langProp = DEFAULT_LANG_PROPERTY;

    List<String> identifiedEntitiesProperty = null;

    public static final String DEFAULT_IDENTIFIED_ENTITIES_PROPERTY = "NERDATE,NERMONEY";

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

    @PipeParameter(name = "identifiedEntitiesProperty", description = "Indicates the identified entities through a list of comma-separated values", defaultValue = DEFAULT_IDENTIFIED_ENTITIES_PROPERTY)
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

    public NewNERFromStringBufferPipe(String identifiedEntitiesProp) {
        super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class }, new Class<?>[0]);

        this.identifiedEntitiesProp = identifiedEntitiesProp;
    }

    //Proper pipe
    @Override
    public Instance pipe (Instance carrier){
        if (carrier.getData() instanceof StringBuffer){
            String data = carrier.getData().toString();
            //DateEntity dateEntity = new DateEntity();
            //System.out.println(dateEntity.testingFastNERTime(data));
            //RegExpressionForDates regExpressionForDates = new RegExpressionForDates();
            //System.out.println(regExpressionForDates.testingRegExpressionTime(data));
            CurrencyFastNER currency = new CurrencyFastNER();
            //currency.findAllCurrenciesAsociatedToANumber("es",data);
            currency.findAllCurrenciesAsociatedToANumber("es",data);
            CurrencyRegExpr currencyRegExpr = new CurrencyRegExpr();
            currencyRegExpr.findAllCurrencyAsociatedToANumberEntities("en", data);
        }else{
            logger.error("Data it's not a Stringbuffer " + carrier.getName() + " it's a " + carrier.getData().getClass().getName());
        }
        carrier.setProperty("NERDate", 0);
        return carrier;
    }

}
