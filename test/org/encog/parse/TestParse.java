package org.encog.parse;

import java.util.Collection;
import java.util.List;

import org.encog.parse.signal.Signal;



import junit.framework.TestCase;

public class TestParse extends TestCase {
	
	public void testString() throws Throwable
	{
		Parse parse = new Parse();
		parse.load();
		Signal signal = parse.parse("Hello world");
		Collection<Signal> list = signal.findByType("word");
		
		TestCase.assertEquals(2, list.size());
		int i=0;
		for(Signal s:list)
		{
			switch(i)
			{
				case 0:TestCase.assertEquals("Hello", s.toString());break;
				case 1:TestCase.assertEquals("world", s.toString());break;
			}
			i++;
		}
	}
}
