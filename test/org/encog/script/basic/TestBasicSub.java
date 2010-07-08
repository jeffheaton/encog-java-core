package org.encog.script.basic;

import org.encog.util.BasicEvaluate;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBasicSub extends TestCase {

	public void testSub()
	{
		String str = BasicEvaluate.evaluate("test-sub");
		Assert.assertEquals("test5.010.03.0", str);	
	}
	
}
