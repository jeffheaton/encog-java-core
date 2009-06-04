package org.encog.bot.browse;

import java.net.URL;

import org.encog.bot.browse.range.Form;
import org.encog.bot.browse.range.Input;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class TestBrowse extends TestCase {
	
	@Test
	public void testBrowse() throws Throwable
	{
		Browser b = new Browser();
		b.navigate(new URL("http://www.httprecipes.com"));
		WebPage page = b.getCurrentPage();
		Assert.assertTrue( page.getTitle().getTextOnly().indexOf("HTTP")!=-1 );
	}
	
	@Test
	public void testFormGET() throws Throwable
	{
		Browser b = new Browser();
		b.navigate(new URL("http://www.httprecipes.com/1/7/get.php"));
		WebPage page = b.getCurrentPage();
		Assert.assertTrue( page.getTitle().getTextOnly().indexOf("HTTP")!=-1 );
		Form form = (Form)page.find(Form.class, 0);
		Input input1 = form.findType("text", 0);		
		input1.setValue("New York");
		b.navigate(form);
		page = b.getCurrentPage();
		Assert.assertTrue( page.getTitle().getTextOnly().indexOf("HTTP")!=-1 );
	}
}
