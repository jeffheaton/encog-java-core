package org.encog.neural.prune;

import java.util.Arrays;

import junit.framework.TestCase;

import org.encog.engine.network.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.util.simple.EncogUtility;
import org.junit.Assert;

public class TestPruneSelective extends TestCase {
	
	private BasicNetwork obtainNetwork()
	{
		BasicNetwork network = EncogUtility.simpleFeedForward(2,3,0,4,false);
		double[] weights = { 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25 };
		NetworkCODEC.arrayToNetwork(weights, network);
		
		Assert.assertEquals(1.0, network.getWeight(1, 0, 0),0.01);		
		Assert.assertEquals(2.0, network.getWeight(1, 1, 0),0.01);
		Assert.assertEquals(3.0, network.getWeight(1, 2, 0),0.01);
		Assert.assertEquals(4.0, network.getWeight(1, 3, 0),0.01);
		
		Assert.assertEquals(5.0, network.getWeight(1, 0, 1),0.01);
		Assert.assertEquals(6.0, network.getWeight(1, 1, 1),0.01);
		Assert.assertEquals(7.0, network.getWeight(1, 2, 1),0.01);
		Assert.assertEquals(8.0, network.getWeight(1, 3, 1),0.01);
		
		Assert.assertEquals(9.0, network.getWeight(1, 0, 2),0.01);
		Assert.assertEquals(10.0, network.getWeight(1, 1, 2),0.01);
		Assert.assertEquals(11.0, network.getWeight(1, 2, 2),0.01);
		Assert.assertEquals(12.0, network.getWeight(1, 3, 2),0.01);
		
		Assert.assertEquals(13.0, network.getWeight(1, 0, 3),0.01);
		Assert.assertEquals(14.0, network.getWeight(1, 1, 3),0.01);
		Assert.assertEquals(15.0, network.getWeight(1, 2, 3),0.01);
		Assert.assertEquals(16.0, network.getWeight(1, 3, 3),0.01);
		
		Assert.assertEquals(17.0, network.getWeight(0, 0, 0),0.01);
		Assert.assertEquals(18.0, network.getWeight(0, 1, 0),0.01);
		Assert.assertEquals(19.0, network.getWeight(0, 2, 0),0.01);
		Assert.assertEquals(20.0, network.getWeight(0, 0, 1),0.01);
		Assert.assertEquals(21.0, network.getWeight(0, 1, 1),0.01);
		Assert.assertEquals(22.0, network.getWeight(0, 2, 1),0.01);
		
		Assert.assertEquals(20.0, network.getWeight(0, 0, 1),0.01);
		Assert.assertEquals(21.0, network.getWeight(0, 1, 1),0.01);
		Assert.assertEquals(22.0, network.getWeight(0, 2, 1),0.01);
		
		Assert.assertEquals(23.0, network.getWeight(0, 0, 2),0.01);
		Assert.assertEquals(24.0, network.getWeight(0, 1, 2),0.01);
		Assert.assertEquals(25.0, network.getWeight(0, 2, 2),0.01);

		
		return network;
	}
	
	private void checkWithModel(FlatNetwork model, FlatNetwork pruned)
	{
		Assert.assertArrayEquals(model.getContextTargetOffset(),pruned.getContextTargetOffset());
		Assert.assertArrayEquals(model.getContextTargetSize(),pruned.getContextTargetSize());
		Assert.assertArrayEquals(model.getLayerCounts(),pruned.getLayerCounts());
		Assert.assertArrayEquals(model.getLayerFeedCounts(),pruned.getLayerFeedCounts());
		Assert.assertArrayEquals(model.getLayerIndex(),pruned.getLayerIndex());
		Assert.assertEquals(model.getLayerOutput().length,model.getLayerOutput().length);
		Assert.assertArrayEquals(model.getWeightIndex(),pruned.getWeightIndex());
	}
	
	public void testPruneNeuronInput()
	{
		BasicNetwork network = obtainNetwork();
		System.out.println(network.dumpWeights());
		PruneSelective prune = new PruneSelective(network);
		prune.prune(0, 1);
		Assert.assertEquals(22, network.encodedArrayLength());
		Assert.assertEquals(1,network.getLayerNeuronCount(0));
		Assert.assertEquals("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,19,20,22,23,25", network.dumpWeights());
		
		BasicNetwork model = EncogUtility.simpleFeedForward(1,3,0,4,false);
		//checkWithModel(model.getStructure().getFlat(),network.getStructure().getFlat());
		
	}
}
