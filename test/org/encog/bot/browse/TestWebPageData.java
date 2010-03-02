/*
 * Encog(tm) Core v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.bot.browse;

import org.encog.bot.browse.LoadWebPage;
import org.encog.bot.browse.WebPage;
import org.encog.bot.browse.range.DocumentRange;
import org.encog.bot.browse.range.Link;
import org.encog.bot.dataunit.TagDataUnit;
import org.encog.bot.dataunit.TextDataUnit;
import org.encog.parse.tags.Tag;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class TestWebPageData extends TestCase {

	@Test
	public void testSimple() throws Throwable
	{
		LoadWebPage load = new LoadWebPage(null);
		WebPage page = load.load("a<b>b</b>c");
		TestCase.assertEquals(5,page.getData().size());
		
		TextDataUnit textDU;
		TagDataUnit tagDU;
		
		// Index 0 (text)
		textDU = (TextDataUnit)page.getDataUnit(0);
		Assert.assertEquals("a", textDU.toString());
		// Index 1 (tag)
		tagDU = (TagDataUnit)page.getDataUnit(1);
		Assert.assertEquals("b", tagDU.getTag().getName());
		Assert.assertEquals("<b>", tagDU.toString());
		Assert.assertEquals(Tag.Type.BEGIN, tagDU.getTag().getType());
		// Index 2 (text)
		textDU = (TextDataUnit)page.getDataUnit(2);
		Assert.assertEquals("b", textDU.toString());
		// Index 3 (tag)
		tagDU = (TagDataUnit)page.getDataUnit(3);
		Assert.assertEquals("b", tagDU.getTag().getName());
		Assert.assertEquals(Tag.Type.END, tagDU.getTag().getType());
		// Index 4 (text)
		textDU = (TextDataUnit)page.getDataUnit(4);
		Assert.assertEquals("c", textDU.toString());		
	}
	
	@Test
	public void testLink() throws Throwable
	{
		LoadWebPage load = new LoadWebPage(null);
		WebPage page = load.load("<a href=\"index.html\">Link <b>1</b></a>");
		Assert.assertEquals(1,page.getContents().size());
		
		DocumentRange span = page.getContents().get(0);
		Assert.assertEquals(0, span.getBegin());
		Assert.assertEquals(5, span.getEnd());
		Assert.assertTrue(span instanceof Link);
		Link link = (Link)span;
		Assert.assertEquals("index.html",link.getTarget().getOriginal().toString());
		Address address = link.getTarget();
		Assert.assertNotNull(address.toString());
		
	}
	
}
