/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.encog.util.orm.ORMSession;
import org.encog.util.orm.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for using the Hypersonic SQL (HSQL) engine.  Encog uses this
 * SQL database for in-memory spidering, as well as unit testing.
 * @author jheaton
 *
 */
public class HSQLUtil {
 	
    public static final String DRIVER = "org.hsqldb.jdbcDriver";
    public static final String URL = "jdbc:hsqldb:mem:encog";
	public static final String UID = "sa";
	public static final String PWD = "";
	public static final String DIALECT = "org.hibernate.dialect.HSQLDialect";
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public static void loadDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException 
    {
    	Class.forName("org.hsqldb.jdbcDriver"); 
    }
    
    public static Connection getConnection() throws SQLException
    {
    	Properties props = new Properties();
        props.put("user", HSQLUtil.UID);
        props.put("password", HSQLUtil.PWD);

        String dbName = "derbyDB"; // the name of the database
    	return DriverManager.getConnection(HSQLUtil.URL, props);
    }
    
    public static ORMSession getSession()
    {
    	SessionManager manager = new SessionManager(
    			HSQLUtil.DRIVER, 
    			HSQLUtil.URL, 
    			HSQLUtil.UID,
    			HSQLUtil.PWD, 
    			HSQLUtil.DIALECT);
    	manager.export();
    	return manager.openSession();
    }
    
    public static SessionManager getSessionManager()
    {
    	SessionManager manager = new SessionManager(
    			HSQLUtil.DRIVER, 
    			HSQLUtil.URL, 
    			HSQLUtil.UID,
    			HSQLUtil.PWD, 
    			HSQLUtil.DIALECT);
    	manager.export();
    	return manager;
    }
    
	public static void shutdown() {
		/*try {
			DriverManager.getConnection("jdbc:derby:"+DerbyUtil.DB_LOCATION+";shutdown=true");
		} catch (SQLException e) {
		}*/
		
	}
}
