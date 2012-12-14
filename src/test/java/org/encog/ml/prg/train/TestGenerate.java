package org.encog.ml.prg.train;

import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestGenerate extends TestCase {

	public void testDepth() {
		EncogProgramContext context = new EncogProgramContext();
		context.defineVariable("x");
		
		StandardExtensions.createAll(context.getFunctions());
		
		CreateRandom rnd = new CreateRandom(context,2);
		EncogProgram prg = rnd.generate(new Random());
		RenderCommonExpression render = new RenderCommonExpression();
		String str = render.render(prg);
		System.out.println(str);
		Assert.assertTrue(prg.size()<10);
	}
}
