/*
 * Encog Artificial Intelligence Framework v2.x
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
package org.encog.bot.spider.workload;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a Hibernate persisted class that holds the workload for the spider.
 * This becomes a table in the database that holds the spider's workload.
 * 
 * @author jheaton
 * 
 */
@Entity
@Table(name = "spider_workload", uniqueConstraints = { @UniqueConstraint(
		columnNames = { "url" }) })
public class WorkloadItem extends DataObject {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;
	
	/**
	 * The URL this workload is attached to.
	 */
	private String url;
	
	/**
	 * The host.
	 */
	private String host;
	
	/**
	 * The status of this workload item.
	 */
	private char status;
	
	/**
	 * The depth of this URL.
	 */
	private int depth;
	
	/**
	 * The workload item that this workload item was found while processing. 
	 */
	private WorkloadItem source;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final transient Logger logger = LoggerFactory.getLogger(this
			.getClass());

	/**
	 * @return The depth of this workload.
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * @return The host this workload is for.
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * @return The source of this workload item, or null for top level.
	 */
	public WorkloadItem getSource() {
		return this.source;
	}

	/**
	 * @return The status of this workload item, see WorkloadStatus for values.
	 */
	public char getStatus() {
		return this.status;
	}

	/**
	 * @return The URL for this workload.
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Set the depth of this workload.
	 * @param depth The depth of this workload.
	 */
	public void setDepth(final int depth) {
		this.depth = depth;
	}

	/**
	 * The host of this workload.
	 * @param host The host of this workload.
	 */
	public void setHost(final String host) {
		this.host = host;
	}

	/**
	 * Set the source of this workload, null for top level.
	 * @param source The source of this workload.
	 */
	public void setSource(final WorkloadItem source) {
		this.source = source;
	}

	/**
	 * Set the status of this workload.  
	 * @param status The status (see WorkloadStatus).
	 */
	public void setStatus(final char status) {
		this.status = status;
	}

	/**
	 * Set the URL for this workload.
	 * @param url The URL for this workload.
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * @return This object as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append('[');
		result.append(this.status);
		result.append(',');
		result.append(this.url);
		result.append(']');
		return result.toString();
	}

	/**
	 * Validate this object, not really needed.
	 */
	@Override
	public void validate() {

	}
}
