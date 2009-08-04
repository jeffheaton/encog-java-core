package org.encog.neural.networks.training;

import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.CreateNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.prune.PruneSelective;
import org.junit.Test;

public class TestBackpropagation extends TestCase   {
	
	@Test
	public void testBackpropagation() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		Train train = new Backpropagation(network, trainingData, 0.7, 0.9); 	

		train.iteration();
		double error1 = train.getError();
		train.iteration();
		network = (BasicNetwork)train.getNetwork();
		double error2 = train.getError();
		
		double improve = (error1-error2)/error1;
		
		Assert.assertTrue("Error too high for backpropagation",improve>0.001);

	}
	
	@Test
	public void testToString() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		Layer layer = network.getLayer(BasicNetwork.TAG_INPUT);
		layer.toString();
	}
	
	@Test
	public void testCounts() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		Assert.assertEquals(6, network.calculateNeuronCount());		
	}

	@Test
	public void testPrune() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer hiddenLayer = inputLayer.getNext().get(0).getToLayer();
		
		Assert.assertEquals(3,hiddenLayer.getNeuronCount());

		PruneSelective prune = new PruneSelective(network);
		prune.prune(hiddenLayer, 1);
		
		Assert.assertEquals(2,hiddenLayer.getNeuronCount());
	}
}
