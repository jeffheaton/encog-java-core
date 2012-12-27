package org.encog.ml.prg.train.crossover;

import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.opp.EvolutionaryOperator;
import org.encog.ml.genetic.crossover.SubtreeCrossover;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestSubtreeCrossover extends TestCase {
	public void testUnderlyingSimple() {
		RenderCommonExpression render = new RenderCommonExpression();
		EncogProgram prg = new EncogProgram("1+2");
		EncogProgram prg2 = new EncogProgram("4+5");
		prg.replaceNodeAtPosition(prg2, 2, 1);
		Assert.assertEquals("(1+(4+5))",render.render(prg));
	}
	
	public void testUnderlyingUnderlyingShrink() {
		RenderCommonExpression render = new RenderCommonExpression();
		EncogProgram prg = new EncogProgram("(3+2)*4");
		EncogProgram prg2 = new EncogProgram("1");
		prg.replaceNodeAtPosition(prg2, 0, 2);
		Assert.assertEquals("(1*4)",render.render(prg));
	}
	
	public void testUnderlyingUnderlyingShortToDouble() {
		RenderCommonExpression render = new RenderCommonExpression();
		EncogProgram prg = new EncogProgram("1.1+2.2");
		EncogProgram prg2 = new EncogProgram("1");
		prg.replaceNodeAtPosition(prg2, 0, 1);
		Assert.assertEquals("(1.1+1)",render.render(prg));
	}
	
	public void testUnderlyingUnderlyingDoubleToShort() {
		RenderCommonExpression render = new RenderCommonExpression();
		EncogProgram prg = new EncogProgram("1+2");
		EncogProgram prg2 = new EncogProgram("1.1");
		prg.replaceNodeAtPosition(prg2, 0, 1);
		Assert.assertEquals("(1+1.1)",render.render(prg));
	}
	
	public void testSimpleReplace() {
		RenderCommonExpression render = new RenderCommonExpression();
		EncogProgram prg = new EncogProgram("1.5");
		EncogProgram prg2 = new EncogProgram("1");
		EncogProgram[] offspring = prg.allocateOffspring(1);
		offspring[0].copy(prg);
		offspring[0].replaceNode(prg2, 0, 0);
		Assert.assertEquals("1",render.render(offspring[0]));
	}
	
	public void eval(int seed, String parent1Expression, String parent2Expression, String offspringExpression) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createNumericOperators(context.getFunctions());
		
		// parent 1
		EncogProgram parent1 = new EncogProgram(context);
		parent1.getVariables().setVariable("x", 1);
		parent1.compileExpression(parent1Expression);
				
		// parent 2
		EncogProgram parent2 = new EncogProgram(context);
		parent2.getVariables().setVariable("x", 1);
		parent2.compileExpression(parent2Expression);
		
		// offspring
		EncogProgram[] offspring = parent1.allocateOffspring(1);
				
		EvolutionaryOperator cross = new SubtreeCrossover();
		Genome[] parents = {parent1, parent2};
		cross.performOperation(new Random(seed), parents, 0, offspring, 0);
		offspring[0].getVariables().setVariable("x", 1);
		
		RenderCommonExpression render = new RenderCommonExpression();
		//Assert.assertEquals(offspringExpression,render.render(offspring[0]));
	}
	
	public void testCrossover() {
		eval(2,"((1+2)^2/(3+4-2))","(3+2)/2","(((1+(3+2))^2)/((3+4)-2))");
		
	}	
}
