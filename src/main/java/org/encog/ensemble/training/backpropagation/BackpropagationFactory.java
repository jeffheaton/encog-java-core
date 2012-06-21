package org.encog.ensemble.training.backpropagation;

import org.encog.ensemble.EnsembleTrain;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

public class BackpropagationFactory implements EnsembleTrainFactory {

	@Override
	public EnsembleTrain getTraining(MLMethod mlMethod, MLDataSet trainingData) {
		return (EnsembleTrain) new Backpropagation((ContainsFlat) mlMethod, trainingData);
	}

}
