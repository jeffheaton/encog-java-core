package org.encog.ml.bayesian.training;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.estimator.SimpleEstimator;
import org.encog.ml.bayesian.training.search.k2.BayesSearch;
import org.encog.ml.bayesian.training.search.k2.SearchK2;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class TrainBayesian extends BasicTraining {
	private final MLDataSet data;
	private final BayesianNetwork network;
	private final int maximumParents;
	private final BayesSearch search;
	private final SimpleEstimator estimator;

	public TrainBayesian(BayesianNetwork theNetwork, MLDataSet theData, int theMaximumParents) {
		super(TrainingImplementationType.Iterative);
		this.network = theNetwork;
		this.data = theData;
		this.maximumParents = theMaximumParents;
		this.network.removeAllRelations();
		
		this.search = new SearchK2();
		this.search.init(this, theNetwork, theData);
		
		this.estimator = new SimpleEstimator();
		this.estimator.init(this, theNetwork, theData);
	}
		

	@Override
	public void iteration() {
		this.search.iteration();
		this.network.finalizeStructure();
		this.network.reset();
		this.estimator.iteration();
	}
	


	@Override
	public boolean canContinue() {
		return false;
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {

	}

	@Override
	public MLMethod getMethod() {
		return this.network;
	}
	

	/**
	 * @return the network
	 */
	public BayesianNetwork getNetwork() {
		return network;
	}

	/**
	 * @return the maximumParents
	 */
	public int getMaximumParents() {
		return maximumParents;
	}



	public BayesSearch getSearch() {
		return this.search;
	}
}
