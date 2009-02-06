/*
 * Encog Artificial Intelligence Framework v1.x
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

import java.io.IOException;
import java.net.URL;

/**
 * SpiderFilter: Filters will cause the spider to skip URL's.
 */
public interface SpiderFilter {
	/**
	 * Check to see if the specified URL is to be excluded.
	 * 
	 * @param url
	 *            The URL to be checked.
	 * @return Returns true if the URL should be excluded.
	 */
	boolean isExcluded(URL url);

	/**
	 * Called when a new host is to be processed. Hosts are processed one at a
	 * time. SpiderFilter classes can not be shared among hosts.
	 * 
	 * @param host
	 *            The new host.
	 * @param userAgent
	 *            The user agent being used by the spider. Leave null for
	 *            default.
	 * @throws IOException
	 *             Thrown if an I/O error occurs.
	 */
	void newHost(String host, String userAgent) throws IOException;
}
