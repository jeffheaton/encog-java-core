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
