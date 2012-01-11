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
package org.encog.ml.train.strategy;

import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.training.TrainingError;
import org.encog.util.logging.EncogLogging;

/**
 * The reset strategy will reset the weights if the neural network fails to fall
 * below a specified error by a specified number of cycles. This can be useful
 * to throw out initially "bad/hard" random initializations of the weight
 * matrix.
 *
 * @author jheaton
 *
 */
public class ResetStrategy implements Strategy {

	/**
	 * The required minimum error.
	 */
	private final double required;

	/**
	 * The number of cycles to reach the required minimum error.
	 */
	private final int cycles;

	/**
	 * The training algorithm that is using this strategy.
	 */
	private MLTrain train;

	/**
	 * How many bad cycles have there been so far.
	 */
	private int badCycleCount;
	
	/**
	 * The method that is being trained.
	 */
	private MLResettable method;

	/**
	 * Construct a reset strategy.  The error rate must fall
	 * below the required rate in the specified number of cycles,
	 * or the neural network will be reset to random weights and
	 * bias values.
	 * @param required The required error rate.
	 * @param cycles The number of cycles to reach that rate.
	 */
	public ResetStrategy(final double required, final int cycles) {
		this.required = required;
		this.cycles = cycles;
		this.badCycleCount = 0;
	}

	/**
	 * Initialize this strategy.
	 *
	 * @param train
	 *            The training algorithm.
	 */
	public void init(final MLTrain train) {
		this.train = train;
		
		if( !(train.getMethod() instanceof MLResettable) ) {
			throw new TrainingError("To use the reset strategy the machine learning method must support MLResettable.");
		}
		
		this.method = (MLResettable)this.train.getMethod();
	}

	/**
	 * Called just after a training iteration.
	 */
	public void postIteration() {

	}

	/**
	 * Called just before a training iteration.
	 */
	public void preIteration() {
		if (this.train.getError() > this.required) {
			this.badCycleCount++;
			if (this.badCycleCount > this.cycles) {
				EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Failed to imrove network, resetting.");
				this.method.reset();
				this.badCycleCount = 0;
			}
		} else {
			this.badCycleCount = 0;
		}
	}
}
