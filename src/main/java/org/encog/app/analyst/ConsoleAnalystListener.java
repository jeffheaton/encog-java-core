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
package org.encog.app.analyst;

import org.encog.ml.train.MLTrain;
import org.encog.util.Format;
import org.encog.util.Stopwatch;

/**
 * A console implementation of the Encog Analyst listener. Will report all
 * progress to the console.
 */
public class ConsoleAnalystListener implements AnalystListener {

	/**
	 * The current task.
	 */
	private String currentTask = "";

	/**
	 * Stopwatch to time process.
	 */
	private final Stopwatch stopwatch = new Stopwatch();

	/**
	 * True if shutdown has been requested.
	 */
	private boolean shutdownRequested;

	/**
	 * True if the current command should be canceled.
	 */
	private boolean cancelCommand;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void report(final int total, final int current, 
			final String message) {
		if (total == 0) {
			System.out.println(current + " : " + message);
		} else {
			System.out.println(current + "/" + total + " : " + message);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reportCommandBegin(final int total, final int current,
			final String name) {
		System.out.println();
		if (total == 0) {
			System.out.println("Beginning Task#" + current + " : " + name);
		} else {
			System.out.println("Beginning Task#" + current + "/" + total
					+ " : " + name);
		}
		this.currentTask = name;
		this.stopwatch.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reportCommandEnd(final boolean cancel) {
		String cancelStr = "";
		this.cancelCommand = false;
		this.stopwatch.stop();
		
		if (cancel) {
			cancelStr = "canceled";
		} else {
			cancelStr = "completed";
		}
		
		System.out.println("Task "
				+ this.currentTask
				+ " "
				+ cancelStr
				+ ", task elapsed time "
				+ Format.formatTimeSpan((int) (this.stopwatch
						.getElapsedMilliseconds() / Format.MILI_IN_SEC)));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reportTraining(final MLTrain train) {

		System.out.println("Iteration #"
				+ Format.formatInteger(train.getIteration())
				+ " Error:"
				+ Format.formatPercent(train.getError())
				+ " elapsed time = "
				+ Format.formatTimeSpan((int) (this.stopwatch
						.getElapsedMilliseconds() / Format.MILI_IN_SEC)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportTrainingBegin() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportTrainingEnd() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void requestCancelCommand() {
		this.cancelCommand = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void requestShutdown() {
		this.shutdownRequested = true;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized boolean shouldShutDown() {
		return this.shutdownRequested;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized boolean shouldStopCommand() {
		return this.cancelCommand;
	}

}
