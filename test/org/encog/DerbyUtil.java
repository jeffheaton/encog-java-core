package org.encog;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.encog.bot.spider.TestSpiderDB;
import org.encog.util.Directory;

public class DerbyUtil {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String PROTOCOL = "jdbc:derby:";
	public static final String DB_LOCATION = "DerbyDB";
	public static final String UID = "user1";
	public static final String PWD = "user1";
    
    public static void loadDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException 
    {
    	Class.forName(DerbyUtil.DRIVER).newInstance();
    }
    
    public static Connection getConnection() throws SQLException
    {
    	Properties props = new Properties();
        props.put("user", DerbyUtil.UID);
        props.put("password", DerbyUtil.PWD);

        String dbName = "derbyDB"; // the name of the database
    	return DriverManager.getConnection(DerbyUtil.PROTOCOL + dbName
                + ";create=true", props);
    }
    
    public static void cleanup()
    {
    	Directory.deleteDirectory(new File(DerbyUtil.DB_LOCATION));
    }
}
