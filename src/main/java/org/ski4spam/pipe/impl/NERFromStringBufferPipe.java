/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ski4spam.pipe.impl;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.ski4spam.Main;
import static org.ski4spam.pipe.impl.GuessLanguageFromStringBufferPipe.DEFAULT_LANG_PROPERTY;

/**
 * This pipe finds named entity recognition, saves this entities as Instance
 * property and deletes them. The data of the instance should contain a
 * StringBuffer
 *
 * @author María Novo
 */
@PropertyComputingPipe()
public class NERFromStringBufferPipe extends Pipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(TeeCSVFromStringBufferPipe.class);

    /**
     * Initing entity types collection
     */
    static Collection<String> entityTypes = new ArrayList<>();

    /**
     * The default value to entityTypes
     */
    public static final String DEFAULT_ENTITY_TYPES = "DATE,MONEY,NUMBER,ADDRESS,LOCATION";

    static {
        StringTokenizer st = new StringTokenizer(DEFAULT_ENTITY_TYPES, ", \t");
        while (st.hasMoreTokens()) {
            entityTypes.add(st.nextToken());
        }
    }

    /**
     * The default property name to store the identified entities
     */
    public static final String DEFAULT_IDENTIFIED_ENTITIES_PROPERTY = "identifiedEntitiesProp";

    /**
     * The property name to store identified entities.
     */
    private String identifiedEntitiesProp = DEFAULT_IDENTIFIED_ENTITIES_PROPERTY;

    /**
     * Initing a StandfordCoreNLP pipeline
     */
    static StanfordCoreNLP pipeline;
    static Properties props;

    static {
        props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,regexner");
        props.setProperty("ner.useSUTime", "false");
        pipeline = new StanfordCoreNLP(props);
    }

    /**
     * Retrieves the entityTypes to annotate
     *
     * @return String containing the entityTypes to annotate
     */
    public Collection<String> getEntityTypes() {
        return this.entityTypes;
    }

    /**
     * Sets the entityTypes to annotate
     *
     * @param entityTypes the name of the property for the polarity
     */
    public void setEntityTypes(Collection<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Sets the entityTypes to annotate
     *
     * @param entityTypes the name of the property for the polarity
     */
    @PipeParameter(name = "entityTypes",
            description = "Indicates the entity types to annotate through a list of comma-separated values",
            defaultValue = DEFAULT_ENTITY_TYPES)
    public void setEntityTypes(String entityTypes) {
        StringTokenizer st = new StringTokenizer(entityTypes, ", ");
        while (st.hasMoreTokens()) {
            this.entityTypes.add(st.nextToken());
        }
    }

    /**
     * Sets the property where the identified entities will be stored
     *
     * @param identifiedEntitiesProp the name of the property for the identified
     * entities
     */
    @PipeParameter(name = "identitiespropname", description = "Indicates the property name to store the identified entities", defaultValue = DEFAULT_IDENTIFIED_ENTITIES_PROPERTY)
    public void setIdentifiedEntitiesProp(String identifiedEntitiesProp) {
        this.identifiedEntitiesProp = identifiedEntitiesProp;
    }

    /**
     * Retrieves the property name for storing the identified entities
     *
     * @return String containing the property name for storing the identified
     * entities
     */
    public String getIdentifiedEntitiesProp() {
        return this.identifiedEntitiesProp;
    }

    /**
     * Determines the input type for the data attribute of the Instances
     * processed
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     * processing
     */
    @Override
    public Class getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Default constructor
     */
    public NERFromStringBufferPipe() {
    }

    // Indicate which annotators will be used (see: https://stanfordnlp.github.io/CoreNLP)
    /**
     * Build a NERFromStringBufferPipe that stores the identified entities in
     * the property identifiedEntitiesProp
     *
     * @param identifiedEntitiesProp The name of the property to store
     * identified entities
     */
    public NERFromStringBufferPipe(String identifiedEntitiesProp) {
        this.identifiedEntitiesProp = identifiedEntitiesProp;
    }

    /**
     * Build a NERFromStringBufferPipe that stores the entity types to annotate
     *
     * @param entityTypes The list of the entity types to annotate
     */
    public NERFromStringBufferPipe(Collection<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Build a NERFromStringBufferPipe that stores the entity types to annotate
     *
     * @param entityTypes The list of the entity types to annotate
     */
    public NERFromStringBufferPipe(String identifiedEntitiesProp, Collection<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing NER. This is the method by which all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    @Override
    public Instance pipe(Instance carrier) {
        try {

            if (carrier.getData() instanceof StringBuffer) {
                StringBuffer newSb = new StringBuffer();
                String data = carrier.getData().toString();
                CoreDocument doc = new CoreDocument(data);

                if (carrier.getData() instanceof StringBuffer) {
                    // Annotate the document
                    pipeline.annotate(doc);

                    // Iterate over NER identified entities
                    StringBuilder identifiedEntities = new StringBuilder();
                    if (doc.entityMentions().size() > 0) {
                        for (CoreEntityMention em : doc.entityMentions()) {
                            if (entityTypes.contains(em.entityType())) {
                                int begin = data.indexOf(em.text()) - 1;
                                int end = data.indexOf(em.text()) + (em.text().length());

                                if (data.startsWith(em.text())) {
                                    newSb.append(data.substring(end, data.length() - 1));
                                } else if (data.endsWith(em.text())) {
                                    newSb.append(data.substring(0, begin + 1));
                                } else {
                                    newSb.append(data.substring(0, begin) + data.substring(end, data.length()));
                                }

                                if (identifiedEntities.indexOf(em.text()) < 0) {
                                    identifiedEntities.append(em.text()).append("(").append(em.entityType()).append(")|");
                                }
                            } else {
                                newSb.append(data);
                            }
//                   System.out.println("\tdetected entity: \t" + em.text() + "\t" + em.entityType() + "\t" + em.entityTypeConfidences());                    
                        }
                    } else {
                        newSb.append(data);
                    }
//                    if (identifiedEntities.length() > 0) {
//                        System.out.println("carrier: " + carrier.getData().toString());
//                        System.out.println("---------------------------------------------------");
//                        System.out.println("identifiedEntitiesProp: " + identifiedEntities.toString());
//                        System.out.println("---------------------------------------------------");
//                    }
                    carrier.setData(newSb);
//                   if (identifiedEntities.length() > 0) {
//                        System.out.println("carrier: " + carrier.getData().toString());
//                        System.out.println("---------------------------------------------------");
//                    }
                    carrier.setProperty(identifiedEntitiesProp, identifiedEntities.toString());

                }

            }
            return carrier;
        } catch (Exception ex) {
            logger.error(Main.class.getName() + ". " + ex.getMessage());

        }
        return carrier;

    }
}
