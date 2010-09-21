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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.encog.bot.BotUtil;
import org.encog.parse.tags.read.ReadXML;
import org.encog.util.http.FormUtility;

/**
 * An Encog cloud request. Sends a request to the Encog cloud and handles the
 * response.
 * 
 */
public class CloudRequest {

	/**
	 * The header properties.
	 */
	private Map<String, String> headerProperties = 
		new HashMap<String, String>();

	/**
	 * The session properties.
	 */
	private Map<String, String> sessionProperties = 
		new HashMap<String, String>();

	/**
	 * The response properties.
	 */
	private Map<String, String> responseProperties = 
		new HashMap<String, String>();

	/**
	 * @return The message returned from the cloud.
	 */
	public String getMessage() {
		return this.headerProperties.get("message");
	}

	/**
	 * Get a response property.
	 * 
	 * @param key
	 *            The key.
	 * @return The property.
	 */
	public String getResponseProperty(final String key) {
		return this.responseProperties.get(key);
	}

	/**
	 * @return The service.
	 */
	public String getService() {
		return this.headerProperties.get("service");
	}

	/**
	 * @return The url.
	 */
	public String getSession() {
		return this.sessionProperties.get("url");
	}

	/**
	 * @return The status.
	 */
	public String getStatus() {
		return this.headerProperties.get("status");
	}

	/**
	 * Handle the cloud response.
	 * 
	 * @param contents
	 *            The contents.
	 */
	private void handleResponse(final String contents) {
		final ByteArrayInputStream is = new ByteArrayInputStream(contents
				.getBytes());
		final ReadXML xml = new ReadXML(is);
		int ch;

		while ((ch = xml.read()) != -1) {
			if (ch == 0) {
				if (xml.getTag().getName().equalsIgnoreCase("EncogCloud")) {
					processCloud(xml);
				}
			}
		}

		if ((getStatus() == null) || getStatus().equals("failed")) {
			throw new EncogCloudError(getMessage());
		}
	}

	/**
	 * Perform a GET request.
	 * 
	 * @param async
	 *            True if this request should be asynchronous.
	 * @param url
	 *            The URL.
	 */
	public void performURLGET(final boolean async, final String url) {
		try {
			if (async) {
				final AsynchronousCloudRequest request = 
					new AsynchronousCloudRequest(
						new URL(url));
				final Thread t = new Thread(request);
				t.setDaemon(true);
				t.start();
			} else {
				final URL url2 = new URL(url);
				final URLConnection u = url2.openConnection();
				final String contents = BotUtil.loadPage(u.getInputStream());
				handleResponse(contents);
			}
		} catch (final IOException e) {
			throw new EncogCloudError(e);
		}
	}

	/**
	 * Perform a POST to the cloud.
	 * 
	 * @param async
	 *            True if this request should be asynchronous.
	 * @param service
	 *            The service.
	 * @param args
	 *            The POST arguments.
	 */
	public void performURLPOST(final boolean async, final String service,
			final Map<String, String> args) {
		try {
			if (async) {
				final AsynchronousCloudRequest request = 
					new AsynchronousCloudRequest(
						new URL(service), args);
				final Thread t = new Thread(request);
				t.setDaemon(true);
				t.start();
			} else {
				final URL url = new URL(service);
				final URLConnection u = url.openConnection();
				u.setDoOutput(true);
				final OutputStream os = u.getOutputStream();

				final FormUtility form = new FormUtility(os, null);

				for (final String key : args.keySet()) {
					form.add(key, args.get(key));
				}
				form.complete();

				final String contents = BotUtil.loadPage(u.getInputStream());
				handleResponse(contents);
			}
		} catch (final IOException e) {
			throw new EncogCloudError(e);
		}
	}

	/**
	 * Process the cloud request.
	 * 
	 * @param xml
	 *            The XML to parse.
	 */
	private void processCloud(final ReadXML xml) {
		int ch;

		while ((ch = xml.read()) != -1) {
			if (ch == 0) {
				if (xml.getTag().getName().equalsIgnoreCase("Header")) {
					this.headerProperties = xml.readPropertyBlock();
				} else if (xml.getTag().getName().equalsIgnoreCase("Session")) {
					this.sessionProperties = xml.readPropertyBlock();
				} else if (xml.getTag().getName().equalsIgnoreCase(
						"Response")) {
					this.responseProperties = xml.readPropertyBlock();
				}
			}
		}
	}

}
