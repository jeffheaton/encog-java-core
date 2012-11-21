package org.encog.ml.prg.train.crossover;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestSubtreeCrossover extends TestCase {
	public void testCrossoverOperation() {
		RenderCommonExpression render = new RenderCommonExpression();
		EncogProgram prg = new EncogProgram("1+2");
		EncogProgram prg2 = new EncogProgram("4+5");
		ProgramNode node = prg.findNode(2);
		prg.replaceNode(node, prg2.getRootNode());
		Assert.assertEquals("(1+(4+5))",render.render(prg));
	}
}
