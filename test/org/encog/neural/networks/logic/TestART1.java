/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
