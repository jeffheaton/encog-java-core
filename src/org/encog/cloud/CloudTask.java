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

package org.encog.cloud;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates an Encog cloud task.
 */
public class CloudTask {

	/**
	 * The cloud service.
	 */
	private final EncogCloud cloud;
	
	/**
	 * The URL for this task.
	 */
	private String taskURL;

	/**
	 * Construct a cloud task.
	 * @param cloud The cloud this task belongs to.
	 */
	public CloudTask(final EncogCloud cloud) {
		this.cloud = cloud;
	}

	/**
	 * @return The cloud that this task belongs to.
	 */
	public EncogCloud getCloud() {
		return this.cloud;
	}

	/**
	 * Setup this task.
	 * @param name The name of this task.
	 */
	public void init(final String name) {

		if (this.cloud.getSession() == null) {
			throw new EncogCloudError(
					"Session must be established before a task is created.");
		}

		final CloudRequest request = new CloudRequest();
		String url = this.cloud.getSession();
		url += "task/create";
		final Map<String, String> args = new HashMap<String, String>();
		args.put("name", name);
		args.put("status", "Starting...");
		request.performURLPOST(false, url, args);
		this.taskURL = this.cloud.getSession() + "task/"
				+ request.getResponseProperty("id") + "/";
	}

	/**
	 * Set the status for this task.
	 * @param status The status for this task.
	 */
	public void setStatus(final String status) {
		if (this.taskURL == null) {
			throw new EncogCloudError("Can't set status for inactive task.");
		}

		final CloudRequest request = new CloudRequest();
		final String url = this.taskURL + "update";

		final Map<String, String> args = new HashMap<String, String>();
		args.put("status", status);
		request.performURLPOST(true, url, args);

	}

	/**
	 * Stop this task.
	 * @param finalStatus The final status for this task.
	 */
	public void stop(final String finalStatus) {
		if (this.taskURL == null) {
			throw new EncogCloudError("Can't stop inactive task.");
		}

		// send final status
		CloudRequest request = new CloudRequest();
		String url = this.taskURL + "update";

		final Map<String, String> args = new HashMap<String, String>();
		args.put("status", finalStatus);
		request.performURLPOST(false, url, args);

		// stop
		url = this.taskURL + "stop";
		request = new CloudRequest();
		request.performURLGET(false, url);
	}
}
