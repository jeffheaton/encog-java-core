package org.encog.ml.prg.train.rewrite;

import junit.framework.Assert;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.EncogProgramVariables;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.bytearray.ByteArrayHolder;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.junit.Test;

public class TestRewriteConstants {
	
	public void eval(String start, String expect, boolean cycle) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createAll(context.getFunctions());
		EPLHolder holder = new ByteArrayHolder(10,1024);
		// place it on the last population member to increase changes of an error
		EncogProgram expression = new EncogProgram(context,new EncogProgramVariables(),holder,9);
		expression.compileExpression(start);
		RenderCommonExpression render = new RenderCommonExpression();
		RewriteConstants rewrite = new RewriteConstants();
		if( cycle ) {
			while(rewrite.rewrite(expression));
		} else {
			rewrite.rewrite(expression);
		}
		Assert.assertEquals(expect, render.render(expression));
	}

	@Test
	public void testNothing() {
		eval("1","1",true);
	}
	
	@Test
	public void testCycleFull1() {
		eval("1+2","3",true);
	}
	
	@Test
	public void testCycleFull2() {
		eval("1+2+3","6",true);
	}
	
	@Test
	public void testCycleFull3() {
		eval("1+2+3+4","10",true);
	}
	
	@Test
	public void testCyclePartial1() {
		eval("1+2+x","(3+x)",true);
	}
	
	@Test
	public void testCyclePartial2() {
		eval("1+x","(1+x)",true);
	}
	
	@Test
	public void testCyclePartial3() {
		eval("1.0/2.0+x","(0.5+x)",true);
	}
	
	
	
	
	
	@Test
	public void testFull1() {
		eval("1+2","3",false);
	}
	
	@Test
	public void testFull2() {
		eval("1+2+3","(3+3)",false);
	}
	
	@Test
	public void testFull3() {
		eval("((3+3)+4)","(6+4)",false);
	}
	
	@Test
	public void testPartial1() {
		eval("1+2+x","(3+x)",false);
	}
	
	@Test
	public void testPartial2() {
		eval("1+x","(1+x)",false);
	}
	
	@Test
	public void testPartial3() {
		eval("1.0/2.0+x","(0.5+x)",false);
	}	
	
	@Test
	public void testOther() {
		eval("-((x-6))","(-(x-6))",true);		
	}
}
