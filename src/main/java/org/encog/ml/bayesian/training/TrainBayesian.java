package org.encog.ml.bayesian.training;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.bayesian.BayesianEvent;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.bayesian.training.estimator.BayesEstimator;
import org.encog.ml.bayesian.training.estimator.SimpleEstimator;
import org.encog.ml.bayesian.training.search.k2.BayesSearch;
import org.encog.ml.bayesian.training.search.k2.SearchK2;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.ml.train.strategy.Strategy;
import org.encog.ml.train.strategy.end.EndTrainingStrategy;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

public class TrainBayesian extends BasicTraining {
	
	private enum Phase {
		Init,
		Search,
		SearchDone,
		Probability,
		Finish,
		Terminated
	};
	
	private Phase p = Phase.Init;
	
	private final MLDataSet data;
	private final BayesianNetwork network;
	private final int maximumParents;
	private final BayesSearch search;
	private final BayesEstimator estimator;
	private BayesianInit initNetwork = BayesianInit.InitNaiveBayes;
	private String holdQuery;

	public TrainBayesian(BayesianNetwork theNetwork, 
			MLDataSet theData, 
			int theMaximumParents) {
		this(theNetwork, theData, theMaximumParents, BayesianInit.InitNaiveBayes, 
				new SearchK2(), new SimpleEstimator());
	}
	
	public TrainBayesian(BayesianNetwork theNetwork, 
			MLDataSet theData, 
			int theMaximumParents,
			BayesianInit theInit,
			BayesSearch theSearch,
			BayesEstimator theEstimator) {
		super(TrainingImplementationType.Iterative);
		this.network = theNetwork;
		this.data = theData;
		this.maximumParents = theMaximumParents;		
		
		this.search = theSearch;
		this.search.init(this, theNetwork, theData);
		
		this.estimator = theEstimator;
		this.estimator.init(this, theNetwork, theData);
		
		this.initNetwork = theInit;
		setError(1.0);
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
	
	private void iterationInit() {
		this.holdQuery = this.network.getClassificationStructure();
		
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
		this.p = Phase.Search;
	}
	
	private void iterationSearch() {
		if( !this.search.iteration() ) {
			this.p = Phase.SearchDone;
		}
	}
	
	private void iterationSearchDone() {
		this.network.finalizeStructure();
		this.network.reset();
		this.p = Phase.Probability;
	}
	
	private void iterationProbability() {		
		if(!this.estimator.iteration()) {
			this.p = Phase.Finish;
		}
	}
	
	private void iterationFinish() {
		this.network.defineClassificationStructure(this.holdQuery);		
		setError(this.network.calculateError(this.data));
		this.p = Phase.Terminated;
	}
	
	/**
	 * @return True if training can progress no further.
	 */
	@Override
	public boolean isTrainingDone() {
		if( super.isTrainingDone() )
			return true;
		else
			return this.p==Phase.Terminated;		
	}
		

	@Override
	public void iteration() {
		switch(p) {
			case Init:
				iterationInit();
				break;
			case Search:
				iterationSearch();
				break;
			case SearchDone:
				iterationSearchDone();
				break;
			case Probability:
				iterationProbability();
				break;
			case Finish:
				iterationFinish();
				break;
		}

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
