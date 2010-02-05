/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
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

package org.encog.neural.networks.training.competitive.neighborhood;

import org.encog.util.math.rbf.GaussianFunction;
import org.encog.util.math.rbf.RadialBasisFunction;
import org.junit.Assert;

import junit.framework.TestCase;

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
		NeighborhoodGaussian bubble = new NeighborhoodGaussian(radial);
		Assert.assertEquals(0.0, bubble.function(5, 0),0.1);
		Assert.assertEquals(1.0, bubble.function(5, 5),0.1);
		Assert.assertEquals(0.6, bubble.function(5, 4),0.1);
	}
	
	public void testGaussianMulti() throws Throwable {
		NeighborhoodGaussianMulti n = new NeighborhoodGaussianMulti(5,5);
		Assert.assertEquals(0.6, n.function(5, 0),0.1);
		Assert.assertEquals(1.0, n.function(5, 5),0.1);
	}
}
