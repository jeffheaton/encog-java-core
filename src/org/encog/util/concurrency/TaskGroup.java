/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
	private int id;

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
	private Lock accessLock = new ReentrantLock();
	
	private Condition mightBeDone = accessLock.newCondition();

	/**
	 * Create a task group with the specified id.
	 * 
	 * @param id
	 *            The ID of the task group.
	 */
	public TaskGroup(int id) {
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
	 * Notify that a task is starting.
	 */
	public void taskStarting() {
		this.accessLock.lock();
		try {
			this.totalTasks++;
		}
		finally {
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
			if( this.completedTasks>=this.totalTasks)
				this.mightBeDone.signal();
		}
		finally {
			this.accessLock.unlock();
		}
	}

	/**
	 * @return Returns true if there are no more tasks.
	 */
	public boolean getNoTasks() {
		this.accessLock.lock();
		try {
			return this.totalTasks == this.completedTasks;
		}
		finally {
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
					this.mightBeDone.await();
				} catch (InterruptedException e) {
					throw new EncogError(e);
				}
			} finally {
				this.accessLock.unlock();
			}
		}
	}
}
