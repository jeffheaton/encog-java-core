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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * SpiderOptions: This class contains options for the spider's execution.
 */
public class SpiderOptions {

	/**
	 * The default timeout.
	 */
	public static final int DEFAULT_TIMEOUT = 60000;

	/**
	 * The default maximum depth is no maximum depth.
	 */
	public static final int DEFAULT_MAX_DEPTH = -1;

	/**
	 * The default core pool size.
	 */
	public static final int DEFAULT_CORE_POOL_SIZE = 100;

	/**
	 * The default min pool size.
	 */
	public static final int DEFAULT_MIN_POOL_SIZE = 100;

	/**
	 * The default keep-alive time.
	 */
	public static final int DEFAULT_KEEP_ALIVE = 60;

	/**
	 * Specifies that when the spider starts up it should clear the workload.
	 */
	public static final String STARTUP_CLEAR = "CLEAR";

	/**
	 * Specifies that the spider should resume processing its workload.
	 */
	public static final String STARTUP_RESUME = "RESUME";

	/**
	 * How many milliseconds to wait when downloading pages.
	 */
	private int timeout = SpiderOptions.DEFAULT_TIMEOUT;

	/**
	 * The maximum depth to search pages. -1 specifies no maximum depth.
	 */
	private int maxDepth = SpiderOptions.DEFAULT_MAX_DEPTH;

	/**
	 * What user agent should be reported to the web site. This allows the web
	 * site to determine what browser is being used.
	 */
	private String userAgent = null;

	/**
	 * The core thread pool size.
	 */
	private int corePoolSize = SpiderOptions.DEFAULT_CORE_POOL_SIZE;

	/**
	 * The maximum thread pool size.
	 */
	private int maximumPoolSize = SpiderOptions.DEFAULT_MIN_POOL_SIZE;

	/**
	 * How long to keep inactive threads alive. Measured in seconds.
	 */
	private long keepAliveTime = SpiderOptions.DEFAULT_KEEP_ALIVE;

	/**
	 * The URL to use for JDBC databases. Used to hold the workload.
	 */
	private String dbURL;

	/**
	 * The user id to use for JDBC databases. Used to hold the workload.
	 */
	private String dbUID;

	/**
	 * The password to use for JDBC databases. Used to hold the workload.
	 */
	private String dbPWD;

	/**
	 * The class to use for JDBC connections. Used to hold the workload.
	 */
	private String dbClass;

	/**
	 * What class to use as a workload manager.
	 */
	private String workloadManager;

	/**
	 * What to do when the spider starts up. This specifies if the workload
	 * should be cleared, or resumed.
	 */
	private String startup = STARTUP_CLEAR;

	/**
	 * Specifies a class to be used a filter.
	 */
	private List<String> filter = new ArrayList<String>();

	/**
	 * @return the corePoolSize
	 */
	public int getCorePoolSize() {
		return this.corePoolSize;
	}

	/**
	 * @return the dbClass
	 */
	public String getDbClass() {
		return this.dbClass;
	}

	/**
	 * @return the dbPWD
	 */
	public String getDbPWD() {
		return this.dbPWD;
	}

	/**
	 * @return the dbUID
	 */
	public String getDbUID() {
		return this.dbUID;
	}

	/**
	 * @return the dbURL
	 */
	public String getDbURL() {
		return this.dbURL;
	}

	/**
	 * @return the filter
	 */
	public List<String> getFilter() {
		return this.filter;
	}

	/**
	 * @return the keepAliveTime
	 */
	public long getKeepAliveTime() {
		return this.keepAliveTime;
	}

	/**
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return this.maxDepth;
	}

	/**
	 * @return the maximumPoolSize
	 */
	public int getMaximumPoolSize() {
		return this.maximumPoolSize;
	}

	/**
	 * @return the startup
	 */
	public String getStartup() {
		return this.startup;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return this.userAgent;
	}

	/**
	 * @return the workloadManager
	 */
	public String getWorkloadManager() {
		return this.workloadManager;
	}

	/**
	 * Load the spider settings from a configuration file.
	 * 
	 * @param inputFile
	 *            The name of the configuration file.
	 */
	public void load(final String inputFile) {
		try {
			final FileReader f = new FileReader(new File(inputFile));
			final BufferedReader r = new BufferedReader(f);
			String line;
			while ((line = r.readLine()) != null) {

				parseLine(line);
			}
			r.close();
			f.close();
		} catch (final IllegalArgumentException e) {
			throw new SpiderError(e);
		} catch (final SecurityException e) {
			throw new SpiderError(e);
		} catch (final IOException e) {
			throw new SpiderError(e);
		}

	}

	/**
	 * Process each line of a configuration file.
	 * 
	 * @param line
	 *            The line of text read from the configuration file.
	 */
	@SuppressWarnings("unchecked")
	private void parseLine(final String line) {
		String name, value;
		final int i = line.indexOf(':');
		if (i == -1) {
			return;
		}
		name = line.substring(0, i).trim();
		value = line.substring(i + 1).trim();

		if (value.trim().length() == 0) {
			value = null;
		}

		try {
			final Field field = this.getClass().getField(name);
			if (field.getType() == String.class) {
				field.set(this, value);
			} else if (field.getType() == List.class) {
				final List<String> list = (List<String>) field.get(this);
				list.add(value);
			} else {
				final int x = Integer.parseInt(value);
				field.set(this, x);
			}
		} catch (final SecurityException e) {
			throw new SpiderError(e);
		} catch (final NoSuchFieldException e) {
			throw new SpiderError(e);
		} catch (final IllegalArgumentException e) {
			throw new SpiderError(e);
		} catch (final IllegalAccessException e) {
			throw new SpiderError(e);
		}
	}

	/**
	 * @param corePoolSize
	 *            the corePoolSize to set
	 */
	public void setCorePoolSize(final int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	/**
	 * @param dbClass
	 *            the dbClass to set
	 */
	public void setDbClass(final String dbClass) {
		this.dbClass = dbClass;
	}

	/**
	 * @param dbPWD
	 *            the dbPWD to set
	 */
	public void setDbPWD(final String dbPWD) {
		this.dbPWD = dbPWD;
	}

	/**
	 * @param dbUID
	 *            the dbUID to set
	 */
	public void setDbUID(final String dbUID) {
		this.dbUID = dbUID;
	}

	/**
	 * @param dbURL
	 *            the dbURL to set
	 */
	public void setDbURL(final String dbURL) {
		this.dbURL = dbURL;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(final List<String> filter) {
		this.filter = filter;
	}

	/**
	 * @param keepAliveTime
	 *            the keepAliveTime to set
	 */
	public void setKeepAliveTime(final long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	/**
	 * @param maxDepth
	 *            the maxDepth to set
	 */
	public void setMaxDepth(final int maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * @param maximumPoolSize
	 *            the maximumPoolSize to set
	 */
	public void setMaximumPoolSize(final int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	/**
	 * @param startup
	 *            the startup to set
	 */
	public void setStartup(final String startup) {
		this.startup = startup;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(final String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * @param workloadManager
	 *            the workloadManager to set
	 */
	public void setWorkloadManager(final String workloadManager) {
		this.workloadManager = workloadManager;
	}

}
