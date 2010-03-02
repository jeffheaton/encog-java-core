/*
 * Encog(tm) Core v2.4
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

package org.encog.neural.networks.training.propagation;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.TrainingError;
import org.encog.neural.networks.training.propagation.gradient.CalculateGradient;
import org.encog.util.EncogValidate;


/**
 * Implements basic functionality that is needed by each of the propagation
 * methods. The specifics of each of the propagation methods is implemented
 * inside of the PropagationMethod interface implementors.
 * 
 * @author jheaton
 * 
 */
public abstract class Propagation extends BasicTraining {

	/**
	 * The number of threads to use.
	 */
	private int numThreads = 0;

	/**
	 * The network.
	 */
	private final BasicNetwork network;

	/**
	 * Construct a propagation object.
	 * 
	 * @param network
	 *            The network.
	 * @param training
	 *            The training set.
	 */
	public Propagation(final BasicNetwork network, 
			final NeuralDataSet training) {
		super();
		this.network = network;
		setTraining( training );
	}

	/**
	 * @return True if this training can be continued.
	 */
	public boolean canContinue() {
		return false;
	}

	/**
	 * @return The network.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return The number of threads.
	 */
	public int getNumThreads() {
		return this.numThreads;
	}

	/**
	 * Determine if this specified training continuation object is valid for
	 * this training method.
	 * 
	 * @param state
	 *            The training continuation object to check.
	 * @return True if the continuation object is valid.
	 */
	public boolean isValidResume(final TrainingContinuation state) {
		return false;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {
		try {
			preIteration();

			CalculateGradient prop = new CalculateGradient(getNetwork(),
					getTraining(), this.getNumThreads());
			double[] weights = NetworkCODEC.networkToArray(getNetwork());
			prop.calculate(weights);

			performIteration(prop, weights);

			NetworkCODEC.arrayToNetwork(weights, getNetwork());
			this.setError(prop.getError());

			postIteration();
		} catch (ArrayIndexOutOfBoundsException ex) {
			EncogValidate.validateNetworkForTraining(network, getTraining());
		}
	}

	/**
	 * Pause the training to continue later.
	 * @return A training continuation object.
	 */
	public TrainingContinuation pause() {
		throw new TrainingError("This training type does not support pause.");
	}

	/**
	 * Perform an iteration. This is implemented for each of the propagation
	 * method types.
	 * 
	 * @param prop
	 *            The gradients.
	 * @param weights
	 *            The weights.
	 */
	public abstract void performIteration(CalculateGradient prop,
			double[] weights);

	/**
	 * Resume training.
	 * @param state The training continuation object to use to continue.
	 */
	public void resume(final TrainingContinuation state) {
		throw new TrainingError("This training type does not support resume.");
	}

	/**
	 * Set the number of threads.
	 * @param numThreads The number of threads.
	 */
	public void setNumThreads(final int numThreads) {
		this.numThreads = numThreads;
	}

}
