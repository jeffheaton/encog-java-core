package org.encog.bot.html;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

public class TestPeekableInputStream extends TestCase {
	public void testSimple() throws Throwable
	{
		ByteArrayInputStream bis = new ByteArrayInputStream("test".getBytes());
		PeekableInputStream peek = new PeekableInputStream(bis);
		
		TestCase.assertTrue(peek.read()=='t');
		TestCase.assertTrue(peek.read()=='e');
		TestCase.assertTrue(peek.read()=='s');
		TestCase.assertTrue(peek.read()=='t');
	}
	
	public void testPeek() throws Throwable
	{
		ByteArrayInputStream bis = new ByteArrayInputStream("test".getBytes());
		PeekableInputStream peek = new PeekableInputStream(bis);
		
		TestCase.assertTrue(peek.peek(0)=='t');
		TestCase.assertTrue(peek.peek(1)=='e');
		TestCase.assertTrue(peek.peek(2)=='s');
		TestCase.assertTrue(peek.peek(3)=='t');
		TestCase.assertTrue(peek.read()=='t');
		TestCase.assertTrue(peek.read()=='e');
		TestCase.assertTrue(peek.read()=='s');
		TestCase.assertTrue(peek.read()=='t');
	}
	
	public void testPeekExtend() throws Throwable
	{
		ByteArrayInputStream bis = new ByteArrayInputStream("012345678901234567890".getBytes());
		PeekableInputStream peek = new PeekableInputStream(bis);
		
		TestCase.assertTrue(peek.peek(0)=='0');
		TestCase.assertTrue(peek.peek(11)=='1');
		// now read past the end of file
		byte junk[] = new byte[100];
		peek.read(junk);
		TestCase.assertTrue(peek.peek()==-1);
		
	}
}
