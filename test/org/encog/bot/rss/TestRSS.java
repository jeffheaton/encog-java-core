package org.encog.bot.rss;

import java.net.URL;

import org.encog.util.logging.Logging;

import junit.framework.TestCase;



public class TestRSS extends TestCase
{
	private void test(URL url) throws Exception
	{
		Logging.stopConsoleLogging();
	    RSS rss = new RSS();
	    rss.load(url);
	    TestCase.assertTrue(rss.toString().length()>0);
	    TestCase.assertEquals(14, rss.getItems().size());
	    RSSItem item = rss.getItems().get(0);
	    TestCase.assertEquals("Chapter 1: The Structure of HTTP Requests", item.getTitle());
	    TestCase.assertEquals("http://www.httprecipes.com/1/1/", item.getLink());
	}
	
  public void testRSS2() throws Exception
  {
	  Logging.stopConsoleLogging();
    URL url = new URL("http://www.httprecipes.com/1/12/rss2.xml");
    test(url);
  }
  
  public void testRSS1() throws Exception
  {
	  Logging.stopConsoleLogging();
	    URL url = new URL("http://www.httprecipes.com/1/12/rss1.xml");  
	    test(url);
  }
}
