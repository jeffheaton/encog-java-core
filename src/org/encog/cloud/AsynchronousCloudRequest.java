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

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.encog.bot.BotUtil;
import org.encog.util.http.FormUtility;

/**
 * An asynchronous cloud request is set to the cloud, but we do not require a
 * reply. This allows status updates to be executed in a separate thread without
 * the need to wait.
 * 
 */
public class AsynchronousCloudRequest implements Runnable {

	/**
	 * The URL that the request was sent to.
	 */
	private final URL url;
	
	/**
	 * The parameters for the request.
	 */
	private final Map<String, String> params;

	/**
	 * Construct the cloud request.  Used for a simple GET request.
	 * @param url The URL this request is to go to.
	 */
	public AsynchronousCloudRequest(final URL url) {
		this.url = url;
		this.params = new HashMap<String, String>();
	}

	/**
	 * Construct the cloud request.  Used for a POST request.
	 * @param url The URL this request goes to.
	 * @param params The POST params.
	 */
	public AsynchronousCloudRequest(final URL url,
			final Map<String, String> params) {
		this.url = url;
		this.params = params;
	}

	/**
	 * @return The POST params.
	 */
	public Map<String, String> getParams() {
		return this.params;
	}

	/**
	 * @return The URL this request is going to.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * Ran by the thread to perform the request.
	 */
	public void run() {

		try {
			if (this.params.size() > 0) {
				final URLConnection u = this.url.openConnection();
				u.setDoOutput(true);
				final OutputStream os = u.getOutputStream();

				final FormUtility form = new FormUtility(os, null);

				for (final String key : this.params.keySet()) {
					form.add(key, this.params.get(key));
				}
				form.complete();

				BotUtil.loadPage(u.getInputStream());
			} else {
				final URLConnection u = this.url.openConnection();
				BotUtil.loadPage(u.getInputStream());

			}
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

}
