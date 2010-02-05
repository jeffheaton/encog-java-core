/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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

package org.encog.parse.tags.read;

import java.io.ByteArrayInputStream;

import org.encog.parse.tags.read.ReadXML;
import org.junit.Assert;

import junit.framework.TestCase;

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
