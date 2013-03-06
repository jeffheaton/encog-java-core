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

public class TestRewriteAlgebraicFloat {
	
	public void eval(String start, String expect) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createAll(context.getFunctions());
		// place it on the last population member to increase changes of an error
		EncogProgram expression = new EncogProgram(context,new EncogProgramVariables());
		expression.compileExpression(start);
		RenderCommonExpression render = new RenderCommonExpression();
		RewriteAlgebraic rewrite = new RewriteAlgebraic();
		render.render(expression);//remove
		rewrite.rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}

	@Test
	public void testDoubleNegative2() {
		eval("(--x)/2.2","(x/2.2)");
	}
		
	@Test
	public void testMinusMinus() {
		eval("x--3.3","(x+3.3)");
	}
	
	@Test
	public void testMinusMinus2() {
		eval("(x--3.3)/2.2","((x+3.3)/2.2)");
	}
	
	@Test
	public void testPlusMinus() {
		eval("x--3.3","(x+3.3)");
	}
	
	@Test
	public void testPlusMinus2() {
		eval("(x--3.3)/2.2","((x+3.3)/2.2)");
	}
	
	@Test
	public void testPlusNeg2() {
		eval("x+-1.1","(x-1.1)");
	}
	
	@Test
	public void testPlusNeg12() {
		eval("(x+-y)/2.2","((x-y)/2.2)");
	}
	
	@Test
	public void testPlusNeg22() {
		eval("(x+-1.1)/2.2","((x-1.1)/2.2)");
	}
	
	@Test
	public void testPlusNeg3() {
		eval("(x+-1.1)/2.2","((x-1.1)/2.2)");
	}
}
