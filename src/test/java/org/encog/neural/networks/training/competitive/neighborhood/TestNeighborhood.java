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
package org.encog.neural.networks.training.competitive.neighborhood;

import junit.framework.TestCase;

import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodBubble;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodRBF1D;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodSingle;
import org.junit.Assert;

public class TestNeighborhood extends TestCase {
	
	public void testBubble() throws Throwable
	{
		NeighborhoodBubble bubble = new NeighborhoodBubble(2);
		Assert.assertEquals(0.0, bubble.function(5, 0),0.1);
		Assert.assertEquals(1.0, bubble.function(5, 4),0.1);
		Assert.assertEquals(1.0, bubble.function(5, 5),0.1);
	}
	
	public void testSingle() throws Throwable {
		NeighborhoodSingle bubble = new NeighborhoodSingle();
		Assert.assertEquals(0.0, bubble.function(5, 0),0.1);
		Assert.assertEquals(1.0, bubble.function(5, 5),0.1);	
	}
	
	public void testGaussian() throws Throwable {
		RadialBasisFunction radial = new GaussianFunction(0.0,1.0,1.0);
		NeighborhoodRBF1D bubble = new NeighborhoodRBF1D(radial);
		Assert.assertEquals(0.0, bubble.function(5, 0),0.1);
		Assert.assertEquals(1.0, bubble.function(5, 5),0.1);
		Assert.assertEquals(0.6, bubble.function(5, 4),0.1);
	}

}
