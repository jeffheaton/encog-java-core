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
package org.encog.parse.expression.common;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.Encog;
import org.encog.ml.prg.EncogProgram;

public class TestExpressionVar extends TestCase {
	public void testAssignment() {
		EncogProgram expression = new EncogProgram("a");
		expression.getVariables().setVariable("a",5);
		Assert.assertEquals(5,expression.evaluate().toFloatValue(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testNegAssignment() {
		EncogProgram expression = new EncogProgram("-a");
		expression.getVariables().setVariable("a",5);
		Assert.assertEquals(-5,expression.evaluate().toFloatValue(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void test2NegAssignment() {
		EncogProgram expression = new EncogProgram("--a");
		expression.getVariables().setVariable("a",5);
		Assert.assertEquals(5,expression.evaluate().toFloatValue(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testAssignment2() {
		EncogProgram expression = new EncogProgram("cccc*(aa+bbb)");
		expression.getVariables().setVariable("aa",1);
		expression.getVariables().setVariable("bbb",2);
		expression.getVariables().setVariable("cccc",3);
		Assert.assertEquals(9,expression.evaluate().toFloatValue(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testAssignment3() {
		EncogProgram expression = new EncogProgram("v1+v2+v3");
		expression.getVariables().setVariable("v1",1);
		expression.getVariables().setVariable("v2",2);
		expression.getVariables().setVariable("v3",3);
		Assert.assertEquals(6,expression.evaluate().toFloatValue(),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testVarComplex() {
		EncogProgram expression = new EncogProgram("(x^((1+((x^-8)-(4^x)))^(((-7/2)-(0--5.8))/x)))");
		expression.getVariables().setVariable("x",10);
		Assert.assertTrue(Double.isNaN(expression.evaluate().toFloatValue()));
	}
	

}
