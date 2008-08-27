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

package org.encog.neural.data.sql;

import java.sql.SQLException;
import java.util.Iterator;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.db.RepeatableConnection;
import org.encog.util.db.RepeatableStatement;
import org.encog.util.db.RepeatableStatement.Results;

/**
 * A dataset based on a SQL query. This is not a memory based dataset, so it can
 * handle very large datasets without a memory issue. and can handle very large
 * datasets.
 * 
 * @author jheaton
 */
public class SQLNeuralDataSet implements NeuralDataSet {

	/**
	 * Iterator used to iterate over SQL results.
	 * 
	 * @author jheaton
	 */
	public class SQLNeuralIterator implements Iterator<NeuralDataPair> {

		/**
		 * Is read and ready?
		 */
		private boolean dataReady;

		/**
		 * Holds results from the SQL query.
		 */
		private final Results results;

		/**
		 * Construct an iterator. Execute a query and retrieve results.
		 */
		public SQLNeuralIterator() {
			this.results = SQLNeuralDataSet.this.statement.executeQuery();
		}

		/**
		 * Returns true if there is more data to be read.
		 * @return True if there is more data to be read.
		 */
		public boolean hasNext() {

			if (this.dataReady) {
				return true;
			}

			try {
				if (this.results.getResultSet().next()) {
					this.dataReady = true;
					return true;
				}

				this.dataReady = false;
				return false;
			} catch (final SQLException e) {
				throw new NeuralDataError(e);
			}

		}

		/**
		 * Read the next row from the database.
		 * @return The next training set pair.
		 */
		public NeuralDataPair next() {
			try {
				final NeuralData input = new BasicNeuralData(
						SQLNeuralDataSet.this.inputSize);
				NeuralData ideal = null;

				for (int i = 1; i <= SQLNeuralDataSet.this.inputSize; i++) {
					input.setData(i - 1, this.results.getResultSet().getDouble(
							i));
				}

				if (SQLNeuralDataSet.this.idealSize > 0) {
					ideal = 
					new BasicNeuralData(SQLNeuralDataSet.this.idealSize);
					for (int i = 1; i <= SQLNeuralDataSet.this.idealSize; i++) {
						ideal.setData(i - 1,
							this.results
							.getResultSet()
							.getDouble(
							i + SQLNeuralDataSet.this.inputSize));
					}

				}

				this.dataReady = false;
				return new BasicNeuralDataPair(input, ideal);
			} catch (final SQLException e) {
				throw new NeuralDataError(e);
			}
		}

		/**
		 * Removes are not supported.
		 */
		public void remove() {
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
	 * What is the size of the input data?
	 */
	private final int inputSize;
	
	/**
	 * What is the size of the ideal data?
	 */
	private final int idealSize;
	
	/**
	 * The database connection.
	 */
	private final RepeatableConnection connection;

	/**
	 * The SQL statement being used.
	 */
	private final RepeatableStatement statement;

	/**
	 * Construct a SQL dataset.
	 * @param sql The SQL command to execute.
	 * @param inputSize The size of the input data.
	 * @param idealSize The size of the ideal data.
	 * @param driver The driver to use.
	 * @param url The database connection URL.
	 * @param uid The database user id.
	 * @param pwd The database password.
	 */
	public SQLNeuralDataSet(final String sql, final int inputSize,
			final int idealSize, final String driver, final String url,
			final String uid, final String pwd) {
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.connection = new RepeatableConnection(driver, url, uid, pwd);
		this.statement = this.connection.createStatement(sql);
		this.connection.open();
	}

	/**
	 * Adds are not supported.
	 * @param data1 Not used.
	 */
	public void add(final NeuralData data1) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adds are not supported.
	 * @param inputData Not used.
	 * @param idealData Not used.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);

	}

	/**
	 * Adds are not supported.
	 * @param inputData Not used.
	 */
	public void add(final NeuralDataPair inputData) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Close the SQL connection.
	 */
	public void close() {
		if (this.statement != null) {
			this.statement.close();
		}

		if (this.connection != null) {
			this.connection.close();
		}
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
	 * Get an iterator for this collection.
	 * @return An iterator for use with this collection.
	 */
	public Iterator<NeuralDataPair> iterator() {
		return new SQLNeuralIterator();
	}

}
