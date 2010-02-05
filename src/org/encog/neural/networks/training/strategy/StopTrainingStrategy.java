/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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
 * This strategy will indicate once training is no longer improving the neural
 * network by a specified amount, over a specified number of cycles. This allows
 * the program to automatically determine when to stop training.
 * 
 * @author jheaton
 * 
 */
public class StopTrainingStrategy implements Strategy {

	/**
	 * The default minimum improvement before training stops.
	 */
	public static final double DEFAULT_MIN_IMPROVEMENT = 0.0000001;
	
	/**
	 * The default number of cycles to tolerate.
	 */
	public static final int DEFAULT_TOLERATE_CYCLES = 100;

	/**
	 * The training algorithm that is using this strategy.
	 */
	private Train train;
	
	/**
	 * Flag to indicate if training should stop.
	 */
	private boolean shouldStop;

	/**
	 * Has one iteration passed, and we are now ready to start evaluation.
	 */
	private boolean ready;

	/**
	 * The error rate from the previous iteration.
	 */
	private double lastError;
	
	/**
	 * The minimum improvement before training stops.
	 */
	private final double minImprovement;
	
	/**
	 * The number of cycles to tolerate the minimum improvement.
	 */
	private final int toleratedCycles;
	
	/**
	 * The number of bad training cycles.
	 */
	private int badCycles;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the strategy with default options.
	 */
	public StopTrainingStrategy() {
		this(StopTrainingStrategy.DEFAULT_MIN_IMPROVEMENT,
				StopTrainingStrategy.DEFAULT_TOLERATE_CYCLES);
	}

	/**
	 * Construct the strategy with the specified parameters.
	 * @param minImprovement The minimum accepted improvement.
	 * @param toleratedCycles The number of cycles to tolerate before stopping.
	 */
	public StopTrainingStrategy(final double minImprovement,
			final int toleratedCycles) {
		this.minImprovement = minImprovement;
		this.toleratedCycles = toleratedCycles;
		this.badCycles = 0;
	}

	/**
	 * Initialize this strategy.
	 * 
	 * @param train
	 *            The training algorithm.
	 */
	public void init(final Train train) {
		this.train = train;
		this.shouldStop = false;
		this.ready = false;
	}

	/**
	 * Called just after a training iteration.
	 */
	public void postIteration() {

		if (this.ready) {
			if (Math.abs(this.lastError 
					- this.train.getError()) < this.minImprovement) {
				this.badCycles++;
				if (this.badCycles > this.toleratedCycles) {
					this.shouldStop = true;
				}
			} else {
				this.badCycles = 0;
			}
		} else {
			this.ready = true;
		}

		this.lastError = this.train.getError();

	}

	/**
	 * Called just before a training iteration.
	 */
	public void preIteration() {
	}

	/**
	 * @return True if training should stop.
	 */
	public boolean shouldStop() {
		return this.shouldStop;
	}

}
