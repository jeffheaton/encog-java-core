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

package org.encog.neural.networks.training.concurrent.jobs;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
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
	 * The number of iterations per cycle.
	 */
	private int iterationsPer;
	
	/**
	 * The local ratio.
	 */
	private double localRatio;
	
	/**
	 * The global ratio.
	 */
	private int globalRatio;
	
	/**
	 * The segmentation ratio.
	 */
	private double segmentationRatio;

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
		this.iterationsPer = 1;
		this.localRatio = 1.0;
		this.globalRatio = 1;
		this.segmentationRatio = 1.0;
	}

	/**
	 * Create a trainer to use.
	 * @param profile The OpenCL training profile to use.
	 */
	public abstract void createTrainer(OpenCLTrainingProfile profile, boolean singleThreaded);

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

	/**
	 * @return the iterationsPer
	 */
	public int getIterationsPer() {
		return iterationsPer;
	}

	/**
	 * @param iterationsPer the iterationsPer to set
	 */
	public void setIterationsPer(int iterationsPer) {
		this.iterationsPer = iterationsPer;
	}

	public double getLocalRatio() {
		return localRatio;
	}

	public void setLocalRatio(double localRatio) {
		this.localRatio = localRatio;
	}

	public int getGlobalRatio() {
		return globalRatio;
	}

	public void setGlobalRatio(int globalRatio) {
		this.globalRatio = globalRatio;
	}

	public double getSegmentationRatio() {
		return segmentationRatio;
	}

	public void setSegmentationRatio(double segmentationRatio) {
		this.segmentationRatio = segmentationRatio;
	}

	
	

}
