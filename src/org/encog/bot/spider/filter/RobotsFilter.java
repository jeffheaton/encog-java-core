/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.bot.spider.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.encog.bot.BotError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This filter causes the spider to skip URL's from a robots.txt file.
 */
public class RobotsFilter implements SpiderFilter {

	/**
	 * The maximum length of a line.
	 */
	public static final int MAXLINE = 80;

	/**
	 * The full URL of the robots.txt file.
	 */
	private URL robotURL;

	/**
	 * A list of URL's to exclude.
	 */
	private final List<String> exclude = new ArrayList<String>();

	/**
	 * Is the parser active? It can become inactive when parsing sections of the
	 * file for other user agents.
	 */
	private boolean active;

	/**
	 * The user agent string we are to use, null for default.
	 */
	private String userAgent;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add the specified URL to the exclude list.
	 * 
	 * @param str
	 *            The URL to add.
	 */
	private void add(final String str) {
		if (!this.exclude.contains(str)) {
			this.exclude.add(str);
		}
	}

	/**
	 * Returns a list of URL's to be excluded.
	 * 
	 * @return A vector of URL's to be excluded.
	 */
	public List<String> getExclude() {
		return this.exclude;
	}

	/**
	 * Returns the full URL of the robots.txt file.
	 * 
	 * @return The full URL of the robots.txt file.
	 */

	public URL getRobotFile() {
		return this.robotURL;
	}

	/**
	 * Check to see if the specified URL is to be excluded.
	 * 
	 * @param url
	 *            The URL to be checked.
	 * @return Returns true if the URL should be excluded.
	 */
	public boolean isExcluded(final URL url) {
		for (final String str : this.exclude) {
			if (url.getFile().startsWith(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called internally to process each line of the robots.txt file.
	 * 
	 * @param line
	 *            The line that was read in.
	 */
	private void loadLine(final String line) {
		final String str = line.trim();
		final int i = str.indexOf(':');

		if ((str.length() == 0) || (str.charAt(0) == '#') || (i == -1)) {
			return;
		}

		final String command = str.substring(0, i);
		final String rest = str.substring(i + 1).trim();
		if (command.equalsIgnoreCase("User-agent")) {
			this.active = false;
			if (rest.equals("*")) {
				this.active = true;
			} else {
				if ((this.userAgent != null)
						&& rest.equalsIgnoreCase(this.userAgent)) {
					this.active = true;
				}
			}
		}
		if (this.active) {
			if (command.equalsIgnoreCase("disallow")) {
				if (rest.trim().length() > 0) {
					URL url;
					try {
						url = new URL(this.robotURL, rest);
						add(url.getFile());
					} catch (final MalformedURLException e) {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug("Exception", e);
						}

						throw new BotError(e);
					}

				}
			}
		}
	}

	/**
	 * Called when a new host is to be processed. Hosts are processed one at a
	 * time. SpiderFilter classes can not be shared among hosts.
	 * 
	 * @param host
	 *            The new host.
	 * @param userAgent
	 *            The user agent being used by the spider. Leave null for
	 *            default.
	 */
	public void newHost(final String host, final String userAgent) {
		try {
			String str;
			this.active = false;
			this.userAgent = userAgent;

			this.robotURL = new URL("http", host, RobotsFilter.MAXLINE,
					"/robots.txt");

			final URLConnection http = this.robotURL.openConnection();

			if (userAgent != null) {
				http.setRequestProperty("User-Agent", userAgent);
			}

			final InputStream is = http.getInputStream();
			final InputStreamReader isr = new InputStreamReader(is);
			final BufferedReader r = new BufferedReader(isr);

			this.exclude.clear();

			try {
				while ((str = r.readLine()) != null) {

					loadLine(str);
				}
			} finally {
				r.close();
				isr.close();
			}
		} catch (final IOException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Exception", e);
			}
			throw new BotError(e);
		}
	}

}
