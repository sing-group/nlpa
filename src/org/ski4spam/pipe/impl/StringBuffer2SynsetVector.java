package org.ski4spam.pipe.impl;

import org.ski4spam.pipe.Pipe;
import org.ski4spam.pipe.TransformationPipe;
import org.ski4spam.ia.types.Instance;
import org.ski4spam.ia.types.SynsetVector;

@TransformationPipe(inputType="StringBuffer", outputType="SynsetVector")
public class StringBuffer2SynsetVector extends Pipe {
	public Instance pipe(Instance carrier){
		SynsetVector sv=new SynsetVector((StringBuffer)carrier.getData());
		
		
		return carrier;
	}
}