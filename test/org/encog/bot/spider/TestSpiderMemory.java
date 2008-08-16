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
import org.encog.bot.spider.workload.memory.MemoryWorkloadManager;
import org.encog.bot.spider.workload.sql.SQLWorkloadManager;

import junit.framework.TestCase;

public class TestSpiderMemory extends TestCase implements SpiderReportable {

    private String base = "www.httprecipes.com";
    
    private int urlsProcessed;
	
	
	public void testSpider() throws Exception
	{            
            SpiderOptions options = new SpiderOptions();
            options.corePoolSize = 10;
            options.startup = SpiderOptions.STARTUP_CLEAR;
            options.workloadManager = MemoryWorkloadManager.class.getCanonicalName();
            
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
