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
		Assert.assertEquals( 4, (int)ExpressionHolder.parseFloat("length(\"test\")"));
		Assert.assertEquals( "5.22", ExpressionHolder.parseString("format(5.2222,2)"));
	}
}
