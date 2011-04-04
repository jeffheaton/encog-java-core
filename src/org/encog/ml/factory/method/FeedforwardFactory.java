package org.encog.ml.factory.method;

import org.encog.app.analyst.AnalystError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

public class FeedforwardFactory {

	public MLMethod create(String a, int input, int output) {
		BasicNetwork result = new BasicNetwork();
		boolean done = false;
		int base = 0;
		
		int questionPhase = 0;
		String line = a.toLowerCase();
		ActivationFunction activation = null;
		
		do {
			String part;
			int index = line.indexOf("->",base );
			if( index!=-1 ) {
				part = line.substring(base,index).trim();
				base = index+2;
			} else {
				part = line.substring(base).trim();
				done = true;
			}
			
			boolean bias = part.endsWith("b");
			if( bias ) {
				part = part.substring(0, part.length()-1);
			}
			
			if (part.equals("?")) {
				switch (questionPhase) {
					case 0:
						part = ""+input;
						break;
					case 1:
						part = ""+output;
						break;
					case 2:
						throw new AnalystError("At most two ?'s may be defined. " + a);
				}
				questionPhase++;
			}
			
			if( part.equals("tanh")) {
				activation = new ActivationTANH();
			} else if( part.equals("linear")) {
				activation = new ActivationLinear();
			} else if( part.equals("sigmoid")) {
				activation = new ActivationSigmoid();
			} else {
				try {
					Integer i = Integer.parseInt(part);
					Layer layer = new BasicLayer(activation,bias,i);
					result.addLayer(layer);
				}
				catch(NumberFormatException ex) {
					throw new AnalystError("Unknown architecture: " + a + ", can't parse: " + part);
				}
			}
		} while(!done);
		
		result.getStructure().finalizeStructure();
		result.reset();
			
		return result;
	}

}
