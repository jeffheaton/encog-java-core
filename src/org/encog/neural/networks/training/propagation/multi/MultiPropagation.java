package org.encog.neural.networks.training.propagation.multi;

import java.util.concurrent.CountDownLatch;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * MPROP - Multipropagation Training.  This is a training technique being
 * developed by Jeff Heaton.  It is meant to be especially optimal for running
 * on multicore and grid computing systems.
 * 
 * This part of Encog is considered "prebeta".  I am very much still tuning it.
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
	 * Keep track of if the threads have been started or not.
	 */
	private boolean threadsStarted;

	/**
	 * If it is not worthwhile to do MPROP, then we will fall back to using
	 * RPROP.
	 */
	private ResilientPropagation fallback;


	/**
	 * Lock used to make sure that only one worker is updating the master neural
	 * network at a time.
	 */
	private Object updateLock = new Object();

	/**
	 * Latch to track all of the threads as they shut down.
	 */
	private CountDownLatch shutDownLatch;

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
	public MultiPropagation(BasicNetwork network, NeuralDataSet training,
			int threadCount) {
		if (!(training instanceof Indexable)) {
			throw new TrainingError(
					"Must use a training set that implements Indexable for multipropagation.");
		}

		this.threadCount = threadCount;
		this.training = (Indexable) training;
		this.network = network;
		this.threadsStarted = false;

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
			this.workers[i] = new MPROPWorker(networkClone, trainingClone, this, low, high);
		}
		
		// link the workers in a ring
		for(int i=0;i<this.threadCount-1;i++)
		{
			this.workers[i].setNext(this.workers[i+1]);
		}
		this.workers[this.threadCount-1].setNext(this.workers[0]);
	}

	/**
	 * Construct a MPROP trainer that will use the number of available
	 * processors plus 1. If there is only one processor, then threads will not
	 * be used and this trainer will fall back to RPROP.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training set to use.
	 */
	public MultiPropagation(BasicNetwork network, NeuralDataSet training) {
		this(network, training,
				(Runtime.getRuntime().availableProcessors() == 1) ? 1 : Runtime
						.getRuntime().availableProcessors() + 1);
	}

	/**
	 * Called internally to start the worker threads.
	 */
	private void startThreads() {
		this.threadsStarted = true;
		for (int i = 0; i < this.workers.length; i++) {
			Thread t = new Thread(this.workers[i]);
			t.start();
		}
	}

	/**
	 * @return The trained neural network. Make sure you call "finishTraining"
	 *         before attempting to access the neural network.  Otherwise you
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
	 * Perform one iteration of training.  No work is actually done by this
	 * method, other than providing an indication of what the current error
	 * level is.  The threads are already running in the background and going
	 * about their own iterations.  
	 */
	public void iteration() {

		if (this.fallback != null) {
			this.fallback.iteration();
			this.setError(this.fallback.getError());
			return;
		}

		if (!this.threadsStarted) {
			this.startThreads();
		}

		double total = 0;
		// get an average error
		for (MPROPWorker worker : this.workers) {
			worker.waitForIteration();
			total += worker.getError();
		}
		total /= this.workers.length;
		this.setError(total);

	}

	/**
	 * Create a new neural data pair object of the correct size for the 
	 * neural network that is being trained. This object will be passed
	 * to the getPair method to allow the neural data pair objects to be
	 * copied to it.
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

	/**
	 * The workers are all training different segments of the training data.
	 * Each worker has its own neural network that is separate from the
	 * master neural network stored here.  Calling updateNetwork will "merge"
	 * the worker's neural network with the master neural network stored here.
	 * At the end of this process, the master and the worker with both hold
	 * identical networks that are the result of this merge.
	 * @param worker The worker to update the master network.
	 */
	public void updateNetwork(MPROPWorker worker) {
		synchronized (this.updateLock) {
			Double[] workerNet = NetworkCODEC.networkToArray(worker
					.getNetwork());
			Double[] masterNet = NetworkCODEC.networkToArray(this.network);

			for (int i = 0; i < masterNet.length; i++) {
				double diff = workerNet[i] - masterNet[i];
				diff /= 2;
				masterNet[i] += diff;
			}

			NetworkCODEC.arrayToNetwork(masterNet, worker.getNetwork());
			NetworkCODEC.arrayToNetwork(masterNet, this.network);
		}
	}

	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 * 
	 * MPROP makes use of multithreading. It is VERY important that this method
	 * be called once training is finished. This method will wait for all
	 * threads to shut down before exiting.
	 */
	public void finishTraining() {
		// create a latch and wait for workers to shut down
		this.shutDownLatch = new CountDownLatch(this.workers.length);
		for (MPROPWorker worker : this.workers) {
			worker.requestShutdown();
		}
		try {
			this.shutDownLatch.await();
		} catch (InterruptedException e) {
		}
		// restore the training object to a point that training could be
		// restarted
		this.threadsStarted = false;
	}

	/**
	 * Called by workers to notify that that worker has shut down.
	 */
	public void notifyShutdown() {
		this.shutDownLatch.countDown();
	}

}
