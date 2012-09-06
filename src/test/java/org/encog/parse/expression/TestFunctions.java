package org.encog.parse.expression;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.Encog;

public class TestFunctions extends TestCase {
	public void testBasicFunctions() {
		Assert.assertEquals( 3, ExpressionHolder.parse("sqrt(9)"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, ExpressionHolder.parse("pow(10,2)"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
