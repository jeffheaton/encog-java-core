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
package org.encog.util.concurrency.job;

import org.encog.StatusReportable;
import org.encog.util.concurrency.EngineConcurrency;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.concurrency.TaskGroup;

/**
 * This class forms the basis for a job that can be run concurrently.
 */
public abstract class ConcurrentJob implements Runnable, MultiThreadable {
	
	/**
	 * The thread count.
	 */
	private int threadCount;

	/**
	 * The class to report status to.
	 */
	private StatusReportable report;

	/**
	 * The number of tasks in this job.
	 */
	private int totalTasks;

	/**
	 * The current task.
	 */
	private int current;

	/**
	 * Flag to note that the job should stop.
	 */
	private boolean shouldStop = false;
	
	/**
	 * Is the job running.
	 */
	private boolean running;

	/**
	 * Construct a concurrent job.
	 * 
	 * @param report
	 *            The object to report status to.
	 */
	public ConcurrentJob(final StatusReportable report) {
		this.report = report;
		this.current = 1;
	}

	/**
	 * Load the subtasks.
	 * 
	 * @return The total number of subtasks.
	 */
	public abstract int loadWorkload();

	/**
	 * Perform one job unit.
	 * 
	 * @param context
	 *            The context for the job unit.
	 */
	public abstract void performJobUnit(JobUnitContext context);

	/**
	 * Process the job.
	 */
	public void process() {
		Object task;
		EngineConcurrency.getInstance().setThreadCount(this.threadCount);

		this.running = true;
		this.totalTasks = loadWorkload();
		int currentTask = 0;
		TaskGroup group = EngineConcurrency.getInstance().createTaskGroup();

		while (((task = requestNextTask()) != null) && !shouldStop) {
			currentTask++;
			final JobUnitContext context = new JobUnitContext();
			context.setJobUnit(task);
			context.setOwner(this);
			context.setTaskNumber(currentTask);

			final JobUnitWorker worker = new JobUnitWorker(context);
			EngineConcurrency.getInstance().processTask(worker, group);
		}

		group.waitForComplete();
		this.running = false;
		EngineConcurrency.getInstance().checkError();		
	}
	
	public void processBackground() {
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * Report the status for this job.
	 * 
	 * @param context
	 *            The job context.
	 * @param status
	 *            The status to report.
	 */
	public void reportStatus(final JobUnitContext context, 
			final String status) {
		this.report.report(this.totalTasks, current++, status);
	}

	/**
	 * Request the next task to be processed.
	 * 
	 * @return The next task to be processed.
	 */
	public abstract Object requestNextTask();

	/**
	 * @return True if the process should stop.
	 */
	public boolean getShouldStop() {
		return this.shouldStop;
	}

	/**
	 * Request the process to stop.
	 */
	public void stop() {
		this.shouldStop = true;
	}
	
	public void run() {
		process();
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}
	
	public void setReport(StatusReportable r) {
		this.report = r;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getThreadCount()
	{
		return this.threadCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setThreadCount(int numThreads)
	{
		this.threadCount = numThreads;
	}
	
}
