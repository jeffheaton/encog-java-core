/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.training.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.Encog;
import org.encog.NullStatusReportable;
import org.encog.engine.StatusReportable;
import org.encog.engine.network.train.prop.RPROPConst;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.concurrent.jobs.RPROPJob;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformer;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformerCPU;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformerOpenCL;
import org.encog.neural.networks.training.strategy.end.EndIterationsStrategy;
import org.encog.neural.networks.training.strategy.end.EndTrainingStrategy;

/**
 * Concurrent training manager. This class allows you to queue up network
 * training tasks to be executed either by the CPU cores or OpenCL devices. This
 * allows the CPU/GPU to train neural networks at the same time.
 * 
 */
public final class ConcurrentTrainingManager implements Runnable {

	/**
	 * The singleton instance.
	 */
	private static ConcurrentTrainingManager instance;

	/**
	 * The event used to sync waiting for tasks to stop.
	 */
	private final Lock accessLock = new ReentrantLock();

	/**
	 * Condition used to check if we are done.
	 */
	private final Condition mightBeDone = this.accessLock.newCondition();

	/**
	 * @return The singleton instance.
	 */
	public static ConcurrentTrainingManager getInstance() {
		if (ConcurrentTrainingManager.instance == null) {
			ConcurrentTrainingManager.instance = new ConcurrentTrainingManager();
		}
		return ConcurrentTrainingManager.instance;
	}

	/**
	 * The performers to use.
	 */
	private final List<ConcurrentTrainingPerformer> performers = new ArrayList<ConcurrentTrainingPerformer>();

	/**
	 * The training jobs to execute.
	 */
	private final List<TrainingJob> queue = new ArrayList<TrainingJob>();

	/**
	 * The background thread.
	 */
	private Thread thread;

	/**
	 * An object used to report status.
	 */
	private StatusReportable report = new NullStatusReportable();

	/**
	 * Private constructor.
	 */
	private ConcurrentTrainingManager() {

	}

	/**
	 * Add a performer.
	 * 
	 * @param performer
	 *            The performer to add.
	 */
	public void addPerformer(final ConcurrentTrainingPerformer performer) {
		try {
			this.accessLock.lock();
			this.performers.add(performer);
			performer.setManager(this);
		} finally {
			this.accessLock.unlock();
		}

	}

	/**
	 * Add a training job.
	 * 
	 * @param job
	 *            The training job to add.
	 */
	public void addTrainingJob(final TrainingJob job) {
		try {
			this.accessLock.lock();
			this.queue.add(job);
		} finally {
			this.accessLock.unlock();
		}

	}

	/**
	 * Add a Resilieint propagation training job, with all parameters.
	 * 
	 * @param network
	 *            The network to add.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True, if binary data should be loaded to memory.
	 * @param initialUpdate
	 *            The initial update value.
	 * @param maxStep
	 *            The max step value.
	 * @param ending
	 *            a EndTrainingStrategy object to specify when to stop training.
	 * @return The newly created training job.
	 */
	public TrainingJob addTrainRPROP(final BasicNetwork network,
			final NeuralDataSet training, final boolean loadToMemory,
			final double initialUpdate, final double maxStep,
			final EndTrainingStrategy ending) {
		final RPROPJob job = new RPROPJob(network, training, loadToMemory,
				initialUpdate, maxStep);
		job.getStrategies().add(ending);
		addTrainingJob(job);
		return job;
	}

	/**
	 * Add a Resilieint propagation training job with default RPROP parameters.
	 * Also attempt to load all binary data to memory.
	 * 
	 * @param network
	 *            The network to add.
	 * @param training
	 *            The training data to use.
	 * @param ending
	 *            a EndTrainingStrategy object to specify when to stop training.
	 * @return The newly created training job.
	 */
	public TrainingJob addTrainRPROP(final BasicNetwork network,
			final NeuralDataSet training, final EndIterationsStrategy ending) {

		return addTrainRPROP(network, training, true,
				RPROPConst.DEFAULT_INITIAL_UPDATE, RPROPConst.DEFAULT_MAX_STEP,
				ending);
	}

	/**
	 * Clear all of the performers.
	 */
	public void clearPerformers() {
		try {
			this.accessLock.lock();
			this.performers.clear();
		} finally {
			this.accessLock.unlock();
		}

	}

	/**
	 * Clear the workload.
	 */
	public void clearQueue() {
		try {
			this.accessLock.lock();
			this.queue.clear();
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * Detect performers. Create one performer for each OpenCL device, and
	 * another for the CPU's. If there is an OpenCL device already for the CPU,
	 * do not create another CPU performer.
	 */
	public void detectPerformers() {
		detectPerformers(false);
	}

	/**
	 * Detect performers. Create one performer for each OpenCL device, and
	 * another for the CPU's. If there is an OpenCL device already for the CPU,
	 * do not create another CPU performer.
	 * 
	 * @param splitCores
	 *            True, if a CPU performer should be created for each core.
	 */
	public void detectPerformers(final boolean splitCores) {
		try {
			this.accessLock.lock();
			boolean useCPU = true;
			clearPerformers();

			// handle OpenCL mode
			if (Encog.getInstance().getCL() != null) {

				// should we let OpenCL run the CPU?
				if (Encog.getInstance().getCL().areCPUsPresent()) {
					useCPU = false;
				}

				// add a performer for each OpenCL device.
				for (final EncogCLDevice device : Encog.getInstance().getCL()
						.getDevices()) {
					addPerformer(new ConcurrentTrainingPerformerOpenCL(device));
				}
			}

			// now create CPU performers
			if (useCPU) {
				int threads;

				if (splitCores) {
					final Runtime runtime = Runtime.getRuntime();
					threads = runtime.availableProcessors();
				} else {
					threads = 1;
				}

				for (int i = 0; i < threads; i++) {
					addPerformer(new ConcurrentTrainingPerformerCPU());
				}
			}
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * Wait for all tasks to finish.
	 */
	public void join() {
		try {
			this.thread.join();
		} catch (final InterruptedException e) {
			return;
		}

	}

	/**
	 * If an error has been reported, then throw it as an exception.
	 */
	private void reportErrors() {
		for (final TrainingJob job : this.queue) {
			if (job.getError() != null) {
				throw new NeuralNetworkError(job.getError());
			}
		}
	}

	/**
	 * Perform the training.
	 */
	public void run() {

		this.report.report(this.queue.size(), 0, "Starting first job");

		int count = 0;
		for (final TrainingJob job : this.queue) {
			// find a performer
			final ConcurrentTrainingPerformer perform = waitForFreePerformer();
			perform.perform(job);
			count++;
			this.report.report(this.queue.size(), count, "Jobs submitted: "
					+ count + " of " + this.queue.size());
			reportErrors();
		}

		// now wait for all performers to finish
		boolean done = false;

		this.report.report(this.queue.size(), count,
				"No more jobs to submit, waiting for last job.");
		while (!done) {
			try {
				this.accessLock.lock();
				boolean foundOne = false;
				for (final ConcurrentTrainingPerformer performer : this.performers) {
					if (!performer.ready()) {
						foundOne = true;
					}
				}
				if (foundOne) {
					try {
						this.mightBeDone.await();
					} catch (final InterruptedException e) {

					}
				} else {
					done = true;
				}
			} finally {
				this.accessLock.unlock();
			}
		}

		this.report.report(this.queue.size(), count, "All training done.");
	}

	/**
	 * Setup the object to report status to.
	 * 
	 * @param report
	 *            The object to report status to.
	 */
	public void setReport(final StatusReportable report) {
		this.report = report;
	}

	/**
	 * Start the manager.
	 */
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	/**
	 * Wait for a free performer.
	 * 
	 * @return The free performer.
	 */
	public ConcurrentTrainingPerformer waitForFreePerformer() {

		try {
			this.accessLock.lock();
			ConcurrentTrainingPerformer result = null;

			while (result == null) {
				for (final ConcurrentTrainingPerformer performer : this.performers) {
					if (performer.ready()) {
						result = performer;
					}
				}

				if (result == null) {
					try {
						this.mightBeDone.await();
					} catch (InterruptedException e) {
						return null;
					}
				}
			}

			return result;
		} finally {
			this.accessLock.unlock();
		}

	}

	public void jobDone() {
		try {
			this.accessLock.lock();
			this.mightBeDone.signal();
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int index = 1;
		for (ConcurrentTrainingPerformer performer : this.performers) {
			builder.append("Performer ");
			builder.append(index++);
			builder.append(": ");
			builder.append(performer.toString());
			builder.append("\n");
		}
		return builder.toString();
	}

}
