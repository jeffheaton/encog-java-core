package org.encog.ml.factory.train;

import java.util.Map;

import org.encog.ml.MLMethod;
import org.encog.ml.MLTrain;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.factory.parse.ArchitectureParse;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.ParamsHolder;

public class BackPropFactory {
	public MLTrain create(MLMethod method, NeuralDataSet training, String argsStr) {
		
		Map<String, String> args = ArchitectureParse.parseParams(argsStr);
		ParamsHolder holder = new ParamsHolder(args);
		
		double learningRate = holder.getDouble(MLTrainFactory.PROPERTY_LEARNING_RATE, false, 0.7);
		double momentum = holder.getDouble(MLTrainFactory.PROPERTY_LEARNING_MOMENTUM, false, 0.3);
		
		return new Backpropagation((BasicNetwork)method, training, learningRate, momentum);
	}
}
