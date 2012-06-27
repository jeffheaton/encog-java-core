package org.encog.ensemble.training.backpropagation;

import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.scg.ScaledConjugateGradient;

public class ScaledConjugateGradientFactory implements EnsembleTrainFactory {

	@Override
	public Train getTraining(MLMethod mlMethod, MLDataSet trainingData) {
		return (Train) new ScaledConjugateGradient((BasicNetwork) mlMethod, trainingData);
	}

}
