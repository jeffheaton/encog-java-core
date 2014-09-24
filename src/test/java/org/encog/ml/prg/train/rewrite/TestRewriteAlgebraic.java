/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.prg.train.rewrite;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.CalculateScore;
import org.encog.ml.ea.score.adjust.ComplexityAdjustedScore;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.PrgCODEC;
import org.encog.ml.prg.expvalue.DivisionByZeroError;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.opp.SubtreeCrossover;
import org.encog.ml.prg.opp.SubtreeMutation;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.ZeroEvalScoreFunction;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestRewriteAlgebraic extends TestCase {
	
	public void eval(String start, String expect) {
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createNumericOperators(context);
		PrgPopulation pop = new PrgPopulation(context,1);
		CalculateScore score = new ZeroEvalScoreFunction();

		TrainEA genetic = new TrainEA(pop, score);
		genetic.setValidationMode(true);
		genetic.setCODEC(new PrgCODEC());
		genetic.addOperation(0.95, new SubtreeCrossover());
		genetic.addOperation(0.05, new SubtreeMutation(context,4));
		genetic.addScoreAdjuster(new ComplexityAdjustedScore());
		genetic.getRules().addRewriteRule(new RewriteConstants());
		genetic.getRules().addRewriteRule(new RewriteAlgebraic());

		EncogProgram expression = new EncogProgram(context);
		expression.compileExpression(start);
		RenderCommonExpression render = new RenderCommonExpression();
		genetic.getRules().rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}
	
	public void testMinusZero() {
		eval("x-0","x");
		eval("0-0","0");
		eval("10-0","10");
	}

	public void testZeroMul() {
		eval("0*0","0");
		eval("1*0","0");
		eval("0*1","0");
	}
	
	public void testZeroDiv() {
		try {
			eval("0/0","(0/0)");
			Assert.assertFalse(true);
		} catch(DivisionByZeroError ex) {
			// expected
		}
		eval("0/5","0");
		eval("0/x","0");
	}
	
	public void testZeroPlus() {
		eval("0+0","0");
		eval("1+0","1");
		eval("0+1","1");
		eval("x+0","x");
	}
	
	public void testPowerZero() {
		eval("0^x","0");
		eval("0^0","1");
		eval("x^0","1");
		eval("1^0","1");
		eval("-1^0","1");
		eval("(x+y)^0","1");
		eval("x+(x+y)^0","(x+1)");
	}
	
	public void testOnePower() {
		eval("1^500","1");
		eval("1^x","1");
		eval("1^1","1");
	}
	
	public void testDoubleNegative() {
		eval("--x","x");
		//eval("-x","-(x)");
	}
	
	public void testMinusMinus() {
		eval("x--3","(x+3)");
	}
	
	public void testPlusNeg() {
		eval("x+-y","(x-y)");
		eval("x+-1","(x-1)");
	}
	
	public void testVarOpVar() {
		eval("x-x","0");
		eval("x+x","(2*x)");
		eval("x*x","(x^2)");
		eval("x/x","1");
	}
	
	public void testMultiple() {
		eval("((x+-((0-(x+x))))*x)","((x-(0-(2*x)))*x)");
	}

}
