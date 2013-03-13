package org.encog.ml.prg;

import org.encog.parse.expression.common.RenderCommonExpression;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestProgramClone extends TestCase {
	
	public void testSimpleClone() {
		
		EncogProgramContext context = new EncogProgramContext();
		context.loadAllFunctions();
		RenderCommonExpression render = new RenderCommonExpression();
		
		EncogProgram prg1 = context.createProgram("1*2*3");
		EncogProgram prg2 = context.cloneProgram(prg1);
		
		Assert.assertEquals("((1*2)*3)", render.render(prg1));
		Assert.assertEquals("((1*2)*3)", render.render(prg2));
	}
	
	public void testCloneVar() {
		
		EncogProgramContext context = new EncogProgramContext();
		context.loadAllFunctions();
		context.defineVariable("x");
		RenderCommonExpression render = new RenderCommonExpression();
		
		EncogProgram prg1 = context.createProgram("x*2*3");
		EncogProgram prg2 = context.cloneProgram(prg1);
		
		Assert.assertEquals("((x*2)*3)", render.render(prg1));
		Assert.assertEquals("((x*2)*3)", render.render(prg2));
	}
	
	public void testCloneComplex() {
		
		EncogProgramContext context = new EncogProgramContext();
		context.loadAllFunctions();
		context.defineVariable("a");
		RenderCommonExpression render = new RenderCommonExpression();
		
		EncogProgram prg1 = context.createProgram("((a+25)^3/25)-((a*3)^4/250)");
		EncogProgram prg2 = context.cloneProgram(prg1);
	
		Assert.assertEquals("((((a+25)^3)/25)-(((a*3)^4)/250))", render.render(prg1));
		Assert.assertEquals("((((a+25)^3)/25)-(((a*3)^4)/250))", render.render(prg2));
	}
}
