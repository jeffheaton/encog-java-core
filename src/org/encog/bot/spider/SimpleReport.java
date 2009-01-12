/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.bot.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.encog.bot.spider.workload.data.WorkloadHost;

/**
 * SimpleReport: This is a very simple implementation of the SpiderReportable
 * interface. It stays within a single host and does not process any data.
 */
public class SimpleReport implements SpiderReportable {
	/**
	 * The current host, only accept URL's from this host.
	 */
	private WorkloadHost host;

	/**
	 * This function is called when the spider is ready to process a new host.
	 * THis function simply stores the value of the current host.
	 * 
	 * @param host
	 *            The new host that is about to be processed.
	 * @return True if this host should be processed, false otherwise.
	 */
	public boolean beginHost(final WorkloadHost host) {
		if (this.host == null) {
			this.host = host;
			return true;
		}
		return false;
	}

	/**
	 * Called when the spider is starting up. This method provides the
	 * SpiderReportable class with the spider object.
	 * 
	 * @param spider
	 *            The spider that will be working with this object.
	 */
	public void init(final Spider spider) {
	}

	/**
	 * Called when the spider encounters a URL.
	 * 
	 * @param url
	 *            The URL that the spider found.
	 * @param source
	 *            The page that the URL was found on.
	 * @param type
	 *            The type of link this URL is.
	 * @return True if the spider should scan for links on this page.
	 */
	public boolean spiderFoundURL(final URL url, final URL source,
			final URLType type) {
		if (this.host != null && !this.host.getHost().equalsIgnoreCase(url.getHost())) {
			return false;
		}

		return true;
	}

	/**
	 * Called when the spider is about to process a NON-HTML URL. For this
	 * SimpleReport manager, this is ignored.
	 * 
	 * @param url
	 *            The URL that the spider found.
	 * @param stream
	 *            An InputStream to read the page contents from.
	 * @throws IOException
	 *             Thrown if an IO error occurs while processing the page.
	 */
	public void spiderProcessURL(final URL url, final InputStream stream)
			throws IOException {
	}

	/**
	 * Called when the spider is ready to process an HTML URL. For this
	 * SimpleReport manager, this is ignored.
	 * 
	 * @param url
	 *            The URL that the spider is about to process.
	 * @param parse
	 *            An object that will allow you you to parse the HTML on this
	 *            page.
	 * @throws IOException
	 *             Thrown if an IO error occurs while processing the page.
	 */
	public void spiderProcessURL(final URL url, final SpiderParseHTML parse)
			throws IOException {
	}

	/**
	 * Called when the spider tries to process a URL but gets an error. For this
	 * SimpleReport manager, this is ignored.
	 * 
	 * @param url
	 *            The URL that generated an error.
	 */
	public void spiderURLError(final URL url) {
	}

}
