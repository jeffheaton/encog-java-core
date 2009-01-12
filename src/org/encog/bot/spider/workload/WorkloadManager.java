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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.encog.bot.spider.Spider;
import org.encog.bot.spider.workload.WorkloadError;
import org.encog.bot.spider.workload.data.WorkloadHost;
import org.encog.bot.spider.workload.data.WorkloadLocation;
import org.encog.bot.spider.workload.data.WorkloadStatus;
import org.encog.nlp.lexicon.data.Word;
import org.encog.util.orm.ORMSession;
import org.hibernate.Query;


/**
 * SQLWorkloadManager: This workload manager stores the URL lists in an SQL
 * database. This workload manager uses two tables, which can be created as
 * follows:
 * 
 * CREATE TABLE 'spider_host' ( 'host_id' int(10) unsigned NOT NULL
 * auto_increment, 'host' varchar(255) NOT NULL default '', 'status' varchar(1)
 * NOT NULL default '', 'urls_done' int(11) NOT NULL, 'urls_error' int(11) NOT
 * NULL, PRIMARY KEY ('host_id') )
 * 
 * CREATE TABLE 'spider_workload' ( 'workload_id' int(10) unsigned NOT NULL
 * auto_increment, 'host' int(10) unsigned NOT NULL, 'url' varchar(2083) NOT
 * NULL default '', 'status' varchar(1) NOT NULL default '', 'depth' int(10)
 * unsigned NOT NULL, 'url_hash' int(11) NOT NULL, 'source_id' int(11) NOT NULL,
 * PRIMARY KEY ('workload_id'), KEY 'status' ('status'), KEY 'url_hash'
 * ('url_hash'), KEY 'host' ('host') )
 */
public class WorkloadManager {
	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(
	"com.heatonresearch.httprecipes.spider.workload.sql.SQLWorkloadManager");

	
	/**
	 * The mask used to generate URL hash's.
	 */
	public static final int HASH_MASK = 0xffff;
	
	/**
	 * Only one thread at a time is allowed to add to the workload.
	 */
	private Semaphore addLock;

	/**
	 * Is there any work?
	 */
	private CountDownLatch workLatch;

	/**
	 * The maximum size a URL can be.
	 */
	private int maxURLSize;

	/**
	 * The maximum size that a host can be.
	 */
	private int maxHostSize;
	
	private ORMSession session;

	/**
	 * The current host.
	 */
	private WorkloadHost currentHost;
	
	public WorkloadManager(ORMSession session)
	{
		this.session = session;
	}

	/**
	 * Add the specified URL to the workload.
	 * 
	 * @param url
	 *            The URL to be added.
	 * @param source
	 *            The page that contains this URL.
	 * @param depth
	 *            The depth of this URL.
	 * @return True if the URL was added, false otherwise.
	 * @throws WorkloadException
	 */
	public boolean add(final URL url, final URL source, final int depth) {
		boolean result = false;
		try {
			this.addLock.acquire();
			if (!contains(url)) {
				final String strURL = truncate(url.toString(), this.maxURLSize);
				final String strHost = truncate(url.getHost(), this.maxHostSize)
						.toLowerCase();
				result = true;

				// get the host
				WorkloadHost host = getHost(url, false);

				if (host == null) {
					host = new WorkloadHost();
					host.setHost(strHost);
					session.save(host);
				}

				// need to set the current host for the first time?
				if (this.currentHost == null) {
					this.currentHost = host;
							
					host.setStatus(WorkloadStatus.WORKING);

				}

				// now add workload element
				
				WorkloadLocation location = new WorkloadLocation();
				
				if( source==null )
					location.setSource(null);
				else
					location.setSource(source.toString());
				location.setStatus(WorkloadStatus.WAITING);
				location.setHost(this.currentHost);
				location.setHash(computeHash(url));
				location.setDepth(depth);
				location.setUrl(url.toString());
				this.session.save(location);
				
				this.workLatch.countDown();
			}

		} catch (final InterruptedException e) {
			throw new WorkloadError(e);			
		} finally {
			this.addLock.release();
		}
		return result;
	}

	/**
	 * Clear the workload.
	 */
	public void clear() {
		//this.stmtClear.execute();
		//this.stmtClear2.execute();
	}

	/**
	 * Close the workload manager.
	 */
	public void close() {
		this.session.close();
	}

	/**
	 * Compute a hash for a URL.
	 * 
	 * @param url
	 *            The URL to compute the hash for.
	 * @return The hash code.
	 */
	private int computeHash(final URL url) {
		final String str = url.toString().trim();

		int result = str.hashCode();
		result = result % WorkloadManager.HASH_MASK;
		return result;
	}

	/**
	 * Determine if the workload contains the specified URL.
	 * 
	 * @param url
	 *            The URL to search the workload for.
	 * @return True of the workload contains the specified URL. @
	 */
	public boolean contains(final URL url) {
		return this.getLocation(url)!=null;
	}

	/**
	 * Convert the specified String to a URL. If the string is too long or has
	 * other issues, throw a WorkloadException.
	 * 
	 * @param aurl
	 *            A String to convert into a URL.
	 * @return The URL. @ Thrown if, The String could not be converted.
	 */
	public URL convertURL(final String aurl) {
		URL result = null;

		final String url = aurl.trim();
		if (this.maxURLSize != -1 && url.length() > this.maxURLSize) {
			throw new WorkloadError("URL size is too big, must be under "
					+ this.maxURLSize + " bytes.");
		}

		try {
			result = new URL(url);
		} catch (final MalformedURLException e) {
			throw new WorkloadError(e);
		}
		return result;
	}


	/**
	 * Return the size of the specified column.
	 * 
	 * @param table
	 *            The table that contains the column.
	 * @param column
	 *            The column to get the size for.
	 * @return The size of the column.
	 */
	public int getColumnSize(final String table, final String column) {
		return -1;
	}

	/**
	 * Get the depth of the specified URL.
	 * 
	 * @param url
	 *            The URL to get the depth of.
	 * @return The depth of the specified URL. @ Thrown if the depth could not
	 *         be found.
	 */
	public int getDepth(final URL url) {
		WorkloadLocation location = getLocation(url);
		if( location==null )
			return -1;
		else
			return location.getDepth();
	}


	/**
	 * Get the id for the specified host name.
	 * 
	 * @param host
	 *            The host to lookup.
	 * @param require
	 *            Should an exception be thrown if the host is not located.
	 * @return The id of the specified host name. @ Thrown if the host id is not
	 *         found, and is required.
	 * @throws SQLException
	 *             Thrown if a SQL error occurs.
	 */
	private WorkloadHost getHost(final String host, final boolean require)
	{
		// is this the current host?
		if (this.currentHost != null) {
			if (this.currentHost.getHost().equalsIgnoreCase(host)) {
				return this.currentHost;
			}
		}
		
		Query q = session.createQuery("from org.encog.bot.spider.workload.data.WorkloadHost where host = :h");
		q.setString("h", host);
		WorkloadHost result = (WorkloadHost)q.uniqueResult();		

		if (require) {
			final StringBuilder str = new StringBuilder();
			str.append("Failed to find previously visited Host,");
			str.append("Host=\"");
			str.append(host);
			str.append("\".");
			throw new WorkloadError(str.toString());
		}		
		
		return result;
	}

	/**
	 * Get the ID for the given host. The host name is extracted from the
	 * specified URL.
	 * 
	 * @param url
	 *            The URL that specifies the host name to lookup.
	 * @param require
	 *            Should an exception be thrown if the host is not located.
	 * @return The id of the specified host name. @ Thrown if the host id is not
	 *         found, and is required.
	 * @throws SQLException
	 *             Thrown if a SQL error occurs.
	 */
	private WorkloadHost getHost(final URL url, final boolean require)
	{
		final String host = url.getHost().toLowerCase();
		return getHost(host, require);
	}
	
	private WorkloadLocation getLocation(final URL url)
	{
		Query q = session.createQuery("from org.encog.bot.spider.workload.data.WorkloadLocation where url = :u");
		q.setString("u", url.toString());
		WorkloadLocation result = (WorkloadLocation)q.uniqueResult();		
		return result;
	}

	/**
	 * Get the source page that contains the specified URL.
	 * 
	 * @param url
	 *            The URL to seek the source for.
	 * @return The source of the specified URL. @ Thrown if the source of the
	 *         specified URL could not be found.
	 */
	public URL getSource(final URL url) {
		WorkloadLocation result = getLocation(url);
		if( result==null )
			return null;
		else
		{
			URL r = null;
			try
			{
				r = new URL(result.getSource());
			}
			catch(MalformedURLException e)
			{
				throw new WorkloadError(e);
			}
			return r;
		}
	}

	/**
	 * Get a new URL to work on. Wait if there are no URL's currently available.
	 * Return null if done with the current host. The URL being returned will be
	 * marked as in progress.
	 * 
	 * @return The next URL to work on, @ Thrown if the next URL could not be
	 *         obtained.
	 */
	public WorkloadLocation getWork() {
		WorkloadLocation url = null;
		do {
			url = getWorkInternal();
			if (url == null) {
				if (workloadEmpty()) {
					break;
				}

			}
		} while (url == null);

		return url;
	}

	/**
	 * Called internally to get a work unit. This function does not wait for
	 * work, rather it simply returns null.
	 * 
	 * @return The next URL to process. @ Thrown if unable to obtain a URL.
	 */
	private WorkloadLocation getWorkInternal() {
		if (this.currentHost == null ) {
			throw new WorkloadError(
					"Attempting to obtain work before adding first URL.");
		}

		Query q = session.createQuery("from org.encog.bot.spider.workload.data.WorkloadLocation WHERE status = :s ORDER BY id");
		q.setMaxResults(1);
		q.setEntity("s", WorkloadStatus.WAITING);
		WorkloadLocation result = (WorkloadLocation)q.uniqueResult();		
		return result;
		
		
	}


	/**
	 * Setup this workload manager for the specified spider.
	 * 
	 * @param spider
	 * The spider using this workload manager. @ Thrown if there is an error
	 *            setting up the workload manager.
	 */
	public void init(final Spider spider) {
		this.addLock = new Semaphore(1);
		this.workLatch = new CountDownLatch(1);
		this.workLatch.countDown();


		this.maxURLSize = getColumnSize("spider_workload", "url");
		this.maxHostSize = getColumnSize("spider_host", "host");

	}



	/**
	 * Move on to process the next host. This should only be called after
	 * getWork returns null.
	 * 
	 * @return The name of the next host. @ Thrown if the workload manager was
	 *         unable to move to the next host.
	 */
	public String nextHost() {
		return null;
	}

	/**
	 * Setup the workload so that it can be resumed from where the last spider
	 * left the workload.
	 *  @ Thrown if we were unable to resume the processing.
	 */
	public void resume() {
		
	}


	/**
	 * Truncate a string to the specified length.
	 * 
	 * @param str
	 *            The string to truncate.
	 * @param length
	 *            The length to truncate the string to.
	 * @return The truncated string.
	 */
	private String truncate(final String str, final int length) {
		if (length == -1) {
			return str;
		}

		if (str.length() < length) {
			return str;
		}
		return str.substring(0, length);
	}

	/**
	 * If there is currently no work available, then wait until a new URL has
	 * been added to the workload.
	 * 
	 * @param time
	 *            The amount of time to wait.
	 * @param unit
	 *            What time unit is being used.
	 */
	public void waitForWork(final int time, final TimeUnit unit) {
		try {
			this.workLatch.await(time, unit);
		} catch (final InterruptedException e) {
			logger.info("Workload latch timed out.");
		}

	}

	/**
	 * Return true if there are no more workload units.
	 * 
	 * @return Returns true if there are no more workload units. @ Thrown if
	 *         there was an error determining if the workload is empty.
	 */
	public boolean workloadEmpty() {
		return true;
	}

	public WorkloadHost getCurrentHost() {
		return currentHost;
	}
	
	

}
