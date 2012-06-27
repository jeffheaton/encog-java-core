package org.encog.ensemble;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;

public interface EnsembleTrainFactory {

	public Train getTraining(MLMethod method, MLDataSet trainingData);

}
