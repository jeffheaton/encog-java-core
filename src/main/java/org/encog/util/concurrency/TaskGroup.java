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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.encog.EncogError;

/**
 * A task group is a group of tasks that you would like to execute at once. You
 * can wait for all tasks in a task group to exit before your program continues.
 */
public class TaskGroup {

	/**
	 * The ID for this task group.
	 */
	private final int id;

	/**
	 * The total number of tasks in this group.
	 */
	private int totalTasks;

	/**
	 * The number of tasks that have completed.
	 */
	private int completedTasks;

	/**
	 * The event used to sync waiting for tasks to stop.
	 */
	private final Lock accessLock = new ReentrantLock();

	/**
	 * Condition used to check if we are done.
	 */
	private final Condition mightBeDone = 
		this.accessLock.newCondition();

	/**
	 * Create a task group with the specified id.
	 * 
	 * @param id
	 *            The ID of the task group.
	 */
	public TaskGroup(final int id) {
		this.id = id;
		this.totalTasks = 0;
	}

	/**
	 * @return The ID of the task group.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * @return Returns true if there are no more tasks.
	 */
	public boolean getNoTasks() {
		this.accessLock.lock();
		try {
			return this.totalTasks == this.completedTasks;
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * Notify that a task is starting.
	 */
	public void taskStarting() {
		this.accessLock.lock();
		try {
			this.totalTasks++;
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * Notify that a task is stopping.
	 */
	public void taskStopping() {
		this.accessLock.lock();
		try {
			this.completedTasks++;
			if (this.completedTasks >= this.totalTasks) {
				this.mightBeDone.signal();
			}
		} finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * Wait for all tasks to complete in this group.
	 */
	public void waitForComplete() {
		while (!getNoTasks()) {
			this.accessLock.lock();
			try {
				try {
					if (!getNoTasks()) {
						this.mightBeDone.await();
					}
				} catch (InterruptedException e) {
					throw new EncogError(e);
				}
			} finally {
				this.accessLock.unlock();
			}
		}
	}
}
