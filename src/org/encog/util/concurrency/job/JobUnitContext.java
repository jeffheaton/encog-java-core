/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
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
