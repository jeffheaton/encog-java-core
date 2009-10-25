package org.encog.neural.networks.training.propagation.multi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkCODEC;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagationMethod;

/**
 * MPROP - Multipropagation Training. This is a training technique being
 * developed by Jeff Heaton. It is meant to be especially optimal for running on
 * multicore and grid computing systems.
 * 
 * This part of Encog is considered "prebeta". I am very much still tuning it.
 * 
 * - Jeff Heaton
 */
public class MultiPropagation extends BasicTraining {

	/**
	 * How many threads are being used to train the network.
	 */
	private int threadCount;

	/**
	 * The workers to be used, one for each thread.
	 */
	private MPROPWorker[] workers;

	/**
	 * The training set to be used. This must be an indexable training set to
	 * that it can be divided by the threads.
	 */
	private Indexable training;

	/**
	 * The neural network to be trained.
	 */
	private BasicNetwork network;

	/**
	 * If it is not worthwhile to do MPROP, then we will fall back to using
	 * RPROP.
	 */
	private ResilientPropagation fallback;

	/**
	 * The RPROP method being used by the master network.
	 */
	private ResilientPropagationMethod method;

	/**
	 * The propagation utility being used by the master network. This is the
	 * master training data.
	 */
	private PropagationUtil propagationUtil;

	/**
	 * A map that allows gradients from the worker threads to be quickly copied
	 * to the master.
	 */
	private GradientMap map;

	public MultiPropagation(BasicNetwork network, NeuralDataSet training,
			int threadCount) {
		init(network, training, threadCount);
	}

	/**
	 * Construct a MPROP trainer using the specified number of threads. You can
	 * also call a constructor that determines how many threads to use based on
	 * the number of processors in the system.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The set to use.
	 * @param threadCount
	 *            The number of threads to use, must be 1 or higher.
	 */
	private void init(BasicNetwork network, NeuralDataSet training,
			int threadCount) {

		// must be using indexable training set
		if (!(training instanceof Indexable)) {
			throw new TrainingError(
					"Must use a training set that implements Indexable for multipropagation.");
		}

		// store params
		this.threadCount = threadCount;
		this.training = (Indexable) training;
		this.network = network;

		// create the master RPROP method and util

		this.method = new ResilientPropagationMethod(
				ResilientPropagation.DEFAULT_ZERO_TOLERANCE,
				ResilientPropagation.DEFAULT_MAX_STEP,
				ResilientPropagation.DEFAULT_INITIAL_UPDATE);
		this.propagationUtil = new PropagationUtil(network, method);

		// setup the workers
		this.workers = new MPROPWorker[threadCount];

		long size = this.training.getRecordCount();
		long sizePerThread = size / this.threadCount;

		// should we fall back to RPROP?
		if (threadCount == 1 || sizePerThread < 1000) {
			this.fallback = new ResilientPropagation(network, training);
			return;
		}

		// create the workers
		for (int i = 0; i < this.threadCount; i++) {
			long low = i * sizePerThread;
			long high;

			// if this is the last record, then high to be the last item
			// in the training set.
			if (i == (this.threadCount - 1))
				high = size - 1;
			else
				high = ((i + 1) * sizePerThread) - 1;

			BasicNetwork networkClone = (BasicNetwork) this.network.clone();
			Indexable trainingClone = this.training.openAdditional();
			this.workers[i] = new MPROPWorker(networkClone, trainingClone,
					this, low, high);
		}

		// link the workers in a ring
		for (int i = 0; i < this.threadCount - 1; i++) {
			this.workers[i].setNext(this.workers[i + 1]);
		}
		this.workers[this.threadCount - 1].setNext(this.workers[0]);

		// build the gradient map
		this.map = new GradientMap(this.propagationUtil, this);
	}

	/**
	 * Construct a MPROP trainer that will use the number of available
	 * processors plus 1. If there is only one processor, then threads will not
	 * be used and this trainer will fall back to RPROP.
	 * 
	 * Also make sure that there are not so many threads that the training set
	 * size per thread becomes two small.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 */
	public MultiPropagation(BasicNetwork network, NeuralDataSet training) {

		// must be using indexable training set
		if (!(training instanceof Indexable)) {
			throw new TrainingError(
					"Must use a training set that implements Indexable for multipropagation.");
		}
		this.training = (Indexable) training;

		int threads = Runtime.getRuntime().availableProcessors();

		// if there is more than one processor, use processor count +1
		if (threads != 1) {
			threads++;
		}
		// if there is a single processor, just use one thread

		// Now see how big the training sets are going to be.
		// We want at least 100 training elements in each.
		// This method will likely be further "tuned" in future versions.

		long recordCount = this.training.getRecordCount();
		long workPerThread = recordCount / threads;

		if (workPerThread < 100) {
			threads = Math.max(1, (int) (recordCount / 100));
		}

		init(network, training, threads);

	}

	/**
	 * @return The trained neural network. Make sure you call "finishTraining"
	 *         before attempting to access the neural network. Otherwise you
	 *         will end up with a reference to a network that is still being
	 *         updated.
	 */
	public BasicNetwork getNetwork() {
		if (this.fallback != null) {
			return this.fallback.getNetwork();
		} else {
			return this.network;
		}
	}

	/**
	 * Perform one iteration of training. No work is actually done by this
	 * method, other than providing an indication of what the current error
	 * level is. The threads are already running in the background and going
	 * about their own iterations.
	 */
	public void iteration() {

		if (this.fallback != null) {
			this.fallback.iteration();
			this.setError(this.fallback.getError());
			return;
		}

		Thread[] threadList = new Thread[this.workers.length];

		// start the threads
		for (int i = 0; i < threadList.length; i++) {
			threadList[i] = new Thread(this.workers[i]);
			threadList[i].start();
		}

		// wait for the threads to die
		double totalError = 0;
		for (int i = 0; i < threadList.length; i++) {
			try {
				threadList[i].join();
			} catch (InterruptedException e) {
			}
			totalError += workers[i].getError();
		}

		totalError /= workers.length;
		setError(totalError);

		this.map.collect();
		this.propagationUtil.getMethod().learn();

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

		int idealSize = this.training.getIdealSize();
		int inputSize = this.training.getInputSize();

		if (idealSize > 0) {
			result = new BasicNeuralDataPair(new BasicNeuralData(
					(int) inputSize), new BasicNeuralData((int) idealSize));
		} else {
			result = new BasicNeuralDataPair(new BasicNeuralData(
					(int) inputSize));
		}

		return result;
	}

	public MPROPWorker[] getWorkers() {
		return workers;
	}

}
