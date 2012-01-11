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
package org.encog.util.concurrency;

/**
 * An Encog task being executed by the Java thread pool.
 */
public class PoolItem implements Runnable {

	/**
	 * The task to execute.
	 */
	private final EngineTask task;

	/**
	 * The task group.
	 */
	private final TaskGroup group;

	/**
	 * Create a pool item.
	 * 
	 * @param task
	 *            The task to execute.
	 * @param group
	 *            The group this task belongs to.
	 */
	public PoolItem(final EngineTask task, final TaskGroup group) {
		this.task = task;
		this.group = group;
	}

	/**
	 * Run the task.
	 */
	public void run() {
		try {
			this.task.run();
		} catch (final Throwable t) {
			EngineConcurrency.getInstance().registerError(t);
		} finally {
			if (this.group != null) {
				this.group.taskStopping();
			}
		}
	}
}
