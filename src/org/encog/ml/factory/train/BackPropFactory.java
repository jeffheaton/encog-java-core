package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

public class BackPropFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		
		double learningRate = 0.7; 
		double momentum = 0.7;
		try {
			learningRate = Double.parseDouble(args.get("LEARNINGRATE"));
			momentum = Double.parseDouble(args.get("MOMENTUM"));
		} catch(NumberFormatException ex) {
			throw new AnalystError("Invalid momentum or learning rate.");
		}
		
		return new Backpropagation((BasicNetwork)method, training, learningRate, momentum);
	}
}
