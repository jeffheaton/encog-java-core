package org.encog.neural.prune;

import junit.framework.Assert;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.layers.Layer;
import org.junit.Test;

public class TestPrune {
	@Test
	public void testToString() throws Throwable
	{
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		Layer layer = network.getLayer(BasicNetwork.TAG_INPUT);
		layer.toString();
	}
	
	@Test
	public void testCounts() throws Throwable
	{
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		Assert.assertEquals(6, network.calculateNeuronCount());		
	}

	@Test
	public void testPrune() throws Throwable
	{
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer hiddenLayer = inputLayer.getNext().get(0).getToLayer();
		
		Assert.assertEquals(3,hiddenLayer.getNeuronCount());

		PruneSelective prune = new PruneSelective(network);
		prune.prune(hiddenLayer, 1);
		
		Assert.assertEquals(2,hiddenLayer.getNeuronCount());
	}
}
