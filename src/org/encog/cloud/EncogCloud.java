/*
 * Encog(tm) Core v2.4
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
 * Holds a connection to an Encog cloud.
 */
public class EncogCloud {

	/**
	 * The default cloud server.
	 */
	public final String DEFAULT_SERVER = "http://cloud.encog.com/";

	/**
	 * The session.
	 */
	private String session;

	/**
	 * The server.
	 */
	private String server;

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
	 * @param name The name of the task to begin.
	 * @return The new task.
	 */
	public CloudTask beginTask(final String name) {
		final CloudTask result = new CloudTask(this);
		result.init(name);

		return result;
	}

	/**
	 * Connect to the Encog cloud.
	 * @param uid The user id.
	 * @param pwd The password.
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
	 * @param service The service to connect to.
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
	 * @param failOnError True if an exception should be thrown on error.
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
