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
package org.encog.parse.expression.epl;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;

public class TestRenderEPL extends TestCase {
	
	public void testRenderBasic() {
		EncogProgram expression = new EncogProgram("(2+6)");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);
		Assert.assertEquals("[#const:0:2][#const:0:6][+:2]", result);
	}
	
	public void testRenderComplex() {
		EncogProgram expression = new EncogProgram("((a+25)^3/25)-((a*3)^4/250)");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);
		Assert.assertEquals("[#var:0:0][#const:0:25][+:2][#const:0:3][^:2][#const:0:25][/:2][#var:0:0][#const:0:3][*:2][#const:0:4][^:2][#const:0:250][/:2][-:2]", result);
	}
	
	public void testRenderFunction() {
		EncogProgram expression = new EncogProgram("(sin(x)+cos(x))/2");
		RenderEPL render = new RenderEPL();
		String result = render.render(expression);	
		Assert.assertEquals("[#var:0:0][sin:1][#var:0:0][cos:1][+:2][#const:0:2][/:2]", result);		
	}
}
