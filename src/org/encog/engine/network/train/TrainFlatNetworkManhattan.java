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

package org.encog.engine.network.train;

import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.FlatNetwork;


/**
 * Train the flat network using Manhattan update rule.
 */
public class TrainFlatNetworkManhattan extends TrainFlatNetworkMulti {

	/**
	 * The zero tolerance to use.
	 */
	private final double zeroTolerance;

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * Construct a trainer for flat networks to use the Manhattan update rule.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param learningRate
	 *            The learning rate to use.
	 */
	public TrainFlatNetworkManhattan(final FlatNetwork network,
			final EngineDataSet training, final double learningRate) {
		super(network, training);
		this.learningRate = learningRate;
		this.zeroTolerance = RPROPConst.DEFAULT_ZERO_TOLERANCE;
	}

	/**
	 * Calculate the amount to change the weight by.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index to update.
	 * @return The amount to change the weight by.
	 */
	@Override
	public double updateWeight(final double[] gradients,
			final double[] lastGradient, final int index) {
		if (Math.abs(gradients[index]) < this.zeroTolerance) {
			return 0;
		} else if (gradients[index] > 0) {
			return this.learningRate;
		} else {
			return -this.learningRate;
		}
	}

}
