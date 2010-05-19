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
