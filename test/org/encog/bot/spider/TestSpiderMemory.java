package org.encog.bot.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

public class TestSpiderMemory extends TestCase implements SpiderReportable {

    private String base = "www.httprecipes.com";
    
    private int urlsProcessed;
	
	
	public void testSpider() throws Exception
	{                       
            Spider spider = new Spider(this);
           
            spider.addURL(new URL("http://www.httprecipes.com"), null, 1);
            spider.process();
            TestCase.assertTrue(this.urlsProcessed>100);
            


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
