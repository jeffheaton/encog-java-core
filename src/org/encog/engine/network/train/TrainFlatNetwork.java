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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.engine.EncogEngine;
import org.encog.engine.EncogEngineError;
import org.encog.engine.concurrency.DetermineWorkload;
import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.concurrency.TaskGroup;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.data.EngineDataSet;
import org.encog.engine.network.flat.ActivationFunctions;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.opencl.EncogCLPlatform;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.IntRange;


/**
 * Train a flat network using multithreading, and eventually with GPU support.
 * 
 * The training data must be indexable, it will be broken into groups for each
 * thread to process.
 * 
 * At the end of each iteration the training from each thread is aggregated back
 * to the neural network.
 * 
 */
public abstract class TrainFlatNetwork {

	/**
	 * The number of threads to use.
	 */
	private int numThreads;

	/**
	 * The gradients.
	 */
	private double[] gradients;

	/**
	 * The last gradients, from the last training iteration.
	 */
	private double[] lastGradient;

	/**
	 * The network to train.
	 */
	private final FlatNetwork network;

	/**
	 * The training data.
	 */
	private final EngineDataSet training;

	/**
	 * The network in indexable form.
	 */
	private final EngineIndexableSet indexable;

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
	private double currentError;

	/**
	 * The average number of ticks that each CL worker took.
	 */
	private long clTimePerIteration;

	/**
	 * The average number of ticks that each CPU worker took.
	 */
	private long cpuTimePerIteration;

	/**
	 * The performance ratio between CPU and CL. Positive number means CL
	 * workers are faster than CPU ones.
	 */
	private double calculatedCLRatio;

	/**
	 * Reported exception from the threads.
	 */
	private Throwable reportedException;

	/**
	 * Train a flat network multithreaded.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 */
	public TrainFlatNetwork(final FlatNetwork network,
			final EngineDataSet training) {

		if (!(training instanceof EngineIndexableSet)) {
			throw new EncogEngineError (
					"Training data must be Indexable for this training type.");
		}

		this.training = training;
		this.network = network;

		this.indexable = (EngineIndexableSet) training;
		this.numThreads = 0;
		this.reportedException = null;
	}

	/**
	 * Calculate the GPU vs CPU performance.
	 */
	private void calculatePerformance() {
		long totalCPU = 0;
		long countCPU = 0;
		long totalCL = 0;
		long countCL = 0;

		for (final FlatGradientWorker worker : this.workers) {
			if (worker instanceof GradientWorkerCPU) {
				countCPU++;
				totalCPU += worker.getElapsedTime();
			} else if (worker instanceof GradientWorkerCL) {
				countCL++;
				totalCL += worker.getElapsedTime();
			}
		}

		if (countCPU > 0) {
			this.cpuTimePerIteration = totalCPU / countCPU;
		}

		if (countCL > 0) {
			this.clTimePerIteration = totalCL / countCL;
		}

		this.calculatedCLRatio = ((double) this.cpuTimePerIteration)
				/ ((double) this.clTimePerIteration);
	}

	/**
	 * @return The performance ratio between CPU and CL. Positive number means
	 *         CL workers are faster than CPU ones.
	 */
	public double getCalculatedCLRatio() {
		return this.calculatedCLRatio;
	}

	/**
	 * @return The average number of miliseconds that each CL worker took.
	 */
	public long getCLTimePerIteration() {
		return this.clTimePerIteration;
	}

	/**
	 * @return The average number of milliseconds that each CPU worker took.
	 */
	public long getCPUTimePerIteration() {
		return this.cpuTimePerIteration;
	}

	/**
	 * @return The error from the neural network.
	 */
	public double getError() {
		return this.currentError;
	}

	/**
	 * @return The trained neural network.
	 */
	public FlatNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The data we are training with.
	 */
	public EngineDataSet getTraining() {
		return this.training;
	}

	/**
	 * Init the process.
	 */
	private void init() {
		this.gradients = new double[this.network.getWeights().length];
		this.lastGradient = new double[this.network.getWeights().length];

		List<EncogCLDevice> clDevices = null;

		DetermineWorkload determine;

		// consider CL, if enabled
		if (EncogEngine.getInstance().getCL() != null) {
			
			if( EncogEngine.getInstance().getCL().areCPUsPresent() ) {
				this.numThreads = -1;
				EncogEngine.getInstance().getCL().enableAllCPUs();
			}

			clDevices = EncogEngine.getInstance().getCL().getEnabledDevices();
			determine = new DetermineWorkload(this.numThreads,
					clDevices.size(), (int) this.indexable.getRecordCount());
			determine.setCLRatio(EncogEngine.getInstance().getCL()
					.getEnforcedCLRatio());
		} else {
			determine = new DetermineWorkload(this.numThreads,
					(int) this.indexable.getRecordCount());
		}

		this.workers = new FlatGradientWorker[determine.getTotalWorkerCount()];

		determine.calculateWorkers();
		int index = 0;

		// if we are using CL, then we need to compile the kernels for this
		// network
		if (EncogEngine.getInstance().getCL() != null) {
			final Map<String, String> options = new HashMap<String, String>();
			options.put("NEURON_COUNT", "" + this.network.getNeuronCount());
			options.put("WEIGHT_COUNT", "" + this.network.getWeights().length);

			// is there only one activation function? If so, there are some
			// optimizations we can use.
			final int act = this.network.hasSameActivationFunction();

			if (act == ActivationFunctions.ACTIVATION_SIGMOID) {
				options.put("USE_SIGMOID", "1");
			} else if (act == ActivationFunctions.ACTIVATION_TANH) {
				options.put("USE_TANH", "1");
			}

			for (final EncogCLPlatform platform : EncogEngine.getInstance().getCL()
					.getPlatforms()) {
				platform.getNetworkTrain().compile(options);
				platform.getNetworkTrain().init(this.network);
			}
		}

		// handle CL
		int idx = 0;
		for (final IntRange r : determine.getCLRanges()) {
			this.workers[index++] = new GradientWorkerCL(clDevices.get(idx++),
					this.network.clone(), this,
					this.indexable.openAdditional(), r.getLow(), r.getHigh());
		}

		// handle CPU
		for (final IntRange r : determine.getCPURanges()) {
			this.workers[index++] = new GradientWorkerCPU(this.network.clone(),
					this, this.indexable.openAdditional(), r.getLow(), r
							.getHigh());
		}
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {
		if (this.workers == null) {
			init();
		}
		
		network.initLayerOutput();
		this.totalError = 0;

		if (this.workers.length > 1) {

			final TaskGroup group = EngineConcurrency.getInstance()
					.createTaskGroup();
			
			for (final FlatGradientWorker worker : this.workers) {
				EngineConcurrency.getInstance().processTask(worker, group);
			}

			group.waitForComplete();
		} else {
			workers[0].run();
		}

		learn();
		this.currentError = this.totalError / this.workers.length;

		for (final FlatGradientWorker worker : this.workers) {
			EngineArray.arrayCopy(this.network.getWeights(), 0, worker.getWeights(), 0,
					this.network.getWeights().length);
		}
		
		if( this.reportedException!=null )
			throw(new EncogEngineError(reportedException));

		calculatePerformance();
	}

	/**
	 * Apply and learn.
	 */
	private void learn() {
		double[] weights = network.getWeights();
		for (int i = 0; i < this.gradients.length; i++) {
			weights[i] += updateWeight(this.gradients, this.lastGradient,
					i);
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
	 */
	public void report(final double[] gradients, final double error,
			Throwable ex) {
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
	 * Set the number of threads to use.
	 * 
	 * @param numThreads
	 *            The number of threads to use.
	 */
	public void setNumThreads(final int numThreads) {
		this.numThreads = numThreads;

	}

}
