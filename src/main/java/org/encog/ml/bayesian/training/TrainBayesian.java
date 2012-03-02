/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
import org.encog.neural.networks.training.propagation.TrainingContinuation;

/**
 * Train a Bayesian network.
 */
public class TrainBayesian extends BasicTraining {

	/**
	 * What phase of training are we in?
	 */
	private enum Phase {
		/**
		 * Init phase.
		 */
		Init,
		/**
		 * Searching for a network structure.
		 */
		Search,
		/**
		 * Search complete.
		 */
		SearchDone,
		/**
		 * Finding probabilities.
		 */
		Probability,
		/**
		 * Finished training.
		 */
		Finish,
		/**
		 * Training terminated.
		 */
		Terminated
	};

	/**
	 * The phase that training is currently in.
	 */
	private Phase p = Phase.Init;

	/**
	 * The data used for training.
	 */
	private final MLDataSet data;

	/**
	 * The network to train.
	 */
	private final BayesianNetwork network;

	/**
	 * The maximum parents a node should have.
	 */
	private final int maximumParents;

	/**
	 * The method used to search for the best network structure.
	 */
	private final BayesSearch search;

	/**
	 * The method used to estimate the probabilities.
	 */
	private final BayesEstimator estimator;

	/**
	 * The method used to setup the initial Bayesian network.
	 */
	private BayesianInit initNetwork = BayesianInit.InitNaiveBayes;

	/**
	 * Used to hold the query.
	 */
	private String holdQuery;

	/**
	 * Construct a Bayesian trainer. Use K2 to search, and the SimpleEstimator
	 * to estimate probability.  Init as Naive Bayes
	 * 
	 * @param theNetwork
	 *            The network to train.
	 * @param theData
	 *            The data to train.
	 * @param theMaximumParents
	 *            The max number of parents.
	 */
	public TrainBayesian(BayesianNetwork theNetwork, MLDataSet theData,
			int theMaximumParents) {
		this(theNetwork, theData, theMaximumParents,
				BayesianInit.InitNaiveBayes, new SearchK2(),
				new SimpleEstimator());
	}

	/**
	 * Construct a Bayesian trainer.
	 * @param theNetwork The network to train.
	 * @param theData The data to train with.
	 * @param theMaximumParents The maximum number of parents.
	 * @param theInit How to init the new Bayes network.
	 * @param theSearch The search method.
	 * @param theEstimator The estimation mehod.
	 */
	public TrainBayesian(BayesianNetwork theNetwork, MLDataSet theData,
			int theMaximumParents, BayesianInit theInit, BayesSearch theSearch,
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

	/**
	 * Init to Naive Bayes.
	 */
	private void initNaiveBayes() {
		// clear out anything from before
		this.network.removeAllRelations();

		// locate the classification target event
		BayesianEvent classificationTarget = this.network
				.getClassificationTargetEvent();

		// now link everything to this event
		for (BayesianEvent event : this.network.getEvents()) {
			if (event != classificationTarget) {
				network.createDependency(classificationTarget, event);
			}
		}
		this.network.finalizeStructure();

	}

	/**
	 * Handle iterations for the Init phase.
	 */
	private void iterationInit() {
		this.holdQuery = this.network.getClassificationStructure();

		switch (this.initNetwork) {
		case InitEmpty:
			this.network.removeAllRelations();
			this.network.finalizeStructure();
			break;
		case InitNoChange:
			break;
		case InitNaiveBayes:
			initNaiveBayes();
			break;
		}
		this.p = Phase.Search;
	}

	/**
	 * Handle iterations for the Search phase.
	 */
	private void iterationSearch() {
		if (!this.search.iteration()) {
			this.p = Phase.SearchDone;
		}
	}

	/**
	 * Handle iterations for the Search Done phase.
	 */
	private void iterationSearchDone() {
		this.network.finalizeStructure();
		this.network.reset();
		this.p = Phase.Probability;
	}

	/**
	 * Handle iterations for the Probability phase.
	 */
	private void iterationProbability() {
		if (!this.estimator.iteration()) {
			this.p = Phase.Finish;
		}
	}

	/**
	 * Handle iterations for the Finish phase.
	 */
	private void iterationFinish() {
		this.network.defineClassificationStructure(this.holdQuery);
		setError(this.network.calculateError(this.data));
		this.p = Phase.Terminated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrainingDone() {
		if (super.isTrainingDone())
			return true;
		else
			return this.p == Phase.Terminated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iteration() {
		
		postIteration();
		
		switch (p) {
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
		
		preIteration();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canContinue() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(TrainingContinuation state) {

	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * @return The search method.
	 */
	public BayesSearch getSearch() {
		return this.search;
	}

	/**
	 * @return The init method.
	 */
	public BayesianInit getInitNetwork() {
		return initNetwork;
	}

	/**
	 * Set the network init method.
	 * @param initNetwork The init method.
	 */
	public void setInitNetwork(BayesianInit initNetwork) {
		this.initNetwork = initNetwork;
	}

}
