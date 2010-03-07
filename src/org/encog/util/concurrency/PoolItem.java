package org.encog.util.concurrency;

/**
 * An Encog task being executed by the Java thread pool.
 */
public class PoolItem implements Runnable {

	/**
	 * The task to execute.
	 */
	private EncogTask task;
	
	/**
	 * The task group.
	 */
	private TaskGroup group;

	/**
	 * Create a pool item.
	 * @param task The task to execute.
	 * @param group The group this task belongs to.
	 */
	public PoolItem(EncogTask task, TaskGroup group) {
		this.task = task;
		this.group = group;
	}

	@Override
	public void run() {
		try {
			this.task.run();
		} finally {
			if (this.group != null)
				this.group.taskStopping();
		}
	}
}
