package org.encog.neural.networks.training;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.junit.Test;

import junit.framework.TestCase;

public class TestAnneal extends TestCase {

	@Test
	public void testAnneal() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BasicNetwork network = XOR.createThreeLayerNet();
		CalculateScore score = new TrainingSetScore(trainingData);
		NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(network,score,10,2,100);	

		train.iteration();
		double error1 = train.getError();
		train.iteration();
		network = (BasicNetwork)train.getNetwork();
		double error2 = train.getError();
		
		double improve = (error1-error2)/error1;
		
		TestCase.assertTrue("Error too high for simulated annealing.",improve>0.01);

	}
	
}
