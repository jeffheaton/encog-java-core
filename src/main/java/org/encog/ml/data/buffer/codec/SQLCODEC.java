/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.data.buffer.codec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.encog.ml.data.MLDataError;

/**
 * A CODEC that is designed to read data from an SQL source. This is a read-only
 * codec.
 * 
 */
public class SQLCODEC implements DataSetCODEC {

	/**
	 * The JDBC result set.
	 */
	private ResultSet results;

	/**
	 * The size of the input data.
	 */
	private final int inputSize;

	/**
	 * The size of the ideal data.
	 */
	private final int idealSize;

	/**
	 * Should the connection be closed on a call to close.
	 */
	private final boolean closeConnection;

	/**
	 * The JDBC connection.
	 */
	private Connection connection;

	/**
	 * The JDBC statement.
	 */
	private PreparedStatement statement;

	/**
	 * The default fetch size.
	 */
	private int fetchSize = 0;

	/**
	 * Create a SQLNeuralDataSet based on the specified connection. This
	 * connection WILL NOT be closed when the close method is called.
	 * 
	 * @param theConnection
	 *            The connection to use.
	 * @param theSQL
	 *            The SQL command to execute.
	 * @param theInputSize
	 *            The size of the input data.
	 * @param theIdealSize
	 *            The size of the ideal data.
	 */
	public SQLCODEC(final Connection theConnection, final String theSQL,
			final int theInputSize, final int theIdealSize) {

		this.inputSize = theInputSize;
		this.idealSize = theIdealSize;
		this.connection = theConnection;
		this.closeConnection = false;

		try {
			// prepare the statement
			this.statement = this.connection.prepareStatement(theSQL);
		} catch (final SQLException e) {
			throw new MLDataError(e);
		}
	}

	/**
	 * Construct a SQL dataset. A connection will be opened, this connection
	 * will be closed when the close method is called.
	 * 
	 * @param theSQL
	 *            The SQL command to execute.
	 * @param theInputSize
	 *            The size of the input data.
	 * @param theIdealSize
	 *            The size of the ideal data.
	 * @param theDriver
	 *            The driver to use.
	 * @param theURL
	 *            The database connection URL.
	 * @param theUID
	 *            The database user id.
	 * @param thePWD
	 *            The database password.
	 */
	public SQLCODEC(final String theSQL, final int theInputSize,
			final int theIdealSize, final String theDriver,
			final String theURL, final String theUID, final String thePWD) {

		this.inputSize = theInputSize;
		this.idealSize = theIdealSize;
		this.closeConnection = true;

		try {
			Class.forName(theDriver);

			if ((theUID == null) || (thePWD == null)) {
				this.connection = DriverManager.getConnection(theURL);
			} else {
				this.connection = DriverManager.getConnection(theURL, theUID,
						thePWD);
			}

			// prepare the statement
			this.statement = this.connection.prepareStatement(theSQL);

		} catch (final ClassNotFoundException e) {
			throw new MLDataError(e);
		} catch (final SQLException e) {
			throw new MLDataError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close() {
		try {
			if (this.closeConnection) {
				this.connection.close();
			}
			this.results.close();
		} catch (final SQLException e) {
			throw new MLDataError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		return this.idealSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		return this.inputSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareRead() {
		try {
			if (this.fetchSize != 0) {
				this.statement.setFetchSize(this.fetchSize);
			}
			// execute the statement
			this.results = this.statement.executeQuery();
		} catch (final SQLException e) {
			throw new MLDataError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareWrite(final int recordCount, 
			final int theInputSize,
			final int theIdealSize) {
		throw new MLDataError("Write not supported.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean read(final double[] input, 
			final double[] ideal, double[] significance) {
		try {
			if (!this.results.next()) {
				return false;
			}

			for (int i = 0; i < this.inputSize; i++) {
				final double d = this.results.getDouble(i + 1);
				input[i] = d;
			}

			if (this.idealSize > 0) {

				for (int i = 0; i < this.idealSize; i++) {
					final double d = this.results.getDouble(this.inputSize + i
							+ 1);
					ideal[i] = d;
				}
			}

			significance[0] = 1;
			return true;
		} catch (final SQLException e) {
			throw new MLDataError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(final double[] input, final double[] ideal, double significance) {
		throw new MLDataError("Write not supported.");
	}

	/**
	 * @return the fetchSize
	 */
	public final int getFetchSize() {
		return fetchSize;
	}

	/**
	 * @param theFetchSize
	 *            the fetchSize to set
	 */
	public final void setFetchSize(final int theFetchSize) {
		this.fetchSize = theFetchSize;
	}
	
	
}
