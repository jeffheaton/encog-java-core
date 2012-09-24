package org.encog.parse.expression;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestString extends TestCase {
	public void testSimple() {
		Assert.assertEquals( "test", ExpressionHolder.parseString("\"test\""));
		Assert.assertEquals( "", ExpressionHolder.parseString("\"\""));
	}
	
	public void testConcat() {
		Assert.assertEquals( "helloworld", ExpressionHolder.parseString("\"hello\"+\"world\""));
		Assert.assertEquals( "test:123.0", ExpressionHolder.parseString("\"test:\"+123.0"));
	}
}
