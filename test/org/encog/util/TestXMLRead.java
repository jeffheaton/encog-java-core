package org.encog.util;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

public class TestXMLRead extends TestCase {
	
	final static String XML = "<doc><a t1='text1'>a</a><b>b</b><c>c</c><d>d</d></doc>";
	
	public void testRead() throws Throwable
	{
		/*ByteArrayInputStream bos = new ByteArrayInputStream(TestXMLRead.XML.getBytes());
		XMLRead read = new XMLRead(bos);
		XMLElement element = read.get();
		TestCase.assertEquals(null, element.getText());
		
		element = read.get();
		TestCase.assertEquals("doc", element.getText());
		
		element = read.get();
		TestCase.assertEquals("a", element.getText());
		
		element = read.get();
		TestCase.assertEquals("a", element.getText());
		
		element = read.get();
		TestCase.assertEquals("a", element.getText());

		element = read.get();
		TestCase.assertEquals("b", element.getText());

		element = read.get();
		TestCase.assertEquals("b", element.getText());

		element = read.get();
		TestCase.assertEquals("b", element.getText());

		element = read.get();
		TestCase.assertEquals("c", element.getText());

		
		
		bos.close();*/
	}

}
