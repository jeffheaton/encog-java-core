/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private Train mainTrain;
	
	/**
	 * The alternate training method.
	 */
	private final Train altTrain;
	
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
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a hybrid strategy with the default minimum improvement
	 * and toleration cycles.
	 * @param altTrain The alternative training strategy.
	 */
	public HybridStrategy(final Train altTrain) {
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
	public HybridStrategy(final Train altTrain, final double minImprovement,
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
	public void init(final Train train) {
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
			if (this.logger.isTraceEnabled()) {
				this.logger.trace("Last improvement: {}", this.lastImprovement);
			}

			if ((this.lastImprovement > 0)
					|| (Math.abs(this.lastImprovement) < this.minImprovement)) {
				this.lastHybrid++;

				if (this.lastHybrid > this.tolerateMinImprovement) {
					this.lastHybrid = 0;

					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Performing hybrid cycle");
					}

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
