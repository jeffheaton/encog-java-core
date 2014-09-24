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
package org.encog.ml.prg;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.parse.expression.common.RenderCommonExpression;

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
