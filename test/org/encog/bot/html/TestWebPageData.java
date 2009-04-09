package org.encog.bot.html;

import org.encog.bot.browse.LoadWebPage;
import org.encog.bot.browse.WebPage;
import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Link;
import org.encog.bot.dataunit.TagDataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.encog.parse.tags.Tag;

import junit.framework.TestCase;

public class TestWebPageData extends TestCase {

	public void testSimple() throws Throwable
	{
		LoadWebPage load = new LoadWebPage(null);
		WebPage page = load.load("a<b>b</b>c");
		TestCase.assertEquals(5,page.getData().size());
		
		TextDataUnit textDU;
		TagDataUnit tagDU;
		
		// Index 0 (text)
		textDU = (TextDataUnit)page.getDataUnit(0);
		TestCase.assertEquals("a", textDU.toString());
		// Index 1 (tag)
		tagDU = (TagDataUnit)page.getDataUnit(1);
		TestCase.assertEquals("b", tagDU.getTag().getName());
		TestCase.assertEquals("<b>", tagDU.toString());
		TestCase.assertEquals(Tag.Type.BEGIN, tagDU.getTag().getType());
		// Index 2 (text)
		textDU = (TextDataUnit)page.getDataUnit(2);
		TestCase.assertEquals("b", textDU.toString());
		// Index 3 (tag)
		tagDU = (TagDataUnit)page.getDataUnit(3);
		TestCase.assertEquals("b", tagDU.getTag().getName());
		TestCase.assertEquals(Tag.Type.END, tagDU.getTag().getType());
		// Index 4 (text)
		textDU = (TextDataUnit)page.getDataUnit(4);
		TestCase.assertEquals("c", textDU.toString());		
	}
	
	public void testLink() throws Throwable
	{
		LoadWebPage load = new LoadWebPage(null);
		WebPage page = load.load("<a href=\"index.html\">Link <b>1</b></a>");
		TestCase.assertEquals(1,page.getContents().size());
		
		DocumentRange span = page.getContents().get(0);
		TestCase.assertEquals(0, span.getBegin());
		TestCase.assertEquals(5, span.getEnd());
		TestCase.assertTrue(span instanceof Link);
		Link link = (Link)span;
		TestCase.assertEquals("index.html",link.getTarget().getOriginal().toString());
		
	}
	
}
