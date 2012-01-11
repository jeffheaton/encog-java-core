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
package org.encog.platformspecific.j2se.data;

import java.sql.Connection;

import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.buffer.codec.SQLCODEC;

/**
 * A dataset based on a SQL query. This is not a memory based dataset, so it can
 * handle very large datasets without a memory issue. This class makes use of
 * JDBC to query the database.
 * 
 * If you are running into "out of memory" issues with this class try setting a
 * lower "fetch size". This can be done with:
 * 
 * SQLCODEC.FETCH_SIZE = 1000; 
 * 
 */
public class SQLNeuralDataSet extends BasicMLDataSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	public SQLNeuralDataSet(
			final Connection theConnection, final String theSQL,
			final int theInputSize, final int theIdealSize) {

		DataSetCODEC codec = new SQLCODEC(theConnection, theSQL, theInputSize,
				theIdealSize);
        MemoryDataLoader load = new MemoryDataLoader(codec);
        load.setResult(this);
        load.external2Memory();
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

		DataSetCODEC codec = new SQLCODEC(sql, inputSize, idealSize, driver,
				url, uid, pwd);
        MemoryDataLoader load = new MemoryDataLoader(codec);
        load.setResult(this);
        load.external2Memory();
	}
}
