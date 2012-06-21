package org.encog.ensemble;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataSet;

public interface EnsembleTrainFactory {

	public EnsembleTrain getTraining(MLMethod methos, MLDataSet trainingData);

}
