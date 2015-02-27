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
package org.encog.bot.rss;

import java.net.URL;

import junit.framework.TestCase;



public class TestRSS extends TestCase
{
	private void test(URL url) throws Exception
	{
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
    URL url = new URL("http://www.httprecipes.com/1/12/rss2.xml");
    test(url);
  }
  
  public void testRSS1() throws Exception
  {
	    URL url = new URL("http://www.httprecipes.com/1/12/rss1.xml");  
	    test(url);
  }
}
