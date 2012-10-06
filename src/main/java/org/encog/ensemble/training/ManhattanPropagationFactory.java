package org.encog.ensemble.training;

import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;

public class ManhattanPropagationFactory implements EnsembleTrainFactory {

	private double learningRate = 0.01;

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getLearningRate() {
		return this.learningRate;
	}

	@Override
	public MLTrain getTraining(MLMethod mlMethod, MLDataSet trainingData) {
		return (MLTrain) new ManhattanPropagation((BasicNetwork) mlMethod, trainingData, 0.01);
	}

	@Override
	public String getLabel() {
		return "resprop";
	}

}
