/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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

import java.util.Random;

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
import org.encog.neural.networks.training.BatchSize;
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
		MultiThreadable, BatchSize, GradientWorkerOwner {

	/**
	 * Used to generate randomness for dropout
	 */
	protected Random dropoutRandomSource = new Random();

	/**
	 * The Dropout rate, between 0 and 1
	 */
	private double dropoutRate = 0;
	
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
	 * The batch size. Specify 1 for pure online training. Specify 0 for pure
	 * batch training (complete training set in one batch). Otherwise specify
	 * the batch size for batch training.
	 */
	private int batchSize = 0;
	
	/**
	 * How much to apply l1 regularization penalty, 0 (default) for none.
	 */
	private double l1;
	
	/**
	 * How much to apply l2 regularization penalty, 0 (default) for none.
	 */
	private double l2;

	private boolean finalized = false;

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
	 * Change the dropout rate
	 * @param rate
	 * 			The dropout rate.
	 */
	public void setDroupoutRate(double rate) {
		this.dropoutRate = rate;
	}
	
	/**
	 * @return the current dropout rate
	 * 
	 */
	public double getDropoutRate() {
		return this.dropoutRate;
	}
	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 */
	@Override
	public void finishTraining() {
		finishTraining(this.dropoutRate);
	}
	public void finishTraining(double dropoutRate) {
		if(!this.finalized) {
			final double[] weights = this.currentFlatNetwork.getWeights();
			if(dropoutRate > 0)
			{
				for (int i = 0; i < weights.length; i++) {
					weights[i] *= (1 - dropoutRate);
				}
			}
			this.finalized = true;
		}
		super.finishTraining();
	}

	/**
	 * @return the currentFlatNetwork
	 */
	public FlatNetwork getCurrentFlatNetwork() {
		return this.currentFlatNetwork;
	}

	/**
	 * {@inheritDoc}
	 */
	public MLMethod getMethod() {
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
	 * Process as pure batch (size 0). Batch size equal to training set size.
	 */
	private void processPureBatch() {
		calculateGradients();

		if (this.currentFlatNetwork.isLimited()) {
			learnLimited();
		} else {
			learn();
		}
	}

	private void processBatches() {
		if (this.workers == null) {
			init();
		}

		if (this.currentFlatNetwork.getHasContext()) {
			this.workers[0].getNetwork().clearContext();
		}

		this.workers[0].getErrorCalculation().reset();

		int lastLearn = 0;

		for (int i = 0; i < this.getTraining().size(); i++) {
			this.workers[0].run(i);

			lastLearn++;

			if (lastLearn++ >= this.batchSize) {
				if (this.currentFlatNetwork.isLimited()) {
					learnLimited();
				} else {
					learn();
					lastLearn = 0;
				}
			}
		}
		
		// handle any remaining learning
		if( lastLearn>0 ) {
			learn();
		}

		this.setError(this.workers[0].getErrorCalculation().calculate());

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
	public void iteration(final int count) {

		try {
			for (int i = 0; i < count; i++) {

				preIteration();

				rollIteration();

				if (this.batchSize == 0) {
					processPureBatch();
				} else {
					processBatches();
				}

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
	public void setThreadCount(final int numThreads) {
		this.numThreads = numThreads;
	}

	@Override
	public int getThreadCount() {
		return this.numThreads;
	}

	/**
	 * Default is true. Call this with false to disable flat spot fix.
	 * 
	 * For more info on flat spot:
	 * 
	 * http://www.heatonresearch.com/wiki/Flat_Spot
	 * 
	 * @param b
	 *            True to fix flat spots, false otherwise.
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

		// Do not use multi-threading for non-pure batch training.
		//
		// At some point it would be good to add multi-threading
		// for batch-sizes that are large enough.
		//
		// Multi-threading cannot be added for pure (size 1)
		// online training.
		if (this.batchSize != 0) {
			this.numThreads = 1;
		}

		final DetermineWorkload determine = new DetermineWorkload(
				this.numThreads, (int) this.indexable.getRecordCount());

		int actualThreadCount = determine.getThreadCount();

		this.workers = new GradientWorker[actualThreadCount];

		int index = 0;

		for (final IntRange r : determine.calculateWorkers()) {
			this.workers[index++] = new GradientWorker(
					this.currentFlatNetwork.clone(), this,
					this.indexable.openAdditional(), r.getLow(), r.getHigh(),
					this.flatSpot, this.ef);
		}

		initOthers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * Apply and learn.
	 */
	protected void learn() {
		final double[] weights = this.currentFlatNetwork.getWeights();
		if(this.dropoutRate > 0) {
			for (int i = 0; i < this.gradients.length; i++) {
				weights[i] += updateWeight(this.gradients, this.lastGradient, i, this.dropoutRate);
				this.gradients[i] = 0;
			}			
		} else {
			for (int i = 0; i < this.gradients.length; i++) {
				weights[i] += updateWeight(this.gradients, this.lastGradient, i);
				this.gradients[i] = 0;
			}			
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
		if(this.dropoutRate > 0) {
			for (int i = 0; i < this.gradients.length; i++) {
				if (Math.abs(weights[i]) < limit) {
					weights[i] = 0;
				} else {
					weights[i] += updateWeight(this.gradients, this.lastGradient, i, this.dropoutRate);
				}
				this.gradients[i] = 0;
			}			
		} else {
			for (int i = 0; i < this.gradients.length; i++) {
				if (Math.abs(weights[i]) < limit) {
					weights[i] = 0;
				} else {
					weights[i] += updateWeight(this.gradients, this.lastGradient, i);
				}
				this.gradients[i] = 0;
			}			
		}
		for (int i = 0; i < this.gradients.length; i++) {
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
	 * Update a weight using dropout, the means by which weights are updated vary depending on
	 * the training.
	 * 
	 * @param gradients
	 *            The gradients.
	 * @param lastGradient
	 *            The last gradients.
	 * @param index
	 *            The index.
	 * @param dropoutRate
	 * 			  The dropout rate
	 * @return The update value.
	 */
	public abstract double updateWeight(double[] gradients,
			double[] lastGradient, int index, double dropoutRate);

	/**
	 * @return the lastGradient
	 */
	public double[] getLastGradient() {
		return lastGradient;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getBatchSize() {
		return this.batchSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBatchSize(int theBatchSize) {
		this.batchSize = theBatchSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getL1() {
		return l1;
	}

	/**
	 * @param l1 How much to apply l1 regularization penalty, 0 (default) for none.
	 */
	public void setL1(double l1) {
		this.l1 = l1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getL2() {
		return l2;
	}

	/**
	 * @param l2 How much to apply l2 regularization penalty, 0 (default) for none.
	 */
	public void setL2(double l2) {
		this.l2 = l2;
	}
	
	
	
}
