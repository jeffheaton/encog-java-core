/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Attempt to automatically set a momentum in a training algorithm that supports
 * momentum.
 * 
 * 
 * @author jheaton
 * 
 */
public class SmartMomentum implements Strategy {

	/**
	 * The minimum improvement to adjust momentum.
	 */
	public static final double MIN_IMPROVEMENT = 0.0001;
	
	/**
	 * The maximum value that momentum can go to.
	 */
	public static final double MAX_MOMENTUM = 4;
	
	/**
	 * The starting momentum.
	 */
	public static final double START_MOMENTUM = 0.1;
	
	/**
	 * How much to increase momentum by.
	 */
	public static final double MOMENTUM_INCREASE = 0.01;
	
	/**
	 * How many cycles to accept before adjusting momentum.
	 */
	public static final double MOMENTUM_CYCLES = 10;

	/**
	 * The training algorithm that is using this strategy.
	 */
	private Train train;
	
	/**
	 * The setter used to change momentum.
	 */
	private Momentum setter;
	
	/**
	 * The last improvement in error rate.
	 */
	private double lastImprovement;

	/**
	 * The error rate from the previous iteration.
	 */
	private double lastError;

	/**
	 * Has one iteration passed, and we are now ready to start evaluation.
	 */
	private boolean ready;
	
	/**
	 * The last momentum.
	 */
	private int lastMomentum;
	
	/**
	 * The current momentum.
	 */
	private double currentMomentum;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Initialize this strategy.
	 * 
	 * @param train
	 *            The training algorithm.
	 */
	public void init(final Train train) {
		this.train = train;
		this.setter = (Momentum) train;
		this.ready = false;
		this.setter.setMomentum(0.0);
		this.currentMomentum = 0;

	}

	/**
	 * Called just after a training iteration.
	 */
	public void postIteration() {
		if (this.ready) {
			final double currentError = this.train.getError();
			this.lastImprovement = (currentError - this.lastError)
					/ this.lastError;
			if (this.logger.isTraceEnabled()) {
				this.logger.trace("Last improvement: {}", this.lastImprovement);
			}

			if ((this.lastImprovement > 0)
					|| (Math.abs(this.lastImprovement) 
							< SmartMomentum.MIN_IMPROVEMENT)) {
				this.lastMomentum++;

				if (this.lastMomentum > SmartMomentum.MOMENTUM_CYCLES) {
					this.lastMomentum = 0;
					if (((int) this.currentMomentum) == 0) {
						this.currentMomentum = SmartMomentum.START_MOMENTUM;
					}
					this.currentMomentum *= 
						(1.0 + SmartMomentum.MOMENTUM_INCREASE);
					this.setter.setMomentum(this.currentMomentum);
					if (this.logger.isDebugEnabled()) {
						this.logger.trace("Adjusting momentum: {}",
								this.currentMomentum);
					}
				}
			} else {
				if (this.logger.isDebugEnabled()) {
					this.logger.trace("Setting momentum back to zero.");
				}

				this.currentMomentum = 0;
				this.setter.setMomentum(0);
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
