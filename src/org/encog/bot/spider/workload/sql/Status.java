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
package org.encog.bot.spider.workload.sql;

/**
 * Status: This class defines the constant status values for
 * both the spider_host and spider_workload tables.
 */
public class Status
{
  /**
   * The item is waiting to be processed.
   */
  public static final String STATUS_WAITING = "W";

  /**
   * The item was processed, but resulted in an error.
   */
  public static final String STATUS_ERROR = "E";

  /**
   * The item was processed successfully.
   */
  public static final String STATUS_DONE = "D";

  /**
   * The item is currently being processed.
   */
  public static final String STATUS_PROCESSING = "P";

  /**
   * This item should be ignored, only applies to hosts.
   */
  public static final String STATUS_IGNORE = "I";
}
