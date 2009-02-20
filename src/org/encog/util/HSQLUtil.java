package org.encog.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.encog.util.orm.ORMSession;
import org.encog.util.orm.SessionManager;

public class HSQLUtil {
 	
    public static final String DRIVER = "org.hsqldb.jdbcDriver";
    public static final String URL = "jdbc:hsqldb:mem:encog";
	public static final String UID = "sa";
	public static final String PWD = "";
	public static final String DIALECT = "org.hibernate.dialect.HSQLDialect";
    
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
