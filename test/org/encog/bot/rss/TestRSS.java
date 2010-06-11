/*
 * Encog(tm) Core v2.5 
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
