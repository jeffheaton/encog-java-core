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
