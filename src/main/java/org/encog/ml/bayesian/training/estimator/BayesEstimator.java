package org.encog.ml.bayesian.training.estimator;

import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.data.MLDataSet;

public interface BayesEstimator {
	void init(TrainBayesian theTrainer ,BayesianNetwork theNetwork, MLDataSet theData);	
	void iteration();
}
