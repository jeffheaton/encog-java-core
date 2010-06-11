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

package org.encog.neural.networks.training;

import org.encog.cloud.CloudTask;
import org.encog.cloud.EncogCloud;
import org.encog.util.Format;

/**
 * Report the status of Encog training to the cloud.
 */
public class TrainingStatusUtility {

	/**
	 * The last time an update was performed.
	 */
	private long lastUpdate;

	/**
	 * The iteration number.
	 */
	private int iteration;

	/**
	 * The training object.
	 */
	private final Train train;

	/**
	 * The cloud.
	 */
	private final EncogCloud cloud;

	/**
	 * The task that we are reporting to.
	 */
	private CloudTask task;

	/**
	 * Create a training status utility.
	 * 
	 * @param cloud
	 *            The cloud to report to.
	 * @param train
	 *            The training object being used.
	 */
	public TrainingStatusUtility(final EncogCloud cloud, final Train train) {
		this.cloud = cloud;
		this.train = train;
		this.lastUpdate = 0;
		this.iteration = 0;
	}

	/**
	 * Report that we are finished.
	 */
	public void finish() {
		final StringBuilder status = new StringBuilder();
		status.append("Done at iteration #");
		status.append(Format.formatInteger(this.iteration));
		status.append(" - Error: ");
		status.append(Format.formatPercent(this.train.getError()));

		this.task.stop(status.toString());
		this.task = null;
	}

	/**
	 * Perform an update.
	 */
	public void update() {
		final long now = System.currentTimeMillis();
		final long elapsed = (now - this.lastUpdate) / 1000;

		if (this.task == null) {
			this.task = this.cloud.beginTask(this.train.getClass()
					.getSimpleName());
		}

		this.iteration++;

		if (elapsed > 10) {
			this.lastUpdate = now;
			final StringBuilder status = new StringBuilder();
			status.append("Iteration #");
			status.append(Format.formatInteger(this.iteration));
			status.append(" - Error: ");
			status.append(Format.formatPercent(this.train.getError()));
			this.task.setStatus(status.toString());
		}
	}
}
