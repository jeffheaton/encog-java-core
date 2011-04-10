package org.encog.ml.factory.method;

import java.util.List;

import org.encog.EncogError;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.pattern.SOMPattern;

public class SOMFactory {
	public MLMethod create(String architecture, int input, int output) {
		
		List<String> layers = ArchitectureParse.parseLayers(architecture);
		if( layers.size()!=2) {
			throw new EncogError("SOM's must have exactly two elements, separated by ->.");
		}
		
		ArchitectureLayer inputLayer = ArchitectureParse.parseLayer(layers.get(0),input);		
		ArchitectureLayer outputLayer = ArchitectureParse.parseLayer(layers.get(1),output);
		
		int inputCount = inputLayer.getCount();
		int outputCount = outputLayer.getCount();
		
		SOMPattern pattern = new SOMPattern();
		pattern.setInputNeurons(inputCount);
		pattern.setOutputNeurons(outputCount);
		return pattern.generate();
	}
}
