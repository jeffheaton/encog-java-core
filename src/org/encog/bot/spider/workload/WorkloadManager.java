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

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.encog.bot.spider.Spider;

/**
 * WorkloadManager: This interface defines a workload
 * manager. A workload manager handles the lists of URLs
 * that have been processed, resulted in an error, and 
 * are waiting to be processed.
 */
public interface WorkloadManager {
	/**
	 * Add the specified URL to the workload.
	 * 
	 * @param url
	 *          The URL to be added.
	 * @param source
	 *          The page that contains this URL.
	 * @param depth
	 *          The depth of this URL.
	 * @return True if the URL was added, false otherwise.
	 * @throws WorkloadException
	 */
	public boolean add(URL url, URL source, int depth);

	/**
	 * Clear the workload.
	 * 
	 * @throws WorkloadException
	 *           An error prevented the workload from being
	 *           cleared.
	 */
	public void clear();

	/**
	 * Determine if the workload contains the specified URL.
	 * 
	 * @param url The URL to search for.
	 * @return True if the specified URL is contained.
	 * @throws WorkloadException
	 */
	public boolean contains(URL url);

	/**
	 * Convert the specified String to a URL. If the string is
	 * too long or has other issues, throw a
	 * WorkloadException.
	 * 
	 * @param url
	 *          A String to convert into a URL.
	 * @return The URL.
	 * @throws WorkloadException
	 *           Thrown if, The String could not be 
	 *           converted.
	 */
	public URL convertURL(String url);

	/**
	 * Get the current host.
	 * 
	 * @return The current host.
	 */
	public String getCurrentHost();

	/**
	 * Get the depth of the specified URL.
	 * 
	 * @param url
	 *          The URL to get the depth of.
	 * @return The depth of the specified URL.
	 * @throws WorkloadException
	 *           Thrown if the depth could not be found.
	 */
	public int getDepth(URL url);

	/**
	 * Get the source page that contains the specified URL.
	 * 
	 * @param url
	 *          The URL to seek the source for.
	 * @return The source of the specified URL.
	 * @throws WorkloadException
	 *           Thrown if the source of the specified URL
	 *           could not be found.
	 */
	public URL getSource(URL url);

	/**
	 * Get a new URL to work on. Wait if there are no URL's
	 * currently available. Return null if done with the
	 * current host. The URL being returned will be marked as
	 * in progress.
	 * 
	 * @return The next URL to work on,
	 * @throws WorkloadException
	 *           Thrown if the next URL could not be obtained.
	 */
	public URL getWork();

	/**
	 * Setup this workload manager for the specified spider.
	 * 
	 * @param spider
	 *          The spider using this workload manager.
	 * @throws WorkloadException
	 *           Thrown if there is an error setting up the
	 *           workload manager.
	 */
	public void init(Spider spider);

	/**
	 * Mark the specified URL as error.
	 * 
	 * @param url
	 *          The URL that had an error.
	 * @throws WorkloadException
	 *           Thrown if the specified URL could not be
	 *           marked.
	 */
	public void markError(URL url);

	/**
	 * Mark the specified URL as successfully processed.
	 * 
	 * @param url
	 *          The URL to mark as processed.
	 * @throws WorkloadException
	 *           Thrown if the specified URL could not be
	 *           marked.
	 */
	public void markProcessed(URL url);

	/**
	 * Move on to process the next host. This should only be
	 * called after getWork returns null.
	 * 
	 * @return The name of the next host.
	 * @throws WorkloadException
	 *           Thrown if the workload manager was unable to
	 *           move to the next host.
	 */
	public String nextHost();

	/**
	 * Setup the workload so that it can be resumed from where
	 * the last spider left the workload.
	 * 
	 * @throws WorkloadException
	 *           Thrown if we were unable to resume the
	 *           processing.
	 */
	public void resume();

	/**
	 * If there is currently no work available, then wait
	 * until a new URL has been added to the workload.
	 * 
	 * @param time
	 *          The amount of time to wait.
	 * @param length
	 *          What time unit is being used.
	 */
	public void waitForWork(int time, TimeUnit length);

	/**
	 * Return true if there are no more workload units.
	 * 
	 * @return Returns true if there are no more workload
	 *         units.
	 * @throws WorkloadException
	 *           Thrown if there was an error determining if
	 *           the workload is empty.
	 */
	public boolean workloadEmpty();

}
