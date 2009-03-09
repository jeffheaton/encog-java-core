package org.encog.neural.networks.training;

import java.util.Iterator;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.CreateNetwork;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.backpropagation.Backpropagation;

import junit.framework.TestCase;

public class TestBackpropagation extends TestCase {
	
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
		
		System.out.println(improve);
		TestCase.assertTrue("Error too high for backpropagation",improve>0.01);

	}
	
	public void testToString() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		network.getInputLayer().toString();
	}
	
	public void testCounts() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		network.getInputLayer().toString();
		TestCase.assertEquals(1, network.getHiddenLayerCount());
		TestCase.assertEquals(6, network.calculateNeuronCount());		
	}
	
	public void testPrune() throws Throwable
	{
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
		Iterator<Layer> itr = network.getHiddenLayers().iterator();
		BasicLayer hidden = (BasicLayer)itr.next();
		
		TestCase.assertEquals(3,hidden.getNeuronCount());

		
		network.prune(hidden, 1);
		
		TestCase.assertEquals(2,hidden.getNeuronCount());
	}
}
