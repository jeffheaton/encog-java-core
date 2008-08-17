package org.encog.bot.spider;

import java.io.File;
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
import org.encog.util.Directory;

import junit.framework.TestCase;

public class TestSpiderDB extends TestCase implements SpiderReportable {

    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby:";
    private String base = "www.httprecipes.com";
    public static final String DB_LOCATION = "derbyDB";
    
    private int urlsProcessed;
	
	public void testSpider() throws Exception
	{
		Directory.deleteDirectory(new File(TestSpiderDB.DB_LOCATION));
		
		Class.forName(driver).newInstance();
		
        Connection conn = null;
            Statement s = null;
            
            Properties props = new Properties();
            props.put("user", "user1");
            props.put("password", "user1");

            conn = DriverManager.getConnection(protocol + TestSpiderDB.DB_LOCATION
                    + ";create=true", props);

            conn.setAutoCommit(true);

            s = conn.createStatement();

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
            
            
            SpiderOptions options = new SpiderOptions();
            options.corePoolSize = 10;
            options.dbClass = driver;
            options.dbPWD = "user1";
            options.dbUID = "user1";
            options.dbURL = protocol + TestSpiderDB.DB_LOCATION;
            options.startup = SpiderOptions.STARTUP_CLEAR;
            options.workloadManager = SQLWorkloadManager.class.getCanonicalName();
            
            Spider spider = new Spider(options, this);
           
            spider.addURL(new URL("http://www.httprecipes.com"), null, 1);
            spider.process();
            TestCase.assertTrue(this.urlsProcessed>100);
            
            Directory.deleteDirectory(new File(TestSpiderDB.DB_LOCATION));
            


	}


	public boolean beginHost(String host) {
		return host.equalsIgnoreCase("www.httprecipes.com");
	}


	public void init(Spider spider) {
		// TODO Auto-generated method stub
		
	}


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


	public void spiderProcessURL(URL url, InputStream stream)
			throws IOException {
		// TODO Auto-generated method stub
		
	}


	public void spiderProcessURL(URL url, SpiderParseHTML parse)
			throws IOException {
	    try {
	        parse.readAll();
	      } catch (IOException e) {	        
	      }
		this.urlsProcessed++;
		
	}


	public void spiderURLError(URL url) {
		// TODO Auto-generated method stub
		
	}
	
}
