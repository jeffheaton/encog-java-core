package org.encog.ml.prg.train.rewrite;

import junit.framework.Assert;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.rewrite.algebraic.RewriteAlgebraic;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.junit.Test;

public class TestRewriteAlgebraic {
	
	public void eval(String start, String expect) {
		EncogProgram expression = new EncogProgram(start);
		expression.getVariables().setVariable("x", 1);
		expression.getVariables().setVariable("y", 2);
		RenderCommonExpression render = new RenderCommonExpression();
		RewriteAlgebraic rewrite = new RewriteAlgebraic();
		rewrite.rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}

	@Test
	public void testDoubleNegative() {
		eval("--x","x");
	}
	
	@Test
	public void testDoubleNegativeNothing() {
		eval("-x","-(x)");
	}
	
	@Test
	public void testMinusMinus() {
		//eval("x--3","(x+3)");
	}
	
	@Test
	public void testPlusNeg() {
		//eval("x+-y","(x-y)");
		//eval("x+-1","(x-1)");
	}
	
	@Test
	public void testVarOpVar() {
		//eval("x-x","0.0");
		//eval("x+x","(2.0*x)");
		//eval("x*x","(x^2.0)");
		//eval("x/x","1.0");
	}

}
