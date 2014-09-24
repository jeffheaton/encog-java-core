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
package org.encog.ml.prg.train;

import java.util.Random;

import junit.framework.TestCase;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.generator.PrgGrowGenerator;
import org.encog.parse.expression.common.RenderCommonExpression;

public class TestGenerate extends TestCase {

	public void testDepth() {
		EncogProgramContext context = new EncogProgramContext();
		context.defineVariable("x");
		
		StandardExtensions.createAll(context);
		
		PrgGrowGenerator rnd = new PrgGrowGenerator(context,2);
		EncogProgram prg = rnd.generate(new Random());
		RenderCommonExpression render = new RenderCommonExpression();
		String str = render.render(prg);
		System.out.println(str);
	}
}
