/*
  * Encog Neural Network and Bot Library for Java v0.5
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
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.encog.bot.spider.workload.WorkloadException;


/**
 * SpiderWorker: This class forms the workloads that are
 * passed onto the thread pool.
 */
public class SpiderWorker implements Runnable {
  /**
   * The logger.
   */
  private static Logger logger = Logger
      .getLogger("com.heatonresearch.httprecipes.spider.SpiderWorker");

  /**
   * The URL being processed.
   */
  private URL url;

  /**
   * The Spider object that this worker belongs to.
   */
  private Spider spider;

  /**
   * Construct a SpiderWorker object.
   * 
   * @param spider
   *          The spider this worker will work with.
   * @param url
   *          The URL to be processed.
   */
  public SpiderWorker(Spider spider, URL url) {
    this.spider = spider;
    this.url = url;
  }

  /*
   * This method is called by the thread pool to process one
   * single URL.
   */
  public void run() {
    URLConnection connection = null;
    InputStream is = null;

    try {
      logger.fine("Processing: " + this.url);
      // get the URL's contents
      connection = this.url.openConnection();
      connection.setConnectTimeout(this.spider.getOptions().timeout);
      connection.setReadTimeout(this.spider.getOptions().timeout);
      if (this.spider.getOptions().userAgent != null) {
        connection.setRequestProperty("User-Agent",
            this.spider.getOptions().userAgent);
      }

      // read the URL
      is = connection.getInputStream();

      // parse the URL
      String contentType = connection.getContentType(); 
      if (contentType.toLowerCase().startsWith("text/html")) {
        SpiderParseHTML parse = new SpiderParseHTML(connection.getURL(),
            new SpiderInputStream(is, null), this.spider);
        this.spider.getReport().spiderProcessURL(this.url, parse);
      } else {
        this.spider.getReport().spiderProcessURL(this.url, is);
      }

    } catch (IOException e) {
      logger.log(Level.INFO, "I/O error on URL:" + this.url.toString());
      try {
        this.spider.getWorkloadManager().markError(this.url);
      } catch (WorkloadException e1) {
        logger.log(Level.WARNING, "Error marking workload(1).", e);
      }
      this.spider.getReport().spiderURLError(this.url);
      return;
    } catch (Throwable e) {
      try {
        this.spider.getWorkloadManager().markError(this.url);
      } catch (WorkloadException e1) {
        logger.log(Level.WARNING, "Error marking workload(2).", e);
      }

      logger.log(Level.SEVERE, "Caught exception at URL:" + this.url.toString(), e);
      this.spider.getReport().spiderURLError(this.url);
      return;
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
        }
      }
    }

    try {
      // mark URL as complete
      this.spider.getWorkloadManager().markProcessed(this.url);
      logger.fine("Complete: " + this.url);
      if (!this.url.equals(connection.getURL())) {
        // save the URL(for redirect's)
        this.spider.getWorkloadManager().add(connection.getURL(), this.url,
            this.spider.getWorkloadManager().getDepth(connection.getURL()));
        this.spider.getWorkloadManager().markProcessed(connection.getURL());
      }
    } catch (WorkloadException e) {
      logger.log(Level.WARNING, "Error marking workload(3).", e);
    }

  }

}
