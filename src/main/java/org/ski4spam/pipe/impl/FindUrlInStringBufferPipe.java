package org.ski4spam.pipe.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.ski4spam.util.EBoolean;

/**
 * This pipe drops URLs The data of the instance should contain a StringBuffer
 *
 * @author Reyes Pavón
 * @author Rosalía Laza
 */
@PropertyComputingPipe()
public class FindUrlInStringBufferPipe extends Pipe {

    private static final Logger logger = LogManager.getLogger(FindUrlInStringBufferPipe.class);

    /**
     * List of the URL Patterns to use
     */
    private static List<Pattern> URLPatterns;

    /**
     * Pattern for URLs
     */
                                                     //regex used: (?:\s|["><¡?¿!;:,.'(]|^)((?:(?:[[:alnum:]]+:(?:\/{1,2}))|\/{0,2}www\.)(?:[\w-]+(?:(?:\.[\w-]+)*))(?:(?:[\w~?=-][.;,@?^=%&:\/~+#-]?)*)[\w@?^=%&\/~+#,;!:<\\"?\-]?(?=(?:[<\\,;!"?)]|\s|$)))
        private static final Pattern URLPattern = Pattern.compile("(?:\\s|[\"><¡?¿!;:,.'\\(]|^)((?:(?:[\\p{Alnum}]+:(?:\\/{1,2}))|\\/{0,2}www\\.)(?:[\\w-]+(?:(?:\\.[\\w-]+)*))(?:(?:[\\w~?=-][.;,@?^=%&:\\/~+#-]?)*)[\\w@?^=%&\\/~+#,;!:<\\\\\"?-]?(?=(?:[<\\\\,;!\"?\\)]|\\s|$)))", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for e-mail addresses
     */
    
                                                  // regex used: (?:\s|["><¡?¿!;:,.'(]|^)((?:[\w_.çñ+-]+)(?:@|\(at\)|<at>)(?:(?:\w[\\.:ñ-]?)*)[[:alnum:]ñ](?:\.[A-Z]{2,4}))[;:?"!,.'>)]?(?=(?:\s|$|>))
    private static final Pattern emailPattern = Pattern.compile("(?:\\s|[\"><¡?¿!;:,.'\\(]|^)((?:[\\w_.çñ+-]+)(?:@|\\(at\\)|<at>)(?:(?:\\w[\\\\.:ñ-]?)*)[\\p{Alnum}ñ](?:\\.[A-Z]{2,4}))[;:?\"!,.'>\\)]?(?=(?:\\s|$|>))", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Return the input type included the data attribute of a Instance
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
     * processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates if URLs should be removed
     */
    private boolean removeURL;

    /**
     * The default value for removing @userName
     */
    public static final String DEFAULT_REMOVE_URL = "no";

    /**
     * The default property name to store @userName
     */
    public static final String DEFAULT_URL_PROPERTY = "URLs";

    /**
     * The property name to store @userName
     */
    private String URLProp = DEFAULT_URL_PROPERTY;

    /**
     * Indicates if URL should be removed
     *
     * @param removeURL True if URL should be removed
     */
    @PipeParameter(name = "removeUrl", description = "Indicates if URL should be removed or not", defaultValue = DEFAULT_REMOVE_URL)
    public void setRemoveURL(String removeURL) {
        this.removeURL = EBoolean.parseBoolean(removeURL);
    }

    /**
     * Indicates if URL should be removed
     *
     * @param removeURL True if URL should be removed
     */
    public void setRemoveURL(boolean removeURL) {
        this.removeURL = removeURL;
    }

    /**
     * Checks whether URL should be removed from data
     *
     * @return True if URL should be removed
     */
    public boolean getRemoveURL() {
        return this.removeURL;
    }

    /**
     * Sets the property where URL will be stored
     *
     * @param URLProp the name of the property for URLs
     */
    @PipeParameter(name = "URLpropname", description = "Indicates the property name to store URL", defaultValue = DEFAULT_URL_PROPERTY)
    public void setURLProp(String URLProp) {
        this.URLProp = URLProp;
    }

    /**
     * Will return true if s contains URL.
     *
     * @param s String to test
     * @return true if string contains URLs
     */
    public static boolean isURL(StringBuffer s) {
        boolean ret = false;
        if (s != null) {
            Iterator<Pattern> it_p = URLPatterns.iterator();
            while (!ret && it_p.hasNext()) {
                ret = it_p.next().matcher(s).find();
            }
        }
        return ret;
    }

    /**
     * Construct a StripUserNameFromStringBufferPipe instance
     */
    public FindUrlInStringBufferPipe() {
        this(DEFAULT_URL_PROPERTY, true);
    }

    /**
     * Build a FindUrlInStringBufferPipe that stores URLs of the StringBuffer in
     * the property URLProp
     *
     * @param URLProp The name of the property to store @userName
     * @param removeURL tells if URL should be removed
     */

    public FindUrlInStringBufferPipe(String URLProp, boolean removeURL) {
        super(new Class<?>[0],new Class<?>[]{FindUserNameInStringBufferPipe.class});

        this.URLProp = URLProp;
        this.removeURL = removeURL;
        URLPatterns = new LinkedList<>();
        URLPatterns.add(emailPattern);
        URLPatterns.add(URLPattern);
    }

    /**
     * Process an Instance. This method takes an input Instance, modifies it
     * removing URL, and returns it. This is the method by which all pipes are
     * eventually run.
     *
     * @param carrier Instance to be processed.
     * @return processed Instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (carrier.getData() instanceof StringBuffer) {

            StringBuffer sb = (StringBuffer) carrier.getData();
            String value = "";

            if (isURL(sb)) {

                for (Pattern URLPat : URLPatterns) {
                    Matcher m = URLPat.matcher(sb);
                    int last = 0;
                    
                    while (m.find(last)) {
                        value += m.group(1) + " ";
                        last=removeURL?m.start(1):m.end(1);
                        
                        if (removeURL) {
                            sb.replace(m.start(1),m.end(1),"");
                        }
                    }
                }    
            } else {
                logger.info("URL not found for instance " + carrier.toString());
            }
            carrier.setProperty(URLProp, value);
        }else{
          logger.error("Data should be an StrinBuffer when processing "+carrier.getName()+" but is a "+carrier.getData().getClass().getName());
        }
        return carrier;
    }
}
