package org.encog.bot.browse.extract;

import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.encog.bot.browse.Browser;
import org.encog.bot.browse.WebPage;
import org.junit.Assert;
import org.junit.Test;

public class TestExtract extends TestCase {
	
	@Test
	public void testWordExtract() throws Throwable
	{
		Browser b = new Browser();
		b.navigate(new URL("http://www.httprecipes.com"));
		WebPage page = b.getCurrentPage();
		ExtractWords extract = new ExtractWords();
		List<Object> list = extract.extractList(page);
		Assert.assertTrue(list.size()>5);
	}
}
