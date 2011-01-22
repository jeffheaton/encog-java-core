/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import org.encog.engine.EncogEngineError;
import org.encog.engine.concurrency.DetermineWorkload;
import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.concurrency.TaskGroup;
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.network.train.TrainFlatNetwork;
import org.encog.engine.network.train.gradient.FlatGradientWorker;
import org.encog.engine.network.train.gradient.GradientWorkerCPU;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.IntRange;

/**
 * Train a flat network using multithreading, and GPU support.
 * 
 * The training data must be indexable, it will be broken into groups for each
 * thread to process.
 * 
 * At the end of each iteration the training from each thread is aggregated back
 * to the neural network.
 * 
 */
public abstract class TrainFlatNetworkProp implements TrainFlatNetwork {

	/**
	 * The number of threads to use.
	 */
	private int numThreads;

	/**
	 * The gradients.
	 */
	protected double[] gradients;

	/**
	 * The last gradients, from the last training iteration.
	 */
	private double[] lastGradient;

	/**
	 * The network to train.
	 */
	protected final FlatNetwork network;

	/**
	 * The training data.
	 */
	private final EngineDataSet training;

	/**
	 * The network in indexable form.
	 */
	private final EngineDataSet indexable;

	/**
	 * The workers.
	 */
	private FlatGradientWorker[] workers;

	/**
	 * The total error. Used to take the average of.
	 */
	private double totalError;

	/**
	 * The current error is the average error over all of the threads.
	 */
	protected double currentError;

	/**
	 * Reported exception from the threads.
	 */
	private Throwable reportedException;

	/**
	 * The iteration.
	 */
	private int iteration;

	/**
	 * Train a flat network multithreaded.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 */
	public TrainFlatNetworkProp(final FlatNetwork network,
			final EngineDataSet training) {

		this.training = training;
		this.network = network;

		this.gradients = new double[this.network.getWeights().length];
		this.lastGradient = new double[this.network.getWeights().length];

		this.indexable = training;
		this.numThreads = 0;
		this.reportedException = null;
	}

	/**
	 * Calculate the gradients.
	 */
	public void calculateGradients() {
		if (this.workers == null) {
			init();
		}

		if( this.network.getHasContext() ) {
			this.workers[0].getNetwork().clearContext();	
		}
		
		this.totalError = 0;

		if (this.workers.length > 1) {

			final TaskGroup group = EngineConcurrency.getInstance()
					.createTaskGroup();

			for (final FlatGradientWorker worker : this.workers) {
				EngineConcurrency.getInstance().processTask(worker, group);
			}

			group.waitForComplete();
		} else {
			this.workers[0].run();
		}

		this.currentError = this.totalError / this.workers.length;

	}

	/**
	 * Copy the contexts to keep them consistent with multithreaded training.
	 */
	private void copyContexts() {
		
		// copy the contexts(layer outputO from each group to the next group
		for (int i = 0; i < (this.workers.length - 1); i++) {
			final double[] src = this.workers[i].getNetwork().getLayerOutput();
			final double[] dst = this.workers[i + 1].getNetwork()
					.getLayerOutput();
			EngineArray.arrayCopy(src, dst);
		}
		 
         // copy the contexts from the final group to the real network
         EngineArray.arrayCopy(
             this.workers[this.workers.length - 1].getNetwork().getLayerOutput(),
             this.network.getLayerOutput());
	}

	/**
	 * {@inheritDoc}
	 */
	public void finishTraining() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	public double getError() {
		return this.currentError;
	}

	/**
	 * @return The gradients from the last iteration;
	 */
	public double[] getLastGradient() {
		return this.lastGradient;
	}

	/**
	 * {@inheritDoc}
	 */
	public FlatNetwork getNetwork() {
		return this.network;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNumThreads() {
		return this.numThreads;
	}

	/**
	 * {@inheritDoc}
	 */
	public EngineDataSet getTraining() {
		return this.training;
	}

	/**
	 * Init the process.
	 */
	private void init() {

		final DetermineWorkload determine = new DetermineWorkload(
				this.numThreads, (int) this.indexable.getRecordCount());

		this.workers = new FlatGradientWorker[determine.getThreadCount()];

		int index = 0;

		// handle CPU
		for (final IntRange r : determine.calculateWorkers()) {
			this.workers[index++] = new GradientWorkerCPU(this.network.clone(),
					this, this.indexable.openAdditional(), r.getLow(),
					r.getHigh());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void iteration() {

		this.iteration++;

		calculateGradients();

		if (this.network.isLimited()) {
			learnLimited();
		} else {
			learn();
		}

		for (final FlatGradientWorker worker : this.workers) {
			EngineArray.arrayCopy(this.network.getWeights(), 0,
					worker.getWeights(), 0, this.network.getWeights().length);
		}

		if( this.network.getHasContext() ) {
			copyContexts();	
		}
		
		if (this.reportedException != null) {
			throw (new EncogEngineError(this.reportedException));
		}
	}

	/**
	 * Apply and learn.
	 */
	protected void learn() {
		final double[] weights = this.network.getWeights();
		for (int i = 0; i < this.gradients.length; i++) {
			weights[i] += updateWeight(this.gradients, this.lastGradient, i);
			this.gradients[i] = 0;
		}
	}

	/**
	 * Apply and learn. This is the same as learn, but it checks to see if any
	 * of the weights are below the limit threshold. In this case, these weights
	 * are zeroed out. Having two methods allows the regular learn method, which
	 * is what is usually use, to be as fast as possible.
	 */
	protected void learnLimited() {
		final double limit = this.network.getConnectionLimit();
		final double[] weights = this.network.getWeights();
		for (int i = 0; i < this.gradients.length; i++) {
			if (weights[i] < limit) {
				weights[i] = 0;
			} else {
				weights[i] += updateWeight(this.gradients, this.lastGradient, i);
			}
			this.gradients[i] = 0;
		}
	}

	/**
	 * Called by the worker threads to report the progress at each step.
	 * 
	 * @param gradients
	 *            The gradients from that worker.
	 * @param error
	 *            The error for that worker.
	 * @param ex
	 *            The exception.
	 */
	public void report(final double[] gradients, final double error,
			final Throwable ex) {
		synchronized (this) {
			if (ex == null) {

				for (int i = 0; i < gradients.length; i++) {
					this.gradients[i] += gradients[i];
				}
				this.totalError += error;
			} else {
				this.reportedException = ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNumThreads(final int numThreads) {
		this.numThreads = numThreads;
	}

	/**
	 * Update a weight, the means by which weights are updated vary depending on
	 * the training.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index.
	 * @return The update value.
	 */
	public abstract double updateWeight(double[] gradients,
			double[] lastGradient, int index);

	/**
	 * Perform the specified number of training iterations. This is a basic
	 * implementation that just calls iteration the specified number of times.
	 * However, some training methods, particularly with the GPU, benefit
	 * greatly by calling with higher numbers than 1.
	 * 
	 * @param count
	 *            The number of training iterations.
	 */
	public void iteration(final int count) {
		for (int i = 0; i < count; i++) {
			iteration();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int getIteration() {
		return this.iteration;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}
}
