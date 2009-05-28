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
package org.encog.neural.prune;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.
	resilient.ResilientPropagation;
import org.encog.util.concurrency.EncogTask;

/**
 * A worker for the incremental pruning process. This allows the pruning process
 * to be multithreaded.
 * 
 * @author jheaton
 * 
 */
public class IncrementalWorker implements EncogTask {

	/**
	 * The owner of this object.
	 */
	private final PruneIncremental owner;

	/**
	 * The network to train.
	 */
	private final BasicNetwork network;

	/**
	 * Construct the worker.
	 * @param owner The owner of this worker.
	 * @param network The network to train.
	 */
	public IncrementalWorker(final PruneIncremental owner,
			final BasicNetwork network) {
		this.owner = owner;
		this.network = network;
	}

	/**
	 * Perform the next task.
	 */
	public void run() {
		final double result = train(this.network);
		this.owner.reportResult(result, this.network);

	}

	/**
	 * Train the network.
	 * @param network The network to train.
	 * @return The final error rate.
	 */
	private double train(final BasicNetwork network) {
		// train the neural network
		final Train train = new ResilientPropagation(network, this.owner
				.getTraining());

		for (int i = 0; i < this.owner.getIterations(); i++) {
			train.iteration();
		}

		return train.getError();

	}

}
