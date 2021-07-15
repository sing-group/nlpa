package org.nlpa.pipe.impl;

import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.types.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.util.Pair;

import javax.json.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nlpa.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe search for leet speak words from texts and adds an Instance property. 
 * The data of the instance should contain a StringBuffer
 *
 * @author Alfonso Rua
 */

public class LeetSpeakFromStringBufferPipe extends AbstractPipe{
        
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(LeetSpeakFromStringBufferPipe.class);

    /**
     * Hashmap containing leetSpeak words from the text and their possible traductions
     */
    private static final HashMap<String, LinkedList<String>> rosetta = new HashMap<>();

    /**
     * Hashmap containing the alphabet and their leetSpeak equivalent
     * The keys are the leetSpeak patterns and the values are the regular alphabet (could be more than 1 for the same pattern)
     */
    private static final HashMap<Pattern, LinkedList<String>> dictionary = new HashMap<>();

    /**
     * Hashmap containing the words of a lenguage as LinkedLists and the lenguage itself as the key
     */
    private static final HashMap<String, LinkedList<Pattern>> words = new HashMap<>();

    
    /**
     * Fill the LinkedList of words depending on the lenguage
     * If more new words are needed, more dictionaries/words can be incorporated in this section 
     */
    static { 

       
        BufferedReader br=null;
        
        for (String i : new String[]{
            "/dict/br.txt",
            "/dict/en.txt",
            "/dict/es.txt",
            "/dict/it.txt",
            "/dict/pt.txt"
        }) {

            String lang = i.substring(6, 8).toUpperCase();
            try {
                //f = new File(i);
                //fr = new FileReader(f);
               
                InputStreamReader isr = new InputStreamReader( LeetSpeakFromStringBuffer.class.getResourceAsStream(i));
                
                br = new BufferedReader(isr);
                LinkedList<Pattern> setWords = new LinkedList<>();
                String lane;
                String wrd;
                while ((lane=br.readLine())!=null){
                    wrd= lane.split(" ")[0];
                    
                    //if word has accent mark, then add it with and without it
                    if(wrd.contains("á")||wrd.contains("é")||wrd.contains("í")||wrd.contains("ó")||wrd.contains("ú")){
                        setWords.add(Pattern.compile("(?:[\\p{Space}]|[\"><¡?¿!;:,.'-]|^)(" + Pattern.quote(wrd) + ")[;:?\"!,.'>-]?(?=(?:[\\p{Space}]|$|>))"));
                        setWords.add(Pattern.compile("(?:[\\p{Space}]|[\"><¡?¿!;:,.'-]|^)(" + Pattern.quote(wrd.replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u")) + ")[;:?\"!,.'>-]?(?=(?:[\\p{Space}]|$|>))"));
                    }else{
                        setWords.add(Pattern.compile("(?:[\\p{Space}]|[\"><¡?¿!;:,.'-]|^)(" + Pattern.quote(wrd) + ")[;:?\"!,.'>-]?(?=(?:[\\p{Space}]|$|>))"));
                    }
                    
                }
                words.put(lang, setWords);

            } catch (Exception e) {
                logger.warn("Exception processing: " + i + " message " + e.getMessage());
            }finally {
                try{
                    if(null!=br){
                        br.close();
                    }
                }catch (Exception e){
                    logger.warn("Exception clossing files: message " + e.getMessage());
                }
            }
        }

    }



    /**
     * Fill the Map of leetSpeak equivalents
     */
    static { 

            String f = "/leetSpeak-json/leet.json";

            try {
                InputStream is = LeetSpeakFromStringBufferPipe.class.getResourceAsStream(f);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();

                for (String leet : jsonObject.keySet()) {
                    String aux=jsonObject.getString(leet);
                    LinkedList<String> list =new LinkedList<String>();
                    if(aux.contains("-")){
                        String[] l= aux.split("-");
                        for (String i : l){
                            list.add(i);
                        }
                    }else{
                        list.add(aux);
                    }
                    dictionary.put(
                            Pattern.compile(Pattern.quote(leet)),
                            list); 
                }
            } catch (Exception e) {
                logger.error("Exception processing: " + f + " message " + e.getMessage());
            }

    }
        


     /**
     * The name of the property where the language is stored
     */
    private String langProp = DEFAULT_LANG_PROPERTY;

    
    /**
     * The default property name to store leetSpeak words
     */
    public static final String DEFAULT_LEET_PROPERTY = "NER_leetSpeak";

        /**
     * The property name to store leetSpeak
     */
    private String leetProp = DEFAULT_LEET_PROPERTY;

    /**
     * The property name to store transalte decision
     */
    private Boolean translateLeetFlag = false;


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
     * Establish the name of the property where the language will be stored
     *
     * @param langProp The name of the property where the language is stored
     */
    @PipeParameter(name = "langpropname", description = "Indicates the property name to store the language", defaultValue = DEFAULT_LANG_PROPERTY)
    public void setLangProp(String langProp) {
        this.langProp = langProp;
    }


    /**
     * Sets the property where leetSpeak words will be stored
     *
     * @param leetProp the name of the property for leetSpeak words
     */
    @PipeParameter(name = "leetProp", description = "Indicates the property name to store leet speak words", defaultValue = DEFAULT_LEET_PROPERTY)
    public void setLeetProp(String leetProp) {
        this.leetProp = leetProp;
    }

    /**
     * Retrieves the property name for storing leetSpeak words
     *
     * @return String containing the property name for storing leetSpeak words
     */
    public String getLeetProp() {
        return this.leetProp;
    }

    /**
     * Default construct. Construct a LeetSpeakFromStringBuffer instance with the default
     * configuration value
     */
    public LeetSpeakFromStringBufferPipe() {
        this(DEFAULT_LANG_PROPERTY, DEFAULT_LEET_PROPERTY, false);
    }

    /**
     * Default construct with translate decision. Construct a LeetSpeakFromStringBuffer instance with the default
     * configuration value but given a translate flag.
     */
    public LeetSpeakFromStringBufferPipe(Boolean tranlate) {
        this(DEFAULT_LANG_PROPERTY, DEFAULT_LEET_PROPERTY, tranlate);
    }


    /**
     * Construct a LeetSpeakFromStringBuffer instance given a language
     * property that stores leet speak words of the StringBuffer in the property
     * leetProp. 
     *
     * @param langProp The property that stores the language of text
     * @param leetProp The name of the property to store leetSpeak words
     * @param translateFlag The name of the object that stores the decision of transaltion (true transalte text, false do nothing)
     */
    public LeetSpeakFromStringBufferPipe(String langProp, String leetProp, Boolean translateFlag) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},new Class<?>[0]);
        this.langProp = langProp;
        this.leetProp = leetProp;
        this.translateLeetFlag=translateFlag;
    }
    /**
     * Construct a LeetSpeakFromStringBuffer instance given a language
     * property that stores leet speak words of the StringBuffer in the property
     * leetProp
     *
     * @param langProp The property that stores the language of text
     * @param leetProp The name of the property to store leetSpeak words
     */
    public LeetSpeakFromStringBufferPipe(String langProp, String leetProp) {
        super(new Class<?>[]{GuessLanguageFromStringBufferPipe.class},new Class<?>[0]);
        this.langProp = langProp;
        this.leetProp = leetProp;
        this.translateLeetFlag=false;
    }


    /**
     * Process an Instance. This method takes an input Instance, 
     * search for leetSpeak, adds an instance property, and returns it. If translate
     * to regular language is required, it is also done in this method. 
     * This is the method by which all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {


            String lang = (String) carrier.getProperty(langProp);
            if(lang == null){ //if cant find a language, set English by default
                lang="en";
            }
            StringBuffer sb = (StringBuffer) carrier.getData();
            String value = "";
            Pattern ptWord = Pattern.compile("[^\s]+");

            LinkedList<Pattern> setWords = words.get(lang);
            if(setWords==null){//When there is not a dictionary for this language
                return carrier;
            }else {

                //For each word in text (between spaces)
                Matcher matcher = ptWord.matcher(sb);
                int last = 0;
                while(matcher.find(last)){
                    String palabra = matcher.group();
                    
                    Boolean isAWord =false;
                    Iterator<Pattern> itr=setWords.listIterator();
                    Matcher mt;
                    while(!isAWord && itr.hasNext()){ // go over the setWords list checking for matches
                        mt=itr.next().matcher(palabra);
                        if(mt.matches()){
                            isAWord=true; //when found a match stop and do not continue with the logic
                        }
                    }
                    
                    if(!isAWord){ 
                    LinkedList<String> posibles = new LinkedList<String>();
                    LinkedList<String> auxList = new LinkedList<String>();
                    
                    //map with combinations of replaceable leet and its positions
                    HashMap<Pair<Integer, Integer>,Pair<Pattern, LinkedList<String>>> replacements = new HashMap<Pair<Integer, Integer>,Pair<Pattern, LinkedList<String>>>();
                    
                    for(int i=0;i<palabra.length();i++){
                        for(int j=i+1;j<palabra.length()+1;j++){
                            String subpalabra =palabra.substring(i,j);

                            for (Pattern p: dictionary.keySet() ){
                                Matcher m=p.matcher(subpalabra);
                                if (m.matches()){ 
                                    replacements.put(new Pair<Integer, Integer>(i,j),new Pair<Pattern, LinkedList<String>>(p,dictionary.get(p)));
                                }
                            } 

                        }
                    }



                    if(!replacements.isEmpty()){
                    
                    //map with all the posible words that can be made with replacements,
                    //last integer in the Pair is the amount of chars erased in the permutation process
                    HashMap <Integer,LinkedList<Pair<String,Integer>>> permutations = new HashMap<Integer,LinkedList<Pair<String,Integer>>>(); 
                    LinkedList<Pair<String,Integer>> wordNchanges=new LinkedList<Pair<String,Integer>>();
                    String auxString=palabra;

                    //main logic here is that you can not change anything before last permutation
                        //first iteration
                        for(Pair<Integer, Integer> pos:replacements.keySet()){
                        
                            for(String c:replacements.get(pos).getObj2()){
                                auxString= palabra.substring(0,pos.getObj1())+c+palabra.substring(pos.getObj2(),palabra.length());
                                wordNchanges =new LinkedList<Pair<String,Integer>>();
                                if(permutations.get(pos.getObj2())!=null){ //if not empty: add, do not override
                                    wordNchanges=permutations.get(pos.getObj2());
                                }
                                wordNchanges.add(new Pair<String,Integer>(auxString,pos.getObj2()-1-pos.getObj1()));
                                permutations.put(pos.getObj2(),wordNchanges);
                            }
                        }

                        //rest of the iterations
                        for(int poss:permutations.keySet()){

                            for(Pair<String,Integer> permString:permutations.get(poss)){

                                for(Pair<Integer, Integer> pos:replacements.keySet()){
                                    if(pos.getObj1()>=poss){
                                        for(String c:replacements.get(pos).getObj2()){
                                            auxString= permString.getObj1().substring(0,pos.getObj1()-permString.getObj2()) + c + permString.getObj1().substring(pos.getObj2()-permString.getObj2(),permString.getObj1().length());
                                            wordNchanges =new LinkedList<Pair<String,Integer>>();
                                            if(!permutations.get(pos.getObj2()).isEmpty()){ //if not empty: add, do not override
                                                wordNchanges=permutations.get(pos.getObj2());
                                            }
                                            wordNchanges.add(new Pair<String,Integer>(auxString,permString.getObj2()+(pos.getObj2()-1-pos.getObj1())));
                                            permutations.put(pos.getObj2(),wordNchanges);
                                        }
                                    }  
                                }

                            }

                        }


                    //put every permuted word in a list of posible leet
                    for(int i:permutations.keySet()){
                        for(Pair<String,Integer> w:permutations.get(i)){
                            posibles.add(w.getObj1().toLowerCase());
                        }
                    }
               
                    //check words in dictionary
                    for(String posible :posibles){
                        //if it is a real word, add it to results

                        Boolean stop=false;
                        itr=setWords.listIterator();
                        while(!stop && itr.hasNext()){ // go over the setWords list checking for matches
                            mt=itr.next().matcher(posible);
                            if(mt.matches()){
                                stop=true;
                                if(auxList!=null){
                                    auxList.add(posible);
                                }else{
                                    auxList = new LinkedList<String>();
                                    auxList.add(posible);
                                }
                                rosetta.put(palabra,auxList);
                            }
                        }
                    
                    }
                }
                }
                last=matcher.end(0);
                }//for each word in the text

            }




                //adding all leet words to a single string
                for(String pal : rosetta.keySet()){
                    value+=pal+" ";
                }

                //if translate is chosen, then change the carrier
                if(this.translateLeetFlag){
                    sb.append("                    "); //add 20spaces to let replace last word without errors
                    for (String leetS : rosetta.keySet()){
                        String auxF="";
                        LinkedList<String> auxList2=rosetta.get(leetS);

                        if(auxList2.size()==1){//if only 1 posibility of leet translate
                            auxF=auxList2.get(0);
                        }else{
                            for(String auxP: auxList2){// if more than one
                                if(auxP.equals(auxList2.get(auxList2.size()-1))){
                                    auxF+=auxP;
                                }else{
                                    auxF+=auxP+"|";
                                }
                                
                            }
                        }   
                        int lst = 0;
                        Pattern pattern = Pattern.compile(Pattern.quote(leetS));
                        Matcher matcher = pattern.matcher(sb);
                        
                        while(matcher.find(lst)){
                            sb.replace(matcher.start(0),lst=matcher.end(0),auxF);
                        }

                    }
                    sb.delete(sb.length()-20, sb.length()+1); //restore sb, delateing last spaces
                }

             
             carrier.setProperty(leetProp, value);
        
        return carrier;
    }


}
