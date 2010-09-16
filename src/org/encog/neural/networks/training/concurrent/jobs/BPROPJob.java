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
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 * A training definition for BPROP training.
 */
public class BPROPJob extends TrainingJob {

	/**
	 * The learning rate to use.
	 */
	private double learningRate;

	/**
	 * The momentum to use.
	 */
	private double momentum;

	/**
	 * Construct a job definition for RPROP. For more information on backprop,
	 * see the Backpropagation class.
	 * 
	 * @param network
	 *            The network to use.
	 * @param training
	 *            The training data to use.
	 * @param loadToMemory
	 *            Should binary data be loaded to memory?
	 * @param learningRate
	 *            THe learning rate to use.
	 * @param momentum
	 *            The momentum to use.
	 */
	public BPROPJob(final BasicNetwork network, final NeuralDataSet training,
			final boolean loadToMemory, final double learningRate,
			final double momentum) {
		super(network, training, loadToMemory);
		this.learningRate = learningRate;
		this.momentum = momentum;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTrainer(final EncogCLDevice device) {
		final Train train = new Backpropagation(getNetwork(), getTraining(),
				device, getLearningRate(), getMomentum());

		for (final Strategy strategy : getStrategies()) {
			train.addStrategy(strategy);
		}

		setTrain(train);
	}

	/**
	 * @return the learningRate
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return the momentum
	 */
	public double getMomentum() {
		return this.momentum;
	}

	/**
	 * @param learningRate
	 *            the learningRate to set
	 */
	public void setLearningRate(final double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * @param momentum
	 *            the momentum to set
	 */
	public void setMomentum(final double momentum) {
		this.momentum = momentum;
	}

}
