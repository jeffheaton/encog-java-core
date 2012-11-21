package org.encog.ml.prg.train.rewrite;

import org.encog.ml.prg.EncogProgram;
import org.encog.parse.expression.common.RenderCommonExpression;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestRewriteAlgebraic extends TestCase {
	
	public void eval(String start, String expect) {
		EncogProgram expression = new EncogProgram(start);
		expression.getVariables().setVariable("x", 1);
		RenderCommonExpression render = new RenderCommonExpression();
		RewriteAlgebraic rewrite = new RewriteAlgebraic();
		rewrite.rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}

	public void testDoubleNegative() {
		eval("--x","x");
		//eval("-x","-(x)");
	}
	
	public void testMinusMinus() {
		eval("x--3","(x+3)");
	}

}
