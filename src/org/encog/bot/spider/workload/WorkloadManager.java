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
package org.encog.bot.spider.workload;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.encog.bot.spider.Spider;

/**
 * MemoryWorkloadManager: This class implements a workload manager that stores
 * the list of URL's in memory. This workload manager only supports spidering
 * against a single host. For multiple hosts use the SQLWorkloadManager.
 */
public class WorkloadManager {
	
	/**
	 * How many seconds to wait for work.
	 */
	public static final int WAIT_FOR_WORK = 5;
	
	/**
	 * The current workload, a map between URL and URLStatus objects.
	 */
	private final Map<URL, URLStatus> workload = new HashMap<URL, URLStatus>();

	/**
	 * The list of those items, which are already in the workload, that are
	 * waiting for processing.
	 */
	private final BlockingQueue<URL> waiting = new LinkedBlockingQueue<URL>();

	/**
	 * How many URL's are currently being processed.
	 */
	private int workingCount = 0;

	/**
	 * Because the MemoryWorkloadManager only supports a single host, the
	 * currentHost is set to the host of the first URL added.
	 */
	private String currentHost;

	/**
	 * Add the specified URL to the workload.
	 * 
	 * @param url
	 *            The URL to be added.
	 * @param source
	 *            The page that contains this URL.
	 * @param depth
	 *            The depth of this URL.
	 * @return True if the URL was added, false otherwise.
	 */
	public boolean add(final URL url, final URL source, final int depth) {
		if (!contains(url)) {
			this.waiting.add(url);
			setStatus(url, source, URLStatus.Status.WAITING, depth);
			if (this.currentHost == null) {
				this.currentHost = url.getHost().toLowerCase();
			}
			return true;
		}
		return false;

	}

	/**
	 * Clear the workload.
	 */
	public void clear() {
		this.workload.clear();
		this.waiting.clear();
		this.workingCount = 0;
	}

	/**
	 * Determine if the workload contains the specified URL.
	 * 
	 * @param url
	 *            The URL to check.
	 * @return True if the URL is contained by the workload.
	 */
	public boolean contains(final URL url) {
		return this.workload.containsKey(url);
	}

	/**
	 * Convert the specified String to a URL. If the string is too long or has
	 * other issues, throw a WorkloadException.
	 * 
	 * @param url
	 *            A String to convert into a URL.
	 * @return The URL.
	 */
	public URL convertURL(final String url) {
		try {
			return new URL(url);
		} catch (final MalformedURLException e) {
			throw new WorkloadError(e);
		}
	}

	/**
	 * Get the current host.
	 * 
	 * @return The current host.
	 */
	public String getCurrentHost() {
		return this.currentHost;
	}

	/**
	 * Get the depth of the specified URL.
	 * 
	 * @param url
	 *            The URL to get the depth of.
	 * @return The depth of the specified URL.
	 */
	public int getDepth(final URL url) {
		final URLStatus s = this.workload.get(url);
		assert s != null;
		return s.getDepth();
	}

	/**
	 * Get the source page that contains the specified URL.
	 * 
	 * @param url
	 *            The URL to seek the source for.
	 * @return The source of the specified URL.
	 */
	public URL getSource(final URL url) {
		final URLStatus s = this.workload.get(url);
		if (s == null) {
			return null;
		}
		return s.getSource();
	}

	/**
	 * Get a new URL to work on. Wait if there are no URL's currently available.
	 * Return null if done with the current host. The URL being returned will be
	 * marked as in progress.
	 * 
	 * @return The next URL to work on,
	 */
	public URL getWork() {
		URL url;
		try {
			url = this.waiting.poll(WAIT_FOR_WORK, TimeUnit.SECONDS);
			if (url != null) {
				setStatus(url, null, URLStatus.Status.WORKING, -1);
				this.workingCount++;
			}
			return url;
		} catch (final InterruptedException e) {
			return null;
		}

	}

	/**
	 * Setup this workload manager for the specified spider. This method is not
	 * used by the MemoryWorkloadManager.
	 * 
	 * @param spider
	 *            The spider using this workload manager.
	 */
	public void init(final Spider spider) {
	}

	/**
	 * Mark the specified URL as error.
	 * 
	 * @param url
	 *            The URL that had an error.
	 */
	public void markError(final URL url) {
		this.workingCount--;
		assert this.workingCount > 0;
		this.waiting.remove(url);
		setStatus(url, null, URLStatus.Status.ERROR, -1);

	}

	/**
	 * Mark the specified URL as successfully processed.
	 * 
	 * @param url
	 *            The URL to mark as processed.
	 */
	public void markProcessed(final URL url) {
		this.workingCount--;
		assert this.workingCount > 0;
		this.waiting.remove(url);
		setStatus(url, null, URLStatus.Status.PROCESSED, -1);
	}

	/**
	 * Move on to process the next host. This should only be called after
	 * getWork returns null. Because the MemoryWorkloadManager is single host
	 * only, this function simply returns null.
	 * 
	 * @return The name of the next host.
	 */
	public String nextHost() {
		return null;
	}

	/**
	 * Setup the workload so that it can be resumed from where the last spider
	 * left the workload.
	 */
	public void resume() {
		throw new WorkloadError(
				"Memory based workload managers can not resume.");
	}

	/**
	 * Set the source, status and depth for the specified URL.
	 * 
	 * @param url
	 *            The URL to set.
	 * @param source
	 *            The source of this URL.
	 * @param status
	 *            The status of this URL.
	 * @param depth
	 *            The depth of this URL.
	 */
	private void setStatus(final URL url, final URL source,
			final URLStatus.Status status, final int depth) {
		URLStatus s = this.workload.get(url);
		if (s == null) {
			s = new URLStatus();
			this.workload.put(url, s);
		}
		s.setStatus(status);

		if (source != null) {
			s.setSource(source);
		}

		if (depth != -1) {
			s.setDepth(depth);
		}
	}

	/**
	 * If there is currently no work available, then wait until a new URL has
	 * been added to the workload. Because the MemoryWorkloadManager uses a
	 * blocking queue, this method is not needed. It is implemented to support
	 * the interface.
	 * 
	 * @param time
	 *            The amount of time to wait.
	 * @param length
	 *            What tiem unit is being used.
	 */
	public void waitForWork(final int time, final TimeUnit length) {
	}

	/**
	 * Return true if there are no more workload units.
	 * 
	 * @return Returns true if there are no more workload units.
	 */
	public boolean workloadEmpty() {
		if (!this.waiting.isEmpty()) {
			return false;
		}

		return this.workingCount < 1;
	}

}
