/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.pipe.impl;

import com.google.auto.service.AutoService;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Instance;

import java.io.IOException;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;

/**
 * This class allows to compute polarity by querying Textblob-ws implemented by
 * Enaitz Ezpeleta Example: wget -O- -q --header "Content-Type:
 * application/json" --post-data '{"text": "Hello World"}' \
 * http://&lt;textblob_server&gt;/postjson
 *
 * To compute the polarity we take advantage of a webservice implementing a REST
 * API. The webservice is implemented python and takes advantae of textblob
 * library to compute polarity.
 *
 * To execute this task, the textblob web service should be executed. The
 * service can be started using adocker container included in the /scripts
 * directory. A starting script (run-textblob-ws-sh) is provided to facilitate
 * launching. The text-blob service has been entirelly developed by Enaitz
 * Ezpeleta (Mondragon Unibertsitatea)
 *
 * If the service is being executed in other server, SSH tunneling (-L) can be
 * used to connect the pipe with the service. As an example the following
 * command can be used
 *
 * sudo ssh -L 80:textblob_ws:80 moncho@ski.4spam.group
 *
 * @author José Ramón Méndez Reboredo
 * @author Enaitz Ezpeleta
 */
@AutoService(Pipe.class)
@PropertyComputingPipe()
public class ComputePolarityTBWSFromStringBuffer extends AbstractPipe {

    /**
     * For logging purposes
     */
    static Logger logger = LogManager.getLogger(ComputePolarityTBWSFromStringBuffer.class);

    /**
     * Contains the default URI for accessing textblob-service
     */
    private static final String DEFAULT_REQUEST_URI = "http://textblob-ws/postjson";
    //public static final String DEFAULT_URI="http://localhost/postjson";

    /**
     * The uri to be used
     */
    private String uri = DEFAULT_REQUEST_URI;

    /**
     * The default name for the polarity property
     */
    public static final String DEFAULT_POLARITY_PROPERTY = "PolarityTBWS";

    /**
     * The polarity property name
     */
    private String polarityProperty = DEFAULT_POLARITY_PROPERTY;

    /**
     * Default constructor. Creates an instance of this pipe with default
     * configuration.
     */
    public ComputePolarityTBWSFromStringBuffer() {
        this(DEFAULT_REQUEST_URI);
    }

    /**
     * Creates an instance of this pipe storing the uri
     *
     * @param uri The uri that will be used for requests
     */
    public ComputePolarityTBWSFromStringBuffer(String uri) {
        this(uri, DEFAULT_POLARITY_PROPERTY);
    }

    /**
     * Creates an instance of this pipe storing the uri and polarity property
     *
     * @param uri The uri that will be used for requests
     * @param polarityProperty The property name to store the polarity
     */
    public ComputePolarityTBWSFromStringBuffer(String uri, String polarityProperty) {
        super(new Class<?>[0], new Class<?>[0]);
        this.uri = uri;
        this.polarityProperty = polarityProperty;
    }

    /**
     * Returns the stored polarity property
     *
     * @return the polarityProperty
     */
    public String getPolarityProperty() {
        return polarityProperty;
    }

    /**
     * Establish the polarity property name
     *
     * @param polarityProperty the polarityProperty to set
     */
    @PipeParameter(name = "polpropname", description = "Indicates the property name to store the polarity", defaultValue = DEFAULT_POLARITY_PROPERTY)
    public void setPolarityProperty(String polarityProperty) {
        this.polarityProperty = polarityProperty;
    }

    /**
     * Returns the stored uri
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Establish the uri to be used for querying purposes
     *
     * @param uri the uri to set
     */
    @PipeParameter(name = "uri", description = "Indicates the URI to make polarity requests", defaultValue = DEFAULT_REQUEST_URI)
    public void setUri(String uri) {
        this.uri = uri;
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
     * Indicates the datatype expected in the data attribute of an Instance
     * after processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Process an Instance. This method takes an input Instance, calculates its
     * polarity, and returns it. This is the method by which all pipes are
     * eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     */
    @Override
    public Instance pipe(Instance carrier) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("text", new JsonPrimitive(carrier.getData().toString()));
        Double result = http(uri, jsonObj.toString());
        if (result != null) {
            carrier.setProperty("PolarityTBWS", result);
            logger.info("Polarity(" + carrier.getData().toString() + ")=" + result);
        } else {
            logger.error("Polarity error error for instance " + carrier + " contents: " + carrier.getData().toString());
            carrier.invalidate();
        }

        return carrier;
    }

    /**
     * Makes a json request to the textblob-ws
     *
     * @param url The URL for the textblob-ws
     * @param body The body of the query
     * @return The polarity of the text
     */
    private Double http(String url, String body) {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(body);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            com.google.gson.Gson gson = new com.google.gson.Gson();
            Response respuesta;
            try {
                respuesta = gson.fromJson(json, Response.class);
            } catch (Exception e) {
                logger.error("[HTTP]: " + e.getMessage());
                return null;
            }

            return respuesta.getPolarity();

        } catch (IOException ex) {
            logger.error("[HTTP builder]: " + ex.getMessage());
            return null;
        }
    }
}

/**
 * Handles the response for the json query to the textblob-ws
 */
class Response {

    /**
     * The polarity
     */
    private double polarity;

    /**
     * The subjectivity
     */
    private double subjectivity;

    /**
     * Returns the stored polarity
     *
     * @return the polarity
     */
    public double getPolarity() {
        return polarity;
    }

    /**
     * Estabilish the polarity
     *
     * @param polarity the polarity to set
     */
    public void setPolarity(double polarity) {
        this.polarity = polarity;
    }

    /**
     * Returns the subjectivity
     *
     * @return the subjectivity
     */
    public double getSubjectivity() {
        return subjectivity;
    }

    /**
     * Estabilish the subjectivity
     *
     * @param subjectivity the subjectivity to set
     */
    public void setSubjectivity(double subjectivity) {
        this.subjectivity = subjectivity;
    }

}
