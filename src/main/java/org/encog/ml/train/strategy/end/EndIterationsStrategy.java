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
package org.encog.ml.train.strategy.end;

import org.encog.ml.train.MLTrain;


public class EndIterationsStrategy implements EndTrainingStrategy {

	private int maxIterations;
	private int currentIteration;
	private MLTrain train;
	
	public EndIterationsStrategy(int maxIterations) {
		this.maxIterations = maxIterations;
		this.currentIteration = 0;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldStop() {
		return (this.currentIteration>=this.maxIterations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MLTrain train) {
		this.train = train;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		this.currentIteration = this.train.getIteration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
	}
}
