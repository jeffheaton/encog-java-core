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
package org.encog.util;

/**
 * A stopwatch, meant to emulate the C# Stopwatch class.
 */
public class Stopwatch {

	/**
	 * Is the stopwatch stopped.
	 */
	private boolean stopped;

	/**
	 * What is the starting time.
	 */
	private long startTime;

	/**
	 * What is the stopped time.
	 */
	private long stopTime;

	/**
	 * Construct a stopwatch.
	 */
	public Stopwatch() {
		reset();
		this.stopped = false;
	}

	/**
	 * @return Elapsed time in milli's.
	 */
	public long getElapsedMilliseconds() {
		return getElapsedTicks() / 1000;
	}

	/**
	 * @return Elapsed time in ticks.
	 */
	public long getElapsedTicks() {
		if (!this.stopped) {
			this.stopTime = System.nanoTime();
		}

		return (this.stopTime - this.startTime) / 1000;
	}

	/**
	 * Reset the stop watch.
	 */
	public void reset() {
		this.startTime = System.nanoTime();
		this.stopTime = System.nanoTime();
		this.stopped = false;
	}

	/**
	 * Start the stop watch.
	 */
	public void start() {
		this.startTime = System.nanoTime();
		this.stopped = false;
	}

	/**
	 * Stop the stopwatch.
	 */
	public void stop() {
		this.stopTime = System.nanoTime();
		this.stopped = true;
	}

}
