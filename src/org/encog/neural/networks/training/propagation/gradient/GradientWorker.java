/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.networks.training.propagation.gradient;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

public class GradientWorker implements Runnable {

	/**
	 * The high index point in the training data to be used by this individual
	 * worker.
	 */
	private final int high;

	/**
	 * The low index point in the training data to be used by this individual
	 * worker.
	 */
	private final int low;

	private final CalculateGradient owner;

	private final BasicNetwork network;
	private final NeuralDataSet training;
	private final GradientUtil gradient;

	public GradientWorker(final CalculateGradient owner,
			final NeuralDataSet training, final int low, final int high) {
		this.owner = owner;
		this.high = high;
		this.low = low;
		this.network = (BasicNetwork) owner.getNetwork().clone();
		this.training = training;
		this.gradient = new GradientUtil(this.network);
	}

	public int getCount() {
		return this.gradient.getCount();
	}

	public double getError() {
		return this.gradient.getError();
	}

	public double[] getErrors() {
		return this.gradient.getErrors();
	}

	public BasicNetwork getNetwork() {
		return this.network;
	}

	public void run() {
		final double[] weights = this.owner.getWeights();
		final NeuralDataPair pair = this.owner.createPair();

		if ((this.training instanceof Indexable) && (this.high != this.low)) {
			final Indexable t2 = (Indexable) this.training;
			this.gradient.reset(weights);
			for (int i = this.low; i <= this.high; i++) {
				t2.getRecord(i, pair);
				this.gradient.calculate(pair.getInput(), pair.getIdeal());
			}
		} else {
			this.gradient.calculate(this.training, weights);
		}
	}

}
