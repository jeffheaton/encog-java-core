package org.encog.neural.networks.training;

import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.mathutil.randomize.Randomizer;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.logging.Logging;
import org.encog.util.simple.EncogUtility;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TrainComplete extends TestCase {
	
	public void testCompleteTrain()
	{
		Logging.stopConsoleLogging();
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = EncogUtility.simpleFeedForward(2, 5, 7, 1, true);
		Randomizer randomizer = new ConsistentRandomizer(-1, 1, 50);
		randomizer.randomize(network);
		Train rprop = new ResilientPropagation(network, trainingData);
		int iteration = 0;
		do {
			rprop.iteration();
			iteration++;
		} while( iteration<5000 && rprop.getError()>0.01);
		System.out.println(iteration);
		Assert.assertTrue(iteration<40);
	}
	
}
