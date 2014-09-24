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
package org.encog.parse.tags.read;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.junit.Assert;

public class TestXMLRead extends TestCase {
	
	final static String XML = "<doc><a t1='text1'>a</a><b>b</b><c>c</c><d>d</d></doc>";
	final static String XML2 = "<a testThis='text1'>";
	
	public void testRead() throws Throwable
	{
		ByteArrayInputStream bos = new ByteArrayInputStream(TestXMLRead.XML.getBytes());
		ReadXML read = new ReadXML(bos);
		Assert.assertEquals(0,read.read());
		Assert.assertTrue(read.is("doc", true));
		Assert.assertEquals(0,read.read());
		Assert.assertTrue(read.is("a", true));
		Assert.assertEquals('a',read.read());
		Assert.assertEquals(0,read.read());
		Assert.assertTrue(read.is("a", false));
		bos.close();
	}
	
	public void testCAPS() throws Throwable
	{
		ByteArrayInputStream bos = new ByteArrayInputStream(TestXMLRead.XML2.getBytes());
		ReadXML read = new ReadXML(bos);
		Assert.assertEquals(0,read.read());
		Assert.assertTrue(read.is("a", true));
		Assert.assertEquals("text1", read.getTag().getAttributeValue("testThis"));
		bos.close();
	}

}
