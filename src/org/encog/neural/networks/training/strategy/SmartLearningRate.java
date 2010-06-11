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

package org.encog.neural.networks.training.strategy;

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Attempt to automatically set the learning rate in a learning method that
 * supports a learning rate.
 * 
 * @author jheaton
 * 
 */
public class SmartLearningRate implements Strategy {

	/**
	 * Learning decay rate.
	 */
	public static final double LEARNING_DECAY = 0.99;

	/**
	 * The training algorithm that is using this strategy.
	 */
	private Train train;

	/**
	 * The class that is to have the learning rate set for.
	 */
	private LearningRate setter;

	/**
	 * The current learning rate.
	 */
	private double currentLearningRate;

	/**
	 * The training set size, this is used to pick an initial learning rate.
	 */
	private long trainingSize;

	/**
	 * The error rate from the previous iteration.
	 */
	private double lastError;

	/**
	 * Has one iteration passed, and we are now ready to start evaluation.
	 */
	private boolean ready;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Determine the training size.
	 * 
	 * @return The training size.
	 */
	private long determineTrainingSize() {
		long result = 0;
		for (@SuppressWarnings("unused")
		final NeuralDataPair pair : this.train.getTraining()) {
			result++;
		}
		return result;
	}

	/**
	 * Initialize this strategy.
	 * 
	 * @param train
	 *            The training algorithm.
	 */
	public void init(final Train train) {
		this.train = train;
		this.ready = false;
		this.setter = (LearningRate) train;
		this.trainingSize = determineTrainingSize();
		this.currentLearningRate = 1.0 / this.trainingSize;
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Starting learning rate: {}",
					this.currentLearningRate);
		}
		this.setter.setLearningRate(this.currentLearningRate);
	}

	/**
	 * Called just after a training iteration.
	 */
	public void postIteration() {
		if (this.ready) {
			if (this.train.getError() > this.lastError) {
				this.currentLearningRate *= SmartLearningRate.LEARNING_DECAY;
				this.setter.setLearningRate(this.currentLearningRate);
				if (this.logger.isInfoEnabled()) {
					this.logger.info("Adjusting learning rate to {}",
							this.currentLearningRate);
				}
			}
		} else {
			this.ready = true;
		}

	}

	/**
	 * Called just before a training iteration.
	 */
	public void preIteration() {
		this.lastError = this.train.getError();
	}

}
