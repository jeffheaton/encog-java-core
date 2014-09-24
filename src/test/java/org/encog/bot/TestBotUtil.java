/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.bot;

import java.net.URL;

import junit.framework.TestCase;

import org.junit.Assert;

public class TestBotUtil extends TestCase {
	public void testLoadPage() throws Throwable
	{
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
