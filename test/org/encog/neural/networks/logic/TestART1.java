/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.networks.logic;

import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ART1Pattern;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestART1 extends TestCase {

	public static final int INPUT_NEURONS = 5;
	public static final int OUTPUT_NEURONS = 2;


	public BiPolarNeuralData setupInput(String pattern) {
		BiPolarNeuralData result = new BiPolarNeuralData(INPUT_NEURONS);
		for (int i = 0; i < INPUT_NEURONS; i++) {
			result.setData(i, pattern.charAt(i) == 'O');
		}

		return result;
	}
	
	public int present(ART1Logic logic, String str)
	{
		BiPolarNeuralData in = setupInput(str);
		BiPolarNeuralData out = new BiPolarNeuralData(OUTPUT_NEURONS);
		logic.compute(in, out);
		if (logic.hasWinner()) {
			return logic.getWinner();
		} else {
			return -1;
		}
	}

	public void testART1Patterns() throws Throwable {

		ART1Pattern pattern = new ART1Pattern();
		pattern.setInputNeurons(INPUT_NEURONS);
		pattern.setOutputNeurons(OUTPUT_NEURONS);
		BasicNetwork network = pattern.generate();
		ART1Logic logic = (ART1Logic) network.getLogic();
		
		Assert.assertEquals(present(logic,"   O "),0);
		Assert.assertEquals(present(logic,"  O O"),1);
		Assert.assertEquals(present(logic,"    O"),1);
		Assert.assertEquals(present(logic,"  O O"),-1);
		Assert.assertEquals(present(logic,"    O"),1);
		Assert.assertEquals(present(logic,"  O O"),-1);
		Assert.assertEquals(present(logic,"    O"),1);

	}

}
