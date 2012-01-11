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
import org.encog.util.logging.EncogLogging;

/**
 * A hybrid stragey allows a secondary training algorithm to be used. Once the
 * primary algorithm is no longer improving by much, the secondary will be used.
 * Using simulated annealing in as a secondary to one of the propagation methods
 * is often a very efficient combination as it can help the propagation method
 * escape a local minimum. This is particularly true with backpropagation.
 * 
 * @author jheaton
 * 
 */
public class HybridStrategy implements Strategy {

	/**
	 * The default minimum improvement before we switch to the alternate
	 * training method.
	 */
	public static final double DEFAULT_MIN_IMPROVEMENT = 0.00001;
	
	/**
	 * The default number of cycles to tolerate bad improvement for.
	 */
	public static final int DEFAULT_TOLERATE_CYCLES = 10;
	
	/**
	 * The default number of cycles to use the alternate training for.
	 */
	public static final int DEFAULT_ALTERNATE_CYCLES = 5;

	/**
	 * The primary training method.
	 */
	private MLTrain mainTrain;
	
	/**
	 * The alternate training method.
	 */
	private final MLTrain altTrain;
	
	/**
	 * The last improvement.
	 */
	private double lastImprovement;
	
	/**
	 * The error rate from the previous iteration.
	 */
	private double lastError;
	
	/**
	 * Has one iteration passed, and we are now ready to start 
	 * evaluation.
	 */
	private boolean ready;
	
	/**
	 * The last time the alternate training algorithm was used.
	 */
	private int lastHybrid;
	
	/**
	 * The minimum improvement before the alternate training 
	 * algorithm is considered.
	 */
	private final double minImprovement;
	
	/**
	 * The number of minimal improvement to tolerate before the
	 * alternate training algorithm is used.
	 */
	private final int tolerateMinImprovement;
	
	/**
	 * How many cycles to engage the alternate algorithm for.
	 */
	private final int alternateCycles;

	/**
	 * Construct a hybrid strategy with the default minimum improvement
	 * and toleration cycles.
	 * @param altTrain The alternative training strategy.
	 */
	public HybridStrategy(final MLTrain altTrain) {
		this(altTrain, HybridStrategy.DEFAULT_MIN_IMPROVEMENT,
				HybridStrategy.DEFAULT_TOLERATE_CYCLES,
				HybridStrategy.DEFAULT_ALTERNATE_CYCLES);
	}

	/**
	 * Create a hybrid strategy.
	 * @param altTrain The alternate training algorithm.
	 * @param minImprovement The minimum improvement to switch algorithms.
	 * @param tolerateMinImprovement The number of cycles to tolerate the 
	 * minimum improvement for.
	 * @param alternateCycles How many cycles should the alternate 
	 * training algorithm be used for.
	 */
	public HybridStrategy(final MLTrain altTrain, final double minImprovement,
			final int tolerateMinImprovement, final int alternateCycles) {
		this.altTrain = altTrain;
		this.ready = false;
		this.lastHybrid = 0;
		this.minImprovement = minImprovement;
		this.tolerateMinImprovement = tolerateMinImprovement;
		this.alternateCycles = alternateCycles;
	}

	/**
	 * Initialize this strategy.
	 * 
	 * @param train
	 *            The training algorithm.
	 */
	public void init(final MLTrain train) {
		this.mainTrain = train;
	}

	/**
	 * Called just after a training iteration.
	 */
	public void postIteration() {
		if (this.ready) {
			final double currentError = this.mainTrain.getError();
			this.lastImprovement = (currentError - this.lastError)
					/ this.lastError;
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Last improvement: " + this.lastImprovement);

			if ((this.lastImprovement > 0)
					|| (Math.abs(this.lastImprovement) < this.minImprovement)) {
				this.lastHybrid++;

				if (this.lastHybrid > this.tolerateMinImprovement) {
					this.lastHybrid = 0;

					EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Performing hybrid cycle");

					for (int i = 0; i < this.alternateCycles; i++) {
						this.altTrain.iteration();
					}
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
		this.lastError = this.mainTrain.getError();

	}
}
