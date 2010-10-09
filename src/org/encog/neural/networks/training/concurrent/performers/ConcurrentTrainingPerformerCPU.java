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

package org.encog.neural.networks.training.concurrent.performers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.Stopwatch;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.concurrent.ConcurrentTrainingManager;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;
import org.encog.util.simple.EncogUtility;

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
	
	private ConcurrentTrainingManager manager;
	
	private int number;

	public ConcurrentTrainingPerformerCPU(int number)
	{
		this.number = number;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(final TrainingJob job) {
		if (!this.ready.get()) {
			throw new NeuralNetworkError(
					"Performer is already performing a job.");
		}

		this.ready.set(false);
		this.currentJob = job;

		final Thread t = new Thread(this);
		t.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean ready() {
		return this.ready.get();
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		Stopwatch watch = new Stopwatch();
		try {
			watch.start();
			OpenCLTrainingProfile profile = null;
			if (this instanceof ConcurrentTrainingPerformerOpenCL) {
				profile = EncogUtility.createProfileRatio(this.currentJob.getNetwork(), this.currentJob.getTraining(), 1.0);
				System.out.println("GO CL");
			}
			
			this.currentJob.createTrainer(profile);
			final Train train = this.currentJob.getTrain();
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
			this.manager.jobDone(watch.getElapsedMilliseconds(),this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return "[CPU-Performer: " + this.number + "]";
	}

	public int getNumber() {
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public ConcurrentTrainingManager getManager() {
		return manager;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setManager(ConcurrentTrainingManager manager) {
		this.manager = manager;
	}
	
	

}
