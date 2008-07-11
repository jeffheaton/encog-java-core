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

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * URLStatus: This class holds in memory status information
 * for URLs. Specifically it holds their processing status,
 * depth and source URL.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class URLStatus {
  /*
   * The values for URL statues.
   * 
   * WAITING - Waiting to be processed. 
   * PROCESSED - Successfully processed. 
   * ERROR - Unsuccessfully processed. 
   * WORKING - Currently being processed.
   */
  public enum Status {
    WAITING, PROCESSED, ERROR, WORKING
  };

  /*
   * The current status of this URL.
   */
  private Status status;

  /*
   * The depth of this URL.
   */
  private int depth;

  /*
   * The source of this URL.
   */
  private URL source;

  /**
   * @return the depth
   */
  public int getDepth() {
    return this.depth;
  }

  /**
   * @return the source
   */
  public URL getSource() {
    return this.source;
  }

  /**
   * @return the status
   */
  public Status getStatus() {
    return this.status;
  }

  /**
   * @param depth
   *          the depth to set
   */
  public void setDepth(int depth) {
    this.depth = depth;
  }

  /**
   * @param source
   *          the source to set
   */
  public void setSource(URL source) {
    this.source = source;
  }

  /**
   * @param status
   *          the status to set
   */
  public void setStatus(Status status) {
    this.status = status;
  }

}
