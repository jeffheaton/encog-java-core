package org.encog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.encog.util.Directory;

public class DerbyUtil {
 	
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
        props.put("user", DerbyUtil.UID);
        props.put("password", DerbyUtil.PWD);

        String dbName = "derbyDB"; // the name of the database
    	return DriverManager.getConnection(DerbyUtil.URL, props);
    }
    
	public static void shutdown() {
		/*try {
			DriverManager.getConnection("jdbc:derby:"+DerbyUtil.DB_LOCATION+";shutdown=true");
		} catch (SQLException e) {
		}*/
		
	}
}
