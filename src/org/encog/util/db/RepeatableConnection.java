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
package org.encog.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RepeatableConnection: Establish a database connection that can be reconnected
 * if it is broken.
 * 
 * @author jheaton
 * 
 */
public class RepeatableConnection {

	/**
	 * The logger.
	 */
	private static Logger logger = Logger
			.getLogger("org.encog.util.db.RepeatableConnection");

	/**
	 * The driver for the JDBC connection.
	 */
	private final String driver;

	/**
	 * The URL for the JDBC connection.
	 */
	private final String url;

	/**
	 * The UID for the JDBC connection.
	 */
	private final String uid;

	/**
	 * The PWD for the JDBC connection.
	 */
	private final String pwd;

	/**
	 * A connection to a JDBC database.
	 */
	private Connection connection;

	/**
	 * All of the RepeatableStatement objects.
	 */
	private final List<RepeatableStatement> statements = 
		new ArrayList<RepeatableStatement>();

	/**
	 * Construct a repeatable statement.
	 * 
	 * @param driver
	 *            The driver to use.
	 * @param url
	 *            The URL to connect to.
	 * @param uid
	 *            The user id to use.
	 * @param pwd
	 *            The password to use.
	 */
	public RepeatableConnection(final String driver, final String url,
			final String uid, final String pwd) {
		this.driver = driver;
		this.url = url;
		this.uid = uid;
		this.pwd = pwd;
	}

	/**
	 * Close the connection.
	 */
	public void close() {
		for (final RepeatableStatement statement : this.statements) {
			statement.close();
		}

		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (final SQLException e) {
				throw new DBError(e);
			}
		}

	}

	/**
	 * Create a statement.
	 * 
	 * @param sql
	 *            The SQL.
	 * @return The new statement.
	 */
	public RepeatableStatement createStatement(final String sql) {
		final RepeatableStatement result = new RepeatableStatement(sql);
		this.statements.add(result);
		return result;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return this.driver;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return this.pwd;
	}

	/**
	 * @return the statements
	 */
	public List<RepeatableStatement> getStatements() {
		return this.statements;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return this.uid;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Open a database connection.
	 * 
	 */
	public void open() {
		try {
			Class.forName(this.driver).newInstance();
			this.connection = DriverManager.getConnection(this.url, this.uid,
					this.pwd);
			for (final RepeatableStatement statement : this.statements) {
				statement.init(this);
			}
		} catch (final SQLException e) {
			throw new DBError(e);
		} catch (final InstantiationException e) {
			throw new DBError(e);
		} catch (final IllegalAccessException e) {
			throw new DBError(e);
		} catch (final ClassNotFoundException e) {
			throw new DBError(e);
		}
	}

	/**
	 * Try to open the database connection.
	 */
	void tryOpen() {
		Exception ex = null;

		logger.log(Level.SEVERE,
				"Lost connection to database, trying to reconnect.");

		for (int i = 1; i < 120; i++) {
			try {
				close();
			} catch (final Exception e1) {
				logger
						.log(
								Level.SEVERE,
								"Failed while trying to close lost connection, ignoring...",
								e1);
			}

			ex = null;

			try {
				logger.log(Level.SEVERE, "Attempting database reconnect");
				open();
				logger.log(Level.SEVERE, "Database connection reestablished");
				break;
			} catch (final Exception e) {
				ex = e;
				logger.log(Level.SEVERE, "Reconnect failed", ex);
			}

			try {
				logger.log(Level.SEVERE, "Reconnect attempt " + i
						+ " failed.  Waiting to try again.");
				Thread.sleep(30000);
			} catch (final InterruptedException e) {
			}

		}

		if (ex != null) {
			throw new DBError(ex);
		}

	}
}
