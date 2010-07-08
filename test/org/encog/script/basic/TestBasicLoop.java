package org.encog.script.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.util.BasicEvaluate;

public class TestBasicLoop extends TestCase {
	
	public void testWhile()
	{
		String str = BasicEvaluate.evaluate("test-while");
		Assert.assertEquals("1.02.03.04.05.06.07.08.09.0", str);	
	}
	
	public void testDo()
	{
		String str = BasicEvaluate.evaluate("test-do1");
		Assert.assertEquals("1.02.03.04.05.06.07.08.09.0", str);	
	}
}
