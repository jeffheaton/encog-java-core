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

import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.RPROPConst;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * A training definition for RPROP training.
 */
public class RPROPJob extends TrainingJob {

	/**
	 * The initial update value.
	 */
	private double initialUpdate = RPROPConst.DEFAULT_INITIAL_UPDATE;

	/**
	 * The maximum step value.
	 */
	private double maxStep = RPROPConst.DEFAULT_MAX_STEP;

	/**
	 * Construct an RPROP job. For more information on RPROP see the
	 * ResilientPropagation class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True if binary training data should be loaded to memory.
	 */
	public RPROPJob(final BasicNetwork network, final MLDataSet training,
			final boolean loadToMemory) {
		super(network, training, loadToMemory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void createTrainer(final boolean singleThreaded) {
		final Propagation train = new ResilientPropagation(getNetwork(),
				getTraining(), getInitialUpdate(), getMaxStep());

		if (singleThreaded) {
			train.setThreadCount(1);
		} else {
			train.setThreadCount(0);
		}

		for (final Strategy strategy : getStrategies()) {
			train.addStrategy(strategy);
		}

		setTrain(train);
	}

	/**
	 * @return the initialUpdate
	 */
	public final double getInitialUpdate() {
		return this.initialUpdate;
	}

	/**
	 * @return the maxStep
	 */
	public final double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * @param initialUpdate
	 *            the initialUpdate to set
	 */
	public final void setInitialUpdate(final double initialUpdate) {
		this.initialUpdate = initialUpdate;
	}

	/**
	 * @param maxStep
	 *            the maxStep to set
	 */
	public final void setMaxStep(final double maxStep) {
		this.maxStep = maxStep;
	}

}
