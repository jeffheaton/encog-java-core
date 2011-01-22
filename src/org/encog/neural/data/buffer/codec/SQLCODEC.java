/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.data.buffer.codec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.encog.neural.data.NeuralDataError;

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

	private int inputSize;
	private int idealSize;

	/**
	 * Should the connection be closed on a call to close.
	 */
	private boolean closeConnection;

	/**
	 * The JDBC connection.
	 */
	private Connection connection;

	/**
	 * The JDBC statement.
	 */
	private PreparedStatement statement;
	
	public static int FETCH_SIZE = 0;

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
	public SQLCODEC(final Connection connection, final String sql,
			final int inputSize, final int idealSize) {

		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.connection = connection;
		this.closeConnection = false;

		try {
			// prepare the statement
			this.statement = this.connection.prepareStatement(sql);
		} catch (final SQLException e) {
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
	public SQLCODEC(final String sql, final int inputSize, final int idealSize,
			final String driver, final String url, final String uid,
			final String pwd) {

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
			throw new NeuralDataError(e);
		} catch (final SQLException e) {
			throw new NeuralDataError(e);
		}
	}

	@Override
	public boolean read(double[] input, double[] ideal) {
		try {
			if (!this.results.next())
				return false;

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

			return true;
		} catch (final SQLException e) {
			throw new NeuralDataError(e);
		}
	}

	@Override
	public void write(double[] input, double[] ideal) {
		throw new NeuralDataError("Write not supported.");

	}

	@Override
	public void prepareWrite(int recordCount, int inputSize, int idealSize) {
		throw new NeuralDataError("Write not supported.");
	}

	@Override
	public void prepareRead() {
		try {
			if( SQLCODEC.FETCH_SIZE!=0) {
				this.statement.setFetchSize(SQLCODEC.FETCH_SIZE);
			}
			// execute the statement
			this.results = this.statement.executeQuery();
		} catch (final SQLException e) {
			throw new NeuralDataError(e);
		}
	}

	@Override
	public int getInputSize() {
		return this.inputSize;
	}

	@Override
	public int getIdealSize() {
		return this.idealSize;
	}

	@Override
	public void close() {
		try {
			this.results.close();
		} catch (final SQLException e) {
			throw new NeuralDataError(e);
		}
	}
}
