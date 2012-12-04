package org.encog.ml.prg.train.rewrite;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestRewriteConstants extends TestCase {
	
	public void eval(String start, String expect) {
		EncogProgram expression = new EncogProgram(start);
		RenderCommonExpression render = new RenderCommonExpression();
		RewriteConstants rewrite = new RewriteConstants();
		rewrite.rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}

	public void testFull() {
		eval("1+2","3");
		eval("1+2+3","6");
		eval("1+2+3+4","10");
	}
	
	public void testPartial() {
		eval("1+2+x","(3+x)");
		eval("1+x","(1+x)");
		eval("1.0/2.0+x","(0.5+x)");
	}
	
	public void testOther() {
		//eval("-((x-6))","-((x-6))");
		
	}
}
