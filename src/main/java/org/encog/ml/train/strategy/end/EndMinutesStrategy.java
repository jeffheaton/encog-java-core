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

import java.util.concurrent.atomic.AtomicInteger;

import org.encog.ml.train.MLTrain;

public class EndMinutesStrategy implements EndTrainingStrategy {
	
	private final int minutes;
	private long startedTime;
	private boolean started;
	private final AtomicInteger minutesLeft = new AtomicInteger(0);
	
	public EndMinutesStrategy(int minutes)
	{
		this.minutes = minutes;
		started = false;
		this.minutesLeft.set(minutes);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldStop() {
		return started && this.minutesLeft.get()>=0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(MLTrain train) {
		this.started = true;
		this.startedTime = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		long now = System.currentTimeMillis();
		this.minutesLeft.set((int)((now - this.startedTime)/60000));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
	}

	/**
	 * @return the minutesLeft
	 */
	public int getMinutesLeft() {
		return minutesLeft.get();
	}

	/**
	 * @return the minutes
	 */
	public int getMinutes() {
		return minutes;
	}
	
	
}
