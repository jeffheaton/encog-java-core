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
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 * A training definition for BPROP training.
 */
public class BPROPJob extends TrainingJob {

	/**
	 * The learning rate to use.
	 */
	private double learningRate;

	/**
	 * The momentum to use.
	 */
	private double momentum;

	/**
	 * Construct a job definition for RPROP. For more information on backprop,
	 * see the Backpropagation class. Use OpenCLratio of 1.0 and process one
	 * iteration per cycle.
	 * 
	 * @param network
	 *            The network to use.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            Should binary data be loaded to memory?
	 * @param learningRate
	 *            THe learning rate to use.
	 * @param momentum
	 *            The momentum to use.
	 */
	public BPROPJob(final BasicNetwork network, final MLDataSet training,
			final boolean loadToMemory, final double learningRate,
			final double momentum) {
		super(network, training, loadToMemory);
		this.learningRate = learningRate;
		this.momentum = momentum;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void createTrainer(final boolean singleThreaded) {
		final Propagation train = new Backpropagation(getNetwork(),
				getTraining(), getLearningRate(), getMomentum());

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
	 * @return the learningRate
	 */
	public final double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return the momentum
	 */
	public final double getMomentum() {
		return this.momentum;
	}

	/**
	 * @param learningRate
	 *            the learningRate to set
	 */
	public final void setLearningRate(final double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * @param momentum
	 *            the momentum to set
	 */
	public final void setMomentum(final double momentum) {
		this.momentum = momentum;
	}

}
