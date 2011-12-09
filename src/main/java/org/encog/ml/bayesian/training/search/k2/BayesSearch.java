package org.encog.ml.bayesian.training.search.k2;

import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.TrainBayesian;
import org.encog.ml.data.MLDataSet;

public interface BayesSearch {
	void init(TrainBayesian theTrainer, BayesianNetwork theNetwork, MLDataSet theData);
	void iteration();
}
