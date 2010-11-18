/*
 * Encog(tm) Core v2.6 - Java Version
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
 * Holds a connection to an Encog cloud.
 */
public class EncogCloud {

	/**
	 * The default cloud server.
	 */
	public static final String DEFAULT_SERVER = "http://cloud.encog.com/";

	/**
	 * The session.
	 */
	private String session;

	/**
	 * The server.
	 */
	private String server;

	/**
	 * Construct an Encog cloud connection to the default server at:
	 * http://cloud.encog.com/ url.
	 */
	public EncogCloud() {
		this(EncogCloud.DEFAULT_SERVER);
	}

	/**
	 * Construct an Encog cloud connection. The connection will not be
	 * established until the connect method is called.
	 * 
	 * @param server
	 *            The server to connect to.
	 */
	public EncogCloud(final String server) {
		this.server = server;
		if (!this.server.endsWith("/")) {
			this.server += '/';
		}
	}

	/**
	 * Begin a task with the specified name.
	 * 
	 * @param name
	 *            The name of the task to begin.
	 * @return The new task.
	 */
	public CloudTask beginTask(final String name) {
		final CloudTask result = new CloudTask(this);
		result.init(name);

		return result;
	}

	/**
	 * Connect to the Encog cloud.
	 * 
	 * @param uid
	 *            The user id.
	 * @param pwd
	 *            The password.
	 */
	public void connect(final String uid, final String pwd) {
		final CloudRequest request = new CloudRequest();
		final Map<String, String> args = new HashMap<String, String>();
		args.put("uid", uid);
		args.put("pwd", pwd);
		request.performURLPOST(false, constructService("login"), args);
		if (!"success".equals(request.getStatus())) {
			throw new EncogCloudError(request.getMessage());
		}
		this.session = request.getSession();
	}

	/**
	 * Construct a string that connects to the specified service.
	 * 
	 * @param service
	 *            The service to connect to.
	 * @return The complete URL.
	 */
	public String constructService(final String service) {
		return this.server + service;
	}

	/**
	 * @return The session we are connected to.
	 */
	public String getSession() {
		return this.session;
	}

	/**
	 * @return True if we are connected.
	 */
	public boolean isConnected() {
		return this.session != null;
	}

	/**
	 * Logout of the specified session.
	 */
	public void logout() {
		final CloudRequest request = new CloudRequest();
		request.performURLGET(false, this.session + "logout");
		this.session = null;
	}

	/**
	 * Validate the session.
	 * 
	 * @param failOnError
	 *            True if an exception should be thrown on error.
	 */
	public void validateSession(final boolean failOnError) {
		int max;

		if (failOnError) {
			max = 1;
		} else {
			max = 5;
		}

		for (int i = 0; i < max; i++) {
			final CloudRequest request = new CloudRequest();
			request.performURLGET(false, this.session);
			if ("success".equals(request.getStatus())) {
				return;
			}
		}

		if (failOnError) {
			throw new EncogCloudError("Connection lost");
		}
	}
}
