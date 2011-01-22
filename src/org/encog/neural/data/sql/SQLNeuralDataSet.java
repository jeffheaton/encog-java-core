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
package org.encog.neural.data.sql;

import java.io.File;
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
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.buffer.MemoryDataLoader;
import org.encog.neural.data.buffer.codec.CSVDataCODEC;
import org.encog.neural.data.buffer.codec.DataSetCODEC;
import org.encog.neural.data.buffer.codec.SQLCODEC;
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
 * SQLCODEC.FETCH_SIZE = 1000; 
 * 
 */
public class SQLNeuralDataSet extends BasicNeuralDataSet {

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

        DataSetCODEC codec = new SQLCODEC(connection,sql,inputSize,idealSize);
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

        DataSetCODEC codec = new SQLCODEC(sql,inputSize,idealSize,driver,url,uid,pwd);
        MemoryDataLoader load = new MemoryDataLoader(codec);
        load.setResult(this);
        load.external2Memory();
	}
}
