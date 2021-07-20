package org.nlpa.pipe.impl;

import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.types.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This pipe replace some regular words from texts to leet speak equivalents. 
 * The data of the instance should contain a StringBuffer
 *
 * @author Alfonso Rua
 */

public class ReverseLeetSpeakFromStringBufferPipe extends AbstractPipe{

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(LeetSpeakFromStringBufferPipe.class);

    /**
     * Hashmap containing the alphabet and their leetSpeak equivalent
     * The keys are the regular alphabet and the values are the leet speak equivalents (more than 1 for the same letter)
     */
    private static final HashMap<String, LinkedList<String>> dictionary = new HashMap<>();

    /**
     * Fill the Map of leetSpeak equivalents
     */
    static { 

        String f = "/leetSpeak-json/reverseLeet.json";

        try {
            InputStream is = LeetSpeakFromStringBufferPipe.class.getResourceAsStream(f);
            JsonReader rdr = Json.createReader(is);
            JsonObject jsonObject = rdr.readObject();
            rdr.close();

            for (String ch : jsonObject.keySet()) {
                String aux=jsonObject.getString(ch);
                LinkedList<String> list =new LinkedList<String>();
                if(aux.contains(" ")){
                    String[] l= aux.split(" ");
                    for (String i : l){
                        list.add(i);
                    }
                }else{
                    list.add(aux);
                }
                dictionary.put(
                        ch,
                        list); 
            }
        } catch (Exception e) {
            logger.error("Exception processing: " + f + " message " + e.getMessage());
        }

    }

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Construct a ReverseLeetSpeakFromStringBuffer instance 
     */
    public ReverseLeetSpeakFromStringBufferPipe() {
        super(new Class<?>[0], new Class<?>[0]);
    }

    /**
     * Process an Instance. This method takes an input Instance, 
     * search for leetSpeak, adds an instance property, and returns it. This is the method by which all
     * pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
            
        StringBuffer sb = (StringBuffer) carrier.getData();

        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(sb);
        String leet;
        String noleet;

        int last = 0;
        while(matcher.find(last)){
            Random rnd = new Random();
            int rd= rnd.nextInt();
            if(rd>0){ //0 for 50% // -1*Integer.MAX_VALUE/2 for 75%
                noleet=matcher.group().toLowerCase();
                leet= dictionary.get(noleet).get(rnd.nextInt(dictionary.get(noleet).size()));
                sb.replace(matcher.start(0),last=matcher.end(0),leet);
            }else{
                last=matcher.end(0); 
            }   
        }
        
        return carrier;
    }









    
}





