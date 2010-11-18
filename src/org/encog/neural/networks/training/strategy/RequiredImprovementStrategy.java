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
package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The reset strategy will reset the weights if the neural network fails to improve by the specified amount over a number of cycles. 
 * 
 * @author jheaton
 * 
 */
public class RequiredImprovementStrategy implements Strategy {

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	private Train train;

	/**
	 * How many bad cycles have there been so far.
	 */
	private int badCycleCount;

	/**
	 * The last error.
	 */
	private double lastError = Double.NaN;

	/**
	 * If the error is below this, then never reset.
	 */
	private double acceptableThreshold;

	/**
	 * Construct a reset strategy. The error rate must fall below the required
	 * rate in the specified number of cycles, or the neural network will be
	 * reset to random weights and bias values.
	 * 
	 * @param required
	 *            The required error rate.
	 * @param cycles
	 *            The number of cycles to reach that rate.
	 */
	public RequiredImprovementStrategy(final double required, final int cycles) {
		this(required, 0.10, cycles);
	}

	/**
	 * Construct a reset strategy. The error rate must fall below the required
	 * rate in the specified number of cycles, or the neural network will be
	 * reset to random weights and bias values.
	 * 
	 * @param required
	 *            The required error rate.
	 * @param threshold
	 *            The accepted threshold, don't reset if error is below this.
	 * @param cycles
	 *            The number of cycles to reach that rate.
	 */
	public RequiredImprovementStrategy(final double required, final double threshold,
			final int cycles) {
		this.required = required;
		this.cycles = cycles;
		this.badCycleCount = 0;
		this.acceptableThreshold = threshold;
	}

	/**
	 * Reset if there is not at least a 1% improvement for 5 cycles. Don't reset
	 * if below 10%.
	 * 
	 * @param cycles
	 */
	public RequiredImprovementStrategy(final int cycles) {
		this(0.01, 0.10, 5);
	}

	/**
	 * Initialize this strategy.
	 * 
	 * @param train
	 *            The training algorithm.
	 */
	public void init(final Train train) {
		this.train = train;
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

		if (train.getError() > this.acceptableThreshold) {
			if (!Double.isNaN(lastError)) {
				double improve = (lastError - train.getError());
				if (improve < this.required) {
					this.badCycleCount++;
					if (this.badCycleCount > this.cycles) {
						if (this.logger.isDebugEnabled()) {
							this.logger
									.debug("Failed to improve network, resetting.");
						}
						this.train.getNetwork().reset();
						this.badCycleCount = 0;
						this.lastError = Double.NaN;
					}
				} else {
					this.badCycleCount = 0;
				}
			}
			else
				lastError = train.getError();
		}

		lastError = Math.min(this.train.getError(),lastError);
	}
}
