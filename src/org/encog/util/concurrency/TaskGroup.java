package org.encog.util.concurrency;

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
	private Object completeEvent = new Object();

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
		synchronized (this) {
			this.totalTasks++;
		}
	}

	/**
	 * Notify that a task is stopping.
	 */
	public void taskStopping() {
		synchronized (this) {
			this.completedTasks++;
			this.completeEvent.notifyAll();
		}
	}

	/**
	 * @return Returns true if there are no more tasks.
	 */
	public boolean getNoTasks() {
		synchronized (this) {
			return this.totalTasks == this.completedTasks;
		}
	}

	/**
	 * Wait for all tasks to complete in this group.
	 */
	public void waitForComplete() {
		while (!getNoTasks()) {
			try {
				this.completeEvent.wait();
			} catch (InterruptedException e) {
				// does not matter
			}
		}
	}
}
