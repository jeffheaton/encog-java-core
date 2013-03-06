package org.encog.ml.prg.train.rewrite;

import junit.framework.Assert;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.bytearray.ByteArrayHolder;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.rewrite.algebraic.RewriteAlgebraic;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.junit.Test;

public class TestRewriteAlgebraic {
	
	public void eval(String start, String expect) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createAll(context.getFunctions());
		// place it on the last population member to increase changes of an error
		EncogProgram expression = new EncogProgram(context,new EncogProgramVariables());
		expression.compileExpression(start);
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
	public void testDoubleNegative2() {
		eval("(--x)/2","(x/2)");
	}
	
	@Test
	public void testDoubleNegativeNothing() {
		eval("-x","(-x)");
	}
	
	@Test
	public void testMinusMinus() {
		eval("x--3","(x+3)");
	}
	
	@Test
	public void testMinusMinus2() {
		eval("(x--3)/2","((x+3)/2)");
	}
	
	@Test
	public void testPlusMinus() {
		eval("x--3","(x+3)");
	}
	
	@Test
	public void testPlusMinus2() {
		eval("(x--3)/2","((x+3)/2)");
	}
	
	@Test
	public void testPlusNeg1() {
		eval("x+-y","(x-y)");
	}
	
	@Test
	public void testPlusNeg2() {
		eval("x+-1","(x-1)");
	}
	
	@Test
	public void testPlusNeg12() {
		eval("(x+-y)/2","((x-y)/2)");
	}
	
	@Test
	public void testPlusNeg22() {
		eval("(x+-1)/2","((x-1)/2)");
	}
	
	@Test
	public void testPlusNeg3() {
		eval("(x+-1)/2","((x-1)/2)");
	}
	
	@Test
	public void testVarDoubleSub1() {
		eval("x-x","0");
	}
	
	@Test
	public void testVarDoubleAdd1() {
		eval("x+x","(2*x)");
	}
	
	@Test
	public void testVarDoubleMul1() {
		eval("x*x","(x^2)");
	}
	
	@Test
	public void testVarDoubleDiv1() {
		eval("x/x","1");
	}
		
	@Test
	public void testVarDoubleSub2() {
		eval("(x-x)/2","(0/2)");
	}
	
	@Test
	public void testVarDoubleAdd2() {
		eval("(x+x)/2","((2*x)/2)");
	}
	
	@Test
	public void testVarDoubleMul2() {
		eval("(x*x)/2","((x^2)/2)");
	}
	
	@Test
	public void testVarDoubleDiv2() {
		eval("(x/x)/2","(1/2)");
	}
	
	@Test
	public void testOther() {
		eval("(x/x)/2","(1/2)");
	}
	
}
