/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
	private final double learningRate;

	/**
	 * The momentum.
	 */
	private final double momentum;

	/**
	 * The last delta values.
	 */
	private final double[] lastDelta;

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

	/**
	 * @return The last deltas.
	 */
	public double[] getLastDelta() {
		return lastDelta;
	}
	
}
