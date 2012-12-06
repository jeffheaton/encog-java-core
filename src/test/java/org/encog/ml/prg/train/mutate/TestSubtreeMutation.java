package org.encog.ml.prg.train.mutate;

import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestSubtreeMutation extends TestCase {
	
	public void eval(int seed, String startExpression, String mutateExpression) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createNumericOperators(context.getFunctions());
		EncogProgram prg = new EncogProgram(context);
		prg.getVariables().setVariable("x", 1);
		EncogProgram[] offspring = prg.allocateOffspring(1);
		prg.compileExpression(startExpression);
		PrgMutate mutate = new SubtreeMutation(context,3);
		mutate.mutate(new Random(seed), prg, offspring, 0, 1);
		offspring[0].getVariables().setVariable("x", 1);
		RenderCommonExpression render = new RenderCommonExpression();
		Assert.assertEquals(mutateExpression,render.render(offspring[0]));
	}
	
	public void testMutate() {
		eval(2,"((1+2)^2/(3+4-2))","(((1+-(-9))^2)/((3+4)-2))");
	}	
}
