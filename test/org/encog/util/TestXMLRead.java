package org.encog.util;

import java.io.ByteArrayInputStream;

import org.encog.parse.tags.read.ReadXML;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestXMLRead extends TestCase {
	
	final static String XML = "<doc><a t1='text1'>a</a><b>b</b><c>c</c><d>d</d></doc>";
	
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

}
