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
package org.encog.bot.spider.filter;

import java.io.*;
import java.net.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * SpiderFilter: Filters will cause the spider to skip
 * URL's.
 * 
 * This software is copyrighted. You may use it in programs
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public interface SpiderFilter {
  /**
   * Check to see if the specified URL is to be excluded.
   * 
   * @param url
   *          The URL to be checked.
   * @return Returns true if the URL should be excluded.
   */
  public boolean isExcluded(URL url);

  /**
   * Called when a new host is to be processed. Hosts 
   * are processed one at a time.  SpiderFilter classes 
   * can not be shared among hosts.
   * 
   * @param host
   *          The new host.
   * @param userAgent
   *          The user agent being used by the spider. Leave
   *          null for default.
   * @throws IOException
   *           Thrown if an I/O error occurs.
   */
  public void newHost(String host, String userAgent) throws IOException;
}
