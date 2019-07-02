package org.nlpa.types;

import java.io.Serializable;
import java.util.Hashtable;

//import es.uvigo.ei.ia.types.Instance;
//import es.uvigo.ei.ia.types.Token;
//import es.uvigo.ei.ia.types.TokenSequence;
//import org.pipe.AbstractPipe;

/**
 * Stemmer de t�rminos irregulares gen�rico y abstracto
 * @author Jos� Ram�n M�ndez Reboredo
 * @since JDK 1.5
 */
public abstract class TokenSequenceStemIregular /*extends AbstractPipe implements Serializable*/{
	
//	/**
//	 * Constructor por defecto de la clase abstracta
//	 */
//	public TokenSequenceStemIregular(){
//	    loadData();
//	}
//
//	@Override
//	public Instance pipe(Instance carrier) {
//		
//		TokenSequence ts = (TokenSequence)carrier.getData();
//		
//		TokenSequence ret = new TokenSequence();
//		
//		for (int i = 0; i < ts.size(); i++) {
//			Token token=ts.getToken(i);
//			
//			
//			//Si el token es irregular se cambia el texto
//			String changeTxt=null;
//			if ((changeTxt=irregularWords.get(token.getText()))!=null)
//				token.setText(changeTxt);
//			
//			ret.add(token);
//		}
//		
//		carrier.setData(ret);
//		
//		return carrier;
//	}
//	
//	protected Hashtable<String,String> irregularWords = new Hashtable<String,String>(1500);
//
//	protected abstract void loadData();
}
