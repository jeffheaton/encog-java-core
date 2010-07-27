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

package org.encog.neural.networks.training.propagation.gradient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.engine.concurrency.DetermineWorkload;
import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.concurrency.TaskGroup;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.IntRange;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.TrainingError;

/**
 * This class is used to calculate the gradients for each of the weights and
 * bias values in a neural network. It is used by the propagation training
 * methods. This class must visit every training set element. Multithreading is
 * used to process every training set element, however it requires an indexable
 * training set to run in multithreaded mode. Multithreaded mode allows the
 * training method to run much faster on a multicore machine.
 * 
 */
public class CalculateGradient {

	/**
	 * How many threads are being used to train the network.
	 */
	private int threadCount;

	/**
	 * The training set to be used. This must be an indexable training set to
	 * that it can be divided by the threads.
	 */
	private Indexable indexed;

	/**
	 * The training set that we are using.
	 */
	private final NeuralDataSet training;

	/**
	 * True if context layers are present. If they are, special handling is
	 * required when multithreading.
	 */
	private final boolean hasContext;

	/**
	 * The workers to be used, one for each thread.
	 */
	private GradientWorker[] workers;

	/**
	 * The network being trained.
	 */
	private BasicNetwork network;

	/**
	 * The weights and bias values being trained.
	 */
	private double[] weights;

	/**
	 * The gradients calculated for every weight and bias value.
	 */
	private final double[] gradients;

	/**
	 * Determine the thread counts and workloads.
	 */
	private DetermineWorkload determine;

	/**
	 * The overall error.
	 */
	private double error;

	/**
	 * The number of training patterns.
	 */
	private int count;

	/**
	 * Construct the object using a network and training set. This constructor
	 * will use only a single thread.
	 * 
	 * @param network
	 *            The network to be used to calculate.
	 * @param training
	 *            The training set to use.
	 */
	public CalculateGradient(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, 1);
	}

	/**
	 * Construct the object for multithreaded use. The number of threads can be
	 * specified.
	 * 
	 * @param network
	 *            The network to use.
	 * @param training
	 *            The training set to use.
	 * @param threads
	 *            The number of threads. Specify one for single threaded.
	 *            Specify zero to allow Encog to determine the best number of
	 *            threads to use, based on how many processors this machine has.
	 */
	public CalculateGradient(final BasicNetwork network,
			final NeuralDataSet training, final int threads) {
		this.training = training;
		this.network = network;

		if (!(this.training instanceof Indexable)) {
			this.network = network;
			this.threadCount = threads;
		} else {
			this.indexed = (Indexable) this.training;
			this.determine = new DetermineWorkload(threads, (int) this.indexed
					.getRecordCount());
			this.threadCount = this.determine.getTotalWorkerCount();
		}

		// setup workers
		this.gradients = new double[network.getStructure().calculateSize()];

		if (this.threadCount == 1) {
			createWorkersSingleThreaded(training);
		} else {
			if (!(training instanceof Indexable)) {
				throw new TrainingError(
						"Must use indexable training set for multithreaded.");
			}

			createWorkersMultiThreaded((Indexable) training);
		}

		this.hasContext = this.network.getStructure().containsLayerType(
				ContextLayer.class);
	}

	/**
	 * Aggregate the results from all of the threads.
	 */
	private void aggregate() {
		for (int i = 0; i < this.gradients.length; i++) {
			this.gradients[i] = 0;
			for (int j = 0; j < this.threadCount; j++) {
				this.gradients[i] += this.workers[j].getErrors()[i];
			}
		}

		this.count = 0;
		for (int i = 0; i < this.threadCount; i++) {
			this.count += this.workers[i].getCount();
		}
	}

	/**
	 * Calculate the gradients based on the specified weights.
	 * 
	 * @param weights
	 *            The weights to use.
	 */
	public void calculate(final double[] weights) {
		this.weights = weights;

		if (this.threadCount == 1) {
			runWorkersSingleThreaded();
		} else {
			runWorkersMultiThreaded();
		}

		aggregate();
		determineError();

		if (this.hasContext) {
			linkContext();
		}

	}

	/**
	 * Create the worker threads for use in multithreaded training.
	 * 
	 * @param training
	 *            The training set to use.
	 */
	private void createWorkersMultiThreaded(final Indexable training) {
		this.indexed = training;
		// setup the workers
		this.workers = new GradientWorker[this.threadCount];
		this.determine.calculateWorkers();
		final List<IntRange> workloadRange = determine.getCPURanges(); 

		int i = 0;
		for (final IntRange range : workloadRange) {
			final Indexable trainingClone = (Indexable)this.indexed.openAdditional();
			this.workers[i++] = new GradientWorker(this, trainingClone, range
					.getLow(), range.getHigh());
		}
	}

	/**
	 * Create a single worker to handle the single threaded mode.
	 * 
	 * @param training
	 *            The training set to use.
	 */
	private void createWorkersSingleThreaded(final NeuralDataSet training) {
		// setup the workers
		this.workers = new GradientWorker[this.threadCount];
		this.workers[0] = new GradientWorker(this, training, 0, 0);
	}

	/**
	 * Determine the current error.
	 */
	private void determineError() {
		double totalError = 0;
		for (int i = 0; i < this.threadCount; i++) {
			totalError += this.workers[i].getError();
		}
		this.error = (totalError / this.threadCount);
	}

	/**
	 * @return The training set count.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return The current overall error.
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return The gradients.
	 */
	public double[] getGradients() {
		return this.gradients;
	}

	/**
	 * @return The network that is being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The weights and bias values from the network that is being
	 *         trained.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/**
	 * Link the context layers. This ensures that the workers pass on the state
	 * of their context layer to the next worker. Without this multithreaded
	 * training could not be used on recurrent neural networks.
	 */
	private void linkContext() {
		final Map<ContextLayer, Object> workload = new HashMap<ContextLayer, Object>();

		// first loop through and build a map of where every context should be
		// copied to
		for (int indexThisWorker = 0; indexThisWorker < this.workers.length; indexThisWorker++) {
			final GradientWorker thisWorker = this.workers[indexThisWorker];
			int indexNextWorker = indexThisWorker + 1;
			if (indexNextWorker == this.workers.length) {
				indexNextWorker = 0;
			}
			final GradientWorker nextWorker = this.workers[indexNextWorker];

			final Object[] thisLayers = thisWorker.getNetwork().getStructure()
					.getLayers().toArray();
			final Object[] nextLayers = nextWorker.getNetwork().getStructure()
					.getLayers().toArray();

			for (int i = 0; i < thisLayers.length; i++) {
				final Layer thisLayer = (Layer) thisLayers[i];
				final Layer nextLayer = (Layer) nextLayers[i];

				if (thisLayer instanceof ContextLayer) {
					final ContextLayer thisContext = (ContextLayer) thisLayer;
					final ContextLayer nextContext = (ContextLayer) nextLayer;

					final double[] source = thisContext.getContext().getData();
					final double[] target = new double[source.length];
					EngineArray.arrayCopy(source, target);
					workload.put(nextContext, target);
				}
			}
		}

		// now actually copy it
		for (final ContextLayer layer : workload.keySet()) {
			final double[] source = (double[]) workload.get(layer);
			final double[] target = layer.getContext().getData();
			EngineArray.arrayCopy(source, target);
		}
	}

	/**
	 * Run all of the workers in a multithreaded way. This function will block
	 * until all threads are done.
	 */
	private void runWorkersMultiThreaded() {
		final TaskGroup group = EngineConcurrency.getInstance()
				.createTaskGroup();

		// start the workers
		for (int i = 0; i < this.threadCount; i++) {
			EngineConcurrency.getInstance().processTask(this.workers[i], group);
		}

		// wait for all workers to finish
		group.waitForComplete();
	}

	/**
	 * Run the single worker for the single threaded mode.
	 */
	private void runWorkersSingleThreaded() {
		this.workers[0].run();
	}
}
