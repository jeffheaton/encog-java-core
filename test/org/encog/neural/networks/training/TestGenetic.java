package org.encog.neural.networks.training;


import junit.framework.TestCase;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.CreateNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;
import org.encog.util.randomize.RangeRandomizer;
import org.junit.Assert;
import org.junit.Test;

public class TestGenetic  extends TestCase  {
	
	@Test
	public void testGenetic() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BasicNetwork network = CreateNetwork.createXORNetworkUntrained();
			
		CalculateScore score = new TrainingSetScore(trainingData);
		NeuralGeneticAlgorithm train = new NeuralGeneticAlgorithm(network, new RangeRandomizer(-1,1), score, 500,0.1,0.25);	

		train.iteration();
		double error1 = train.getError();
		for(int i=0;i<10;i++)
			train.iteration();
		network = (BasicNetwork)train.getNetwork();
		double error2 = train.getError();
		
		double improve = (error1-error2)/error1;		
		
		Assert.assertTrue("Genetic algorithm did not improve.",improve>0.0001);

	}
}
