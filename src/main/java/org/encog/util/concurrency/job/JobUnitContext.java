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

/**
 * The job unit context contains context information to be passed to a job unit.
 * This tells the thread what to work on.
 * 
 * @author jheaton
 * 
 */
public class JobUnitContext {

	/**
	 * Data that defines what job unit should be done.
	 */
	private Object jobUnit;

	/**
	 * The concurrent job owner.
	 */
	private ConcurrentJob owner;

	/**
	 * The task number for this job.
	 */
	private int taskNumber;

	/**
	 * @return The job unit.
	 */
	public Object getJobUnit() {
		return this.jobUnit;
	}

	/**
	 * @return The concurrent job that owns this task.
	 */
	public ConcurrentJob getOwner() {
		return this.owner;
	}

	/**
	 * @return The task number.
	 */
	public int getTaskNumber() {
		return this.taskNumber;
	}

	/**
	 * Set the job unit.
	 * 
	 * @param jobUnit
	 *            The job unit.
	 */
	public void setJobUnit(final Object jobUnit) {
		this.jobUnit = jobUnit;
	}

	/**
	 * Set the job owner.
	 * 
	 * @param owner
	 *            The job owner.
	 */
	public void setOwner(final ConcurrentJob owner) {
		this.owner = owner;
	}

	/**
	 * Set the task number.
	 * 
	 * @param taskNumber
	 *            The task number.
	 */
	public void setTaskNumber(final int taskNumber) {
		this.taskNumber = taskNumber;
	}

}
