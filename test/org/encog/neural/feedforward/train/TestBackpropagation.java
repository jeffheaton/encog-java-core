package org.encog.neural.feedforward.train;

import java.util.Iterator;

import org.encog.neural.XOR;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Layer;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.layers.FeedforwardLayer;
import org.encog.neural.networks.training.backpropagation.Backpropagation;

import junit.framework.TestCase;

public class TestBackpropagation extends TestCase {
	private BasicNetwork createNetwork() 
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new FeedforwardLayer(2));
		network.addLayer(new FeedforwardLayer(3));
		network.addLayer(new FeedforwardLayer(1));
		network.reset();
		return network;
	}
	
	public void testBackpropagation() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = createNetwork();
		Train train = new Backpropagation(network, trainingData, 0.7, 0.9); 	

		for (int i = 0; i < 5000; i++) 
		{
			train.iteration();
			network = (BasicNetwork)train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for backpropagation",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));

	}
	
	public void testToString() throws Throwable
	{
		BasicNetwork network = createNetwork();
		network.getInputLayer().toString();
	}
	
	public void testCounts() throws Throwable
	{
		BasicNetwork network = createNetwork();
		network.getInputLayer().toString();
		TestCase.assertEquals(1, network.getHiddenLayerCount());
		TestCase.assertEquals(6, network.calculateNeuronCount());		
	}
	
	public void testPrune() throws Throwable
	{
		BasicNetwork network = createNetwork();
		Iterator<Layer> itr = network.getHiddenLayers().iterator();
		FeedforwardLayer hidden = (FeedforwardLayer)itr.next();
		
		TestCase.assertEquals(3,hidden.getNeuronCount());
		TestCase.assertEquals(4,hidden.getMatrixSize());
		TestCase.assertEquals(9, network.getInputLayer().getMatrixSize());
		
		hidden.prune(1);
		
		TestCase.assertEquals(2,hidden.getNeuronCount());
		TestCase.assertEquals(3,hidden.getMatrixSize());
		TestCase.assertEquals(6, network.getInputLayer().getMatrixSize());
	}
}
