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
