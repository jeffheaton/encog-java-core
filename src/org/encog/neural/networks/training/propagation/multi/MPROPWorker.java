package org.encog.neural.networks.training.propagation.multi;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.PropagationUtil;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagationMethod;
import org.encog.util.ErrorCalculation;

/**
 * Worker process for MPROP training. Each worker is given a segment of the
 * training data. The workers then train individual neural networks based on
 * this training set. Results are merged back with the main neural network each
 * iteration.
 * 
 */
public class MPROPWorker implements Runnable {

	/**
	 * The object that owns this worker.
	 */
	private MultiPropagation owner;

	/**
	 * The local thread network that is being trained.
	 */
	private BasicNetwork network;

	/**
	 * The high index point in the training data to be used by this individual
	 * worker.
	 */
	private long high;

	/**
	 * The low index point in the training data to be used by this individual
	 * worker.
	 */
	private long low;

	/**
	 * The RPROP method being used by this worker.
	 */
	private ResilientPropagationMethod method;

	/**
	 * The propagation utility being used by this worker.
	 */
	private PropagationUtil propagationUtil;

	/**
	 * The error calculation object used for this thread.
	 */
	private final ErrorCalculation errorCalculation = new ErrorCalculation();

	/**
	 * The calculated error for the last iteration of this worker.
	 */
	private double error;

	/**
	 * Are we done? If this is true, then this worker should shut down at the
	 * end of this iteration.
	 */
	private AtomicBoolean done = new AtomicBoolean(false);

	/**
	 * A lock that is released at the end of each training iteration. This
	 * allows the iteration in the MultiPropagation object to synchronize with
	 * the workers.
	 */
	private Object iterationLock = new Object();

	/**
	 * The training set that should be used for this worker.
	 */
	private Indexable training;

	/**
	 * THe next worker, useful for SRN networks where context layers must be
	 * linked. These form a ring, with the last worker linking to the first.
	 */
	private MPROPWorker next;

	/**
	 * Construct a MPROP worker.
	 * 
	 * @param network
	 *            The individual network for this worker, this is cloned from
	 *            the master.
	 * @param owner
	 *            The MultiPropagation object that this worker belongs to.
	 * @param low
	 *            The low training index.
	 * @param high
	 *            The high training index.
	 */
	public MPROPWorker(BasicNetwork network, Indexable training,
			MultiPropagation owner, long low, long high) {
		this.network = network;
		this.training = training;
		this.owner = owner;
		this.low = low;
		this.high = high;
		this.method = new ResilientPropagationMethod(
				ResilientPropagation.DEFAULT_ZERO_TOLERANCE,
				ResilientPropagation.DEFAULT_MAX_STEP,
				ResilientPropagation.DEFAULT_INITIAL_UPDATE);
		this.propagationUtil = new PropagationUtil(network, method);
		this.errorCalculation.reset();
	}

	/**
	 * Perform one training iteration for this worker. Called internally only.
	 */
	private void iteration() {
		errorCalculation.reset();
		NeuralDataPair pair = owner.createPair();
		for (long l = this.low; l <= this.high; l++) {
			this.training.getRecord(l, pair);
			NeuralData actual = this.propagationUtil.forwardPass(pair
					.getInput());
			this.propagationUtil.backwardPass(pair.getIdeal());
			errorCalculation.updateError(actual, pair.getIdeal());
		}
		this.setError(errorCalculation.calculateRMS());
		this.propagationUtil.getMethod().learn();
		this.owner.updateNetwork(this);

		synchronized (this.iterationLock) {
			this.iterationLock.notifyAll();
		}
	}

	/**
	 * The thread entry point. This will execute iterations until a shutdown is
	 * requested.
	 */
	public void run() {

		this.done.set(false);

		while (!this.done.get()) {
			iteration();
		}
		this.owner.notifyShutdown();
	}

	/**
	 * @return The error for this worker's last iteration.
	 */
	public synchronized double getError() {
		return error;
	}

	/**
	 * Set the error for this worker.
	 * 
	 * @param error
	 *            The error.
	 */
	public synchronized void setError(double error) {
		this.error = error;
	}

	/**
	 * @return This worker's local network.
	 */
	public BasicNetwork getNetwork() {
		return network;
	}

	/**
	 * Called to request that this worker shut down. This set's the done
	 * instance variable to true.
	 */
	public void requestShutdown() {
		this.done.set(true);
	}

	/**
	 * Wait for one training iteration to complete in this worker. This allows
	 * the main MultiPropagation object to wait for one iteration to pass in
	 * each of the workers.
	 */
	public void waitForIteration() {
		synchronized (this.iterationLock) {
			try {
				this.iterationLock.wait();
			} catch (InterruptedException e) {
				throw new TrainingError(e);
			}
		}
	}

	/**
	 * @return The next worker in the ring.
	 */
	public MPROPWorker getNext() {
		return next;
	}

	/**
	 * @param next The previous worker in the ring.
	 */
	public void setNext(MPROPWorker next) {
		this.next = next;
	}
	
	

}
