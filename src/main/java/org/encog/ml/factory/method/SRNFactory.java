package org.encog.ml.factory.method;

import java.util.List;

import org.encog.EncogError;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.ml.MLMethod;
import org.encog.ml.factory.parse.ArchitectureLayer;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.rbf.RBFNetwork;

public class SRNFactory {
	public MLMethod create(String architecture, int input, int output) {
		
		List<String> layers = ArchitectureParse.parseLayers(architecture);
		if( layers.size()!=3) {
			throw new EncogError("SRN Networks must have exactly three elements, separated by ->.");
		}
		
		ArchitectureLayer inputLayer = ArchitectureParse.parseLayer(layers.get(0),input);
		ArchitectureLayer rbfLayer = ArchitectureParse.parseLayer(layers.get(1),-1);
		ArchitectureLayer outputLayer = ArchitectureParse.parseLayer(layers.get(2),output);
		
		int inputCount = inputLayer.getCount();
		int outputCount = outputLayer.getCount();

		RBFEnum t;
		
		if( rbfLayer.getName().equalsIgnoreCase("Gaussian"))
			t = RBFEnum.Gaussian;
		else if( rbfLayer.getName().equalsIgnoreCase("Multiquadric"))
			t = RBFEnum.Multiquadric;
		else if( rbfLayer.getName().equalsIgnoreCase("InverseMultiquadric"))
			t = RBFEnum.InverseMultiquadric;
		else if( rbfLayer.getName().equalsIgnoreCase("MexicanHat"))
			t = RBFEnum.MexicanHat;
		else 
			t = RBFEnum.Gaussian;
		
		RBFNetwork result = new RBFNetwork(inputCount,rbfLayer.getCount(),outputCount,t);
		
		return result;
	}
}
