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
