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

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.strategy.end.EndTrainingStrategy;

/**
 * Base class for all concurrent training jobs.
 */
public abstract class TrainingJob {

	/**
	 * The network to train.
	 */
	private BasicNetwork network;

	/**
	 * The training data to use.
	 */
	private NeuralDataSet training;

	/**
	 * The strategies to use.
	 */
	private final List<Strategy> strategies = new ArrayList<Strategy>();

	/**
	 * True, if binary training data should be loaded to memory.
	 */
	private boolean loadToMemory;

	/**
	 * The trainer being used.
	 */
	private Train train;

	/**
	 * Holds any errors that occur during training.
	 */
	private Throwable error;

	/**
	 * Construct a training job.
	 * @param network The network to train.
	 * @param training The training data to use.
	 * @param loadToMemory True, if binary data should be loaded to memory.
	 */
	public TrainingJob(final BasicNetwork network,
			final NeuralDataSet training, final boolean loadToMemory) {
		super();
		this.network = network;
		this.training = training;
		this.loadToMemory = loadToMemory;
	}

	/**
	 * Create a trainer to use.
	 * @param device The OpenCL device to use, or null for the CPU.
	 */
	public abstract void createTrainer(EncogCLDevice device);

	/**
	 * @return the error
	 */
	public Throwable getError() {
		return this.error;
	}

	/**
	 * @return the network
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return the strategies
	 */
	public List<Strategy> getStrategies() {
		return this.strategies;
	}

	/**
	 * @return the train
	 */
	public Train getTrain() {
		return this.train;
	}

	/**
	 * @return the training
	 */
	public NeuralDataSet getTraining() {
		return this.training;
	}

	/**
	 * @return the loadToMemory
	 */
	public boolean isLoadToMemory() {
		return this.loadToMemory;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(final Throwable error) {
		this.error = error;
	}

	/**
	 * @param loadToMemory
	 *            the loadToMemory to set
	 */
	public void setLoadToMemory(final boolean loadToMemory) {
		this.loadToMemory = loadToMemory;
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * @param train
	 *            the train to set
	 */
	public void setTrain(final Train train) {
		this.train = train;
	}

	/**
	 * @param training
	 *            the training to set
	 */
	public void setTraining(final NeuralDataSet training) {
		this.training = training;
	}

	/**
	 * @return True, if training should continue.
	 */
	public boolean shouldContinue() {
		for (final Strategy strategy : this.train.getStrategies()) {
			if (strategy instanceof EndTrainingStrategy) {
				final EndTrainingStrategy end = (EndTrainingStrategy) strategy;

				if (end.shouldStop()) {
					return false;
				}
			}
		}
		return true;
	}

}
