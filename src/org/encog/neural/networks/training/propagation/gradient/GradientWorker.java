/*
 * Encog(tm) Core v2.4
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

package org.encog.neural.networks.training.propagation.gradient;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.concurrency.EncogTask;

/**
 * A worker handles one thread. Used to allow the gradient calculation process
 * to run multithreaded. Even if running in single threaded mode, a single
 * worker is created and run by the main thread.
 * 
 */
public class GradientWorker implements EncogTask {

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

	/**
	 * The owner of this worker.
	 */
	private final CalculateGradient owner;

	/**
	 * The network being used by this worker.
	 */
	private final BasicNetwork network;
	
	/**
	 * The training set used by this worker.
	 */
	private final NeuralDataSet training;
	
	/**
	 * The gradient util used by this worker.
	 */
	private final GradientUtil gradient;

	/**
	 * Construct a worker.
	 * @param owner The owner of this worker.
	 * @param training The training set that this worker is to use.
	 * @param low The low element in the training set.
	 * @param high The high element in the training set.
	 */
	public GradientWorker(final CalculateGradient owner,
			final NeuralDataSet training, final int low, final int high) {
		this.owner = owner;
		this.high = high;
		this.low = low;
		this.network = (BasicNetwork) owner.getNetwork().clone();
		this.training = training;
		this.gradient = new GradientUtil(this.network);
	}

	/**
	 * @return The number of training elements ot be processed by this worker.
	 */
	public int getCount() {
		return this.gradient.getCount();
	}

	/**
	 * @return The overall error for this worker.
	 */
	public double getError() {
		return this.gradient.getError();
	}

	/**
	 * @return The gradients calculated for this worker.
	 */
	public double[] getErrors() {
		return this.gradient.getErrors();
	}

	/**
	 * @return The network to calculate gradients for.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * The main loop for this thread.
	 */
	public void run() {		
		final double[] weights = this.owner.getWeights();
		final NeuralDataPair pair = BasicNeuralDataPair.createPair(
				this.training.getInputSize(), 
				this.training.getIdealSize());

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
