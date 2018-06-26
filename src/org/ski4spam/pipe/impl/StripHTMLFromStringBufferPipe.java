package org.ski4spam.pipe.impl;

import org.ski4spam.ia.types.Instance;
import org.ski4spam.pipe.Pipe;

import java.util.regex.Pattern;

import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;




/**
 * This pipe drops HTML tags and changes entities by their corresponding character
 * @author José Ramón Méndez Reboredo
 */
public class StripHTMLFromStringBufferPipe extends Pipe {
	private static final Logger logger = LogManager.getLogger(Html2Text.class);
	
    // adapted from post by Phil Haack and modified to match better
    public final static String tagStart=
        "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
    public final static String tagEnd=
        "\\</\\w+\\>";
    public final static String tagSelfClosing=
        "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
    public final static String htmlEntity=
        "&[a-zA-Z][a-zA-Z0-9]+;";
    public final static Pattern htmlPattern=Pattern.compile(
      "("+tagStart+".*"+tagEnd+")|("+tagSelfClosing+")|("+htmlEntity+")",
      Pattern.DOTALL
    );

    /**
     * Will return true if s contains HTML markup tags or entities.
     *
     * @param s String to test
     * @return true if string contains HTML
     */
    public static boolean isHtml(String s) {
        boolean ret=false;
        if (s != null) {
            ret=htmlPattern.matcher(s).find();
        }
        return ret;
    }

    public StripHTMLFromStringBufferPipe() {
    }


    @Override
    public Instance pipe(Instance carrier) {
        if ( carrier.getData() instanceof StringBuffer){
			 StringBuffer newSb=new StringBuffer();

			String data=carrier.getData().toString();
			if (isHtml(data)){
				    Document doc=Jsoup.parse(data);
					//doc.outputSettings().prettyPrint(false);
					doc.charset(Charset.forName("UTF-16"));
					
					String title;
					if ((title=doc.title())!=null && title.length()>0) newSb.append(title+"\n\n");
					
					Elements elements = doc.getAllElements();
					for(Element element : elements) {
					  for(TextNode node : element.textNodes()){
						  System.out.print(" -*- ");
					    newSb.append( node + "\n" );
					  }
				    }
				    //newSb.append(doc.text());

				    carrier.setData(newSb);

     		} else
				logger.info("HTML not found for instance "+carrier.toString());
			
			
				 
			System.out.println("HTML +++ "+ carrier.getData());
		}

        return carrier;
    }
}

