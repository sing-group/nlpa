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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;

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
    private static final Logger logger = LogManager.getLogger(NERFromStringBufferPipe.class);

    /**
     * Dependencies of the type alwaysAfter
     * These dependences indicate what pipes should be  
     * executed before the current one. So this pipe
     * shoudl be executed always after other dependant pipes
     * included in this variable
     */
    final Class<?> alwaysAftterDeps[]={};

    /**
     * Dependencies of the type notAfter
     * These dependences indicate what pipes should not be  
     * executed before the current one. So this pipe
     * shoudl be executed before other dependant pipes
     * included in this variable
     */
    final Class<?> notAftterDeps[]={};

    /**
     * Initing entity types collection
     */
    List<String> entityTypes = null;

    /**
     * The default value to entityTypes
     */
    public static final String DEFAULT_ENTITY_TYPES = "DATE,MONEY,NUMBER,ADDRESS,LOCATION";

    List<String> identifiedEntitiesProperty = null;
    /**
     * The default property name to store the identified entities
     */
    public static final String DEFAULT_IDENTIFIED_ENTITIES_PROPERTY = "NERDATE,NERMONEY,NERNUMBER,NERADDRESS,NERLOCATION";

    private void init() {
        setEntityTypes(DEFAULT_ENTITY_TYPES);
        setIdentifiedEntitiesProperty(DEFAULT_IDENTIFIED_ENTITIES_PROPERTY);
    }

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
    public void setEntityTypes(List<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Sets the entityTypes to annotate
     *
     * @param entityTypes the name of the property for the polarity
     */
    @PipeParameter(name = "entityTypes", description = "Indicates the entity types to annotate through a list of comma-separated values", defaultValue = DEFAULT_ENTITY_TYPES)
    public void setEntityTypes(String entityTypes) {
        this.entityTypes = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(entityTypes, ", \t");
        while (st.hasMoreTokens()) {
            this.entityTypes.add(st.nextToken());
        }
    }

    /**
     * Return the identified properties as string
     * @return The identified properties as string
     */
    public String getIdentifiedEntitiesProp(){
        return identifiedEntitiesProp;
    }

    /**
     * Sets the property where the identified entities will be stored
     *
     * @param identifiedEntitiesProperty the properties to identify
     */
    @PipeParameter(name = "identifiedEntitiesProperty", description = "Indicates the property name to store the identified entities", defaultValue = DEFAULT_IDENTIFIED_ENTITIES_PROPERTY)
    public void setIdentifiedEntitiesProp(List<String> identifiedEntitiesProperty) {
        this.identifiedEntitiesProp = "";
        for (String p : identifiedEntitiesProperty)
            this.identifiedEntitiesProp += (p + " ");
        this.identifiedEntitiesProperty = identifiedEntitiesProperty;
    }

    /**
     * Sets the properties to annotate
     *
     * @param identifiedEntitiesProperty the properties to identify
     */
    @PipeParameter(name = "identifiedEntitiesProperty", description = "Indicates the identified entities through a list of comma-separated values", defaultValue = DEFAULT_IDENTIFIED_ENTITIES_PROPERTY)
    public void setIdentifiedEntitiesProperty(String identifiedEntitiesProperty) {
        this.identifiedEntitiesProp = identifiedEntitiesProperty;
        this.identifiedEntitiesProperty = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(identifiedEntitiesProperty, ", ");
        while (st.hasMoreTokens()) {
            this.identifiedEntitiesProperty.add(st.nextToken());
        }
    }

    /**
     * Retrieves the property name for storing the identified entities
     *
     * @return String containing the property name for storing the identified
     *         entities
     */
    public Collection<String> getIdentifiedEntitiesProperty() {
        return this.identifiedEntitiesProperty;
    }

    /**
     * Determines the input type for the data attribute of the Instances processed
     *
     * @return the input type for the data attribute of the Instances processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of a Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of a Instance after
     *         processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Default constructor
     */
    public NERFromStringBufferPipe() {
        init();
    }

    // Indicate which annotators will be used (see:
    // https://stanfordnlp.github.io/CoreNLP)
    /**
     * Build a NERFromStringBufferPipe that stores the identified entities in the
     * property identifiedEntitiesProp
     *
     * @param identifiedEntitiesProp The name of the property to store identified
     *                               entities
     */
    public NERFromStringBufferPipe(String identifiedEntitiesProp) {
        this.identifiedEntitiesProp = identifiedEntitiesProp;
    }

    /**
     * Build a NERFromStringBufferPipe that stores the entity types to annotate
     *
     * @param entityTypes The list of the entity types to annotate
     */
    public NERFromStringBufferPipe(List<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Build a NERFromStringBufferPipe that stores the entity types to annotate
     * 
     * @param identifiedEntitiesProp The property names for the identified entities
     * @param entityTypes            The list of the entity types to annotate
     */
    public NERFromStringBufferPipe(String identifiedEntitiesProp, List<String> entityTypes) {
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
        if (this.entityTypes.size() != this.identifiedEntitiesProperty.size()) {
            logger.fatal(
                    "The number of entity types to detect is different to the number of property names. Unable to find a property store strategy for this.");
            System.exit(0);
        }

        HashMap<String, String> hmProperties = new HashMap<>();

        try {
            StringBuffer newSb = new StringBuffer();
            String data = carrier.getData().toString();
            CoreDocument doc = new CoreDocument(data);
            int lastIndex = 0;

            // Annotate the document
            pipeline.annotate(doc);

            // Iterate over NER identified entities
            if (doc.entityMentions() != null && doc.entityMentions().size() > 0) {
                for (CoreEntityMention em : doc.entityMentions()) {
                    int idx = entityTypes.indexOf(em.entityType());
                    if (idx > -1) {
                        int begin = data.indexOf(em.text()) - 1;

                        int end = data.indexOf(em.text()) + (em.text().length());

                        if (data.startsWith(em.text())) {
                            if (lastIndex < end + 1) {
                                lastIndex = end;
                            }
                        } else if (data.endsWith(em.text())) {
                            if (begin >= lastIndex) {
                                newSb.append(data.substring(lastIndex, begin + 1));
                                lastIndex = data.length() - 1;
                            }
                        } else if (begin > 0) {
                            if (begin >= lastIndex) {
                                newSb.append(data.substring(lastIndex, begin));
                                lastIndex = end;
                            }
                        }

                        String property = this.identifiedEntitiesProperty.get(idx);

                        String propertyValue = em.text();
                        if (identifiedEntitiesProperty.contains(property)) {
                            hmProperties.put(property, propertyValue);
                        }

                    }
                }
                newSb.append(data.substring(lastIndex));

                carrier.setData(newSb);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }

        identifiedEntitiesProperty.stream().filter((propName) -> (carrier.getProperty(propName) == null))
                .forEachOrdered((propName) -> {
                    String val = hmProperties.get(propName);
                    carrier.setProperty(propName, val == null ? "" : val);
                });

        return carrier;

    }
}
