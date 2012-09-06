package org.encog.parse.expression;

import org.encog.Encog;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestExpressionVar extends TestCase {
	public void testAssignment() {
		ExpressionHolder expression = new ExpressionHolder("a");
		expression.set("a",5);
		Assert.assertEquals(5,expression.evaluate(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testAssignment2() {
		ExpressionHolder expression = new ExpressionHolder("cccc*(aa+bbb)");
		expression.set("aa",1);
		expression.set("bbb",2);
		expression.set("cccc",3);
		Assert.assertEquals(9,expression.evaluate(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testError() {
		try {
			ExpressionHolder expression = new ExpressionHolder("b");
			expression.set("a", 5);
			expression.evaluate();
			Assert.assertTrue(false);
		} catch (ExpressionError ex) {
			// we want to get here
		}
	}
}
