package org.encog.neural.networks.training;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestTrainingContinuation extends TestCase {
	public void testContRPROP()
	{
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = NetworkUtil.createXORNetworkUntrained();
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		// train network 1, no continue
		ResilientPropagation rprop1 = new ResilientPropagation(network1,trainingData);
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		
		// train network 2, continue
		ResilientPropagation rprop2 = new ResilientPropagation(network2,trainingData);
		rprop2.iteration();
		rprop2.iteration();
		TrainingContinuation state = rprop2.pause();
		rprop2 = new ResilientPropagation(network2,trainingData);
		rprop2.resume(state);
		rprop2.iteration();
		rprop2.iteration();
		
		// verify weights are the same
		double[] weights1 = NetworkCODEC.networkToArray(network1);
		double[] weights2 = NetworkCODEC.networkToArray(network2);
		
		Assert.assertEquals(rprop1.getError(), rprop2.getError(), 0.01);
		Assert.assertEquals(weights1.length, weights2.length);
		Assert.assertArrayEquals(weights1, weights2, 0.01);
		
	}
	
	public void testContBackprop()
	{
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = NetworkUtil.createXORNetworkUntrained();
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		// train network 1, no continue
		Backpropagation rprop1 = new Backpropagation(network1,trainingData,0.4,0.4);
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		
		// train network 2, continue
		Backpropagation rprop2 = new Backpropagation(network2,trainingData,0.4,0.4);
		rprop2.iteration();
		rprop2.iteration();
		TrainingContinuation state = rprop2.pause();
		rprop2 = new Backpropagation(network2,trainingData,0.4,0.4);
		rprop2.resume(state);
		rprop2.iteration();
		rprop2.iteration();
		
		// verify weights are the same
		double[] weights1 = NetworkCODEC.networkToArray(network1);
		double[] weights2 = NetworkCODEC.networkToArray(network2);
		
		Assert.assertEquals(rprop1.getError(), rprop2.getError(), 0.01);
		Assert.assertEquals(weights1.length, weights2.length);
		Assert.assertArrayEquals(weights1, weights2, 0.01);
		
	}
}
