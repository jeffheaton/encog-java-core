package org.encog.parse.expression;

import org.encog.Encog;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestExpression extends TestCase {
	public void testConst() {
		Assert.assertEquals( 100, ExpressionHolder.parse("100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, ExpressionHolder.parse("+100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -100, ExpressionHolder.parse("-100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1000, ExpressionHolder.parse("1e3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 0.001, ExpressionHolder.parse("1e-3"),Encog.DEFAULT_DOUBLE_EQUAL);		
		Assert.assertEquals( 1.5, ExpressionHolder.parse("1.5"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -1.5, ExpressionHolder.parse("-1.5"),Encog.DEFAULT_DOUBLE_EQUAL);		
		Assert.assertEquals( 1500, ExpressionHolder.parse("1.5e3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -0.0015, ExpressionHolder.parse("-1.5e-3"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testAdd() {
		Assert.assertEquals( 5, ExpressionHolder.parse("2+3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 3, ExpressionHolder.parse("5+-2"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testSub() {
		Assert.assertEquals( -1, ExpressionHolder.parse("2-3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 7, ExpressionHolder.parse("5--2"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testMul() {
		Assert.assertEquals( 6, ExpressionHolder.parse("2*3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -6, ExpressionHolder.parse("-2*3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 6, ExpressionHolder.parse("-2*-3"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testPower() {
		Assert.assertEquals( 8, ExpressionHolder.parse("2^3"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testParen() {
		Assert.assertEquals( 14, ExpressionHolder.parse("2*(3+4)"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 10, ExpressionHolder.parse("(2*3)+4"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, ExpressionHolder.parse("(2*3)^2+(4*2)^2"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 4, ExpressionHolder.parse("2^(1+1)"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testBad() {
		try {
			Assert.assertEquals( 0, ExpressionHolder.parse("2*(3+4"),Encog.DEFAULT_DOUBLE_EQUAL);
			Assert.assertTrue(false);
		} catch(ExpressionError ex) {
			// good, we want an exception.
		}
		
		try {
			Assert.assertEquals( 0, ExpressionHolder.parse("5+"),Encog.DEFAULT_DOUBLE_EQUAL);
			Assert.assertTrue(false);
		} catch(ExpressionError ex) {
			// good, we want an exception.
		}
	}
	
}
