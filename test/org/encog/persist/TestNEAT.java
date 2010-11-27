package org.encog.persist;

import org.encog.engine.network.activation.ActivationStep;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.neat.NEATTraining;
import org.encog.util.logging.Logging;

import junit.framework.TestCase;

public class TestNEAT extends TestCase {
	
public static void testNEATPersist() {
	
	Logging.stopConsoleLogging();

	NeuralDataSet trainingSet = XOR.createXORDataSet();
	
	CalculateScore score = new TrainingSetScore(trainingSet);
	// train the neural network
	ActivationStep step = new ActivationStep();
	step.setCenter(0.5);
	
	final NEATTraining train = new NEATTraining(
			score, 2, 1, 1000);
	train.setOutputActivationFunction(step);

	train.iteration();
	
	BasicNetwork network = train.getNetwork();
	
	EncogPersistedCollection encog = 
		new EncogPersistedCollection("encogtest.eg");
	encog.create();
	encog.add("a", network);
	
	network = (BasicNetwork)encog.find("a");
}

	
}
