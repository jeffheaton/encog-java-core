/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.engine.concurrency.job;

import org.encog.engine.StatusReportable;
import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.concurrency.TaskGroup;

/**
 * This class forms the basis for a job that can be run concurrently.
 */
public abstract class ConcurrentJob {

	/**
	 * The class to report status to.
	 */
	private final StatusReportable report;

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

		EngineConcurrency.getInstance().checkError();
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
}
