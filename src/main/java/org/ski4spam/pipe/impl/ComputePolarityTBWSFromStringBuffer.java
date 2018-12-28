package org.ski4spam.pipe.impl;

import java.io.IOException;

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
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.types.Instance;

/**
 * This class allows to compute polarity by querying Textblob-ws implemented by Enaitz Ezpeleta
 * Example: 
 * wget -O- -q --header "Content-Type: application/json" --post-data '{"text": "Hello World"}' http://172.18.0.2/postjson
 * @author José Ramón Méndez Reboredo
 * @author Enaitz Ezpeleta
 */
public class ComputePolarityTBWSFromStringBuffer extends Pipe {
    static Logger logger=LogManager.getLogger(ComputePolarityTBWSFromStringBuffer.class);

    /**
     * Contains the default URI for accessing textblob-service
     */
    private static final String DEFAULT_REQUEST_URI="http://textblob-ws/postjson";
    //public static final String DEFAULT_URI="http://localhost/postjson";

    /**
     * The uri to be used
     */
    private String uri=DEFAULT_REQUEST_URI;

    /**
     * The default name for the polarity property
     */
    public static final String DEFAULT_POLARITY_PROPERTY="PolarityTBWS";

    /**
     * The polarity property name
     */
    private String polarityProperty=DEFAULT_POLARITY_PROPERTY;

    /**
     * Creates an instance of this pipe
     */
    public ComputePolarityTBWSFromStringBuffer(){
        this(DEFAULT_REQUEST_URI);
    }

    /**
     * Creates an instance of this pipe
     * @param uri The uri that will be used for requests
     */
    public ComputePolarityTBWSFromStringBuffer(String uri){
        this(uri,DEFAULT_POLARITY_PROPERTY);
    }

    /**
     * Creates an instance of this pipe
     * @param uri The uri that will be used for requests
     * @param polarityProperty The property name to store the polarity
     */
    public ComputePolarityTBWSFromStringBuffer(String uri, String polarityProperty){
        super(new Class<?>[0], new Class<?>[0]);
        this.uri=uri;
        this.polarityProperty=polarityProperty;
    }
    /**
     * @return the polarityProperty
     */
    public String getPolarityProperty() {
        return polarityProperty;
    }

    /**
     * Stablish the polarity property name
     * @param polarityProperty the polarityProperty to set
     */
    @PipeParameter(name = "polpropname", description = "Indicates the property name to store the polarity", defaultValue = DEFAULT_POLARITY_PROPERTY)
    public void setPolarityProperty(String polarityProperty) {
        this.polarityProperty = polarityProperty;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Stablish the uri to be used for querying purposes
     * @param uri the uri to set
     */
    @PipeParameter(name = "uri", description = "Indicates the URI to make polarity requests", defaultValue = DEFAULT_REQUEST_URI)
    public void setUri(String uri) {
        this.uri = uri;
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
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("text", new JsonPrimitive(carrier.getData().toString()));
        Double result=http(uri,jsonObj.toString());
        if(result!=null){
            carrier.setProperty("PolarityTBWS", result);
            logger.info("Polarity("+carrier.getData().toString()+")="+result);
        }else{
            logger.error("Polarity error error for instance "+carrier+" contents: "+carrier.getData().toString());
            carrier.invalidate();
        }

        return carrier;
	}


    /**
     * Makes a json request to the textblob-ws
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
            try{
                respuesta = gson.fromJson(json, Response.class);
            }catch (Exception e){
                logger.error(e);
                e.printStackTrace();
                return null;
            }
            
            
            return respuesta.getPolarity();

        } catch (IOException ex) {
            logger.error(ex);
            ex.printStackTrace();
            return null;            
        }

    } 

}

/**
 * Handles the response for the json query to the textblob-ws
 */
class Response{

    /**
     * The polaroty
     */
    private double polarity;

    /**
     * The subjectivity
     */
    private double subjectivity;

    /**
     * @return the polarity
     */
    public double getPolarity() {
        return polarity;
    }

    /**
     * @param polarity the polarity to set
     */
    public void setPolarity(double polarity) {
        this.polarity = polarity;
    }

    /**
     * @return the subjectivity
     */
    public double getSubjectivity() {
        return subjectivity;
    }

    /**
     * @param subjectivity the subjectivity to set
     */
    public void setSubjectivity(double subjectivity) {
        this.subjectivity = subjectivity;
    }

}