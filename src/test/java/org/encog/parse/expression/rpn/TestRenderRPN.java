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
package org.encog.parse.expression.rpn;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;

public class TestRenderRPN extends TestCase {
	
	public void testRenderBasic() {
		EncogProgram expression = new EncogProgram("(2+6)");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);
		Assert.assertEquals("2 6 [+]", result);
	}
	
	public void testRenderComplex() {
		EncogProgram expression = new EncogProgram("((a+25)^3/25)-((a*3)^4/250)");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);
		Assert.assertEquals("a 25 [+] 3 [^] 25 [/] a 3 [*] 4 [^] 250 [/] [-]", result);
	}
	
	public void testRenderFunction() {
		EncogProgram expression = new EncogProgram("(sin(x)+cos(x))/2");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);		
		Assert.assertEquals("x [sin] x [cos] [+] 2 [/]", result);		
	}
	
	public void testKnownConst() {
		EncogProgram expression = new EncogProgram("x*2*PI");
		RenderRPN render = new RenderRPN();
		String result = render.render(expression);		
		Assert.assertEquals("x 2 [*] PI [*]", result);		
	}
}
