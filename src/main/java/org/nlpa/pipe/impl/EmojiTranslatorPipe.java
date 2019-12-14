package org.nlpa.pipe.impl;

import java.util.HashMap;

import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;

/**
 * This pipe..
 * 
 * 
 * @author Rodrigo Currás
 */
@TransformationPipe
public class EmojiTranslatorPipe extends AbstractPipe {

    static HashMap<String,String> emojiDict=new HashMap<>(); 

    static {

    }

    public EmojiTranslatorPipe(){
        super(new Class[0], new Class[0]);
    }

    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    @Override
    public Instance pipe(Instance carrier) {
        StringBuffer text= (StringBuffer) carrier.getData();

        

        return carrier;
    }

}