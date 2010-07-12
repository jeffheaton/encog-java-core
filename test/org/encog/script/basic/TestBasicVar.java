package org.encog.script.basic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.util.BasicEvaluate;

public class TestBasicVar extends TestCase {
	
	public void testSub()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC,"test-array");
		Assert.assertEquals("12", str);	
	}

}
