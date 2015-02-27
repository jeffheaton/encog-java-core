/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utilities for using the Hypersonic SQL (HSQL) engine. Encog uses this SQL
 * database for in-memory spidering, as well as unit testing.
 * 
 * @author jheaton
 * 
 */
public final class HSQLUtil {

	/**
	 * The driver to use for the memory database.
	 */
	public static final String DRIVER = "org.hsqldb.jdbcDriver";
	
	/**
	 * The URL to use for the memory database.
	 */
	public static final String URL = "jdbc:hsqldb:mem:encog";
	
	/**
	 * The user id to use for the memory database.
	 */
	public static final String UID = "sa";
	
	/**
	 * The password to use for the memory database.
	 */
	public static final String PWD = "";
	
	/**
	 * The dialect to use for the memory database.
	 */
	public static final String DIALECT = "org.hibernate.dialect.HSQLDialect";

	/**
	 * @return A connection to the memory database.
	 * @throws SQLException An SQL error.
	 */
	public static Connection getConnection() throws SQLException {
		final Properties props = new Properties();
		props.put("user", HSQLUtil.UID);
		props.put("password", HSQLUtil.PWD);

		return DriverManager.getConnection(HSQLUtil.URL, props);
	}



	/**
	 * Load the driver for the memory database.
	 * @throws InstantiationException Database error.
	 * @throws IllegalAccessException Database error.
	 * @throws ClassNotFoundException Database error.
	 */
	public static void loadDriver() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver");
	}

	/**
	 * Shutdown the database. Not currently implemented.
	 */
	public static void shutdown() {

	}
	
	/**
	 * Private constructor.
	 */
	private HSQLUtil() {		
	}
}
