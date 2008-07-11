/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.bot.spider.workload.memory;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.encog.bot.spider.Spider;
import org.encog.bot.spider.workload.WorkloadException;
import org.encog.bot.spider.workload.WorkloadManager;


/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * MemoryWorkloadManager: This class implements a workload
 * manager that stores the list of URL's in memory. This
 * workload manager only supports spidering against a single
 * host.  For multiple hosts use the SQLWorkloadManager.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class MemoryWorkloadManager implements WorkloadManager {
  /**
   * The current workload, a map between URL and URLStatus
   * objects.
   */
  private Map<URL, URLStatus> workload = new HashMap<URL, URLStatus>();

  /**
   * The list of those items, which are already in the
   * workload, that are waiting for processing.
   */
  private BlockingQueue<URL> waiting = new LinkedBlockingQueue<URL>();

  /**
   * How many URL's are currently being processed.
   */
  private int workingCount = 0;

  /*
   * Because the MemoryWorkloadManager only supports a
   * single host, the currentHost is set to the host of the
   * first URL added.
   */
  private String currentHost;

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
   * @throws WorkloadException If any error occurs.
   */
  public boolean add(URL url, URL source, int depth) {
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
   * 
   * @throws WorkloadException
   *           An error prevented the workload from being
   *           cleared.
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
   * @return
   * @throws WorkloadException
   */
  public boolean contains(URL url) {
    return (this.workload.containsKey(url));
  }

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
  public URL convertURL(String url) throws WorkloadException {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new WorkloadException(e);
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
   *          The URL to get the depth of.
   * @return The depth of the specified URL.
   * @throws WorkloadException
   *           Thrown if the depth could not be found.
   */
  public int getDepth(URL url) {
    URLStatus s = this.workload.get(url);
    assert (s != null);
    if (s != null) {
      return s.getDepth();
    } else {
      return 1;
    }
  }

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
  public URL getSource(URL url) {
    URLStatus s = this.workload.get(url);
    if (s == null) {
      return null;
    } else {
      return s.getSource();
    }
  }

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
  public URL getWork() {
    URL url;
    try {
      url = this.waiting.poll(5, TimeUnit.SECONDS);
      if (url != null) {
        setStatus(url, null, URLStatus.Status.WORKING, -1);
        this.workingCount++;
      }
      return url;
    } catch (InterruptedException e) {
      return null;
    }

  }

  /**
   * Setup this workload manager for the specified spider.
   * This method is not used by the MemoryWorkloadManager.
   * 
   * @param spider
   *          The spider using this workload manager.
   * @throws WorkloadException
   *           Thrown if there is an error setting up the
   *           workload manager.
   */
  public void init(Spider spider) throws WorkloadException {
  }

  /**
   * Mark the specified URL as error.
   * 
   * @param url
   *          The URL that had an error.
   * @throws WorkloadException
   *           Thrown if the specified URL could not be
   *           marked.
   */
  public void markError(URL url) {
    this.workingCount--;
    assert this.workingCount > 0;
    this.waiting.remove(url);
    setStatus(url, null, URLStatus.Status.ERROR, -1);

  }

  /**
   * Mark the specified URL as successfully processed.
   * 
   * @param url
   *          The URL to mark as processed.
   * @throws WorkloadException
   *           Thrown if the specified URL could not be
   *           marked.
   */
  public void markProcessed(URL url) {
    this.workingCount--;
    assert this.workingCount > 0;
    this.waiting.remove(url);
    setStatus(url, null, URLStatus.Status.PROCESSED, -1);
  }

  /**
   * Move on to process the next host. This should only be
   * called after getWork returns null. Because the
   * MemoryWorkloadManager is single host only, this
   * function simply returns null.
   * 
   * @return The name of the next host.
   * @throws WorkloadException
   *           Thrown if the workload manager was unable to
   *           move to the next host.
   */
  public String nextHost() {
    return null;
  }

  /**
   * Setup the workload so that it can be resumed from where
   * the last spider left the workload.
   * 
   * @throws WorkloadException
   *           Thrown if we were unable to resume the
   *           processing.
   */
  public void resume() throws WorkloadException {
    throw (new WorkloadException(
        "Memory based workload managers can not resume."));
  }

  /**
   * If there is currently no work available, then wait
   * until a new URL has been added to the workload. Because
   * the MemoryWorkloadManager uses a blocking queue, this
   * method is not needed. It is implemented to support the
   * interface.
   * 
   * @param time
   *          The amount of time to wait.
   * @param length
   *          What tiem unit is being used.
   */
  public void waitForWork(int time, TimeUnit length) {
  }

  /**
   * Return true if there are no more workload units.
   * 
   * @return Returns true if there are no more workload
   *         units.
   * @throws WorkloadException
   *           Thrown if there was an error determining if
   *           the workload is empty.
   */
  public boolean workloadEmpty() {
    if (!this.waiting.isEmpty()) {
      return false;
    }

    return (this.workingCount < 1);
  }

  /**
   * Set the source, status and depth for the specified URL.
   * 
   * @param url
   *          The URL to set.
   * @param source
   *          The source of this URL.
   * @param status
   *          The status of this URL.
   * @param depth
   *          The depth of this URL.
   */
  private void setStatus(URL url, URL source, URLStatus.Status status, int depth) {
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

}
