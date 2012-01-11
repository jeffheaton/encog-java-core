/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.training.strategy;

import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.training.LearningRate;
import org.encog.util.logging.EncogLogging;

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
	private MLTrain train;

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
	 * Initialize this strategy.
	 * 
	 * @param train
	 *            The training algorithm.
	 */
	public final void init(final MLTrain train) {
		this.train = train;
		this.ready = false;
		this.setter = (LearningRate) train;
		this.trainingSize = train.getTraining().getRecordCount();
		this.currentLearningRate = 1.0 / this.trainingSize;
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Starting learning rate: " + 
					this.currentLearningRate);
		this.setter.setLearningRate(this.currentLearningRate);
	}

	/**
	 * Called just after a training iteration.
	 */
	public final void postIteration() {
		if (this.ready) {
			if (this.train.getError() > this.lastError) {
				this.currentLearningRate *= SmartLearningRate.LEARNING_DECAY;
				this.setter.setLearningRate(this.currentLearningRate);
				EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Adjusting learning rate to {}" +
							this.currentLearningRate);
			}
		} else {
			this.ready = true;
		}

	}

	/**
	 * Called just before a training iteration.
	 */
	public final void preIteration() {
		this.lastError = this.train.getError();
	}

}
