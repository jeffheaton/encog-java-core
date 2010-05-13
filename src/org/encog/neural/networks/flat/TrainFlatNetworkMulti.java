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
package org.encog.neural.networks.flat;

import java.util.List;

import org.encog.mathutil.IntRange;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.concurrency.DetermineWorkload;
import org.encog.util.concurrency.EncogConcurrency;
import org.encog.util.concurrency.TaskGroup;

/**
 * Train a flat network using multithreading, and eventually with GPU support.
 *
 * The training data must be indexable, it will be broken into groups for each
 * thread to process.
 *
 * At the end of each iteration the training from each thread is aggregated
 * back to the neural network.
 *
 */
public class TrainFlatNetworkMulti {

	/**
	 * The gradients
	 */
	private final double[] gradients;

	/**
	 * The last gradients, from the last training iteration.
	 */
	private final double[] lastGradient;

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The training data.
	 */
	private final NeuralDataSet training;

	/**
	 * The update values, for the weights and bias values.
	 */
	private final double[] updateValues;

	/**
	 * The network in indexable form.
	 */
	private Indexable indexable;

	/**
	 * The weights and bias values.
	 */
	private final double[] weights;

	/**
	 * The workers.
	 */
	private GradientWorker[] workers;

	/**
	 * The total error.  Used to take the average of.
	 */
	private double totalError;

	/**
	 * The current error is the average error over all of the threads.
	 */
	private double currentError;

	/**
	 * Train a flat network multithreaded.
	 * @param network The network to train.
	 * @param training The training data to use.
	 */
	public TrainFlatNetworkMulti(final FlatNetwork network,
			final NeuralDataSet training) {

		if( !(training instanceof Indexable) )
			throw new TrainingError("Training data must be Indexable for this training type.");

		this.training = training;
		this.network = network;

		this.indexable = (Indexable)training;

		gradients = new double[network.getWeights().length];
		updateValues = new double[network.getWeights().length];
		lastGradient = new double[network.getWeights().length];

		weights = network.getWeights();

		for (int i = 0; i < updateValues.length; i++) {
			updateValues[i] = ResilientPropagation.DEFAULT_INITIAL_UPDATE;
		}

		DetermineWorkload determine = new DetermineWorkload(0,(int)this.indexable.getRecordCount());
		this.workers = new GradientWorker[ determine.getThreadCount() ];
		List<IntRange> range = determine.calculateWorkers();

		int index = 0;
		for(IntRange r : range)
		{
			this.workers[index++] = new GradientWorker(network.clone(), this, indexable, r.getLow(), r.getHigh());
		}
	}

	/**
	 * Called by the worker threads to report the progress at each step.
	 * @param gradients The gradients from that worker.
	 * @param error The error for that worker.
	 */
	public void report(double[] gradients, double error)
	{
		synchronized(this)
		{
			for(int i=0;i<gradients.length;i++)
			{
				this.gradients[i]+=gradients[i];
			}
			this.totalError+=error;
		}
	}

	/**
	 * @return The error from the neural network.
	 */
	public double getError() {
		return this.currentError;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		TaskGroup group = EncogConcurrency.getInstance().createTaskGroup();
		this.totalError = 0;

		for(GradientWorker worker: this.workers)
		{
			EncogConcurrency.getInstance().processTask(worker,group);
		}

		group.waitForComplete();

		learn();
		this.currentError = this.totalError/this.workers.length;

		for(GradientWorker worker: this.workers)
		{
			System.arraycopy(this.weights, 0, worker.getWeights(), 0, this.weights.length);
		}
	}

	/**
	 * Apply and learn.
	 */
	private void learn() {
		for (int i = 0; i < gradients.length; i++) {
			weights[i] += updateWeight(gradients, i);
			gradients[i] = 0;
		}
	}


	/**
	 * Determine the sign of the value.
	 *
	 * @param value
	 *            The value to check.
	 * @return -1 if less than zero, 1 if greater, or 0 if zero.
	 */
	private int sign(final double value) {
		if (Math.abs(value) < ResilientPropagation.DEFAULT_ZERO_TOLERANCE) {
			return 0;
		} else if (value > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Determine the amount to change a weight by.
	 *
	 * @param gradients
	 *            The gradients.
	 * @param index
	 *            The weight to adjust.
	 * @return The amount to change this weight by.
	 */
	private double updateWeight(final double[] gradients, final int index) {
		// multiply the current and previous gradient, and take the
		// sign. We want to see if the gradient has changed its sign.
		final int change = sign(this.gradients[index] * lastGradient[index]);
		double weightChange = 0;

		// if the gradient has retained its sign, then we increase the
		// delta so that it will converge faster
		if (change > 0) {
			double delta = updateValues[index]
					* ResilientPropagation.POSITIVE_ETA;
			delta = Math.min(delta, ResilientPropagation.DEFAULT_MAX_STEP);
			weightChange = sign(this.gradients[index]) * delta;
			updateValues[index] = delta;
			lastGradient[index] = this.gradients[index];
		} else if (change < 0) {
			// if change<0, then the sign has changed, and the last
			// delta was too big
			double delta = updateValues[index]
					* ResilientPropagation.NEGATIVE_ETA;
			delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
			updateValues[index] = delta;
			// set the previous gradent to zero so that there will be no
			// adjustment the next iteration
			lastGradient[index] = 0;
		} else if (change == 0) {
			// if change==0 then there is no change to the delta
			final double delta = lastGradient[index];
			weightChange = sign(this.gradients[index]) * delta;
			lastGradient[index] = this.gradients[index];
		}

		// apply the weight change, if any
		return weightChange;
	}

	/**
	 * @return The trained neural network.
	 */
	public FlatNetwork getNetwork() {
		return network;
	}

	/**
	 * @return The data we are training with.
	 */
	public NeuralDataSet getTraining() {
		return training;
	}


}
