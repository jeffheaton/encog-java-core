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
package org.encog.neural.networks.training.concurrent.performers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.concurrent.jobs.TrainingJob;

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
		try {
			EncogCLDevice device = null;
			if (this instanceof ConcurrentTrainingPerformerOpenCL) {
				device = ((ConcurrentTrainingPerformerOpenCL) this).getDevice();
			}
			this.currentJob.createTrainer(device);
			final Train train = this.currentJob.getTrain();
			int interation = 1;

			while (this.currentJob.shouldContinue()) {
				train.iteration();
				interation++;
			}
		} catch (final Throwable t) {
			this.currentJob.setError(t);
		} finally {
			this.ready.set(true);
		}
	}
	
	public String toString()
	{
		return "[CPU-Performer]";
	}

}
