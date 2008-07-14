package org.encog.neural.feedforward.train;

import java.util.Iterator;

import org.encog.neural.XOR;
import org.encog.neural.feedforward.FeedforwardLayer;
import org.encog.neural.feedforward.FeedforwardNetwork;
import org.encog.neural.feedforward.train.backpropagation.Backpropagation;

import junit.framework.TestCase;

public class TestBackpropagation extends TestCase {
	private FeedforwardNetwork createNetwork() 
	{
		FeedforwardNetwork network = new FeedforwardNetwork();
		network.addLayer(new FeedforwardLayer(2));
		network.addLayer(new FeedforwardLayer(3));
		network.addLayer(new FeedforwardLayer(1));
		network.reset();
		return network;
	}
	
	public void testBackpropagation() throws Throwable
	{
		FeedforwardNetwork network = createNetwork();
		Train train = new Backpropagation(network, XOR.XOR_INPUT, XOR.XOR_IDEAL, 0.7, 0.9); 	

		for (int i = 0; i < 5000; i++) 
		{
			train.iteration();
			network = train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for backpropagation",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));

	}
	
	public void testToString() throws Throwable
	{
		FeedforwardNetwork network = createNetwork();
		network.getInputLayer().toString();
	}
	
	public void testCounts() throws Throwable
	{
		FeedforwardNetwork network = createNetwork();
		network.getInputLayer().toString();
		TestCase.assertEquals(1, network.getHiddenLayerCount());
		TestCase.assertEquals(6, network.calculateNeuronCount());		
	}
	
	public void testPrune() throws Throwable
	{
		FeedforwardNetwork network = createNetwork();
		Iterator<FeedforwardLayer> itr = network.getHiddenLayers().iterator();
		FeedforwardLayer hidden = itr.next();
		
		TestCase.assertEquals(3,hidden.getNeuronCount());
		TestCase.assertEquals(4,hidden.getMatrixSize());
		TestCase.assertEquals(9, network.getInputLayer().getMatrixSize());
		
		hidden.prune(1);
		
		TestCase.assertEquals(2,hidden.getNeuronCount());
		TestCase.assertEquals(3,hidden.getMatrixSize());
		TestCase.assertEquals(6, network.getInputLayer().getMatrixSize());
	}
	
	/*
	public void testGenetic() throws Throwable
	{
		Network network = createNetwork();
		Train train = new NeuralNetworkGeneticAlgorithm(network, true, XOR.XOR_INPUT, XOR.XOR_IDEAL,5000,0.1,0.25,0.5);	

		for (int i = 0; i < 100; i++) 
		{
			train.iteration();
			network = train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for genetic algorithm",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));

	}
	
	public void testAnneal() throws Throwable
	{
		Network network = createNetwork();
		Train train = new NeuralSimulatedAnnealing(network,XOR.XOR_INPUT, XOR.XOR_IDEAL,10,2,100);	

		for (int i = 0; i < 100; i++) 
		{
			train.iteration();
			network = train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for simulated annealing",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));

	}*/
}
