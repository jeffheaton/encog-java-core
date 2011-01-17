/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.neural.networks.training;

import java.util.ArrayList;
import java.util.List;

import org.encog.cloud.EncogCloud;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.strategy.end.EndTrainingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that implements basic training for most training
 * algorithms. Specifically training strategies can be added to enhance the
 * training.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicTraining implements Train {

	/**
	 * The training strategies to use.
	 */
	private final List<Strategy> strategies = new ArrayList<Strategy>();

	/**
	 * The training data.
	 */
	private NeuralDataSet training;

	/**
	 * The cloud to report status to.
	 */
	private EncogCloud cloud;

	/**
	 * The cloud reporting utility.
	 */
	private TrainingStatusUtility statusUtil;

	/**
	 * The current error rate.
	 */
	private double error;

	/**
	 * The current iteration.
	 */
	private int iteration;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Training strategies can be added to improve the training results. There
	 * are a number to choose from, and several can be used at once.
	 * 
	 * @param strategy
	 *            The strategy to add.
	 */
	public void addStrategy(final Strategy strategy) {
		strategy.init(this);
		this.strategies.add(strategy);
	}

	/**
	 * Should be called after training has completed and the iteration method
	 * will not be called any further.
	 */
	public void finishTraining() {
		if (this.statusUtil != null) {
			this.statusUtil.finish();
			this.statusUtil = null;
		}
		
		if( getNetwork() instanceof BasicNetwork ) {		
			((BasicNetwork)getNetwork()).getStructure().updateFlatNetwork();
		}
	}

	/**
	 * @return The cloud used to track this training.
	 */
	public EncogCloud getCloud() {
		return this.cloud;
	}

	/**
	 * Get the current error percent from the training.
	 * 
	 * @return The current error.
	 */
	public double getError() {
		return this.error;
	}

	/**
	 * @return the iteration
	 */
	public int getIteration() {
		return this.iteration;
	}

	/**
	 * @return The strategies to use.
	 */
	public List<Strategy> getStrategies() {
		return this.strategies;
	}

	/**
	 * @return The training data to use.
	 */
	public NeuralDataSet getTraining() {
		return this.training;
	}

	/**
	 * @return True if training can progress no further.
	 */
	public boolean isTrainingDone() {
		for (Strategy strategy : this.strategies) {
			if (strategy instanceof EndTrainingStrategy) {
				EndTrainingStrategy end = (EndTrainingStrategy)strategy;
				if( end.shouldStop() ) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Perform the specified number of training iterations. This is a basic
	 * implementation that just calls iteration the specified number of times.
	 * However, some training methods, particularly with the GPU, benefit
	 * greatly by calling with higher numbers than 1.
	 * 
	 * @param count
	 *            The number of training iterations.
	 */
	public void iteration(final int count) {
		for (int i = 0; i < count; i++) {
			iteration();
		}
	}

	/**
	 * Call the strategies after an iteration.
	 */
	public void postIteration() {
		for (final Strategy strategy : this.strategies) {
			strategy.postIteration();
		}
	}

	/**
	 * Call the strategies before an iteration.
	 */
	public void preIteration() {

		this.iteration++;

		if (this.statusUtil != null) {
			this.statusUtil.update();
		} else {
			if (this.cloud != null) {
				this.statusUtil = new TrainingStatusUtility(this.cloud, this);
				this.statusUtil.update();
			}
		}

		for (final Strategy strategy : this.strategies) {
			strategy.preIteration();
		}
	}

	/**
	 * Set the cloud use to track this training.
	 * 
	 * @param cloud
	 *            The cloud.
	 */
	public void setCloud(final EncogCloud cloud) {
		this.cloud = cloud;
	}

	/**
	 * @param error
	 *            Set the current error rate. This is usually used by training
	 *            strategies.
	 */
	public void setError(final double error) {
		this.error = error;
	}

	/**
	 * @param iteration
	 *            the iteration to set
	 */
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}

	/**
	 * Set the training object that this strategy is working with.
	 * 
	 * @param training
	 *            The training object.
	 */
	public void setTraining(final NeuralDataSet training) {
		this.training = training;
	}

}
