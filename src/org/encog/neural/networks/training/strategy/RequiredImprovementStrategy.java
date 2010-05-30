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
