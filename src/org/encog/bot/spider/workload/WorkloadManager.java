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

	/**
	 * The current host.
	 */
	private String currentHost;

	/**
	 * The ID of the current host.
	 */
	private int currentHostID = -1;

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
				int hostID = getHostID(url, false);

				if (hostID == -1) {
					WorkloadHost host = new WorkloadHost();
					host.setHost(strHost);
					host.setStatus(WorkloadStatus.WAITING);
					this.stmtAdd2.execute(strHost, Status.STATUS_WAITING, 0, 0);
					hostID = getHostID(url, true);
				}

				// need to set the current host for the first time?
				if (this.currentHostID == -1) {
					this.currentHostID = hostID;
					this.currentHost = strHost;
					this.stmtSetHostStatus.execute(Status.STATUS_PROCESSING,
							this.currentHostID);
				}

				// now add workload element
				if (source != null) {
					final int sourceID = getWorkloadID(source, true);
					this.stmtAdd.execute(hostID, strURL, Status.STATUS_WAITING,
							depth, computeHash(url), sourceID);
				} else {
					this.stmtAdd.execute(hostID, strURL, Status.STATUS_WAITING,
							depth, computeHash(url), 0);
				}

				this.workLatch.countDown();
			}

		} catch (final InterruptedException e) {
			throw new WorkloadError(e);			
		} catch (final SQLException e) {
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
		this.stmtClear.execute();
		this.stmtClear2.execute();
	}

	/**
	 * Close the workload manager.
	 */
	public void close() {
		if (this.workResultSet != null) {
			try {
				this.workResultSet.close();
			} catch (final Exception e) {
				logger
						.log(Level.SEVERE,
 "Error trying to close workload result set, ignoring...");
			}
			this.workResultSet = null;
		}

		if (this.connection != null) {
			this.connection.close();
		}

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
		result = result % SQLWorkloadManager.HASH_MASK;
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
		try {
			return getWorkloadID(url, false) != -1;
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		}
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
	 * Create the correct type of SQL holder for this workload managers.
	 * This will likely be overridden by subclasses.
	 * @return A SQL holder.
	 */
	public SQLHolder createSQLHolder() {
		return new SQLHolder();
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
		try {
			final ResultSet rs = this.connection.getConnection().getMetaData()
					.getColumns(null, null, table, null);
			while (rs.next()) {

				final String c = rs.getString("COLUMN_NAME");
				final int size = rs.getInt("COLUMN_SIZE");
				if (c.equalsIgnoreCase(column)) {
					return size;
				}
			}
			return -1;
		} catch (final SQLException e) {
			throw new DBError(e);
		}

	}

	/**
	 * @return the connection
	 */
	public RepeatableConnection getConnection() {
		return this.connection;
	}

	/**
	 * Get the current host.
	 * 
	 * @return The current host.
	 */
	public String getCurrentHost() {
		return this.currentHost;
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
		RepeatableStatement.Results rs = null;
		try {
			rs = this.stmtGetDepth.executeQuery(computeHash(url));
			while (rs.getResultSet().next()) {
				final String u = rs.getResultSet().getString(1);
				if (u.equals(url.toString())) {
					return rs.getResultSet().getInt(2);
				}
			}
			return 1;
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

	/**
	 * Get the host name associated with the specified host id.
	 * 
	 * @param hostID
	 *            The host id to look up.
	 * @return The name of the host. @ Thrown if unable to obtain the host name.
	 */
	private String getHost(final int hostID) {
		RepeatableStatement.Results rs = null;

		try {
			rs = this.stmtGetHost.executeQuery(hostID);
			if (!rs.getResultSet().next()) {
				throw new WorkloadError("Can't find previously created host.");
			}
			return rs.getResultSet().getString(1);
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
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
	private int getHostID(final String host, final boolean require)
			throws SQLException {
		RepeatableStatement.Results rs = null;

		// is this the current host?
		if (this.currentHostID != -1) {
			if (this.currentHost.equalsIgnoreCase(host)) {
				return this.currentHostID;
			}
		}

		// use the database to find it
		try {
			rs = this.stmtGetHostID.executeQuery(host);
			if (rs.getResultSet().next()) {
				return rs.getResultSet().getInt(1);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (require) {
			final StringBuilder str = new StringBuilder();
			str.append("Failed to find previously visited Host,");
			str.append("Host=\"");
			str.append(host);
			str.append("\".");
			throw new WorkloadError(str.toString());
		}
		return -1;
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
	private int getHostID(final URL url, final boolean require)
			throws SQLException  {
		final String host = url.getHost().toLowerCase();
		return getHostID(host, require);
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
		RepeatableStatement.Results rs = null;
		try {
			rs = this.stmtGetSource.executeQuery(computeHash(url));
			while (rs.getResultSet().next()) {
				final String u = rs.getResultSet().getString(1);
				if (u.equals(url.toString())) {
					return new URL(rs.getResultSet().getString(2));
				}
			}
			return null;
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		} catch (final MalformedURLException e) {
			throw new WorkloadError(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
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
	public URL getWork() {
		URL url = null;
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
	private URL getWorkInternal() {
		if (this.currentHostID == -1) {
			throw new WorkloadError(
					"Attempting to obtain work before adding first URL.");
		}

		try {
			boolean requery = false;

			if (this.workResultSet == null) {
				requery = true;
			} else {
				if (!this.workResultSet.getResultSet().next()) {
					requery = true;
				}
			}

			if (requery) {
				if (this.workResultSet != null) {
					this.workResultSet.close();
				}

				this.workResultSet = this.stmtGetWork.executeQuery(
						Status.STATUS_WAITING, this.currentHostID);

				if (!this.workResultSet.getResultSet().next()) {
					return null;
				}
			}

			final int id = this.workResultSet.getResultSet().getInt(1);
			final String url = this.workResultSet.getResultSet().getString(2);

			this.stmtGetWork2.execute(Status.STATUS_PROCESSING, id);
			return new URL(url);

		} catch (final SQLException e) {
			throw new WorkloadError(e);
		} catch (final MalformedURLException e) {
			throw new WorkloadError(e);
		}
	}

	/**
	 * Get the workload ID, given a URL.
	 * 
	 * @param url
	 *            The URL to look up.
	 * @param require
	 *            Should an exception be thrown if the workload is not located.
	 * @return The ID of the workload. @ Thrown if the host id is not found, and
	 *         is required.
	 * @throws SQLException
	 *             Thrown if a SQL error occurs.
	 */
	private int getWorkloadID(final URL url, final boolean require)
			throws SQLException  {
		int hash = 0;
		RepeatableStatement.Results rs = null;
		try {
			hash = computeHash(url);
			rs = this.stmtGetWorkloadID.executeQuery(hash);
			while (rs.getResultSet().next()) {
				if (rs.getResultSet().getString(2).equals(url.toString())) {
					return rs.getResultSet().getInt(1);
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (require) {
			final StringBuilder str = new StringBuilder();
			str.append("Failed to find previously visited URL, hash=\"");
			str.append(hash);
			str.append("\", URL=\"");
			str.append(url.toString());
			str.append("\".");
			throw new WorkloadError(str.toString());
		}
		return -1;
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

		this.connection = new RepeatableConnection(
				spider.getOptions().getDbClass(),				
				spider.getOptions().getDbURL(), 
				spider.getOptions().getDbUID(), spider
						.getOptions().getDbPWD());

		this.stmtClear = this.connection.createStatement(this.holder
				.getSQLClear());
		this.stmtClear2 = this.connection.createStatement(this.holder
				.getSQLClear2());
		this.stmtAdd = this.connection.createStatement(this.holder.getSQLAdd());
		this.stmtAdd2 = this.connection.createStatement(this.holder
				.getSQLAdd2());
		this.stmtGetWork = this.connection.createStatement(this.holder
				.getSQLGetWork());
		this.stmtGetWork2 = this.connection.createStatement(this.holder
				.getSQLGetWork2());
		this.stmtWorkloadEmpty = this.connection.createStatement(this.holder
				.getSQLWorkloadEmpty());
		this.stmtSetWorkloadStatus = this.connection
				.createStatement(this.holder.getSQLSetWorkloadStatus());
		this.stmtSetWorkloadStatus2 = this.connection
				.createStatement(this.holder.getSQLSetWorkloadStatus2());
		this.stmtGetDepth = this.connection.createStatement(this.holder
				.getSQLGetDepth());
		this.stmtGetSource = this.connection.createStatement(this.holder
				.getSQLGetSource());
		this.stmtResume = this.connection.createStatement(this.holder
				.getSQLResume());
		this.stmtResume2 = this.connection.createStatement(this.holder
				.getSQLResume2());
		this.stmtGetWorkloadID = this.connection.createStatement(this.holder
				.getSQLGetWorkloadID());
		this.stmtGetHostID = this.connection.createStatement(this.holder
				.getSQLGetHostID());
		this.stmtGetNextHost = this.connection.createStatement(this.holder
				.getSQLGetNextHost());
		this.stmtSetHostStatus = this.connection.createStatement(this.holder
				.getSQLSetHostStatus());
		this.stmtGetHost = this.connection.createStatement(this.holder
				.getSQLGetHost());

		this.connection.open();

		this.maxURLSize = getColumnSize("spider_workload", "url");
		this.maxHostSize = getColumnSize("spider_host", "host");

	}

	/**
	 * Mark the specified URL as error.
	 * 
	 * @param url
	 * The URL that had an error. @ Thrown if the specified URL could not be
	 *            marked.
	 */
	public void markError(final URL url) {
		try {
			setStatus(url, Status.STATUS_ERROR);
			this.workLatch.countDown();
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		}
	}

	/**
	 * Mark the specified host as processed.
	 * 
	 * @param host
	 * The host to mark. @ Thrown if the host cannot be marked.
	 */
	private void markHostProcessed(final String host) {
		try {
			final int hostID = this.getHostID(host, true);
			this.stmtSetHostStatus.execute(Status.STATUS_DONE, hostID);
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		}
	}

	/**
	 * Mark the specified URL as successfully processed.
	 * 
	 * @param url
	 * The URL to mark as processed. @ Thrown if the specified URL could not be
	 *            marked.
	 */
	public void markProcessed(final URL url) {
		try {
			setStatus(url, Status.STATUS_DONE);
			this.workLatch.countDown();
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		}
	}

	/**
	 * Move on to process the next host. This should only be called after
	 * getWork returns null.
	 * 
	 * @return The name of the next host. @ Thrown if the workload manager was
	 *         unable to move to the next host.
	 */
	public String nextHost() {
		if (this.currentHostID == -1) {
			throw new WorkloadError(
					"Attempting to obtain host before adding first URL.");
		}
		markHostProcessed(this.currentHost);

		try {
			boolean requery = false;

			if (this.hostResultSet == null) {
				requery = true;
			} else {
				if (!this.hostResultSet.getResultSet().next()) {
					requery = true;
				}
			}

			if (requery) {
				if (this.hostResultSet != null) {
					this.hostResultSet.close();
				}

				this.hostResultSet = this.stmtGetNextHost
						.executeQuery(Status.STATUS_WAITING);

				if (!this.hostResultSet.getResultSet().next()) {
					return null;
				}
			}

			this.currentHostID = this.hostResultSet.getResultSet().getInt(1);
			this.currentHost = this.hostResultSet.getResultSet().getString(2);
			this.stmtSetHostStatus.execute(Status.STATUS_PROCESSING,
					this.currentHostID);
			logger.log(Level.INFO, "Moving to new host: " + this.currentHost);
			return this.currentHost;

		} catch (final SQLException e) {
			throw new WorkloadError(e);
		}

	}

	/**
	 * Setup the workload so that it can be resumed from where the last spider
	 * left the workload.
	 *  @ Thrown if we were unable to resume the processing.
	 */
	public void resume() {
		RepeatableStatement.Results rs = null;

		try {
			rs = this.stmtResume.executeQuery();

			if (!rs.getResultSet().next()) {
				throw new WorkloadError(
						"Can't resume, unable to determine current host.");
			}

			this.currentHostID = rs.getResultSet().getInt(1);
			this.currentHost = getHost(this.currentHostID);
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		this.stmtResume2.execute();

	}

	/**
	 * Set the status for the specified URL.
	 * 
	 * @param url
	 *            The URL to set the status for.
	 * @param status
	 * What to set the status to. @ Thrown if the status cannot be set.
	 * @throws SQLException
	 *             Thrown if a SQL error occurs.
	 */
	private void setStatus(final URL url, final String status)
			throws SQLException {
		final int id = getWorkloadID(url, true);
		this.stmtSetWorkloadStatus.execute("" + status, id);
		if (status.equalsIgnoreCase(Status.STATUS_ERROR)) {
			this.stmtSetWorkloadStatus2.execute(0, 1, url.getHost()
					.toLowerCase());
		} else if (status.equalsIgnoreCase(Status.STATUS_DONE)) {
			this.stmtSetWorkloadStatus2.execute(1, 0, url.getHost()
					.toLowerCase());
		}

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
		RepeatableStatement.Results rs = null;

		try {
			rs = this.stmtWorkloadEmpty.executeQuery(this.currentHostID);
			if (!rs.getResultSet().next()) {
				return true;
			}
			return rs.getResultSet().getInt(1) < 1;
		} catch (final SQLException e) {
			throw new WorkloadError(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
	}

}
