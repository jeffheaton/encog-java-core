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
package org.encog.neural.networks.training.propagation;

import org.encog.EncogError;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.IntRange;
import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.error.ErrorFunction;
import org.encog.neural.error.LinearErrorFunction;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.Train;
import org.encog.util.EncogValidate;
import org.encog.util.EngineArray;
import org.encog.util.concurrency.DetermineWorkload;
import org.encog.util.concurrency.EngineConcurrency;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.concurrency.TaskGroup;
import org.encog.util.logging.EncogLogging;

/**
 * Implements basic functionality that is needed by each of the propagation
 * methods. The specifics of each of the propagation methods is implemented
 * inside of the PropagationMethod interface implementors.
 * 
 * @author jheaton
 * 
 */
public abstract class Propagation extends BasicTraining implements Train,
		MultiThreadable {

	/**
	 * The current flat network we are using for training, or null for none.
	 */
	private FlatNetwork currentFlatNetwork;

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
	private final double[] lastGradient;

	/**
	 * The network to train.
	 */
	protected final ContainsFlat network;

	/**
	 * The network in indexable form.
	 */
	private final MLDataSet indexable;

	/**
	 * The workers.
	 */
	private GradientWorker[] workers;

	/**
	 * The total error. Used to take the average of.
	 */
	private double totalError;

	/**
	 * The last error.
	 */
	protected double lastError;

	/**
	 * Reported exception from the threads.
	 */
	private Throwable reportedException;

	/**
	 * The iteration.
	 */
	private int iteration;

	/**
	 * The flat spot constants.
	 */
	private double[] flatSpot;

	/**
	 * Should we fix flat spots.
	 */
	private boolean shouldFixFlatSpot;

	/**
	 * The error function.
	 */
	private ErrorFunction ef = new LinearErrorFunction();

	/**
	 * Construct a propagation object.
	 * 
	 * @param network
	 *            The network.
	 * @param training
	 *            The training set.
	 */
	public Propagation(final ContainsFlat network, final MLDataSet training) {
		super(TrainingImplementationType.Iterative);
		this.network = network;
		this.currentFlatNetwork = network.getFlat();
		setTraining(training);

		this.gradients = new double[this.currentFlatNetwork.getWeights().length];
		this.lastGradient = new double[this.currentFlatNetwork.getWeights().length];

		this.indexable = training;
		this.numThreads = 0;
		this.reportedException = null;
		this.shouldFixFlatSpot = true;
	}

	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 */
	@Override
	public final void finishTraining() {
		super.finishTraining();
	}

	/**
	 * @return the currentFlatNetwork
	 */
	public final FlatNetwork getCurrentFlatNetwork() {
		return this.currentFlatNetwork;
	}

	/**
	 * {@inheritDoc}
	 */
	public final MLMethod getMethod() {
		return this.network;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {
		iteration(1);
	}
	
	/**
	 * Increase the iteration by one.
	 */
	public void rollIteration() {
		this.iteration++;
	}

	/**
	 * Perform the specified number of training iterations. This can be more
	 * efficient than single training iterations. This is particularly true if
	 * you are training with a GPU.
	 * 
	 * @param count
	 *            The number of training iterations.
	 */
	@Override
	public final void iteration(final int count) {

		try {
			for (int i = 0; i < count; i++) {

				preIteration();

				rollIteration();

				calculateGradients();

				if (this.currentFlatNetwork.isLimited()) {
					learnLimited();
				} else {
					learn();
				}

				this.lastError = this.getError();

				for (final GradientWorker worker : this.workers) {
					EngineArray.arrayCopy(this.currentFlatNetwork.getWeights(),
							0, worker.getWeights(), 0,
							this.currentFlatNetwork.getWeights().length);
				}

				if (this.currentFlatNetwork.getHasContext()) {
					copyContexts();
				}

				if (this.reportedException != null) {
					throw (new EncogError(this.reportedException));
				}

				postIteration();

				EncogLogging.log(EncogLogging.LEVEL_INFO,
						"Training iteration done, error: " + getError());

			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			EncogValidate.validateNetworkForTraining(this.network,
					getTraining());
			throw new EncogError(ex);
		}
	}

	/**
	 * Set the number of threads. Specify zero to tell Encog to automatically
	 * determine the best number of threads for the processor. If OpenCL is used
	 * as the target device, then this value is not used.
	 * 
	 * @param numThreads
	 *            The number of threads.
	 */
	public final void setThreadCount(final int numThreads) {
		this.numThreads = numThreads;
	}

	@Override
	public int getThreadCount() {
		return this.numThreads;
	}

	/**
	 * Default is true.  Call this with false to disable flat spot fix.
	 * 
	 * For more info on flat spot:
	 * 
	 * http://www.heatonresearch.com/wiki/Flat_Spot
	 * 
	 * @param b True to fix flat spots, false otherwise.
	 */
	public void fixFlatSpot(boolean b) {
		this.shouldFixFlatSpot = b;
	}

	public void setErrorFunction(ErrorFunction ef) {
		this.ef = ef;
	}

	/**
	 * Calculate the gradients.
	 */
	public void calculateGradients() {
		if (this.workers == null) {
			init();
		}

		if (this.currentFlatNetwork.getHasContext()) {
			this.workers[0].getNetwork().clearContext();
		}

		this.totalError = 0;

		if (this.workers.length > 1) {

			final TaskGroup group = EngineConcurrency.getInstance()
					.createTaskGroup();

			for (final GradientWorker worker : this.workers) {
				EngineConcurrency.getInstance().processTask(worker, group);
			}

			group.waitForComplete();
		} else {
			this.workers[0].run();
		}

		this.setError(this.totalError / this.workers.length);

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
		EngineArray.arrayCopy(this.workers[this.workers.length - 1]
				.getNetwork().getLayerOutput(), this.currentFlatNetwork
				.getLayerOutput());
	}

	/**
	 * Init the process.
	 */
	private void init() {

		// fix flat spot, if needed
		this.flatSpot = new double[this.currentFlatNetwork
				.getActivationFunctions().length];

		if (this.shouldFixFlatSpot) {
			for (int i = 0; i < this.currentFlatNetwork
					.getActivationFunctions().length; i++) {
				final ActivationFunction af = this.currentFlatNetwork
						.getActivationFunctions()[i];

				if (af instanceof ActivationSigmoid) {
					this.flatSpot[i] = 0.1;
				} else {
					this.flatSpot[i] = 0.0;
				}
			}
		} else {
			EngineArray.fill(this.flatSpot, 0.0);
		}

		// setup workers
		final DetermineWorkload determine = new DetermineWorkload(
				this.numThreads, (int) this.indexable.getRecordCount());

		this.workers = new GradientWorker[determine.getThreadCount()];

		int index = 0;

		// handle CPU
		for (final IntRange r : determine.calculateWorkers()) {
			this.workers[index++] = new GradientWorker(
					this.currentFlatNetwork.clone(), this,
					this.indexable.openAdditional(), r.getLow(), r.getHigh(),
					this.flatSpot, this.ef);
		}

		initOthers();
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
	public final void report(final double[] gradients, final double error,
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
	 * Apply and learn.
	 */
	protected void learn() {
		final double[] weights = this.currentFlatNetwork.getWeights();
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
		final double limit = this.currentFlatNetwork.getConnectionLimit();
		final double[] weights = this.currentFlatNetwork.getWeights();
		for (int i = 0; i < this.gradients.length; i++) {
			if (Math.abs(weights[i]) < limit) {
				weights[i] = 0;
			} else {
				weights[i] += updateWeight(this.gradients, this.lastGradient, i);
			}
			this.gradients[i] = 0;
		}
	}

	public abstract void initOthers();

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
	 * @return the lastGradient
	 */
	public double[] getLastGradient() {
		return lastGradient;
	}

}
