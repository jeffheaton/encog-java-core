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

import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.EndTrainingStrategy;

/**
 * This strategy will indicate once training is no longer improving the neural
 * network by a specified amount, over a specified number of cycles. This allows
 * the program to automatically determine when to stop training.
 * 
 * @author jheaton
 * 
 */
public class StopTrainingStrategy implements EndTrainingStrategy {

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
	private MLTrain train;
	
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
	 * The error rate from the previous iteration.
	 */
	private double bestError;
	
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
		this.bestError = Double.MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final MLTrain train) {
		this.train = train;
		this.shouldStop = false;
		this.ready = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void postIteration() {

		if (this.ready) {
			if (Math.abs(this.bestError 
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
		this.bestError = Math.min(this.lastError, this.bestError);

	}

	/**
	 * {@inheritDoc}
	 */
	public void preIteration() {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean shouldStop() {
		return this.shouldStop;
	}

}
