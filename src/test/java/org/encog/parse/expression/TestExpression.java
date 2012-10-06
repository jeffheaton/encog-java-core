package org.encog.parse.expression;

import org.encog.Encog;
import org.encog.parse.expression.expvalue.ExpressionValue;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestExpression extends TestCase {
	public void testConst() {
		Assert.assertEquals( 100, ExpressionHolder.parseFloat("100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, ExpressionHolder.parseFloat("+100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -100, ExpressionHolder.parseFloat("-100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1000, ExpressionHolder.parseFloat("1e3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 0.001, ExpressionHolder.parseFloat("1e-3"),Encog.DEFAULT_DOUBLE_EQUAL);		
		Assert.assertEquals( 1.5, ExpressionHolder.parseFloat("1.5"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -1.5, ExpressionHolder.parseFloat("-1.5"),Encog.DEFAULT_DOUBLE_EQUAL);		
		Assert.assertEquals( 1500, ExpressionHolder.parseFloat("1.5e3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -0.0015, ExpressionHolder.parseFloat("-1.5e-3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1.2345678, ExpressionHolder.parseFloat("1.2345678"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testTypes() {
		ExpressionValue exp = ExpressionHolder.parseExpression("cint(1.2345678)");
		Assert.assertTrue(exp.isInt());
		Assert.assertEquals( 1, exp.toIntValue());
	
		exp = ExpressionHolder.parseExpression("cstr(1.2345678)");
		Assert.assertTrue(exp.isString());
		Assert.assertEquals( "1.2345678", exp.toStringValue());
		
		exp = ExpressionHolder.parseExpression("cfloat(\"1.2345678\")");
		Assert.assertTrue(exp.isFloat());
		Assert.assertEquals( "1.2345678", exp.toStringValue());
		
	}
	
	public void testAdd() {
		Assert.assertEquals( 5, ExpressionHolder.parseFloat("2+3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 3, ExpressionHolder.parseFloat("5+-2"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testSub() {
		Assert.assertEquals( -1, ExpressionHolder.parseFloat("2-3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 7, ExpressionHolder.parseFloat("5--2"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testMul() {
		Assert.assertEquals( 6, ExpressionHolder.parseFloat("2*3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -6, ExpressionHolder.parseFloat("-2*3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 6, ExpressionHolder.parseFloat("-2*-3"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testPower() {
		Assert.assertEquals( 8, ExpressionHolder.parseFloat("2^3"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testParen() {
		Assert.assertEquals( 14, ExpressionHolder.parseFloat("2*(3+4)"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 10, ExpressionHolder.parseFloat("(2*3)+4"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, ExpressionHolder.parseFloat("(2*3)^2+(4*2)^2"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 4, ExpressionHolder.parseFloat("2^(1+1)"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testBad() {
		try {
			Assert.assertEquals( 0, ExpressionHolder.parseFloat("2*(3+4"),Encog.DEFAULT_DOUBLE_EQUAL);
			Assert.assertTrue(false);
		} catch(ExpressionError ex) {
			// good, we want an exception.
		}
		
		try {
			Assert.assertEquals( 0, ExpressionHolder.parseFloat("5+"),Encog.DEFAULT_DOUBLE_EQUAL);
			Assert.assertTrue(false);
		} catch(ExpressionError ex) {
			// good, we want an exception.
		}
	}
	
}
