package org.encog.bot.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.encog.bot.spider.Spider;
import org.encog.bot.spider.SpiderOptions;
import org.encog.bot.spider.SpiderParseHTML;
import org.encog.bot.spider.SpiderReportable;
import org.encog.bot.spider.workload.sql.SQLWorkloadManager;

import junit.framework.TestCase;

public class TestSpiderDB extends TestCase implements SpiderReportable {

    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby:";
    private String base = "www.httprecipes.com";
    
    private int urlsProcessed;
	
    private void loadDriver() {
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("\nUnable to load the JDBC driver " + driver);
            System.err.println("Please check your CLASSPATH.");
            cnfe.printStackTrace(System.err);
        } catch (InstantiationException ie) {
            System.err.println(
                        "\nUnable to instantiate the JDBC driver " + driver);
            ie.printStackTrace(System.err);
        } catch (IllegalAccessException iae) {
            System.err.println(
                        "\nNot allowed to access the JDBC driver " + driver);
            iae.printStackTrace(System.err);
        }
    }
	
	
	public void testSpider() throws Exception
	{
		loadDriver();
		
        Connection conn = null;
            ArrayList statements = new ArrayList();
            PreparedStatement psInsert = null;
            PreparedStatement psUpdate = null;
            Statement s = null;
            ResultSet rs = null;
            
            Properties props = new Properties();
            props.put("user", "user1");
            props.put("password", "user1");

            String dbName = "derbyDB"; // the name of the database

            conn = DriverManager.getConnection(protocol + dbName
                    + ";create=true", props);

            System.out.println("Connected to and created database " + dbName);

            conn.setAutoCommit(true);

            s = conn.createStatement();
            statements.add(s);

            // We create a table...
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE \"SPIDER_HOST\" (");
       		sql.append(" \"HOST_ID\" int generated always as identity,");
			sql.append(" \"HOST\" varchar(255),");
			sql.append(" \"STATUS\" varchar(1),");
            sql.append(" \"URLS_DONE\" int,");
            sql.append(" \"URLS_ERROR\" int");
            sql.append(" )");
            s.execute(sql.toString());
            
            sql = new StringBuilder();
            sql.append("CREATE TABLE \"SPIDER_WORKLOAD\" (");
      		sql.append(" \"WORKLOAD_ID\" int  generated always as identity NOT NULL ,");
			sql.append(" \"HOST\" int NOT NULL,");
			sql.append(" \"URL\" varchar(2083) NOT NULL,");
            sql.append(" \"STATUS\" varchar(1) NOT NULL,");
            sql.append(" \"DEPTH\" int NOT NULL,");
            sql.append(" \"URL_HASH\" int NOT NULL,");
            sql.append(" \"SOURCE_ID\" int NOT NULL)");
            s.execute(sql.toString());
            
            System.out.println("Created table location");
            
            SpiderOptions options = new SpiderOptions();
            options.corePoolSize = 10;
            options.dbClass = driver;
            options.dbPWD = "user1";
            options.dbUID = "user1";
            options.dbURL = protocol + dbName;
            options.startup = SpiderOptions.STARTUP_CLEAR;
            options.workloadManager = SQLWorkloadManager.class.getCanonicalName();
            
            Spider spider = new Spider(options, this);
           
            spider.addURL(new URL("http://www.httprecipes.com"), null, 1);
            spider.process();
            TestCase.assertTrue(this.urlsProcessed>100);
            


	}


	@Override
	public boolean beginHost(String host) {
		return host.equalsIgnoreCase("www.httprecipes.com");
	}


	@Override
	public void init(Spider spider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean spiderFoundURL(URL url, URL source, URLType type) {
		if( type != URLType.HYPERLINK )
		{
			return true;
		}
		else if ((this.base != null) && (!this.base.equalsIgnoreCase(url.getHost()))) {
	        return false;
	      }

	    return true;
	}


	@Override
	public void spiderProcessURL(URL url, InputStream stream)
			throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void spiderProcessURL(URL url, SpiderParseHTML parse)
			throws IOException {
	    try {
	        parse.readAll();
	      } catch (IOException e) {	        
	      }
		this.urlsProcessed++;
		
	}


	@Override
	public void spiderURLError(URL url) {
		// TODO Auto-generated method stub
		
	}
	
}
