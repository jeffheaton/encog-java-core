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
package org.encog.neural.networks.training.concurrent.jobs;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.ml.train.strategy.end.EndTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;

/**
 * Base class for all concurrent training jobs.
 */
public abstract class TrainingJob {

	/**
	 * The network to train.
	 */
	private BasicNetwork network;

	/**
	 * The training data to use.
	 */
	private MLDataSet training;

	/**
	 * The strategies to use.
	 */
	private final List<Strategy> strategies = new ArrayList<Strategy>();

	/**
	 * True, if binary training data should be loaded to memory.
	 */
	private boolean loadToMemory;

	/**
	 * The trainer being used.
	 */
	private MLTrain train;

	/**
	 * Holds any errors that occur during training.
	 */
	private Throwable error;

	/**
	 * Construct a training job.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True, if binary data should be loaded to memory.
	 */
	public TrainingJob(final BasicNetwork network, final MLDataSet training,
			final boolean loadToMemory) {
		super();
		this.network = network;
		this.training = training;
		this.loadToMemory = loadToMemory;
	}

	/**
	 * Create a trainer to use.
	 */
	public abstract void createTrainer(boolean singleThreaded);

	/**
	 * @return the error
	 */
	public final Throwable getError() {
		return this.error;
	}

	/**
	 * @return the network
	 */
	public final BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return the strategies
	 */
	public final List<Strategy> getStrategies() {
		return this.strategies;
	}

	/**
	 * @return the train
	 */
	public final MLTrain getTrain() {
		return this.train;
	}

	/**
	 * @return the training
	 */
	public final MLDataSet getTraining() {
		return this.training;
	}

	/**
	 * @return the loadToMemory
	 */
	public final boolean isLoadToMemory() {
		return this.loadToMemory;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public final void setError(final Throwable error) {
		this.error = error;
	}

	/**
	 * @param loadToMemory
	 *            the loadToMemory to set
	 */
	public final void setLoadToMemory(final boolean loadToMemory) {
		this.loadToMemory = loadToMemory;
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public final void setNetwork(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * @param train
	 *            the train to set
	 */
	public final void setTrain(final MLTrain train) {
		this.train = train;
	}

	/**
	 * @param training
	 *            the training to set
	 */
	public final void setTraining(final MLDataSet training) {
		this.training = training;
	}

	/**
	 * @return True, if training should continue.
	 */
	public final boolean shouldContinue() {
		for (final Strategy strategy : this.train.getStrategies()) {
			if (strategy instanceof EndTrainingStrategy) {
				final EndTrainingStrategy end = (EndTrainingStrategy) strategy;

				if (end.shouldStop()) {
					return false;
				}
			}
		}
		return true;
	}
}
