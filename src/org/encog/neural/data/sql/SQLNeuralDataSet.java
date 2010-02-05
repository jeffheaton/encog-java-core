/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.data.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dataset based on a SQL query. This is not a memory based dataset, so it can
 * handle very large datasets without a memory issue. This class makes use of
 * JDBC to query the database.
 * 
 * If you are running into "out of memory" issues with this class try setting a
 * lower "fetch size". This can be done with:
 * 
 * sqlDataSet.getStatement().setFetchSize(1000);
 * 
 */
public class SQLNeuralDataSet implements NeuralDataSet {

	/**
	 * Iterator used to iterate over SQL results.
	 * 
	 * @author jheaton
	 */
	public class SQLNeuralIterator implements Iterator<NeuralDataPair> {

		/**
		 * The JDBC result set.
		 */
		private final ResultSet results;

		/**
		 * Is read and ready?
		 */
		private boolean dataReady;

		/**
		 * The results from the query.
		 */
		// private final ScrollableResults results;
		/**
		 * Construct an iterator. Execute a query and retrieve results.
		 */
		public SQLNeuralIterator() {
			try {
				// execute the statement
				this.results = SQLNeuralDataSet.this.statement.executeQuery();
			} catch (final SQLException e) {
				SQLNeuralDataSet.this.logger.error("Exception", e);
				throw new NeuralDataError(e);
			}
		}

		/**
		 * Close this iterator and release any resources it had.
		 */
		public void close() {

			try {
				this.results.close();
			} catch (final SQLException e) {
				SQLNeuralDataSet.this.logger.error("Exception", e);
				throw new NeuralDataError(e);
			}

		}

		/**
		 * Returns true if there is more data to be read.
		 * 
		 * @return True if there is more data to be read.
		 */
		public boolean hasNext() {

			try {
				if (this.dataReady) {
					return true;
				}

				if (this.results.next()) {
					this.dataReady = true;
					return true;
				}

				this.dataReady = false;
				return false;
			} catch (final SQLException e) {
				SQLNeuralDataSet.this.logger.error("Exception", e);
				throw new NeuralDataError(e);
			}

		}

		/**
		 * Read the next row from the database.
		 * 
		 * @return The next training set pair.
		 */
		public NeuralDataPair next() {

			try {
				final NeuralData input = new BasicNeuralData(
						SQLNeuralDataSet.this.inputSize);
				NeuralData ideal = null;

				for (int i = 0; i < SQLNeuralDataSet.this.inputSize; i++) {
					final double d = this.results.getDouble(i + 1);
					input.setData(i, d);
				}

				if (SQLNeuralDataSet.this.idealSize > 0) {
					ideal = new BasicNeuralData(
							SQLNeuralDataSet.this.idealSize);
					for (int i = 0; i < SQLNeuralDataSet.this.idealSize; i++) {
						final double d = this.results
								.getDouble(SQLNeuralDataSet.this.inputSize + i
										+ 1);
						ideal.setData(i, d);
					}

				}

				this.dataReady = false;
				return new BasicNeuralDataPair(input, ideal);
			} catch (final SQLException e) {
				SQLNeuralDataSet.this.logger.error("Exception", e);
				throw new NeuralDataError(e);
			}

		}

		/**
		 * Removes are not supported.
		 */
		public void remove() {
			if (SQLNeuralDataSet.this.logger.isErrorEnabled()) {
				SQLNeuralDataSet.this.logger
						.error(SQLNeuralDataSet.REMOVE_NOT_SUPPORTED);
			}
			throw new NeuralDataError(SQLNeuralDataSet.REMOVE_NOT_SUPPORTED);
		}
	}

	/**
	 * Error message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED = 
		"Adds are not supported with this dataset, it is read only.";

	/**
	 * Error message: removes are not supported.
	 */
	public static final String REMOVE_NOT_SUPPORTED = 
		"Removes are not supported with this dataset, it is read only.";

	/**
	 * The JDBC connection.
	 */
	private Connection connection;

	/**
	 * The JDBC statement.
	 */
	private PreparedStatement statement;

	/**
	 * Should the connection be closed on a call to close.
	 */
	private boolean closeConnection;

	/**
	 * A collection of iterations currently in use.
	 */
	private final List<SQLNeuralDataSet> iterators = 
		new ArrayList<SQLNeuralDataSet>();

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * What is the size of the input data?
	 */
	private final int inputSize;

	/**
	 * What is the size of the ideal data?
	 */
	private final int idealSize;

	/**
	 * Create a SQLNeuralDataSet based on the specified connection. This
	 * connection WILL NOT be closed when the close method is called.
	 * 
	 * @param connection
	 *            The connection to use.
	 * @param sql
	 *            The SQL command to execute.
	 * @param inputSize
	 *            The size of the input data.
	 * @param idealSize
	 *            The size of the ideal data.
	 */
	public SQLNeuralDataSet(final Connection connection, final String sql,
			final int inputSize, final int idealSize) {

		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.connection = connection;
		this.closeConnection = false;

		try {
			// prepare the statement
			this.statement = this.connection.prepareStatement(sql);
		} catch (final SQLException e) {
			this.logger.error("Exception", e);
			throw new NeuralDataError(e);
		}
	}

	/**
	 * Construct a SQL dataset. A connection will be opened, this connection
	 * will be closed when the close method is called.
	 * 
	 * @param sql
	 *            The SQL command to execute.
	 * @param inputSize
	 *            The size of the input data.
	 * @param idealSize
	 *            The size of the ideal data.
	 * @param driver
	 *            The driver to use.
	 * @param url
	 *            The database connection URL.
	 * @param uid
	 *            The database user id.
	 * @param pwd
	 *            The database password.
	 */
	public SQLNeuralDataSet(final String sql, final int inputSize,
			final int idealSize, final String driver, final String url,
			final String uid, final String pwd) {

		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.closeConnection = true;

		try {
			Class.forName(driver);

			if ((uid == null) || (pwd == null)) {
				this.connection = DriverManager.getConnection(url);
			} else {
				this.connection = DriverManager.getConnection(url, uid, pwd);
			}

			// prepare the statement
			this.statement = this.connection.prepareStatement(sql);

		} catch (final ClassNotFoundException e) {
			this.logger.error("Exception", e);
			throw new NeuralDataError(e);
		} catch (final SQLException e) {
			this.logger.error("Exception", e);
			throw new NeuralDataError(e);
		}
	}

	/**
	 * Adds are not supported.
	 * 
	 * @param data1
	 *            Not used.
	 */
	public void add(final NeuralData data1) {
		if (this.logger.isErrorEnabled()) {
			this.logger.error(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
		}
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adds are not supported.
	 * 
	 * @param inputData
	 *            Not used.
	 * @param idealData
	 *            Not used.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		if (this.logger.isErrorEnabled()) {
			this.logger.error(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
		}
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Adds are not supported.
	 * 
	 * @param inputData
	 *            Not used.
	 */
	public void add(final NeuralDataPair inputData) {
		if (this.logger.isErrorEnabled()) {
			this.logger.error(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
		}
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Close the SQL connection.
	 */
	public void close() {
		for (final SQLNeuralDataSet i : this.iterators) {
			i.close();
		}

		try {
			this.statement.close();
			if (this.closeConnection) {
				this.connection.close();
			}
		} catch (final SQLException e) {
			this.logger.error("Exception", e);
			throw new NeuralDataError(e);
		}
	}

	/**
	 * @return The JDBC connection being used.
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * @return The size of the ideal data.
	 */
	public int getIdealSize() {
		return this.idealSize;
	}

	/**
	 * @return The size of the input data.
	 */
	public int getInputSize() {
		return this.inputSize;
	}

	/**
	 * @return The JDBC PreparedStatement being used.
	 */
	public PreparedStatement getStatement() {
		return this.statement;
	}

	/**
	 * @return True if the connection should be closed when the close method is
	 *         called.
	 */
	public boolean isCloseConnection() {
		return this.closeConnection;
	}

	/**
	 * Get an iterator for this collection.
	 * 
	 * @return An iterator for use with this collection.
	 */
	public Iterator<NeuralDataPair> iterator() {
		return new SQLNeuralIterator();
	}

	/**
	 * Allows you to determine if the connection should be closed. See the two
	 * constructors for how this is set by default. You can override this
	 * default behavior using this method.
	 * 
	 * @param closeConnection
	 *            True if the connection should be closed.
	 */
	public void setCloseConnection(final boolean closeConnection) {
		this.closeConnection = closeConnection;
	}

}
