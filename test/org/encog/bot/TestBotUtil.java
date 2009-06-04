package org.encog.bot;

import java.net.URL;

import junit.framework.TestCase;

import org.encog.util.logging.Logging;
import org.junit.Assert;

public class TestBotUtil extends TestCase {
	public void testLoadPage() throws Throwable
	{
		Logging.stopConsoleLogging();
		// test good web site
		String str = BotUtil.loadPage(new URL("http://www.httprecipes.com/"));
		Assert.assertTrue(str.indexOf("Recipes")!=-1);
		// test bad website
		try
		{
			str = BotUtil.loadPage(new URL("http://www.httprecipes.com/sdhfuishdfui"));
			Assert.assertFalse(true);
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void testExtractFromIndex() throws Throwable {
		Logging.stopConsoleLogging();
		String html = "<b>first</b><b>second</b>";		
		String str = BotUtil.extractFromIndex(html, "<b>", "</b>", 0, 0);
		Assert.assertEquals("first", str);
		str = BotUtil.extractFromIndex(html, "<b>", "</b>", 1, 0);
		Assert.assertEquals("second", str);
		str = BotUtil.extractFromIndex(html, "<b>", "</b>", 0, 2);
		Assert.assertEquals("second", str);
		
		str = BotUtil.extractFromIndex(html, "bad", "</b>", 0, 0);
		Assert.assertNull(str);
		str = BotUtil.extractFromIndex(html, "<b>", "bad", 0, 0);
		Assert.assertNull(str);
	}
	
	public void testExtract() throws Throwable {
		Logging.stopConsoleLogging();
		String html = "<b>first</b><b>second</b>";		
		String str = BotUtil.extract(html, "<b>", "</b>", 0);
		Assert.assertEquals("first", str);
		str = BotUtil.extract(html, "<b>", "</b>", 2 );
		Assert.assertEquals("second", str);
		str = BotUtil.extract(html, "bad", "</b>", 0);
		Assert.assertNull(str);
		str = BotUtil.extract(html, "<b>", "bad", 0);
		Assert.assertNull(str);
	}
}
