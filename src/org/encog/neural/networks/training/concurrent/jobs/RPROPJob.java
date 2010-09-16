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
package org.encog.neural.networks.training.concurrent.jobs;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * A training definition for RPROP training.
 */
public class RPROPJob extends TrainingJob {

	/**
	 * The initial update value.
	 */
	private double initialUpdate;

	/**
	 * The maximum step value.
	 */
	private double maxStep;

	/**
	 * Construct an RPROP job. For more information on RPROP see the
	 * ResilientPropagation class.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            True if binary training data should be loaded to memory.
	 * @param initialUpdate
	 *            The initial update.
	 * @param maxStep
	 *            The max step.
	 */
	public RPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory, final double initialUpdate,
			final double maxStep) {
		super(network, training, loadToMemory);
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTrainer(final EncogCLDevice device) {
		final Train train = new ResilientPropagation(getNetwork(),
				getTraining(), device, getInitialUpdate(), getMaxStep());

		for (final Strategy strategy : getStrategies()) {
			train.addStrategy(strategy);
		}

		setTrain(train);
	}

	/**
	 * @return the initialUpdate
	 */
	public double getInitialUpdate() {
		return this.initialUpdate;
	}

	/**
	 * @return the maxStep
	 */
	public double getMaxStep() {
		return this.maxStep;
	}

	/**
	 * @param initialUpdate
	 *            the initialUpdate to set
	 */
	public void setInitialUpdate(final double initialUpdate) {
		this.initialUpdate = initialUpdate;
	}

	/**
	 * @param maxStep
	 *            the maxStep to set
	 */
	public void setMaxStep(final double maxStep) {
		this.maxStep = maxStep;
	}

}
