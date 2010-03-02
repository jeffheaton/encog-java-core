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
