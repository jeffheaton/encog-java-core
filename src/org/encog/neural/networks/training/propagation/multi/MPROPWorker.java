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
package org.encog.neural.networks.training.propagation.multi;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkCODEC;
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
	 * The thread entry point. This will execute iterations until a shutdown is
	 * requested.
	 */
	public void run() {

		Double[] masterWeights = NetworkCODEC.networkToArray( this.owner.getNetwork() );
		NetworkCODEC.arrayToNetwork(masterWeights, this.network);
		
		// perform the training for this iteration
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


	public PropagationUtil getPropagationUtil() {
		return propagationUtil;
	}
	
	
}
