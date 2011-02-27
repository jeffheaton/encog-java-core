package org.encog.ml.factory.method;

import java.util.List;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureParse;

public class RBFNetworkFactory {
	public MLMethod create(String architecture, int input, int output) {
		
		List<String> layers = ArchitectureParse.parseLayers(architecture);
		if( layers.size()!=3) {
			throw new EncogError("RBF network must have exactly 3 layers.");
		}
		
		
		
		return null;
	}
}
