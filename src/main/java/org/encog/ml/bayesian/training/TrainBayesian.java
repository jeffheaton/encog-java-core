package org.encog.ml.bayesian.training;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.bayesian.BayesianEvent;
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
	private BayesianInit initNetwork = BayesianInit.InitNaiveBayes;

	public TrainBayesian(BayesianNetwork theNetwork, MLDataSet theData, int theMaximumParents) {
		super(TrainingImplementationType.Iterative);
		this.network = theNetwork;
		this.data = theData;
		this.maximumParents = theMaximumParents;		
		
		this.search = new SearchK2();
		this.search.init(this, theNetwork, theData);
		
		this.estimator = new SimpleEstimator();
		this.estimator.init(this, theNetwork, theData);
	}
	
	private void initNaiveBayes() {
		// clear out anything from before
		this.network.removeAllRelations();
		
		// locate the classification target event
		BayesianEvent classificationTarget = this.network.getClassificationTargetEvent();
		
		// now link everything to this event
		for(BayesianEvent event: this.network.getEvents() ) {
			if( event!=classificationTarget ) {
				network.createDependancy(classificationTarget, event);
			}
		}
		
	}
		

	@Override
	public void iteration() {
		switch(this.initNetwork) {
			case InitEmpty:
				this.network.removeAllRelations();
				break;
			case InitNoChange:
				break;
			case InitNaiveBayes:
				initNaiveBayes();
				break;
		}
		
		
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


	public BayesianInit getInitNetwork() {
		return initNetwork;
	}


	public void setInitNetwork(BayesianInit initNetwork) {
		this.initNetwork = initNetwork;
	}
	
	
}
