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
package org.encog.bot.spider.workload.sql;

/**
 * SQLHolder: This class holds the SQL statements used by the Heaton Research
 * Spider. This is the generic set that should work with most DBMS systems.
 * However you will also find customized versions of this class to support
 * specific DBMS systems, when it is needed.
 */
public class SQLHolder {

	/**
	 * Add to the workload. 
	 * @return A SQL command.
	 */
	public String getSQLAdd() {
		return "INSERT INTO spider_workload(host,url,status,depth,url_hash,"
		+ "source_id) VALUES(?,?,?,?,?,?)";
	}

	/**
	 * Add to the hosts list.
	 * @return A SQL command.
	 */
	public String getSQLAdd2() {
		return "INSERT INTO spider_host(host,status,urls_done,urls_error) "
		+ "VALUES(?,?,?,?)";
	}

	/**
	 * Clear the workload.
	 * @return A SQL command.
	 */
	public String getSQLClear() {
		return "DELETE FROM spider_workload";
	}

	/**
	 * Clear the host table.
	 * @return A SQL command.
	 */
	public String getSQLClear2() {
		return "DELETE FROM spider_host WHERE status <> 'I'";
	}

	/**
	 * Get the depth of a URL.
	 * @return A SQL command.
	 */
	public String getSQLGetDepth() {
		return "SELECT url,depth FROM spider_workload WHERE url_hash =  ?";
	}

	/**
	 * Find a host.
	 * @return A SQL command.
	 */
	public String getSQLGetHost() {
		return "SELECT host FROM spider_host WHERE host_id =  ?";
	}

	/**
	 * Find a host ID.
	 * @return A SQL command.
	 */
	public String getSQLGetHostID() {
		return "SELECT host_id,host FROM spider_host WHERE host =  ?";
	}

	/**
	 * Get the next host to process.
	 * @return A SQL command.
	 */
	public String getSQLGetNextHost() {
		return "SELECT host_id,host FROM spider_host WHERE status =  ?";
	}

	/**
	 * Get the source for a URL.
	 * @return A SQL command.
	 */
	public String getSQLGetSource() {
		return "SELECT w.url,s.url FROM spider_workload w,spider_workload s " 
		+ "WHERE w.source_id =  s.workload_id AND w.url_hash =  ?";
	}

	/**
	 * Get a URL from the workload.
	 * @return A SQL command.
	 */
	public String getSQLGetWork() {
		return "SELECT workload_id,URL FROM spider_workload WHERE status =  ? " 
		+ "AND host = ?";
	}

	/**
	 * Set the status of a workload element.
	 * @return A SQL command.
	 */
	public String getSQLGetWork2() {
		return "UPDATE spider_workload SET status =  ? WHERE workload_id = ?";
	}

	/**
	 * Get the workload id for a url. 
	 * @return A SQL command.
	 */
	public String getSQLGetWorkloadID() {
		return "SELECT workload_id,url FROM spider_workload WHERE url_hash " 
		+ "=  ?";
	}

	/**
	 * Resume an aborted spider run.
	 * @return A SQL command.
	 */
	public String getSQLResume() {
		return "SELECT distinct host FROM spider_workload WHERE status =  'P'";
	}

	/**
	 * Resume an aborted spider run.
	 * @return A SQL command.
	 */
	public String getSQLResume2() {
		return "UPDATE spider_workload SET status =  'W' WHERE status =  'P'";
	}

	/**
	 * Update the status of a host.
	 * @return A SQL command.
	 */
	public String getSQLSetHostStatus() {
		return "UPDATE spider_host SET status =  ? WHERE host_id =  ?";
	}
	
	/**
	 * Update the status of a workload.
	 * @return A SQL command.
	 */
	public String getSQLSetWorkloadStatus() {
		return "UPDATE spider_workload SET status =  ? WHERE workload_id =  ?";
	}

	/**
	 * Get a workload status.
	 * @return A SQL command.
	 */
	public String getSQLSetWorkloadStatus2() {
		return "UPDATE spider_host SET urls_done =  urls_done + ?, " 
		+ "urls_error =  urls_error + ? WHERE host =  ?";
	}

	/**
	 * Determine if the workload is empty.
	 * @return A SQL command.
	 */
	public String getSQLWorkloadEmpty() {
		return "SELECT COUNT(*) FROM spider_workload WHERE status in " 
		+ "('P','W') AND host =  ?";
	}

}
