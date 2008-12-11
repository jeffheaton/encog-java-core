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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RepeatableStatement: This class implements a repeatable statement. A
 * repeatable statement is a regular PreparedStatement that can be repeated if
 * the connection fails.
 * 
 * Additionally, the repeatable statement maintains a cache of PreparedStatement
 * objects for the threads. This prevents two threads from using the same
 * PreparedStatement at the same time. To obtain a PreparedStatement a thread
 * should call the obtainStatement function. Once the thread no longer needs the
 * statement, the releaseStatement method should be called.
 */
public class RepeatableStatement {

	/**
	 * Simple internal class that holds the ResultSet from a query.
	 * 
	 * @author jeff
	 * 
	 */
	public class Results {

		/**
		 * The PreparedStatement that generated these results.
		 */
		private final PreparedStatement statement;

		/**
		 * The ResultSet that was generated.
		 */
		private final ResultSet resultSet;

		/**
		 * Construct a Results object.
		 * 
		 * @param statement
		 *            The PreparedStatement for these results.
		 * @param resultSet
		 *            The ResultSet.
		 */
		public Results(final PreparedStatement statement,
				final ResultSet resultSet) {
			this.statement = statement;
			this.resultSet = resultSet;
		}

		/**
		 * Close the ResultSet.
		 */
		public void close() {
			try {
				this.resultSet.close();
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, "Failed to close ResultSet", e);
			}
			releaseStatement(this.statement);
		}

		/**
		 * Get the ResultSet.
		 * 
		 * @return The ResultSet.
		 */
		public ResultSet getResultSet() {
			return this.resultSet;
		}
	}

	/**
	 * The logger.
	 */
	private static Logger logger = Logger
			.getLogger("org.encog.util.db.RepeatableStatement");

	/**
	 * The SQLWorkloadManager that created this object.
	 */
	private RepeatableConnection manager;

	/**
	 * The SQL for this statement.
	 */
	private final String sql;

	/**
	 * A mutex to make sure that only one thread at a time is in the process of
	 * getting a PreparedStatement assigned. More than one thread at a time can
	 * have a PreparedStatement, however only one can be in the obtainStatement
	 * function at a time.
	 */
	private final Semaphore mutex;

	/**
	 * The PreparedStatements that are assigned to each thread.
	 */
	private final List<PreparedStatement> statementCache = 
		new ArrayList<PreparedStatement>();

	/**
	 * Construct a repeatable statement based on the specified SQL.
	 * 
	 * @param sql
	 *            The SQL to base this statement on.
	 */
	public RepeatableStatement(final String sql) {
		this.sql = sql;
		this.mutex = new Semaphore(1);
	}

	/**
	 * Close the statement.
	 */
	public void close() {
		try {
			try {
				this.mutex.acquire();
			} catch (final InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (final PreparedStatement statement : this.statementCache) {
				try {
					statement.close();
				} catch (final SQLException e) {
					logger.log(Level.SEVERE,
							"Failed to close PreparedStatement", e);
				}
			}
		} finally {
			this.mutex.release();
		}
	}

	/**
	 * Execute SQL that does not return a result set. If an error occurs, the
	 * statement will be retried until it is successful. This handles broken
	 * connections.
	 * 
	 * @param parameters
	 *            The parameters for this SQL.
	 */
	public void execute(final Object... parameters) {
		PreparedStatement statement = null;

		try {
			statement = obtainStatement();

			for (;;) {
				try {
					for (int i = 0; i < parameters.length; i++) {
						if (parameters[i] == null) {
							statement.setNull(i, Types.INTEGER);
						} else {
							statement.setObject(i + 1, parameters[i]);
						}
					}
					long time = System.currentTimeMillis();
					statement.execute();
					time = System.currentTimeMillis() - time;

					return;
				} catch (final SQLException e) {
					logger.log(Level.SEVERE, "SQL Exception", e);
					if (!(e.getCause() instanceof SQLException)) {
						this.manager.tryOpen();
					} else {
						throw new DBError(e);
					}
				}
			}
		} catch (final SQLException e) {
			System.out.println(this.sql);
			throw new DBError(e);
		} finally {
			if (statement != null) {
				releaseStatement(statement);
			}
		}
	}

	/**
	 * Execute an SQL query that returns a result set. If an error occurs, the
	 * statement will be retried until it is successful. This handles broken
	 * connections.
	 * 
	 * @param parameters
	 *            The parameters for this SQL.
	 * @return The results of the query.
	 */
	public Results executeQuery(final Object... parameters) {

		for (;;) {
			try {
				final PreparedStatement statement = obtainStatement();

				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
				long time = System.currentTimeMillis();
				final ResultSet rs = statement.executeQuery();
				time = System.currentTimeMillis() - time;
				// System.out.println( time + ":" + sql);
				return new Results(statement, rs);
			} catch (final SQLException e) {
				logger.log(Level.SEVERE, "SQL Exception", e);
				if (!(e.getCause() instanceof SQLException)) {
					this.manager.tryOpen();
				} else {
					throw new DBError(e);
				}
			}
		}

	}

	/**
	 * Create the statement, so that it is ready to assign PreparedStatements.
	 * 
	 * @param manager
	 *            The manager that created this statement.
	 */
	public void init(final RepeatableConnection manager) {
		close();
		this.manager = manager;
	}

	/**
	 * Obtain a statement. Each thread should use their own statement, and then
	 * call the releaseStatement method when they are done.
	 * 
	 * @return A PreparedStatement object.
	 * @throws SQLException
	 *             Thrown if the statement could not be obtained.
	 */
	private PreparedStatement obtainStatement() throws SQLException {
		PreparedStatement result = null;

		try {
			this.mutex.acquire();
			if (this.statementCache.size() == 0) {
				result = this.manager.getConnection()
						.prepareStatement(this.sql);
			} else {
				result = this.statementCache.get(0);
				this.statementCache.remove(0);
			}

		} catch (final InterruptedException e) {
			return null;
		} finally {
			this.mutex.release();
		}

		return result;
	}

	/**
	 * This method releases statements after the thread is done with them. These
	 * statements are not closed, but rather cached until another thread has
	 * need of them.
	 * 
	 * @param stmt
	 *            The statement that is to be released.
	 */
	private void releaseStatement(final PreparedStatement stmt) {
		try {
			try {
				this.mutex.acquire();
			} catch (final InterruptedException e) {
				return;
			}
			this.statementCache.add(stmt);
		} finally {
			this.mutex.release();
		}
	}
}
