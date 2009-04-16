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
package org.encog.bot.spider;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.encog.util.orm.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a Hibernate persisted class that holds the workload for the spider.
 * This becomes a table in the database that holds the spider's workload.
 * @author jheaton
 *
 */
@Entity
@Table(name="spider_workload",
    uniqueConstraints = {@UniqueConstraint(columnNames={"url"})} )
public class WorkloadItem extends DataObject {
	

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;
	private String url;
	private String host;
	private char status;
	private int depth;
	private WorkloadItem source;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}
	
	

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	

	public WorkloadItem getSource() {
		return source;
	}

	public void setSource(WorkloadItem source) {
		this.source = source;
	}

	@Override
	public void validate() {
		// TODO Auto-generated method stub
		
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append('[');
		result.append(this.status);
		result.append(',');
		result.append(this.url);
		result.append(']');
		return result.toString();
	}
}
