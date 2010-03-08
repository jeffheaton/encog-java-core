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
