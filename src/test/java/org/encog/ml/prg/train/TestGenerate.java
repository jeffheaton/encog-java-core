package org.encog.ml.prg.train;

import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.generator.PrgGrowGenerator;
import org.encog.ml.prg.generator.PrgPopulationGenerator;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestGenerate extends TestCase {

	public void testDepth() {
		EncogProgramContext context = new EncogProgramContext();
		context.defineVariable("x");
		
		StandardExtensions.createAll(context.getFunctions());
		
		PrgPopulationGenerator rnd = new PrgGrowGenerator(context,null,2);
		EncogProgram prg = new EncogProgram(context);
		rnd.generate(new Random(),prg);
		RenderCommonExpression render = new RenderCommonExpression();
		String str = render.render(prg);
		System.out.println(str);
		Assert.assertTrue(prg.size()<10);
	}
}
