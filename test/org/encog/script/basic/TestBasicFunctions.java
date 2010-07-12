package org.encog.script.basic;

import org.encog.script.basic.commands.BasicFunctions;
import org.encog.util.BasicEvaluate;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBasicFunctions extends TestCase {
	
	public static void testMath()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC_FN,"test-math");
		Assert.assertEquals("1.01.0010010016416014410-103.00", str);		
	}
	
	public static void testConvert()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC_FN,"test-convert");	
		Assert.assertEquals("115.00", str);
	}
	
	public static void testString()
	{
		String str = BasicEvaluate.evaluate(BasicEvaluate.FILE_TEST_BASIC_FN,"test-string");
		Assert.assertEquals("65A205abcABCacb|trim || trim|| |", str);
	}
}
