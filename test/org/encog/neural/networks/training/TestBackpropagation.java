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
		
		Assert.assertTrue("Error too high for backpropagation",improve>0.01);

	}
	
	@Test
	public void testToString() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		network.getInputLayer().toString();
	}
	
	@Test
	public void testCounts() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		network.getInputLayer().toString();
		Assert.assertEquals(1, network.getHiddenLayerCount());
		Assert.assertEquals(6, network.calculateNeuronCount());		
	}

	@Test
	public void testPrune() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		Iterator<Layer> itr = network.getHiddenLayers().iterator();
		BasicLayer hidden = (BasicLayer)itr.next();
		
		Assert.assertEquals(3,hidden.getNeuronCount());

		PruneSelective prune = new PruneSelective(network);
		prune.prune(hidden, 1);
		
		Assert.assertEquals(2,hidden.getNeuronCount());
	}
}
