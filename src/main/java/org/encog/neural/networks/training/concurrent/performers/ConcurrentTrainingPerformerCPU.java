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
package org.encog.neural.networks.training.concurrent.performers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.ml.train.MLTrain;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.concurrent.ConcurrentTrainingManager;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;
import org.encog.util.Stopwatch;
import org.encog.util.concurrency.EngineConcurrency;

/**
 * This performer allows jobs to be performed by the CPU.
 * 
 */
public class ConcurrentTrainingPerformerCPU implements
		ConcurrentTrainingPerformer, Runnable {

	/**
	 * True, if this performer is ready for more work.
	 */
	private final AtomicBoolean ready = new AtomicBoolean(true);

	/**
	 * The current job.
	 */
	private TrainingJob currentJob;

	/**
	 * The manager.
	 */
	private ConcurrentTrainingManager manager;

	/**
	 * The job number.
	 */
	private final int number;

	/**
	 * Construct the performer.
	 * @param number
	 */
	public ConcurrentTrainingPerformerCPU(final int number) {
		this.number = number;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConcurrentTrainingManager getManager() {
		return this.manager;
	}

	public final int getNumber() {
		return this.number;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void perform(final TrainingJob job) {
		if (!this.ready.get()) {
			throw new NeuralNetworkError(
					"Performer is already performing a job.");
		}

		this.ready.set(false);
		this.currentJob = job;

		final PerformerTask task = new PerformerTask(this);
		EngineConcurrency.getInstance().processTask(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean ready() {
		return this.ready.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void run() {
		final Stopwatch watch = new Stopwatch();
		try {
			watch.start();

			this.currentJob.createTrainer(this.manager.isSingleThreaded());
			final MLTrain train = this.currentJob.getTrain();
			int interation = 1;

			while (this.currentJob.shouldContinue()) {
				train.iteration();
				interation++;
			}
			watch.stop();
		} catch (final Throwable t) {
			this.currentJob.setError(t);
		} finally {
			this.ready.set(true);
			this.manager.jobDone(watch.getElapsedMilliseconds(), this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setManager(final ConcurrentTrainingManager manager) {
		this.manager = manager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "[CPU-Performer: " + this.number + "]";
	}

}
