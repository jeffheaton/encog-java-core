/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
