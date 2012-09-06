package org.encog.parse.expression;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.Encog;

public class TestFunctions extends TestCase {
	public void testBasicFunctions() {
		Assert.assertEquals( 9, ExpressionHolder.parse("sqrt(9)"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
}
