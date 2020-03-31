package org.nlpa.util;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetQuery;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.data.BabelLemma;
import it.uniroma1.lcl.babelnet.data.BabelLemmaType;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

public class LanguageLexiconGenerator {
    static final Language language=Language.PT;
    public static void main(String[] args) throws IOException {
        String file = "lexicon_pt.json";
        HashMap<String, List<Double>> htPolarities = new HashMap<>();
        
        for (String i : new String[]{"/dictionarities_polarity/lexicon_babelnet.json"}) {         
            try {
                InputStream is = LanguageLexiconGenerator.class.getResourceAsStream(i);
                JsonReader rdr = Json.createReader(is);
                JsonObject jsonObject = rdr.readObject();
                rdr.close();
                JsonObjectBuilder obj = Json.createObjectBuilder();
                
                jsonObject.keySet().forEach((syns) -> {
                    JsonArray array = jsonObject.getJsonArray(syns);
                    BabelNet bn = BabelNet.getInstance();
                    BabelNetQuery bnq=(new BabelNetQuery.Builder(new BabelSynsetID(syns))).to(language).build();
                    List<BabelSynset> list= bn.getSynsets(bnq);
                    BabelSynset synset=list.get(0); 
                    List<BabelLemma>  lemas= synset.getLemmas(language,BabelLemmaType.HIGH_QUALITY);
                    for (BabelLemma current:lemas){
                        List<Double> l = new LinkedList<>(); //pos 0 polaridad +, pos 1 polaridad -, pos 2 numElem
                        String key=current.getLemma().trim().toLowerCase().replaceAll("(?<!^|\\p{Punct})\\p{Punct}*$", "").replaceAll("_"," ");

                        if (htPolarities.containsKey(key)){
                            l.add(htPolarities.get(key).get(0)+array.getJsonNumber(0).doubleValue());
                            l.add(htPolarities.get(key).get(1)+array.getJsonNumber(1).doubleValue());
                            l.add(htPolarities.get(key).get(2)+1);
                        }
                        else {
                            l.add(array.getJsonNumber(0).doubleValue());
                            l.add(array.getJsonNumber(1).doubleValue());
                            l.add(1.0);
                        }
                        
                        htPolarities.put(key,l);    
                    }
                });

                htPolarities.keySet().forEach((word) -> {
                    
                    JsonArray array = Json.createArrayBuilder().add(htPolarities.get(word).get(0)/htPolarities.get(word).get(2))
                                                                .add(htPolarities.get(word).get(1)/htPolarities.get(word).get(2)).build();

                    obj.add(word,array);

                });

                JsonObject obj1 = obj.build();
                
                try {
                    
                    Writer write = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file),"UTF16"));

                    write.write(obj1.toString());
                    write.flush();
                    write.close();
            
                } catch (IOException e) {
                    System.out.println("Exception write file: " + i + " message " + e.getMessage());
                }
                   

            } catch (Exception e) {
                System.out.println("Exception processing: " + i + " message " + e.getMessage());
            }
        }
        
    }
}
