/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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

import org.encog.EncogError;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.score.adjust.ComplexityAdjustedScore;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.PrgCODEC;
import org.encog.ml.prg.expvalue.DivisionByZeroError;
import org.encog.ml.prg.extension.FunctionFactory;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.opp.SubtreeCrossover;
import org.encog.ml.prg.opp.SubtreeMutation;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.ml.prg.train.ZeroEvalScoreFunction;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

public class TestRewriteAlgebraic {

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
		pop.getRules().addRewriteRule(new RewriteConstants());
		pop.getRules().addRewriteRule(new RewriteAlgebraic());

		EncogProgram expression = new EncogProgram(context);
		expression.compileExpression(start);
		RenderCommonExpression render = new RenderCommonExpression();
		pop.getRules().rewrite(expression);
		Assert.assertEquals(expect, render.render(expression));
	}

	public static void checkMissingOperator(EncogProgramContext context) {


        PrgPopulation pop = new PrgPopulation(context,1);
        CalculateScore score = new ZeroEvalScoreFunction();

        TrainEA genetic = new TrainEA(pop, score);
        genetic.setValidationMode(true);
        genetic.setCODEC(new PrgCODEC());
        genetic.addOperation(0.95, new SubtreeCrossover());
        genetic.addOperation(0.05, new SubtreeMutation(context,4));
        genetic.addScoreAdjuster(new ComplexityAdjustedScore());
        pop.getRules().addRewriteRule(new RewriteConstants());
        pop.getRules().addRewriteRule(new RewriteAlgebraic());

        EncogProgram expression = new EncogProgram(context);
        expression.compileExpression("1");
        RenderCommonExpression render = new RenderCommonExpression();
        pop.getRules().rewrite(expression);
        Assert.assertEquals("1", render.render(expression));
    }

    @Test
    public void testRequiredExtensions() {

        List<ProgramExtensionTemplate> required = new ArrayList<ProgramExtensionTemplate>();
        required.add(StandardExtensions.EXTENSION_VAR_SUPPORT);
        required.add(StandardExtensions.EXTENSION_CONST_SUPPORT);
        required.add(StandardExtensions.EXTENSION_NEG);
        required.add(StandardExtensions.EXTENSION_ADD);
        required.add(StandardExtensions.EXTENSION_SUB);
        required.add(StandardExtensions.EXTENSION_MUL);
        required.add(StandardExtensions.EXTENSION_PDIV);
        required.add(StandardExtensions.EXTENSION_POWER);

        // try to rewrite with each of the above missing
        for(ProgramExtensionTemplate missing: required) {
            // build a set, with the specified template missing
            EncogProgramContext context = new EncogProgramContext();

            FunctionFactory factory = context.getFunctions();



            for(ProgramExtensionTemplate temp: required) {
                if( temp!= missing ) {
                    factory.addExtension(temp);
                }
            }

            // Should throw an exception
            try {
                TestRewriteAlgebraic.checkMissingOperator(context);
                Assert.fail("Did not throw error on missing: " + missing.toString());
            } catch(EncogError e) {
                // Should happen
            }

        }
    }

	@Test
	public void testMinusZero() {
		eval("x-0","x");
		eval("0-0","0");
		eval("10-0","10");
	}

	@Test
	public void testZeroMul() {
		eval("0*0","0");
		eval("1*0","0");
		eval("0*1","0");
	}

	@Test
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

	@Test
	public void testZeroPlus() {
		eval("0+0","0");
		eval("1+0","1");
		eval("0+1","1");
		eval("x+0","x");
	}

	@Test
	public void testPowerZero() {
		eval("0^x","0");
		eval("0^0","1");
		eval("x^0","1");
		eval("1^0","1");
		eval("-1^0","1");
		eval("(x+y)^0","1");
		eval("x+(x+y)^0","(x+1)");
	}

	@Test
	public void testOnePower() {
		eval("1^500","1");
		eval("1^x","1");
		eval("1^1","1");
	}

	@Test
	public void testDoubleNegative() {
		eval("--x","x");
		//eval("-x","-(x)");
	}

	@Test
	public void testMinusMinus() {
		eval("x--3","(x+3)");
	}

	@Test
	public void testPlusNeg() {
		eval("x+-y","(x-y)");
		eval("x+-1","(x-1)");
	}

	@Test
	public void testVarOpVar() {
		eval("x-x","0");
		eval("x+x","(2*x)");
		eval("x*x","(x^2)");
		eval("x/x","1");
	}

	@Test
	public void testMultiple() {
		eval("((x+-((0-(x+x))))*x)","((x-(0-(2*x)))*x)");
	}

}
