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

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.TrainingError;
import org.encog.util.EncogArray;

/**
 * This class is used to calculate the gradients for each of the weights and
 * thresholds values in a neural network. It is used by the propagation training
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
	 * The threads used.
	 */
	private Thread[] threads;

	/**
	 * The network being trained.
	 */
	private BasicNetwork network;

	/**
	 * The weights and thresholds being trained.
	 */
	private double[] weights;
	
	/**
	 * The gradients calculated for every weight and threshold.
	 */
	private final double[] gradients;
	
	/**
	 * The overall error.
	 */
	private double error;
	
	/**
	 * The number of training patterns.
	 */
	private int count;

	public CalculateGradient(final BasicNetwork network,
			final NeuralDataSet training) {
		this(network, training, 1);
	}

	public CalculateGradient(final BasicNetwork network,
			final NeuralDataSet training, final int threads) {
		this.training = training;
		this.network = network;

		if ((threads != 0) || !(this.training instanceof Indexable)) {
			this.network = network;
			this.threadCount = threads;
		} else {
			this.indexed = (Indexable) this.training;
			int num = Runtime.getRuntime().availableProcessors();

			// if there is more than one processor, use processor count +1
			if (num != 1) {
				num++;
			}
			// if there is a single processor, just use one thread

			// Now see how big the training sets are going to be.
			// We want at least 100 training elements in each.
			// This method will likely be further "tuned" in future versions.

			final long recordCount = this.indexed.getRecordCount();
			final long workPerThread = recordCount / num;

			if (workPerThread < 100) {
				num = Math.max(1, (int) (recordCount / 100));
			}

			this.threadCount = num;
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
	 * @param weights The weights to use.
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
	 * Create a new neural data pair object of the correct size for the neural
	 * network that is being trained. This object will be passed to the getPair
	 * method to allow the neural data pair objects to be copied to it.
	 * 
	 * @return A new neural data pair object.
	 */
	public NeuralDataPair createPair() {
		NeuralDataPair result;

		final int idealSize = this.training.getIdealSize();
		final int inputSize = this.training.getInputSize();

		if (idealSize > 0) {
			result = new BasicNeuralDataPair(new BasicNeuralData(inputSize),
					new BasicNeuralData(idealSize));
		} else {
			result = new BasicNeuralDataPair(new BasicNeuralData(inputSize));
		}

		return result;
	}

	private void createWorkersMultiThreaded(final Indexable training) {
		this.indexed = training;
		// setup the workers
		this.workers = new GradientWorker[this.threadCount];
		this.threads = new Thread[this.threadCount];

		final int size = (int) this.indexed.getRecordCount();
		final int sizePerThread = size / this.threadCount;

		// create the workers
		for (int i = 0; i < this.threadCount; i++) {
			final int low = i * sizePerThread;
			int high;

			// if this is the last record, then high to be the last item
			// in the training set.
			if (i == (this.threadCount - 1)) {
				high = size - 1;
			} else {
				high = ((i + 1) * sizePerThread) - 1;
			}

			final Indexable trainingClone = this.indexed.openAdditional();
			this.workers[i] = new GradientWorker(this, trainingClone, low, high);
			this.threads[i] = new Thread(this.workers[i]);
		}
	}

	private void createWorkersSingleThreaded(final NeuralDataSet training) {
		// setup the workers
		this.workers = new GradientWorker[this.threadCount];
		this.workers[0] = new GradientWorker(this, training, 0, 0);
	}

	private void determineError() {
		double totalError = 0;
		for (int i = 0; i < this.threadCount; i++) {
			totalError += this.workers[i].getError();
		}
		this.error = (totalError / this.threadCount);
	}

	public int getCount() {
		return this.count;
	}

	public double getError() {
		return this.error;
	}

	public double[] getGradients() {
		return this.gradients;
	}

	public BasicNetwork getNetwork() {
		return this.network;
	}

	public double[] getWeights() {
		return this.weights;
	}

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
					EncogArray.arrayCopy(source, target);
					workload.put(nextContext, target);
				}
			}
		}

		// now actually copy it
		for (final ContextLayer layer : workload.keySet()) {
			final double[] source = (double[]) workload.get(layer);
			final double[] target = layer.getContext().getData();
			EncogArray.arrayCopy(source, target);
		}
	}

	private void runWorkersMultiThreaded() {
		// start the workers
		for (int i = 0; i < this.threadCount; i++) {
			this.threads[i].start();
		}

		// wait for all workers to finish
		for (int i = 0; i < this.threadCount; i++) {
			try {
				this.threads[i].join();
			} catch (final InterruptedException e) {
			}
		}
	}

	private void runWorkersSingleThreaded() {
		this.workers[0].run();
	}
}
