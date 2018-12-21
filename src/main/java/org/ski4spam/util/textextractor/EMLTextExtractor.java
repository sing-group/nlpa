package org.ski4spam.util.textextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.util.Pair;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.ski4spam.util.Configuration;

/**
 * A TextExtractor used to extract text from emails represented in its original
 * format (.eml) Files using this TextExtractor should contain the original
 * representation in the format defined by RFC 2822
 * (https://tools.ietf.org/html/rfc2822)
 *
 * @author José Ramón Méndez
 */
public class EMLTextExtractor extends TextExtractor {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(EMLTextExtractor.class);

    /**
     * Pattern to find the charset from ContentType headers
     */
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

    /**
     * The part to parse on Multipart/Alternative parts or messages
     */
    private static String cfgPartSelectedOnAlternative = "text/plain";

    static { //Load EML configuration
        cfgPartSelectedOnAlternative = Configuration.getSystemConfig().getConfigOption("eml", "PartSelectedOnMPAlternative");
    }

    /**
     * Gets the part to parse on Multipart/Alternative parts or messages
     *
     * @return The part to parse on Multipart/Alternative parts or messages
     * (text/plain or text/html)
     */
    public static String getCfgPartSelectedOnAlternative() {
        return cfgPartSelectedOnAlternative;
    }

    /**
     * Sets the part to parse on Multipart/Alternative parts or messages
     *
     * @param cfg The part to parse on Multipart/Alternative parts or messages
     * (text/plain or text/html)
     */
    public static void setCfgPartSelectedOnAlternative(String cfg) {
        cfgPartSelectedOnAlternative = cfg;
    }

    /**
     * A static instance of the TexTextractor to implement a singleton pattern
     */
    static TextExtractor instance = null;

    /**
     * Private default constructor
     */
    private EMLTextExtractor() {

    }

    /**
     * Retrieve the extensions that can process this TextExtractor
     *
     * @return An array of Strings containing the extensions of files that this
     * TextExtractor can handle
     */
    public static String[] getExtensions() {
        return new String[]{"eml"};
    }

    /**
     * Return an instance of this TextExtractor
     *
     * @return an instance of this TextExtractor
     */
    public static TextExtractor getInstance() {
        if (instance == null) {
            instance = new EMLTextExtractor();
        }
        return instance;
    }

    /**
     * Reconstruye los adjuntos
     *
     * @param partlist Salida: lista de partes del correo electr�nica dadas en
     * forma de <Tipo de parte> <parte>
     * @param mp Objeto multipart
     * @throws Exception Excepcion que se puede producir al procesar
     */
    private void buildPartInfoList(ArrayList<Pair<String, InputStream>> partlist, Multipart mp) throws Exception {
        if (mp.getContentType().indexOf("multipart/alternative") > -1) {
            for (int j = 0; j < mp.getCount(); j++) {
                Part bpart = mp.getBodyPart(j);
                if (bpart.isMimeType(cfgPartSelectedOnAlternative)) {
                    partlist.add(new Pair<String, InputStream>(bpart.getContentType(), bpart.getInputStream()));
                }
            }
        } else if (mp.getContentType().indexOf("multipart/signed") > -1) {
            for (int j = 0; j < mp.getCount(); j++) {
                Part bpart = mp.getBodyPart(j);
                if (!bpart.isMimeType("application/pgp-signature")) {
                    partlist.add(new Pair<String, InputStream>(bpart.getContentType(), bpart.getInputStream()));
                }
            }
        } else if (mp.getContentType().indexOf("multipart/mixed") > -1) {
            for (int i = 0; i < mp.getCount(); i++) {
                //Get part
                Part apart = mp.getBodyPart(i);
                //handle single & multiparts			
                if (apart.getContentType().indexOf("multipart/") >= 0) {
                    buildPartInfoList(partlist, (Multipart) apart.getContent());
                } else {
                    partlist.add(new Pair<String, InputStream>(apart.getContentType(), apart.getInputStream()));
                }
            }
        }
    }//buildPartList	

    /**
     * Determines the charset from the ContentType header
     *
     * @param contentType the ContentType header
     * @return A string containing the charset of the text
     */
    private String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }

        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            return m.group(1).trim().toUpperCase();
        }
        return null;
    }

    /**
     * Estact text for a file (which is written in eml format)
     *
     * @param f The file where the contents will be extracted
     * @return an StringBuffer with text contents of message
     */
    @Override
    public StringBuffer extractText(File f) {
        StringBuffer sbResult = new StringBuffer();
        ArrayList<Pair<String, InputStream>> parts = new ArrayList<Pair<String, InputStream>>();

        try {
            //Create a mime message
            MimeMessage mimeMultipart = new MimeMessage(null, new FileInputStream(f));

            if (mimeMultipart.getSubject() != null) {
                sbResult.append(mimeMultipart.getSubject() + "\n");
            } else {
                return null;
            }

            //If it is not multipart, anotate the part to handle it later
            if (mimeMultipart.getContentType().indexOf("multipart/") == -1) {
                parts.add(new Pair<String, InputStream>(mimeMultipart.getContentType(), mimeMultipart.getInputStream()));
            } //If multipart, then recursivelly compile parts to handle them later
            else {
                buildPartInfoList(parts, (Multipart) mimeMultipart.getContent());
            }

            //Transform each compiled part
            for (Pair<String, InputStream> i : parts) {
                String contentType = i.getObj1();
                if (contentType.toLowerCase().indexOf("text/") == 0) {
                    InputStream is = null;

                    try {
                        if (i.getObj2() instanceof InputStream) {
                            is = (InputStream) i.getObj2();

                            byte contents[] = new byte[is.available()];
                            is.read(contents);

                            String charsetName = getCharsetFromContentType(contentType);
                            if (charsetName != null) {
                                logger.info("charset found in content-type: " + charsetName);
                                sbResult.append(new String(contents, Charset.forName(charsetName)));
                            } else {
                                CharsetDetector detector = new CharsetDetector();
                                detector.setText(contents);
                                CharsetMatch cm = detector.detect();
                                logger.warn("Charset guesed: " + cm.getName() + " [confidence=" + cm.getConfidence() + "/100]for " + f.getAbsolutePath() + " Content type: " + contentType);
                                sbResult.append(new String(contents, Charset.forName(cm.getName())));
                            }

                            is.close();
                        }
                    } catch (IOException e) {
                        System.out.println("Error while processing " + f.getAbsolutePath());
                        System.out.println(e.getMessage());
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }

        } catch (MessagingException e) {
            logger.error("Messagging Exception caught / " + e.getMessage() + "Current e-mail: " + f.getAbsolutePath());
            return null;
        } catch (IOException e) {
            logger.error("IO Exception caught / " + e.getMessage() + "Current e-mail: " + f.getAbsolutePath());
            return null;
        } catch (Exception e) {
            logger.error("Exception caught / " + e.getMessage() + "Current e-mail: " + f.getAbsolutePath());
            return null;
        }

        //System.out.println(sbResult.toString());
        return sbResult;
    }
}
