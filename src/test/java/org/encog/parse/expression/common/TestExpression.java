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
import org.encog.ml.ea.exception.EAError;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;

public class TestExpression extends TestCase {
	public void testConst() {
		Assert.assertEquals( 1, EncogProgram.parseFloat("1"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -1, EncogProgram.parseFloat("-1"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1, EncogProgram.parseFloat("--1"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -1, EncogProgram.parseFloat("---1"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1, EncogProgram.parseFloat("----1"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, EncogProgram.parseFloat("100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 100, EncogProgram.parseFloat("+100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -100, EncogProgram.parseFloat("-100"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1000, EncogProgram.parseFloat("1e3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 0.001, EncogProgram.parseFloat("1e-3"),Encog.DEFAULT_DOUBLE_EQUAL);		
		Assert.assertEquals( 1.5, EncogProgram.parseFloat("1.5"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -1.5, EncogProgram.parseFloat("-1.5"),Encog.DEFAULT_DOUBLE_EQUAL);		
		Assert.assertEquals( 1500, EncogProgram.parseFloat("1.5e3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( -0.0015, EncogProgram.parseFloat("-1.5e-3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 1.2345678, EncogProgram.parseFloat("1.2345678"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testTypes() {
		ExpressionValue exp = EncogProgram.parseExpression("cint(1.2345678)");
		Assert.assertTrue(exp.isInt());
		Assert.assertEquals( 1, exp.toIntValue());
	
		exp = EncogProgram.parseExpression("cstr(1.2345678)");
		Assert.assertTrue(exp.isString());
		Assert.assertEquals( "1.2345678", exp.toStringValue());
		
		exp = EncogProgram.parseExpression("cfloat(\"1.2345678\")");
		Assert.assertTrue(exp.isFloat());
		Assert.assertEquals( "1.2345678", exp.toStringValue());
		
	}
	
	public void testPrecedence() {
		Assert.assertEquals( -2.5, EncogProgram.parseFloat("1.0+2.0*3.0/4.0-5.0"),Encog.DEFAULT_DOUBLE_EQUAL);		
	}
	
	public void testAdd() {
		Assert.assertEquals( 5, EncogProgram.parseFloat("2+3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 3, EncogProgram.parseFloat("5+-2"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 6, EncogProgram.parseFloat("1+2+3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 10, EncogProgram.parseFloat("1+2+3+4"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testSub() {
		Assert.assertEquals( -1, EncogProgram.parseFloat("2-3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 7, EncogProgram.parseFloat("5--2"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testMul() {
		Assert.assertEquals( -6, EncogProgram.parseFloat("-2*3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 6, EncogProgram.parseFloat("2*3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 6, EncogProgram.parseFloat("-2*-3"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 24, EncogProgram.parseFloat("2*3*4"),Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertEquals( 120, EncogProgram.parseFloat("2*3*4*5"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testPower() {
		Assert.assertEquals( 8, EncogProgram.parseFloat("2^3"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testParen1() {
		Assert.assertEquals( 14, EncogProgram.parseFloat("2*(3+4)"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testParen2() {
		Assert.assertEquals( 10, EncogProgram.parseFloat("(2*3)+4"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testParen3() {
		Assert.assertEquals( 100, EncogProgram.parseFloat("(2*3)^2+(4*2)^2"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testParen4() {
		Assert.assertEquals( 4, EncogProgram.parseFloat("2^(1+1)"),Encog.DEFAULT_DOUBLE_EQUAL);
	}
	
	public void testBad() {
		try {
			Assert.assertEquals( 0, EncogProgram.parseFloat("2*(3+4"),Encog.DEFAULT_DOUBLE_EQUAL);
			Assert.assertTrue(false);
		} catch(EAError ex) {
			// good, we want an exception.
		}
		
		try {
			Assert.assertEquals( 0, EncogProgram.parseFloat("5+"),Encog.DEFAULT_DOUBLE_EQUAL);
			Assert.assertTrue(false);
		} catch(EAError ex) {
			// good, we want an exception.
		}
	}
	
}
