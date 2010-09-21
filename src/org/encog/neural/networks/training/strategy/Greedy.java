/*
 * Encog(tm) Core v2.5 - Java Version
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

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple greedy strategy. If the last iteration did not improve training,
 * then discard it. Care must be taken with this strategy, as sometimes a
 * training algorithm may need to temporarily decrease the error level before
 * improving it.
 * 
 * @author jheaton
 * 
 */
public class Greedy implements Strategy {

	/**
	 * The training algorithm that is using this strategy.
	 */
	private Train train;
	
	/**
	 * The error rate from the previous iteration.
	 */
	private double lastError;
	
	/**
	 * The last state of the network, so that we can restore to this
	 * state if needed.
	 */
	private double[] lastNetwork;
	
	/**
	 * Has one iteration passed, and we are now ready to start 
	 * evaluation.
	 */
	private boolean ready;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Initialize this strategy.
	 * @param train The training algorithm.
	 */
	public void init(final Train train) {
		this.train = train;
		this.ready = false;
	}

	/**
	 * Called just after a training iteration.
	 */
	public void postIteration() {
		if (this.ready) {
			if (this.train.getError() > this.lastError) {
				if (this.logger.isDebugEnabled()) {
					this.logger
							.debug("Greedy strategy dropped last iteration.");
				}
				this.train.setError(this.lastError);
				NetworkCODEC.arrayToNetwork(this.lastNetwork, this.train
						.getNetwork());
			}
		} else {
			this.ready = true;
		}
	}

	/**
	 * Called just before a training iteration.
	 */
	public void preIteration() {

		final BasicNetwork network = this.train.getNetwork();
		if (network != null) {
			this.lastError = this.train.getError();
			this.lastNetwork = NetworkCODEC.networkToArray(network);
			this.train.setError(this.lastError);
		}
	}
}
