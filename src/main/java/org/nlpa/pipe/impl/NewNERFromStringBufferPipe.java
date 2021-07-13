package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;
import org.nlpa.util.CurrencyPackage.DateEntity;
import org.nlpa.util.RegExpressionForDates;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

@AutoService(Pipe.class)
@PropertyComputingPipe()
public class NewNERFromStringBufferPipe extends AbstractPipe {

    /**
     * The name of the property where the language is stored
     */

    private String langProp = DEFAULT_LANG_PROPERTY;

    /**
     * Logger
     */

    private static final Logger logger = LogManager.getLogger();


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
        super(new Class<?>[] { GuessLanguageFromStringBufferPipe.class },new Class<?>[0]);

    }

    //Proper pipe
    @Override
    public Instance pipe (Instance carrier){
        if (carrier.getData() instanceof StringBuffer){
            String data = carrier.getData().toString();
            DateEntity dateEntity = new DateEntity();
            System.out.println(dateEntity.testingFastNERTime(data));
            RegExpressionForDates regExpressionForDates = new RegExpressionForDates();
            System.out.println(regExpressionForDates.testingRegExpressionTime(data));
        }else{
            logger.error("Data it's not a Stringbuffer " + carrier.getName() + " it's a " + carrier.getData().getClass().getName());
        }
        return carrier;
    }

}
