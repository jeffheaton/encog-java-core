package org.encog.neural.feedforward.train;


import org.encog.neural.XOR;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.feedforward.FeedforwardNetwork;
import org.encog.neural.networks.feedforward.train.genetic.TrainingSetNeuralGeneticAlgorithm;

import junit.framework.TestCase;

public class TestGenetic extends TestCase {
	public void testGenetic() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		FeedforwardNetwork network = XOR.createThreeLayerNet();
				
		TrainingSetNeuralGeneticAlgorithm train = new TrainingSetNeuralGeneticAlgorithm(network, true, trainingData,5000,0.1,0.25);	

		for (int i = 0; i < 100; i++) 
		{
			train.iteration();
			network = train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for genetic algorithm",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));

	}
}
