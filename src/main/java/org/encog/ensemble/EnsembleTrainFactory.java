package org.encog.ensemble;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;

public interface EnsembleTrainFactory {

	public MLTrain getTraining(MLMethod methos, MLDataSet trainingData);

}
