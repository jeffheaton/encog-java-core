/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.engine.network.train.prop;

import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.FlatNetwork;

/**
 * Train a flat network, using backpropagation.
 */
public class TrainFlatNetworkBackPropagation extends TrainFlatNetworkProp {

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The momentum.
	 */
	private double momentum;

	/**
	 * The last delta values.
	 */
	private double[] lastDelta;

	/**
	 * Construct a backprop trainer for flat networks.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param learningRate
	 *            The learning rate.
	 * @param momentum
	 *            The momentum.
	 */
	public TrainFlatNetworkBackPropagation(final FlatNetwork network,
			final EngineDataSet training, final double learningRate,
			final double momentum) {
		super(network, training);
		this.momentum = momentum;
		this.learningRate = learningRate;
		this.lastDelta = new double[network.getWeights().length];
	}

	/**
	 * @return The last deltas.
	 */
	public double[] getLastDelta() {
		return this.lastDelta;
	}

	/**
	 * @return the learningRate
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return the momentum
	 */
	public double getMomentum() {
		return this.momentum;
	}

	/**
	 * Set the last delta.
	 * 
	 * @param ds
	 *            The last delta.
	 */
	public void setLastDelta(final double[] ds) {
		this.lastDelta = ds;
	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

	/**
	 * Set the momentum.
	 * 
	 * @param rate
	 *            The momentum.
	 */
	public void setMomentum(final double rate) {
		this.momentum = rate;
	}

	/**
	 * Update a weight.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index.
	 * @return The weight delta.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {
		final double delta = (gradients[index] * this.learningRate)
				+ (this.lastDelta[index] * this.momentum);
		this.lastDelta[index] = delta;
		return delta;
	}

}
