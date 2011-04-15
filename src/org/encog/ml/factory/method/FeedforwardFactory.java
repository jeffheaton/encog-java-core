package org.encog.ml.factory.method;

import java.util.List;

import org.encog.app.analyst.AnalystError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

public class FeedforwardFactory {

	public MLMethod create(String architecture, int input, int output) {
		BasicNetwork result = new BasicNetwork();
		List<String> layers = ArchitectureParse.parseLayers(architecture);
		ActivationFunction activation = new ActivationTANH();
		
		int questionPhase = 0;
		for(String layerStr: layers) {
			int defaultCount;
			// determine default
			if( questionPhase==0 ) {
				defaultCount = input;
			} else {
				defaultCount = output;
			}
			
			ArchitectureLayer layer = ArchitectureParse.parseLayer(layerStr,defaultCount);
			boolean bias = layer.isBias();
			
			String part = layer.getName();
			
			if( "tanh".equalsIgnoreCase(part) ) {
				activation = new ActivationTANH();
			} else if( "linear".equalsIgnoreCase(part) ) {
				activation = new ActivationLinear();
			} else if( "sigmoid".equalsIgnoreCase(part)) {
				activation = new ActivationSigmoid();
			} else {
				try {
					if( layer.isUsedDefault() ) {
						questionPhase++;
						if( questionPhase>2 ) {
							throw new AnalystError("Only two ?'s may be used.");
						}
					}
					Layer layer2 = new BasicLayer(activation,bias,layer.getCount());
					result.addLayer(layer2);
				}
				catch(NumberFormatException ex) {
					throw new AnalystError("Unknown architecture: " + activation + ", can't parse: " + part);
				}
			}
		}
				
		result.getStructure().finalizeStructure();
		result.reset();
			
		return result;
	}

}
