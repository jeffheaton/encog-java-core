package org.encog.neural.networks;

import org.encog.engine.util.EngineArray;
import org.encog.mathutil.randomize.FanInRandomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.util.simple.EncogUtility;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestWeightAccess extends TestCase {
	
	public void testTracks()
	{		
		BasicNetwork network = EncogUtility.simpleFeedForward(5,10,15,20, true);
		double[] weights = network.getStructure().getFlat().getWeights();
		EngineArray.fill(weights, 100);
		(new RangeRandomizer(-1,1)).randomize(network);
		
		for(int i=0;i<weights.length;i++ )
		{
			Assert.assertTrue(weights[i]<10);
		}
		
	}
	
	public void testFanIn()
	{		
		BasicNetwork network = EncogUtility.simpleFeedForward(5,10,15,20, true);
		double[] weights = network.getStructure().getFlat().getWeights();
		EngineArray.fill(weights, 100);
		(new FanInRandomizer()).randomize(network);
		System.out.println(network.dumpWeights());
		for(int i=0;i<weights.length;i++ )
		{
			Assert.assertTrue(weights[i]<10);
		}
		
	}
	
	public void testWeights()
	{
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 3, 0, 1, true);
		double[] weights = network.getStructure().getFlat().getWeights();
		Assert.assertEquals(weights.length, 13);
		for(int i=0;i<weights.length;i++)
		{
			weights[i] = i;
		}
		
		Assert.assertEquals(0.0, network.getWeight(1, 0, 0) );
		Assert.assertEquals(1.0, network.getWeight(1, 1, 0) );
		Assert.assertEquals(2.0, network.getWeight(1, 2, 0) );
		Assert.assertEquals(3.0, network.getWeight(1, 3, 0) );
		Assert.assertEquals(4.0, network.getWeight(0, 0, 0) );
		Assert.assertEquals(5.0, network.getWeight(0, 1, 0) );
		Assert.assertEquals(6.0, network.getWeight(0, 2, 0) );		
		Assert.assertEquals(7.0, network.getWeight(0, 0, 1) );
		Assert.assertEquals(8.0, network.getWeight(0, 1, 1) );
		Assert.assertEquals(9.0, network.getWeight(0, 2, 1) );		
		Assert.assertEquals(10.0, network.getWeight(0, 0, 2) );
		Assert.assertEquals(11.0, network.getWeight(0, 1, 2) );
		Assert.assertEquals(12.0, network.getWeight(0, 2, 2) );
		
	}
}
