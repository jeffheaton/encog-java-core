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
package org.encog.neural.networks.training.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.EncogError;
import org.encog.NullStatusReportable;
import org.encog.StatusReportable;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformer;
import org.encog.neural.networks.training.concurrent.performers.ConcurrentTrainingPerformerCPU;

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
	 * @return The singleton instance.
	 */
	public static ConcurrentTrainingManager getInstance() {
		if (ConcurrentTrainingManager.instance == null) {
			ConcurrentTrainingManager.instance = new ConcurrentTrainingManager();
		}
		return ConcurrentTrainingManager.instance;
	}

	/**
	 * The event used to sync waiting for tasks to stop.
	 */
	private final Lock accessLock = new ReentrantLock();

	/**
	 * Condition used to check if we are done.
	 */
	private final Condition mightBeDone = this.accessLock.newCondition();

	/**
	 * The job number.
	 */
	private int jobNumber;

	/**
	 * Single threaded?
	 */
	private boolean singleThreaded;

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
		if( job.getStrategies().size()==0 ) {
			throw new EncogError("Job has no strategies, it will have no way to know when to end.");
		}
		try {
			this.accessLock.lock();
			this.queue.add(job);
		} finally {
			this.accessLock.unlock();
		}

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
			int threads = 1;
			this.accessLock.lock();
			clearPerformers();

			setSingleThreaded(splitCores);

			if (splitCores) {
				final Runtime runtime = Runtime.getRuntime();
				threads = runtime.availableProcessors();
			} else {
				threads = 1;
			}

			int cpuCount = 0;
			for (int i = 0; i < threads; i++) {
				addPerformer(new ConcurrentTrainingPerformerCPU(cpuCount++));
			}
			
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * @return True, if single threaded.
	 */
	public boolean isSingleThreaded() {
		return this.singleThreaded;
	}

	/**
	 * Wait for job to finish.
	 * @param time The time to wait.
	 * @param perf The performer.
	 */
	public void jobDone(final long time,
			final ConcurrentTrainingPerformerCPU perf) {
		try {
			this.jobNumber++;
			reportStatus("Job finished in " + time + "ms, on "
					+ perf.toString());
			this.accessLock.lock();
			this.mightBeDone.signal();
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

	private void reportStatus(final String str) {
		this.report.report(this.queue.size(), this.jobNumber, str);

	}

	/**
	 * Perform the training.
	 */
	@Override
	public void run() {

		this.jobNumber = 0;
		this.report.report(this.queue.size(), 0, "Starting first job");

		int count = 0;
		for (final TrainingJob job : this.queue) {
			// find a performer
			final ConcurrentTrainingPerformer perform = waitForFreePerformer();
			perform.perform(job);
			count++;
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

	public void setSingleThreaded(final boolean singleThreaded) {
		this.singleThreaded = singleThreaded;
	}

	/**
	 * Start the manager.
	 */
	public void start() {
		this.thread = new Thread(this);
		this.thread.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		int index = 1;
		for (final ConcurrentTrainingPerformer performer : this.performers) {
			builder.append("Performer ");
			builder.append(index++);
			builder.append(": ");
			builder.append(performer.toString());
			builder.append("\n");
		}
		return builder.toString();
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
					} catch (final InterruptedException e) {
						return null;
					}
				}
			}

			return result;
		} finally {
			this.accessLock.unlock();
		}

	}

}
