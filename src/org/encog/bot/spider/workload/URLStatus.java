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

/**
 * URLStatus: This class holds in memory status information for URLs.
 * Specifically it holds their processing status, depth and source URL.
 */
public class URLStatus {
	/**
	 * The values for URL statues.
	 */
	public enum Status {
		/**
		 * WAITING - Waiting to be processed. 
		 */
		WAITING, 
		
		/**
		 * PROCESSED - Successfully processed. 
		 */
		PROCESSED,
		
		/**
		 * ERROR - Unsuccessfully processed. 
		 */		
		ERROR,
		
		/**
		 * WORKING - Currently being processed.
		 */
		WORKING
	}

	/**
	 * The current status of this URL.
	 */
	private Status status;

	/**
	 * The depth of this URL.
	 */
	private int depth;

	/**
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
	 *            the depth to set
	 */
	public void setDepth(final int depth) {
		this.depth = depth;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(final URL source) {
		this.source = source;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Status status) {
		this.status = status;
	}

}
